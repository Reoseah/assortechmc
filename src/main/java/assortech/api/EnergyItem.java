package assortech.api;

import assortech.item.CustomDurabilityItem;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import team.reborn.energy.api.base.SimpleBatteryItem;

public interface EnergyItem extends SimpleBatteryItem, FabricItem, CustomDurabilityItem {
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

    @Override
    default boolean hasDurabilityBar(ItemStack stack) {
        return stack.getCount() == 1 && this.getStoredEnergy(stack) < this.getEnergyCapacity();
    }

    @Override
    default double getDurabilityBarProgress(ItemStack stack) {
        return 1 - ((float) this.getStoredEnergy(stack)) / this.getEnergyCapacity();
    }
}
