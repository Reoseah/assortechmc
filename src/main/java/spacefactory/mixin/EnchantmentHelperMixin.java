package spacefactory.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spacefactory.SpaceFactory;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@Inject(at = @At("HEAD"), method = "set")
	private static void set(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci) {
		if (stack.isOf(SpaceFactory.Items.FLAK_VEST)) {
			enchantments.entrySet().removeIf(entry -> entry.getKey() instanceof ProtectionEnchantment);
		}
	}
}
