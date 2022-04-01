package spacefactory.features;

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

public class PotatoBatteryItem extends Item implements EU.SimpleDischargeableItem {
    public static final int CAPACITY = 500, TRANSFER_RATE = 1;

    public PotatoBatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getCount() == 1) {
            tooltip.add(new TranslatableText("tooltip.spacefactory.energy_and_capacity", this.getEnergy(stack), this.getCapacity(stack)).formatted(Formatting.GRAY));
        }
    }

    @Override
    public int getEnergy(ItemStack stack) {
        return stack.hasNbt() ? stack.getNbt().getInt(ENERGY_KEY) : CAPACITY;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));
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

    @Override
    public int getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public int getTransferLimit(ItemStack stack) {
        return TRANSFER_RATE;
    }
}
