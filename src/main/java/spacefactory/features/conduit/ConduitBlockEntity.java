package spacefactory.features.conduit;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;

public class ConduitBlockEntity extends BlockEntity {
    protected int current = 0;

    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.CONDUIT, pos, state);
    }

    public int getTransferRate() {
        if (this.getCachedState().getBlock() instanceof ConduitBlock block) {
            return 1024;
        }
        return 128;
    }

    @Override
    public void markRemoved() {
        if (this.world != null) {
            ConduitNetwork.of(this.world).onCableUpdate(this.pos);
        }
        super.markRemoved();
    }

    protected int getTransferLimit() {
        return this.getTransferRate();
    }

    public static void tick(World world, BlockPos pos, BlockState state, ConduitBlockEntity be) {
        if (be.current > 0) {
            be.current = 0;
            be.markDirty();
        }
    }

//    public static class EnergyInserter implements EnergyStorage {
//        protected final ConduitNetwork network;
//        protected final BlockPos pos;
//        protected final Direction side; // doesn't matter for cables currently
//
//        public EnergyInserter(ConduitNetwork network, BlockPos pos, Direction side) {
//            this.network = network;
//            this.pos = pos;
//            this.side = side;
//        }
//
//        @Override
//        public boolean supportsInsertion() {
//            return true;
//        }
//
//        @Override
//        public long insert(long maxAmount, TransactionContext transaction) {
//            return this.network.send(this.pos, maxAmount, transaction);
//        }
//
//        @Override
//        public boolean supportsExtraction() {
//            return false;
//        }
//
//        @Override
//        public long extract(long maxAmount, TransactionContext transaction) {
//            return 0;
//        }
//
//        @Override
//        public long getAmount() {
//            return 0;
//        }
//
//        @Override
//        public long getCapacity() {
//            return Long.MAX_VALUE;
//        }
//    }
}
