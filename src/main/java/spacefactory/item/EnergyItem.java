package spacefactory.item;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import spacefactory.api.EnergyTier;
import team.reborn.energy.api.base.SimpleBatteryItem;

public interface EnergyItem extends SimpleBatteryItem, FabricItem {
    EnergyTier getEnergyTier();

    @Override
    default long getEnergyMaxInput() {
        return this.getEnergyTier().transferRate;
    }

    @Override
    default long getEnergyMaxOutput() {
        return this.getEnergyTier().transferRate;
    }

    @Override
    default boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return ItemStack.areItemsEqual(oldStack, newStack);
    }

    @Override
    default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return ItemStack.areItemsEqual(oldStack, newStack);
    }
}
