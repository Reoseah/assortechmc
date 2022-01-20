package assortech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

@Mixin(FoliagePlacerType.class)
public interface FoliagePlacerTypeInvoker {
	@Invoker("<init>")
	static <P extends FoliagePlacer> FoliagePlacerType<P> create(Codec<P> codec) {
		return null;
	}
}
