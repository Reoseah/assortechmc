package assortech.block.entity;

import assortech.Assortech;
import assortech.screen.GeneratorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class GeneratorBlockEntity extends InventoryBlockEntity {
    public static final int CAPACITY = 4000;
    public static final int TRANSFER_LIMIT = 32;
    public static final int FUEL_PER_TICK = 4;
    public static final int PRODUCTION = 10;

    protected int fuelLeft, fuelDuration;
    protected SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(CAPACITY, 0, TRANSFER_LIMIT) {
        @Override
        protected void onFinalCommit() {
            GeneratorBlockEntity.this.markDirty();
        }
    };

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.GENERATOR, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createInventory() {
        return DefaultedList.ofSize(1, ItemStack.EMPTY);
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
        this.energyStorage.amount = nbt.getLong("Energy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("FuelLeft", (short) this.fuelLeft);
        nbt.putShort("FuelDuration", (short) this.fuelDuration);
        nbt.putLong("Energy", this.energyStorage.amount);
    }

    public int getFuelLeft() {
        return this.fuelLeft;
    }

    public int getFuelDuration() {
        return this.fuelDuration;
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public int getEnergy() {
        return (int) this.energyStorage.amount;
    }

    public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
        boolean wasBurning = be.fuelLeft > 0;
        boolean markDirty = false;

        if (be.energyStorage.amount > 0) {
            int max = TRANSFER_LIMIT;
            for (Direction side : Direction.values()) {
                max -= EnergyStorageUtil.move(be.energyStorage, EnergyStorage.SIDED.find(world, pos.offset(side), side.getOpposite()), max, null);
                if (max == 0) {
                    break;
                }
            }
        }

        if (be.fuelLeft <= 0 && be.energyStorage.amount < CAPACITY) {
            ItemStack fuel = be.inventory.get(0);
            int value = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuel.getItem(), 0) / FUEL_PER_TICK;
            if (value > 0) {
                fuel.decrement(1);
                be.fuelDuration = be.fuelLeft = value;
                markDirty = true;
            } else {
                be.fuelDuration = 0;
                markDirty = true;
            }
        }
        if (be.fuelLeft > 0) {
            be.fuelLeft--;
            if (be.energyStorage.amount < CAPACITY) {
                be.energyStorage.amount = Math.min(be.energyStorage.amount + PRODUCTION, CAPACITY);
            }
            markDirty = true;
        }
        boolean isBurning = be.fuelLeft > 0;

        if (isBurning != wasBurning) {
            be.world.setBlockState(be.pos, be.getCachedState().with(Properties.LIT, isBurning));
            markDirty = true;
        }
        if (markDirty) {
            be.markDirty();
        }
    }

}
