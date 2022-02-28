package spacefactory.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EnergyTier;
import spacefactory.block.entity.conduit.ConduitNetwork;
import spacefactory.block.entity.conduit.ConduitBlockEntity;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class ConduitBlock extends BlockWithEntity {
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;

    public final VoxelShape[] shapes;

    public static VoxelShape[] generateShapes(int radius) {
        VoxelShape[] shapes = new VoxelShape[64];
        float min = 8 - radius;
        float max = 8 + radius;
        VoxelShape center = Block.createCuboidShape(min, min, min, max, max, max);
        VoxelShape[] connections = {
                Block.createCuboidShape(min, 0, min, max, max, max),
                Block.createCuboidShape(min, min, min, max, 16, max),
                Block.createCuboidShape(min, min, 0, max, max, max),
                Block.createCuboidShape(min, min, min, max, max, 16),
                Block.createCuboidShape(0, min, min, max, max, max),
                Block.createCuboidShape(min, min, min, 16, max, max)
        };

        for (int i = 0; i < 64; i++) {
            VoxelShape shape = center;
            for (int face = 0; face < 6; face++) {
                if ((i & 1 << face) != 0) {
                    shape = VoxelShapes.union(shape, connections[face]);
                }
            }
            shapes[i] = shape;
        }
        return shapes;
    }

    public static BooleanProperty getConnectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            case DOWN -> DOWN;
            case UP -> UP;
        };
    }

    public final EnergyTier tier;

    public ConduitBlock(int radius, EnergyTier tier, Settings settings) {
        super(settings);
        this.tier = tier;
        this.setDefaultState(this.getDefaultState()
                .with(DOWN, false)
                .with(UP, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
        this.shapes = generateShapes(radius);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateForPos(ctx.getWorld(), ctx.getBlockPos());
    }

    public BlockState getStateForPos(BlockView world, BlockPos pos) {
        return this.getDefaultState()
                .with(DOWN, this.connectsTo(world, pos, Direction.DOWN))
                .with(UP, this.connectsTo(world, pos, Direction.UP))
                .with(WEST, this.connectsTo(world, pos, Direction.WEST))
                .with(EAST, this.connectsTo(world, pos, Direction.EAST))
                .with(NORTH, this.connectsTo(world, pos, Direction.NORTH))
                .with(SOUTH, this.connectsTo(world, pos, Direction.SOUTH));
    }

    protected boolean connectsTo(BlockView view, BlockPos pos, Direction side) {
        BlockState neighbor = view.getBlockState(pos.offset(side));
        Block block = neighbor.getBlock();
        if (block instanceof ConduitBlock) {
            return true;
        }
        if (view instanceof World world) {
            return EnergyStorage.SIDED.find(world, pos.offset(side), neighbor, world.getBlockEntity(pos.offset(side)), side) != null;
        }
        return false;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockState state2 = state.with(getConnectionProperty(direction), this.connectsTo(world, pos, direction));
        if (world instanceof World) {
            ConduitNetwork.of((World) world).onCableUpdate(pos);
        }
        return state2;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        ConduitNetwork.of(world).onCableUpdate(pos);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        ConduitNetwork.of(world).onCableUpdate(pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int i = (state.get(DOWN) ? 1 : 0) |
                (state.get(UP) ? 2 : 0) |
                (state.get(NORTH) ? 4 : 0) |
                (state.get(SOUTH) ? 8 : 0) |
                (state.get(WEST) ? 16 : 0) |
                (state.get(EAST) ? 32 : 0);
        return this.shapes[i];
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConduitBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SpaceFactory.SFBlockEntityTypes.CONDUIT, ConduitBlockEntity::tick);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(new TranslatableText("container.spacefactory.energy_per_tick_max", this.tier.transferRate).formatted(Formatting.GRAY));
    }

}
