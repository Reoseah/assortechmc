package spacefactory.features.battery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.InventoryBlock;

import java.util.List;

public class BatteryBlock extends InventoryBlock implements EU.ElectricBlock {
    public static final Text IO_TOOLTIP = new TranslatableText("tooltip.spacefactory.energy_per_tick", SpaceFactory.config.redstoneBatteryRate).formatted(Formatting.GRAY);
    public static final Text CAPACITY_TOOLTIP = new TranslatableText("tooltip.spacefactory.battery_capacity", SpaceFactory.config.redstoneBatteryCapacity, SpaceFactory.config.redstoneBatteryDischarge).formatted(Formatting.GRAY);

    public static final EnumProperty<Direction> FACING = Properties.FACING;
    public static final IntProperty CHARGE = IntProperty.of("charge", 0, 3);

    public BatteryBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.DOWN).with(CHARGE, 0));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean sneaking = ctx.getPlayer() != null && ctx.getPlayer().isSneaking();
        Direction direction = ctx.getPlayerLookDirection();
        return this.getDefaultState().with(FACING, sneaking ? direction.getOpposite() : direction);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(IO_TOOLTIP);
        tooltip.add(CAPACITY_TOOLTIP);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHARGE, FACING);
    }
}
