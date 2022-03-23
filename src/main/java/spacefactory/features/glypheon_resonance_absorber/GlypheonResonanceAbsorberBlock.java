package spacefactory.features.glypheon_resonance_absorber;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import spacefactory.core.block.InventoryBlock;

public class GlypheonResonanceAbsorberBlock extends InventoryBlock {
    public static final BooleanProperty LIT = Properties.LIT;

    public GlypheonResonanceAbsorberBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

//    @Override
//    @Nullable
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.DRAGON_EGG_SIPHON, DragonEggSiphonBlockEntity::tick);
//    }
}
