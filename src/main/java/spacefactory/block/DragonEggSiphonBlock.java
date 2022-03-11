package spacefactory.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
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
import spacefactory.block.entity.DragonEggSiphonBlockEntity;
import spacefactory.block.entity.GeneratorBlockEntity;

import java.util.List;
import java.util.Random;

public class DragonEggSiphonBlock extends InventoryBlock {
    public static final BooleanProperty LIT = Properties.LIT;

    public DragonEggSiphonBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DragonEggSiphonBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SpaceFactory.SFBlockEntityTypes.DRAGON_EGG_SIPHON, DragonEggSiphonBlockEntity::tick);
    }
}
