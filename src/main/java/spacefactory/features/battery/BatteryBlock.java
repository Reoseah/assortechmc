package spacefactory.features.battery;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.api.Wrenchable;
import spacefactory.core.block.InventoryBlock;

import java.util.List;

public class BatteryBlock extends InventoryBlock implements EU.ElectricBlock, Wrenchable {
    public static final Text IO_TOOLTIP = new TranslatableText("tooltip.spacefactory.battery_rate", SpaceFactory.config.redstoneBatteryRate, SpaceFactory.config.redstoneBatteryDischarge).formatted(Formatting.GRAY);
    public static final Text CAPACITY_TOOLTIP = new TranslatableText("tooltip.spacefactory.battery_capacity", SpaceFactory.config.redstoneBatteryCapacity).formatted(Formatting.GRAY);

    public static final EnumProperty<Direction> FACING = Properties.FACING;

    public BatteryBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.DOWN));
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
        builder.add(FACING);
    }

    @Override
    public boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, Hand hand, Vec3d hitPos) {
        if (world.canPlayerModifyAt(player, pos)) {
            Direction facing = state.get(FACING);
            if (facing == side && player != null && player.isSneaking()) {
                if (world.setBlockState(pos, Blocks.AIR.getDefaultState())) {
                    player.getInventory().offerOrDrop(new ItemStack(this));
                    return true;
                }
            } else {
                world.setBlockState(pos, state.with(FACING, side));
                return true;
            }
        }
        return false;
    }
}
