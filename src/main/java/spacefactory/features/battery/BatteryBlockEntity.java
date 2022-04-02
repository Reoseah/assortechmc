package spacefactory.features.battery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.entity.MachineBlockEntity;

public class BatteryBlockEntity extends MachineBlockEntity implements EU.Sender {
    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.BATTERY, pos, state);
    }

    @Override
    protected int getInventorySize() {
        return 2;
    }

    @Override
    protected int getCapacity() {
        return SpaceFactory.config.redstoneBatteryCapacity;
    }

    @Override
    protected int getReceiveRate() {
        return SpaceFactory.config.redstoneBatteryRate;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BatteryBoxScreenHandler(syncId, this, player);
    }

    public void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        super.tick(world, pos, state, be);

        this.energy += EU.tryDischarge(this.getCapacity() - this.energy, this.getStack(0), stack -> this.setStack(0, stack));
        this.energy -= EU.tryCharge(this.energy, this.getStack(1), stack -> this.setStack(1, stack));

        if (world.getTime() % 20 == 0 && this.energy > 0) {
            this.energy -= SpaceFactory.config.redstoneBatteryDischarge;
        }

        Direction facing = be.getCachedState().get(BatteryBlock.FACING);
        this.energy -= EU.trySend(Math.min(this.energy, SpaceFactory.config.redstoneBatteryRate), world, pos.offset(facing), facing.getOpposite());
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        return side == this.getCachedState().get(BatteryBlock.FACING).getOpposite();
    }

    @Override
    public boolean canSendEnergy(Direction side) {
        return side == this.getCachedState().get(BatteryBlock.FACING);
    }
}
