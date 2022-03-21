package spacefactory.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.screen.SolarPanelScreenHandler;

public class SolarPanelBlockEntity extends InventoryBlockEntity implements SidedInventory, EU.Sender {
	public static final int PRODUCTION = 1;

	public boolean generating = false, skyView = false;

	public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
		super(SpaceFactory.BlockEntityTypes.SOLAR_PANEL, pos, state);
	}

	@Override
	protected int getInventorySize() {
		return 1;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new SolarPanelScreenHandler(syncId, this, player);
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[]{0};
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return EU.isElectricItem(stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	public boolean isGenerating() {
		return this.generating;
	}

	public boolean hasSkyView() {
		return this.skyView;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return EU.isElectricItem(stack);
	}

	public static void tick(World world, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
		boolean skyView = world.isSkyVisible(pos.up());
		if (skyView) {
			if (world.isDay() && world.getAmbientDarkness() == 0) {
				if (!be.generating) {
					be.markDirty();
				}
				be.generating = true;

				int energy = PRODUCTION;
				// TODO
//                EnergyStorage itemEnergy = be.getItemApi(0, EnergyStorage.ITEM);
//                if (itemEnergy != null) {
//                    EnergyStorageUtil.move(producedEnergy, itemEnergy, PRODUCTION, null);
//                }
				for (Direction side : Direction.values()) {
					if (!be.canSend(side)) {
						continue;
					}
					energy -= EU.send(energy, world, pos.offset(side), side.getOpposite());
					if (energy == 0) {
						break;
					}
				}
				return;
			}
		}
		if (be.generating || skyView != be.skyView) {
			be.markDirty();
		}
		be.generating = false;
		be.skyView = skyView;
	}

	@Override
	public boolean canSend(Direction side) {
		return side != Direction.UP;
	}
}
