package spacefactory.features.conduit;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;

public class ConduitBlockEntity extends BlockEntity implements EU.Receiver {
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

    @Override
    public int receiveEnergy(int energy, Direction side) {
        return ConduitNetwork.of(this.world).send(this.pos, energy);
    }
}
