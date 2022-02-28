package spacefactory.block.entity.conduit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.block.ConduitBlock;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class ConduitPath {
    public final BlockPos end;
    public final Direction side;
    // TODO add BlockApiCache for EnergyStorage for end+side
    public final int length;
    public final List<BlockPos> cables;

    private ConduitPath(BlockPos end, Direction side, int length, List<BlockPos> cables) {
        this.end = end;
        this.side = side;
        this.length = length;
        this.cables = cables;
    }

    public static List<ConduitPath> buildPaths(World world, BlockPos start, boolean reverse) {
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
        List<ConduitPath> ret = new ArrayList<>();
        for (Map.Entry<BlockPos, Link> entry : visited.entrySet()) {
            BlockPos pos = entry.getKey();
            Link link = entry.getValue();
            if (link.previous != null && isEndPoint(world, pos, link.side, reverse)) {
                List<BlockPos> cableList = new ArrayList<>();
                BlockPos cable = link.previous;
                while (cable != null) {
                    cableList.add(cable);
                    cable = visited.get(cable).previous;
                }
                Collections.reverse(cableList);

                ConduitPath path = new ConduitPath(pos, link.side.getOpposite(), link.length, cableList);
                ret.add(path);
            }
        }
        ret.sort(Comparator.comparingInt(path -> path.length));
        return ret;
    }

    public static boolean isCableOrEndPoint(World world, BlockPos pos, Direction side, boolean reverse) {
        return isCable(world, pos) || isEndPoint(world, pos, side, reverse);
    }

    public static boolean isCable(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof ConduitBlock;
    }

    private static boolean isEndPoint(World world, BlockPos pos, Direction side, boolean reverse) {
        if (world.getBlockState(pos).getBlock() instanceof ConduitBlock) {
            return false;
        }
        if (side == null) {
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
