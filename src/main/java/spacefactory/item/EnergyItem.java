package spacefactory.item;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import spacefactory.api.EnergyTier;

// FIXME
public interface EnergyItem extends FabricItem {
	EnergyTier getEnergyTier();

	@Override
	default boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return ItemStack.areItemsEqual(oldStack, newStack);
	}

	@Override
	default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return ItemStack.areItemsEqual(oldStack, newStack);
	}
}
