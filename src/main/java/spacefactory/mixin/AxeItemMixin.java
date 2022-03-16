package spacefactory.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import spacefactory.SpaceFactory;

import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin {
    @Mutable
    @Shadow
    @Final
    protected static Map<Block, Block> STRIPPED_BLOCKS;

    static {
        ImmutableMap.Builder<Block, Block> builder = ImmutableMap.builder();
        builder.putAll(STRIPPED_BLOCKS);
        builder.put(SpaceFactory.Blocks.RUBBER_LOG, SpaceFactory.Blocks.STRIPPED_RUBBER_LOG);
        STRIPPED_BLOCKS = builder.build();
    }
}
