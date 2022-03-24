package spacefactory.features.solar_panel;

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
import spacefactory.core.block.InventoryBlock;

import java.util.List;

public class SolarPanelBlock extends InventoryBlock {
	public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.generators", SpaceFactory.Constants.SOLAR_PANEL_OUTPUT, new TranslatableText("tooltip.spacefactory.daylight")).formatted(Formatting.GRAY);

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
