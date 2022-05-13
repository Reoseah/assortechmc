package spacefactory.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.BlockView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import spacefactory.SpaceFactory;

import java.util.Random;

public class RubberSaplingBlock extends SaplingBlock {
    public RubberSaplingBlock(AbstractBlock.Settings settings) {
        super(new RubberSaplingGenerator(), settings);
    }

    public static class RubberSaplingGenerator extends SaplingGenerator {
        @Override
        protected RegistryEntry<? extends ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
            return BuiltinRegistries.CONFIGURED_FEATURE.getEntry(BuiltinRegistries.CONFIGURED_FEATURE.getKey(SpaceFactory.ConfiguredFeatures.RUBBER_TREE).orElseThrow()).orElseThrow();
        }
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        // changes visibility to public
        return super.canPlantOnTop(floor, world, pos);
    }
}
