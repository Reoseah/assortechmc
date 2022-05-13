package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;

public class CableBlockEntity extends BlockEntity implements EU.Receiver {
    protected int current = 0;
    public int prevCurrent = 0;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.CABLE, pos, state);
    }

    public int getTransferRate() {
        if (this.getCachedState().isOf(SpaceFactory.Blocks.COPPER_WIRE) || this.getCachedState().isOf(SpaceFactory.Blocks.COPPER_CABLE)) {
            return SpaceFactory.config.copperWireTransferRate;
        }
        return SpaceFactory.config.copperBusTransferRate;
    }

    @Override
    public void markRemoved() {
        if (this.world != null) {
            CableNetwork.of(this.world).onCableUpdate(this.pos);
        }
        super.markRemoved();
    }

    protected int getTransferLimit() {
        return this.getTransferRate();
    }

    @SuppressWarnings("unused")
    public static void tick(World world, BlockPos pos, BlockState state, CableBlockEntity be) {
        be.prevCurrent = be.current;
        if (be.current > 0) {
            be.current = 0;
        }
    }

    @Override
    public int receiveEnergy(int energy, Direction side) {
        return CableNetwork.of(this.world).send(this.pos, energy);
    }
}
