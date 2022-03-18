package spacefactory.block.entity.conduit;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.api.EnergyTier;
import spacefactory.block.ConduitBlock;

public class ConduitBlockEntity extends BlockEntity {
    protected int current = 0;

    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.CONDUIT, pos, state);
    }

    public EnergyTier getTier() {
        if (this.getCachedState().getBlock() instanceof ConduitBlock block) {
            return block.tier;
        }
        return EnergyTier.MEDIUM;
    }

    @Override
    public void markRemoved() {
        if (this.world != null) {
            ConduitNetwork.of(this.world).onCableUpdate(this.pos);
        }
        super.markRemoved();
    }

    protected int getTransferLimit() {
        return this.getTier().transferRate;
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
