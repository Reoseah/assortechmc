package assortech.block.entity;

import assortech.Assortech;
import assortech.screen.BatteryBoxScreenHandler;
import net.minecraft.block.BlockState;
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
import team.reborn.energy.api.base.DelegatingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class BatteryBoxBlockEntity extends InventoryBlockEntity {
    public static final int CAPACITY = 40000;
    protected SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(CAPACITY, 32, 32) {
        @Override
        protected void onFinalCommit() {
            BatteryBoxBlockEntity.this.markDirty();
        }
    };

    public BatteryBoxBlockEntity(BlockPos pos, BlockState state) {
        super(Assortech.AtBlockEntityTypes.BATTERY_BOX, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createInventory() {
        return DefaultedList.ofSize(2, ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BatteryBoxScreenHandler(syncId, this, player);
    }

    public EnergyStorage getEnergyHandler(Direction side) {
        return getCachedState().get(Properties.FACING) == side ?
                new DelegatingEnergyStorage(this.energyStorage, null) {
                    @Override
                    public boolean supportsInsertion() {
                        return false;
                    }
                } :
                new DelegatingEnergyStorage(this.energyStorage, null) {
                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }
                };
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.energyStorage.amount = nbt.getLong("Energy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("Energy", this.energyStorage.amount);
    }

    public int getEnergy() {
        return (int) this.energyStorage.amount;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BatteryBoxBlockEntity be) {
        EnergyStorageUtil.move(be.energyStorage, EnergyStorage.SIDED.find(world, pos.offset(be.getCachedState().get(Properties.FACING)), be.getCachedState().get(Properties.FACING).getOpposite()), Integer.MAX_VALUE, null);
    }
}
