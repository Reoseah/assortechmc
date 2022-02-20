package spacefactory.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;

import java.util.Random;

public class RubberSaplingBlock extends SaplingBlock {
    public RubberSaplingBlock(AbstractBlock.Settings settings) {
        super(new RubberSaplingGenerator(), settings);
    }

    public static class RubberSaplingGenerator extends SaplingGenerator {
        @Nullable
        @Override
        protected ConfiguredFeature<?, ?> getTreeFeature(Random random, boolean bees) {
            return SpaceFactory.SFFeatures.RUBBER_TREE;
        }
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos);
    }
}
