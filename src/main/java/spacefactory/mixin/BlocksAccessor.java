package spacefactory.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Blocks.class)
public interface BlocksAccessor {
	@Invoker("canSpawnOnLeaves")
	static Boolean invokeCanSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Invoker("never")
	static Boolean invokeNever(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Invoker("always")
	static Boolean invokeAlways(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Invoker("never")
	static boolean invokeNever(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

	@Invoker("always")
	static boolean invokeAlways(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}

}
