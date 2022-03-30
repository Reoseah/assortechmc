package spacefactory.features.generator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spacefactory.SpaceFactory;
import spacefactory.api.EU;
import spacefactory.core.block.OrientableMachineBlock;

import java.util.List;
import java.util.Random;

public class GeneratorBlock extends OrientableMachineBlock {
	public static final Text TOOLTIP = new TranslatableText("tooltip.spacefactory.generators", SpaceFactory.config.generatorProduction, new TranslatableText("tooltip.spacefactory.conventional_fuel")).formatted(Formatting.GRAY);

	public GeneratorBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GeneratorBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.GENERATOR, GeneratorBlockEntity::tick);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
		tooltip.add(TOOLTIP);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.get(LIT)) {
			double x = pos.getX() + 0.5;
			double y = pos.getY();
			double z = pos.getZ() + 0.5;
			if (random.nextDouble() < 0.1) {
				world.playSound(x, y + 0.5, z, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1F, 1F, false);
			}

			Direction facing = state.get(FACING);

			double dx = facing.getAxis() == Direction.Axis.X ? facing.getOffsetX() * 0.52 : random.nextDouble() * 0.6 - 0.3;
			double dz = facing.getAxis() == Direction.Axis.Z ? facing.getOffsetZ() * 0.52 : random.nextDouble() * 0.6 - 0.3;
			double dy = random.nextDouble() * 9.0 / 16.0;

			world.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0, 0, 0);
			world.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0, 0, 0);
		}
	}
}
