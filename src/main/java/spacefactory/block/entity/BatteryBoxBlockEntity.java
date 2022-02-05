package spacefactory.block.entity;

import spacefactory.SpaceFactory;
import spacefactory.api.EnergyTier;
import spacefactory.screen.BatteryBoxScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.DelegatingEnergyStorage;

public class BatteryBoxBlockEntity extends ElectricInventoryBlockEntity {
    public static final int CAPACITY = 40000;

    public BatteryBoxBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.SFBlockEntityTypes.BATTERY_BOX, pos, state);
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

    public EnergyStorage getEnergyHandler(Direction side) {
        return getCachedState().get(Properties.FACING) == side ?
                new DelegatingEnergyStorage(this.energy, null) {
                    @Override
                    public boolean supportsInsertion() {
                        return false;
                    }
                } :
                new DelegatingEnergyStorage(this.energy, null) {
                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }
                };
    }

    public static void tick(World world, BlockPos pos, BlockState state, BatteryBoxBlockEntity be) {
        ElectricInventoryBlockEntity.tick(world, pos, state, be);
        EnergyStorageUtil.move(be.getItemApi(0, EnergyStorage.ITEM), be.energy, Integer.MAX_VALUE, null);
        EnergyStorageUtil.move(be.energy, be.getItemApi(1, EnergyStorage.ITEM), Integer.MAX_VALUE, null);
        EnergyStorageUtil.move(be.energy, EnergyStorage.SIDED.find(world, pos.offset(be.getCachedState().get(Properties.FACING)), be.getCachedState().get(Properties.FACING).getOpposite()), Integer.MAX_VALUE, null);
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
}
