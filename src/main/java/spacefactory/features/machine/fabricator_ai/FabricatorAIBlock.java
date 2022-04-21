package spacefactory.features.machine.fabricator_ai;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.core.block.OrientableMachineBlock;

import java.util.List;

public class FabricatorAIBlock extends OrientableMachineBlock {
    public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.energy_per_tick", SpaceFactory.config.fabricatorAIConsumption).formatted(Formatting.GRAY);

    public FabricatorAIBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FabricatorAIBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(TOOLTIP);
    }
}
