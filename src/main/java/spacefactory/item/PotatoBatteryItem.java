package spacefactory.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.api.EnergyTier;

import java.util.List;

public class PotatoBatteryItem extends EnergyStorageItem {
    public static final int CAPACITY = 500;

    public PotatoBatteryItem(Settings settings) {
        super(settings);
    }

//    @Override
//    public EnergyTier getEnergyTier() {
//        return EnergyTier.LOW;
//    }
//
//    @Override
//    public long getEnergyCapacity() {
//        return CAPACITY;
//    }
//
//    @Override
//    public long getEnergyMaxInput() {
//        return 0;
//    }
//
//    @Override
//    public long getEnergyMaxOutput() {
//        return 1;
//    }


    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getCount() == 1) {
//            tooltip.add(new TranslatableText("container.spacefactory.energy", this.getStoredEnergy(stack), this.getEnergyCapacity()).formatted(Formatting.GRAY));
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            stacks.add(new ItemStack(this));
        }
    }
}
