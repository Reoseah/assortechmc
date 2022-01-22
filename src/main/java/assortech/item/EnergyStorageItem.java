package assortech.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import team.reborn.energy.api.base.SimpleBatteryItem;

import java.util.List;

public abstract class EnergyStorageItem extends Item implements SimpleBatteryItem, CustomDurabilityItem {
    public EnergyStorageItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));

            ItemStack full = new ItemStack(this);
            this.setStoredEnergy(full, this.getEnergyCapacity());
            stacks.add(full);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.getCount() == 1) {
            tooltip.add(new TranslatableText("container.assortech.energy", this.getStoredEnergy(stack), this.getEnergyCapacity()).formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }


    @Override
    public boolean hasDurabilityBar(ItemStack stack) {
        return stack.getCount() == 1 && 0 < this.getStoredEnergy(stack) && this.getStoredEnergy(stack) < this.getEnergyCapacity();
    }

    @Override
    public double getDurabilityBarProgress(ItemStack stack) {
        return 1 - ((float) this.getStoredEnergy(stack)) / this.getEnergyCapacity();
    }

}
