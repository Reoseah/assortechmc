package spacefactory.features.battery;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.entity.MachineBlockEntity;

public class BatteryBlockEntity extends MachineBlockEntity implements EU.Sender {
	public static final int CAPACITY = 100000;

	public BatteryBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.BATTERY, pos, state);
	}

	@Override
	protected int getInventorySize() {
		return 2;
	}

	@Override
	protected int getCapacity() {
		return CAPACITY;
	}

	@Override
	protected int getReceiveRate() {
		return 20;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new BatteryBoxScreenHandler(syncId, this, player);
	}

	public void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
		super.tick(world, pos, state, be);
		this.energy -= EU.tryCharge(this.energy, this.getStack(1));

		Direction facing = be.getCachedState().get(Properties.FACING);
		this.energy -= EU.trySend(this.energy, world, pos.offset(facing), facing.getOpposite());
	}

	@Override
	public boolean canReceiveEnergy(Direction side) {
		return side != this.getCachedState().get(BatteryBlock.FACING);
	}

	@Override
	public boolean canSendEnergy(Direction side) {
		return side == this.getCachedState().get(BatteryBlock.FACING);
	}
}
