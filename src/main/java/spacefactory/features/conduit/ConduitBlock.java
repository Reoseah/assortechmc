package spacefactory.features.conduit;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;

import java.util.List;

public class ConduitBlock extends ConductiveBlock {
    public final VoxelShape[] shapes;

    public static VoxelShape[] generateShapes(int radius) {
        VoxelShape[] shapes = new VoxelShape[64];
        float min = 8 - radius;
        float max = 8 + radius;
        VoxelShape center = Block.createCuboidShape(min, min, min, max, max, max);
        VoxelShape[] connections = {Block.createCuboidShape(min, 0, min, max, max, max), Block.createCuboidShape(min, min, min, max, 16, max), Block.createCuboidShape(min, min, 0, max, max, max), Block.createCuboidShape(min, min, min, max, max, 16), Block.createCuboidShape(0, min, min, max, max, max), Block.createCuboidShape(min, min, min, 16, max, max)};

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

    protected final boolean insulated;

    public ConduitBlock(int radius, boolean insulated, Settings settings) {
        super(settings);

        this.shapes = generateShapes(radius);
        this.insulated = insulated;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int i = (state.get(DOWN) ? 1 : 0) | (state.get(UP) ? 2 : 0) | (state.get(NORTH) ? 4 : 0) | (state.get(SOUTH) ? 8 : 0) | (state.get(WEST) ? 16 : 0) | (state.get(EAST) ? 32 : 0);
        return this.shapes[i];
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        if (this == SpaceFactory.Blocks.COPPER_WIRE || this == SpaceFactory.Blocks.COPPER_CABLE) {
            tooltip.add(new TranslatableText("tooltip.spacefactory.energy_per_tick", SpaceFactory.config.copperWireTransferRate).formatted(Formatting.GRAY));
        } else {
            tooltip.add(new TranslatableText("tooltip.spacefactory.energy_per_tick", SpaceFactory.config.copperBusTransferRate).formatted(Formatting.GRAY));
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient || this.insulated) {
            return;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            if (conduit.prevCurrent >= SpaceFactory.config.minShockCurrent && entity instanceof LivingEntity victim) {
                int damage = 1 + (conduit.prevCurrent - SpaceFactory.config.minShockCurrent) / SpaceFactory.config.shockCurrentToDamageCoefficient;
                victim.damage(SpaceFactory.DamageSources.UNINSULATED_WIRE_SHOCK, damage);
            }
        }
    }
}
