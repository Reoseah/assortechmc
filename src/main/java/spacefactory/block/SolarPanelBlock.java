package spacefactory.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.core.block.ContainerBlock;
import spacefactory.block.entity.SolarPanelBlockEntity;

import java.util.List;

public class SolarPanelBlock extends ContainerBlock {
	public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.generators", SpaceFactory.config.solarPanelProduction, new TranslatableText("tooltip.spacefactory.sunlight")).formatted(Formatting.GRAY);

	public SolarPanelBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SolarPanelBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.SOLAR_PANEL, SolarPanelBlockEntity::tick);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		tooltip.add(TOOLTIP);
	}
}
