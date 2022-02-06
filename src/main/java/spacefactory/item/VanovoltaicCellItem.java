package spacefactory.item;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class VanovoltaicCellItem extends Item implements EnergyStorage {
    public static final long EU_PER_TICK = 10;

    public VanovoltaicCellItem(Settings settings) {
        super(settings);
        EnergyStorage.ITEM.registerSelf(this);
    }
    // TODO charging items in inventory at 10 EU/tick
    // TODO instability if more than one in inventory, like Extra Utilities' Unstable Ingots?

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("container.spacefactory.energy_per_tick", EU_PER_TICK).formatted(Formatting.GRAY));
    }

    // implements EnergyStorage
    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return Math.min(maxAmount, EU_PER_TICK);
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }
}
