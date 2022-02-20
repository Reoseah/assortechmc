package spacefactory.block;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spacefactory.SpaceFactory;

import java.util.Locale;
import java.util.Random;

public class AliveRubberLogBlock extends Block {
    public static final EnumProperty<State> STATE = EnumProperty.of("state", State.class);

    public AliveRubberLogBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STATE, State.NATURAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(STATE) == State.TAPPED) {
            boolean hasLeaves = false;
            for (int y = 1; y < 10; y++) {
                BlockState s = world.getBlockState(pos.up(y));
                if (s.isOf(this) && s.get(STATE) != State.NATURAL) {
                    hasLeaves = false;
                    break;
                }
                if (s.isOf(SpaceFactory.SFBlocks.RUBBER_LEAVES)) {
                    hasLeaves = true;
                    break;
                } else if (!s.isOf(this)) {
                    break;
                }
            }
            if (hasLeaves && random.nextInt(4) == 0) {
                world.setBlockState(pos, state.with(STATE, State.READY));
            } else {
                world.setBlockState(pos, SpaceFactory.SFBlocks.STRIPPED_RUBBER_LOG.getDefaultState());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (hitResult.getSide().getAxis().isHorizontal() && world.canPlayerModifyAt(player, pos)) {
            if (state.get(STATE) == State.READY) {
                world.setBlockState(pos, state.with(STATE, State.TAPPED));
                if (!world.isClient()) {
                    ItemStack stack = new ItemStack(SpaceFactory.SFItems.STICKY_RESIN, 3 + world.getRandom().nextInt(3));
                    Direction side = hitResult.getSide();
                    Random random = world.random;

                    double x = pos.getX() + 0.5 + 0.5 * side.getOffsetX();
                    double y = pos.getY() + 0.5 + 0.5 * side.getOffsetY();
                    double z = pos.getZ() + 0.5 + 0.5 * side.getOffsetZ();
                    double velocityX = 0.01 * side.getOffsetX() + random.nextFloat(0.01F) - 0.005F;
                    double velocityY = 0.01 * side.getOffsetY() + random.nextFloat(0.01F) - 0.005F;
                    double velocityZ = 0.01 * side.getOffsetZ() + random.nextFloat(0.01F) - 0.005F;

                    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
                    entity.setVelocity(velocityX, velocityY, velocityZ);
                    world.spawnEntity(entity);
                }
                return ActionResult.SUCCESS;
            } else if (state.get(STATE) == State.NATURAL && FabricToolTags.AXES.contains(player.getStackInHand(hand).getItem())) {
                world.setBlockState(pos, state.with(STATE, State.TAPPED));
                player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public enum State implements StringIdentifiable {
        NATURAL,
        TAPPED,
        READY;

        @Override
        public String asString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
