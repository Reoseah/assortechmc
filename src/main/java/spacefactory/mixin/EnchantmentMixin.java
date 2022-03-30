package spacefactory.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spacefactory.SpaceFactory;
import spacefactory.core.item.UnicutterItem;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(at = @At("HEAD"), method = "isAcceptableItem", cancellable = true)
	public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
		if ((Object) this instanceof ProtectionEnchantment && stack.isOf(SpaceFactory.Items.FLAK_VEST)) {
			ci.setReturnValue(false);
		}
	}

}
