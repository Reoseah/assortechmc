package spacefactory.block;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.block.CableBlock;

import java.util.List;

public class HiddenCableBlock extends CableBlock {
    public HiddenCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(new TranslatableText("tooltip.spacefactory.energy_transfer", SpaceFactory.config.copperBusTransferRate).formatted(Formatting.GRAY));
    }
}
