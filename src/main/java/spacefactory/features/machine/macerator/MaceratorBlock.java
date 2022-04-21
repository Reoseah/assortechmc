package spacefactory.features.machine.macerator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.core.block.OrientableMachineBlock;

import java.util.List;
import java.util.Random;

public class MaceratorBlock extends OrientableMachineBlock {
    public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.energy_per_tick", SpaceFactory.config.maceratorConsumption).formatted(Formatting.GRAY);

    public MaceratorBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MaceratorBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(TOOLTIP);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;

            double dx = 0.5 * random.nextFloat() - 0.25;
            double dy = 1 + 0.1 * random.nextFloat();
            double dz = 0.5 * random.nextFloat() - 0.25;
            world.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0, 0, 0);
        }
    }
}
