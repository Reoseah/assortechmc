package assortech.block;

import assortech.Assortech;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class RubberLogBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty HAS_RESIN = BooleanProperty.of("has_resin");

    public RubberLogBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HAS_RESIN, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(HAS_RESIN) && random.nextInt(7) == 0) {
            world.setBlockState(pos, state.with(HAS_RESIN, true));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (state.get(HAS_RESIN) && state.get(FACING) == hitResult.getSide()) {
            world.setBlockState(pos, state.with(HAS_RESIN, false));
            if (!world.isClient()) {
                ItemStack stack = new ItemStack(Assortech.AtItems.STICKY_RESIN, 1 + world.getRandom().nextInt(3));
                if (!player.getInventory().insertStack(stack)) {
                    Direction side = hitResult.getSide();
                    Random random = world.random;

                    double x = pos.getX() + 0.5 + 0.5 * side.getOffsetX();
                    double y = pos.getY() + 0.5 + 0.5 * side.getOffsetY();
                    double z = pos.getZ() + 0.5 + 0.5 * side.getOffsetZ();
                    double velocityX = 0.05 * side.getOffsetX() + random.nextFloat(0.05F) - 0.025F;
                    double velocityY = 0.05 * side.getOffsetY() + random.nextFloat(0.05F) - 0.025F;
                    double velocityZ = 0.05 * side.getOffsetZ() + random.nextFloat(0.05F) - 0.025F;

                    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
                    entity.setVelocity(velocityX, velocityY, velocityZ);
                    world.spawnEntity(entity);
                }
            }

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_RESIN);
    }
}
