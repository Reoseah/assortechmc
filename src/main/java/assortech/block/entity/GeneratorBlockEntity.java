package assortech.block.entity;

import assortech.Assortech;
import assortech.api.EnergyTier;
import assortech.screen.GeneratorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class GeneratorBlockEntity extends ElectricInventoryBlockEntity {
    public static final int CAPACITY = 4000;
    public static final int FUEL_PER_TICK = 4;
    public static final int PRODUCTION = 10;

    protected int fuelLeft, fuelDuration;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.GENERATOR, pos, state);
    }

    @Override
    protected int getInventorySize() {
        return 1;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, this, player);
    }

    @Override
    protected EnergyTier getEnergyTier() {
        return EnergyTier.LV;
    }

    @Override
    protected int getEnergyCapacity() {
        return CAPACITY;
    }

    @Override
    protected boolean canInsertEnergy() {
        return false;
    }

    @Override
    protected boolean canExtractEnergy() {
        return true;
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

    public static void tick(World world, BlockPos pos, BlockState state, GeneratorBlockEntity be) {
        ElectricInventoryBlockEntity.tick(world, pos, state, be);

        boolean wasBurning = be.fuelLeft > 0;
        boolean markDirty = false;

        if (be.energy.amount > 0) {
            int max = be.getEnergyTier().transferRate;
            for (Direction side : Direction.values()) {
                max -= EnergyStorageUtil.move(be.energy, EnergyStorage.SIDED.find(world, pos.offset(side), side.getOpposite()), max, null);
                if (max == 0) {
                    break;
                }
            }
        }

        if (be.fuelLeft <= 0 && be.energy.amount < CAPACITY) {
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
            if (be.energy.amount < CAPACITY) {
                be.energy.amount = Math.min(be.energy.amount + PRODUCTION, CAPACITY);
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
