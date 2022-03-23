package spacefactory.features.rubber_tree;

import spacefactory.SpaceFactory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.Random;
import java.util.function.BiConsumer;

public class RubberFoliagePlacer extends BlobFoliagePlacer {
    public static final Codec<RubberFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> createCodec(instance).apply(instance, RubberFoliagePlacer::new));

    public RubberFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset, height);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return SpaceFactory.FoliagePlacers.RUBBER;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        for (int i = offset; i >= offset - foliageHeight; --i) {
            int j = Math.max(radius + treeNode.getFoliageRadius() - i / 2 - 1, treeNode.getFoliageRadius());
            this.generateSquare(world, replacer, random, config, treeNode.getCenter(), j, i, treeNode.isGiantTrunk());
        }
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return y == 1 && (dx != 0 || dz != 0) || radius >= 3 && (dx + dz > 1) || y != -1 && super.isInvalidForLeaves(random, dx, y, dz, radius, giantTrunk);
    }
}
