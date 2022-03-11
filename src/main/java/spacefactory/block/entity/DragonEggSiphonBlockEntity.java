package spacefactory.block.entity;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class DragonEggSiphonBlockEntity extends InventoryBlockEntity {
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

    public boolean generating = false, dragonEgg = false;

    public DragonEggSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.SFBlockEntityTypes.DRAGON_EGG_SIPHON, pos, state);
    }

    @Override
    protected int getInventorySize() {
        return 0;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    public static void tick(World world, BlockPos pos, BlockState state, DragonEggSiphonBlockEntity be) {
        boolean dragonEgg = world.getBlockState(pos.up()).isOf(Blocks.DRAGON_EGG);
        if (dragonEgg) {
            if (!be.generating) {
                be.markDirty();
            }
            be.generating = true;
            SimpleEnergyStorage producedEnergy = new SimpleEnergyStorage(SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT, SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT, SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT);
            producedEnergy.amount = SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT;

            for (Direction side : Direction.values()) {
                if (producedEnergy.amount == 0) {
                    break;
                }
                EnergyStorageUtil.move(producedEnergy, EnergyStorage.SIDED.find(world, pos.offset(side), side.getOpposite()), SpaceFactory.Constants.DRAGON_EGG_SYPHON_OUTPUT, null);
            }
            world.setBlockState(pos, state.with(Properties.LIT, true));
            return;
        }
        if (be.generating || dragonEgg != be.dragonEgg) {
            be.markDirty();
        }
        be.generating = false;
        be.dragonEgg = dragonEgg;
        world.setBlockState(pos, state.with(Properties.LIT, false));
    }

}
