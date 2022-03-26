package spacefactory.features.nano_steel;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.core.item.MacheteItem;

import java.util.List;

public class NanoSteelMacheteItem extends MacheteItem implements FabricItem {
	public static final Text SELF_REGENERATION_TEXT = new TranslatableText("tooltip.spacefactory.self_regeneration").formatted(Formatting.GRAY);

	public NanoSteelMacheteItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
		super(toolMaterial, attackDamage, attackSpeed, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(SELF_REGENERATION_TEXT);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (stack.getDamage() > 0 && world.getTime() % 20 == 0) {
			stack.setDamage(stack.getDamage() - 1);
		}
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return newStack.getDamage() < oldStack.getDamage();
	}

	@Override
	public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return true;
	}
}
