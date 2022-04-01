package spacefactory.features.dragon_egg_siphon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.core.block.InventoryBlock;

import java.util.List;
import java.util.Random;

public class DragonEggSiphonBlock extends InventoryBlock {
    public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.generators", SpaceFactory.config.dragonEggSiphonProduction, Items.DRAGON_EGG.getName()).formatted(Formatting.GRAY);

    public static final BooleanProperty LIT = Properties.LIT;

    public DragonEggSiphonBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DragonEggSiphonBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.DRAGON_ENERGY_ABSORBER, DragonEggSiphonBlockEntity::tick);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(TOOLTIP);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double x = pos.getX() + random.nextFloat();
            double y = pos.getY() + 1 + random.nextFloat() * 1.5;
            double z = pos.getZ() + random.nextFloat();

            world.addParticle(ParticleTypes.PORTAL, x, y, z, 0, 0, 0);
        }
    }

}
