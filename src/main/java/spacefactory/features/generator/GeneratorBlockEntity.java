package spacefactory.features.generator;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.entity.InventoryBlockEntity;

import java.util.Locale;

public class GeneratorBlockEntity extends InventoryBlockEntity implements SidedInventory, EU.Sender {
	protected int fuelLeft, fuelDuration;

	public GeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.GENERATOR, pos, state);
	}

	@Override
	protected int getInventorySize() {
		return 2;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new GeneratorScreenHandler(syncId, this, player);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.fuelLeft = nbt.getShort("FuelLeft");
		this.fuelDuration = nbt.getShort("FuelDuration");
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putShort("FuelLeft", (short) this.fuelLeft);
		nbt.putShort("FuelDuration", (short) this.fuelDuration);
	}

	public int getFuelLeft() {
		return this.fuelLeft;
	}

	public int getFuelDuration() {
		return this.fuelDuration;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[]{0};
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return slot != 1 || EU.isElectricItem(stack);
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot != 0 || !AbstractFurnaceBlockEntity.canUseAsFuel(stack);
	}

	public boolean canConsumeFuel() {
		return 0 < AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(this.getStack(0).getItem(), 0) / SpaceFactory.config.generatorBurnRate;
	}

	public void consumeFuel() {
		int fuelValue = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(this.getStack(0).getItem(), 0);
		if (fuelValue > 0) {
			this.getStack(0).decrement(1);
			this.fuelDuration = this.fuelLeft = fuelValue / SpaceFactory.config.generatorBurnRate;
			this.markDirty();
		} else {
			throw new IllegalStateException(String.format(Locale.ROOT, "%s is not a valid fuel", this.getStack(0)));
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
		boolean wasBurning = be.fuelLeft > 0;

		if (wasBurning || be.canConsumeFuel()) {
			boolean sentAnyEnergy = false;
			int energy = SpaceFactory.config.generatorProduction;
			for (Direction side : Direction.values()) {
				int sent = EU.trySend(energy, world, pos.offset(side), side.getOpposite());
				energy -= sent;
				if (sent > 0) {
					sentAnyEnergy = true;
				}
				if (energy == 0) {
					break;
				}
			}
			if (!wasBurning && sentAnyEnergy) {
				be.consumeFuel();
			}
			be.fuelLeft--;
			be.markDirty();
		}

		boolean isBurning = be.fuelLeft > 0;

		if (isBurning != wasBurning) {
			world.setBlockState(pos, be.getCachedState().with(Properties.LIT, isBurning));
		}
	}
}
