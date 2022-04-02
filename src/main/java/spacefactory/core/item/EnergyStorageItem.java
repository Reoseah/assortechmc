package spacefactory.core.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import spacefactory.api.EU;

import java.util.List;

public abstract class EnergyStorageItem extends Item implements EU.SimpleRechargeableItem {
	public EnergyStorageItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (this.isIn(group)) {
			stacks.add(new ItemStack(this));

			ItemStack full = new ItemStack(this);
			this.withEnergy(full, this.getCapacity(full));
			stacks.add(full);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		if (stack.getCount() == 1) {
			tooltip.add(new TranslatableText("tooltip.spacefactory.energy_and_capacity", this.getEnergy(stack), this.getCapacity(stack)).formatted(Formatting.GRAY));
		}
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.getCount() == 1 && this.getEnergy(stack) < this.getCapacity(stack);
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13 * ((float) this.getEnergy(stack)) / this.getCapacity(stack));
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return MathHelper.hsvToRgb(Math.max(0.0F, (float) getItemBarStep(stack) / 13F) / 3.0F, 1.0F, 1.0F);
	}
}
