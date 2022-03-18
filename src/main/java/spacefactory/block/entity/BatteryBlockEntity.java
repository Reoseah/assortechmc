package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.api.EnergyTier;
import spacefactory.block.BatteryBlock;
import spacefactory.screen.BatteryBoxScreenHandler;

public class BatteryBlockEntity extends ElectricInventoryBlockEntity implements EU.Receiver {
	public static final int CAPACITY = 100000;

	public BatteryBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.BATTERY_BOX, pos, state);
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
		ElectricInventoryBlockEntity.tick(world, pos, state, be);
//        EnergyStorageUtil.move(be.getItemApi(0, EnergyStorage.ITEM), be.energy, Integer.MAX_VALUE, null);
//        EnergyStorageUtil.move(be.energy, be.getItemApi(1, EnergyStorage.ITEM), Integer.MAX_VALUE, null);
//        EnergyStorageUtil.move(be.energy, EnergyStorage.SIDED.find(world, pos.offset(be.getCachedState().get(Properties.FACING)), be.getCachedState().get(Properties.FACING).getOpposite()), Integer.MAX_VALUE, null);
	}

	@Override
	protected EnergyTier getEnergyTier() {
		return EnergyTier.LOW;
	}

	@Override
	protected int getEnergyCapacity() {
		return CAPACITY;
	}

	@Override
	protected boolean canInsertEnergy() {
		return true;
	}

	@Override
	protected boolean canExtractEnergy() {
		return true;
	}

	@Override
	public boolean canReceive(Direction side) {
		return side != this.getCachedState().get(BatteryBlock.FACING);
	}

	@Override
	public int receive(int energy, Direction side) {
		if (!this.canReceive(side)) {
			return 0;
		}
		int change = Math.min(CAPACITY - this.energy, energy);
		this.energy += change;
		this.markDirty();
		return change;
	}
}
