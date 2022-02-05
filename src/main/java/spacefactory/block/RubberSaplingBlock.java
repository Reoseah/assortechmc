package spacefactory.block;

import spacefactory.SpaceFactory;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

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
}
