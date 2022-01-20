package assortech.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import team.reborn.energy.api.base.SimpleBatteryItem;

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
    public boolean hasDurabilityBar(ItemStack stack) {
        return stack.getCount() == 1 && this.getStoredEnergy(stack) < this.getEnergyCapacity();
    }

    @Override
    public double getDurabilityBarProgress(ItemStack stack) {
        return 1 - ((float) this.getStoredEnergy(stack)) / this.getEnergyCapacity();
    }

}
