package spacefactory.features.battery;

import net.minecraft.block.BlockState;
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
import spacefactory.api.LimitedEUReceiver;
import spacefactory.core.block.entity.ElectricInventoryBlockEntity;

public class BatteryBlockEntity extends ElectricInventoryBlockEntity implements EU.Receiver, EU.Sender {
	public static final int CAPACITY = 100000;

	protected final LimitedEUReceiver receiver;

	public BatteryBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.BATTERY_BOX, pos, state);
		this.receiver = new LimitedEUReceiver(this, 32);
	}

	public EU.Receiver getEUReceiver() {
		return this.receiver;
	}

	@Override
	protected int getInventorySize() {
		return 2;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new BatteryBoxScreenHandler(syncId, this, player);
	}

	public static void tick(World world, BlockPos pos, BlockState state, BatteryBlockEntity be) {
		be.receiver.resetLimit();
//        EnergyStorageUtil.move(be.getItemApi(0, EnergyStorage.ITEM), be.energy, Integer.MAX_VALUE, null);
		be.energy -= EU.tryCharge(be.energy, be.getStack(1));

		Direction facing = be.getCachedState().get(Properties.FACING);
		be.energy -= EU.trySend(be.energy, world, pos.offset(facing), facing.getOpposite());
	}

	@Override
	public boolean canReceiveEnergy(Direction side) {
		return side != this.getCachedState().get(BatteryBlock.FACING);
	}

	@Override
	public int receiveEnergy(int energy, Direction side) {
		if (!this.canReceiveEnergy(side)) {
			return 0;
		}
		int change = Math.min(CAPACITY - this.energy, energy);
		this.energy += change;
		this.markDirty();
		return change;
	}

	@Override
	public boolean canSendEnergy(Direction side) {
		return side == this.getCachedState().get(BatteryBlock.FACING);
	}
}
