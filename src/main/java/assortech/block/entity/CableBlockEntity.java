package assortech.block.entity;

import assortech.Assortech;
import assortech.block.CableBlock;
import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class CableBlockEntity extends BlockEntity {
    protected int current = 0;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.CABLE, pos, state);
    }

    @Override
    public void markRemoved() {
        if (this.world != null) {
            CableNetwork.of(this.world).onCableUpdate(this.pos);
        }
        super.markRemoved();
    }

    public EnergyStorage getEnergyHandler(Direction side) {
        return new EnergyInserter(CableNetwork.of(this.world), pos, side);
    }

    protected int getTransferLimit() {
        return 32;
    }

    protected void onEnergyTransfer(int amount) {
        this.current += amount;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CableBlockEntity be) {
        if (be.current > 0) {
            be.current = 0;
            be.markDirty();
        }
    }

    public static class EnergyInserter implements EnergyStorage {
        protected final CableNetwork network;
        protected final BlockPos pos;
        protected final Direction side; // doesn't matter for cables currently

        public EnergyInserter(CableNetwork network, BlockPos pos, Direction side) {
            this.network = network;
            this.pos = pos;
            this.side = side;
        }

        @Override
        public boolean supportsInsertion() {
            return true;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            return this.network.send(this.pos, maxAmount, transaction);
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long getAmount() {
            return 0;
        }

        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }
    }

    public static class CableNetwork {
        private static final Map<World, CableNetwork> INSTANCES = new MapMaker().weakKeys().makeMap();

        public static CableNetwork of(World world) {
            if (!INSTANCES.containsKey(world)) {
                INSTANCES.put(world, new CableNetwork(world));
            }
            return INSTANCES.get(world);
        }

        protected final World world;
        /**
         * Cable paths from a cable position to all connected consumers, sorted by length.
         */
        private final Map<BlockPos, List<CablePath>> cache = new HashMap<>();

        private CableNetwork(World world) {
            this.world = world;
        }

        /**
         * Tries to send specified amount of energy through cables, returns amount that was sent.
         */
        public long send(BlockPos pos, long amount, TransactionContext transaction) {
            long left = amount;

            if (!this.cache.containsKey(pos)) {
                this.cache.put(pos, CablePath.buildPaths(this.world, pos, false));
            }

            for (Iterator<CablePath> it = this.cache.get(pos).iterator(); it.hasNext(); ) {
                CablePath path = it.next();
                EnergyStorage endpoint = EnergyStorage.SIDED.find(this.world, path.end, path.side);
                if (endpoint == null || !endpoint.supportsInsertion()) {
                    it.remove();
                    if (this.cache.get(pos).isEmpty()) {
                        this.cache.remove(pos);
                    }
                    continue;
                }
                long inserted = endpoint.insert(left, transaction);
                left -= inserted;

                for (BlockPos cablePos : path.cables) {
                    BlockEntity cablePosEntity = this.world.getBlockEntity(cablePos);
                    if (cablePosEntity instanceof CableBlockEntity cable) {
                        cable.current += inserted;
                        if (cable.current > cable.getTransferLimit()) {
                            world.removeBlock(cablePos, false);
                            break;
                        }
                    }
                }
                if (left == 0) {
                    break;
                }
            }

            return amount - left;
        }

        public void onCableUpdate(BlockPos cable) {
            List<BlockPos> removed = new ArrayList<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            queue.add(cable);
            while (!queue.isEmpty()) {
                BlockPos pos = queue.remove();
                for (Direction direction : Direction.values()) {
                    BlockPos neighbor = pos.offset(direction);
                    if (this.cache.containsKey(neighbor) || CablePath.isCable(this.world, neighbor)) {
                        this.cache.remove(neighbor);
                    }
                    if (CablePath.isCable(this.world, neighbor) && !queue.contains(neighbor) && !removed.contains(neighbor)) {
                        removed.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            this.cache.remove(cable);
        }
    }


    public static class CablePath {
        public final BlockPos end;
        public final Direction side;
        // TODO add BlockApiCache for EnergyStorage for end+side
        public final int length;
        public final List<BlockPos> cables;

        private CablePath(BlockPos end, Direction side, int length, List<BlockPos> cables) {
            this.end = end;
            this.side = side;
            this.length = length;
            this.cables = cables;
        }

        public static List<CablePath> buildPaths(World world, BlockPos start, boolean reverse) {
            Map<BlockPos, Link> visited = new HashMap<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            visited.put(start, new Link(null, null, 0));
            queue.add(start);
            while (!queue.isEmpty()) {
                BlockPos pos = queue.remove();
                Link link = visited.get(pos);
                int length = link.length;
                for (Direction direction : Direction.values()) {
                    BlockPos next = pos.offset(direction);
                    if (isCableOrEndPoint(world, next, direction, reverse) && (!visited.containsKey(next) || visited.get(next).length > length + 1)) {
                        visited.put(next, new Link(pos, direction, length + 1));
                        if (isCable(world, next) && !queue.contains(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
            List<CablePath> ret = new ArrayList<>();
            for (Map.Entry<BlockPos, Link> entry : visited.entrySet()) {
                BlockPos pos = entry.getKey();
                Link link = entry.getValue();
                if (isEndPoint(world, pos, link.side, reverse)) {
                    List<BlockPos> cableList = new ArrayList<>();
                    BlockPos cable = link.previous;
                    while (cable != null) {
                        cableList.add(cable);
                        cable = visited.get(cable).previous;
                    }
                    Collections.reverse(cableList);

                    CablePath path = new CablePath(pos, link.side.getOpposite(), link.length, cableList);
                    ret.add(path);
                }
            }
            ret.sort(Comparator.comparingInt(path -> path.length));
            return ret;
        }

        private static boolean isCableOrEndPoint(World world, BlockPos pos, Direction side, boolean reverse) {
            return isCable(world, pos) || isEndPoint(world, pos, side, reverse);
        }

        private static boolean isCable(World world, BlockPos pos) {
            return world.getBlockState(pos).getBlock() instanceof CableBlock;
        }

        private static boolean isEndPoint(World world, BlockPos pos, Direction side, boolean reverse) {
            if (world.getBlockState(pos).getBlock() instanceof CableBlock) {
                return false;
            }
            EnergyStorage storage = EnergyStorage.SIDED.find(world, pos, side.getOpposite());
            return storage != null && (reverse ? storage.supportsExtraction() : storage.supportsInsertion());
        }

        private static class Link {
            public final @Nullable BlockPos previous;
            public final @Nullable Direction side;
            public final int length;

            private Link(@Nullable BlockPos previous, @Nullable Direction side, int length) {
                this.previous = previous;
                this.side = side;
                this.length = length;
            }
        }
    }
}
