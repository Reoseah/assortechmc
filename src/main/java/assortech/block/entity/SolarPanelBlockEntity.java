package assortech.block.entity;

import assortech.Assortech;
import assortech.screen.SolarPanelScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class SolarPanelBlockEntity extends InventoryBlockEntity implements SidedInventory {
    public static final int PRODUCTION = 1;
    // for cables to connect to us... we don't store any energy in solar panels
    public static EnergyStorage ENERGY = new EnergyStorage() {
        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long getAmount() {
            return 0;
        }

        @Override
        public long getCapacity() {
            return 0;
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }
    };

    public boolean generating = false;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.SOLAR_PANEL, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createInventory() {
        return DefaultedList.ofSize(1, ItemStack.EMPTY);
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
        return EnergyStorageUtil.isEnergyStorage(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public boolean isGenerating() {
        return this.generating;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
        if (world.isSkyVisible(pos.up()) && world.isDay() && world.getAmbientDarkness() == 0) {
            if (!be.generating) {
                be.markDirty();
            }
            be.generating = true;
            SimpleEnergyStorage producedEnergy = new SimpleEnergyStorage(PRODUCTION, PRODUCTION, PRODUCTION);
            producedEnergy.amount = PRODUCTION;

            EnergyStorage itemEnergy = be.getItemApi(0, EnergyStorage.ITEM);
            if (itemEnergy != null) {
                EnergyStorageUtil.move(producedEnergy, itemEnergy, PRODUCTION, null);
            }
            for (Direction side : Direction.values()) {
                if (producedEnergy.amount == 0) {
                    break;
                }
                EnergyStorageUtil.move(producedEnergy, EnergyStorage.SIDED.find(world, pos.offset(side), side.getOpposite()), PRODUCTION, null);
            }
        } else {
            if (be.generating) {
                be.markDirty();
            }
            be.generating = false;
        }
    }

}
