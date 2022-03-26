package spacefactory.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spacefactory.SpaceFactory;
import spacefactory.core.item.UnicutterItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	@Final
	private Item item;

	@Inject(at = @At("RETURN"), method = "getEnchantments")
	public void getEnchantments(CallbackInfoReturnable<NbtList> ci) {
		if (this.item == SpaceFactory.Items.FLAK_VEST) {
			NbtCompound nbt = EnchantmentHelper.createNbt(Registry.ENCHANTMENT.getId(Enchantments.BLAST_PROTECTION), 3);
			ci.getReturnValue().add(nbt);
		} else if (this.item instanceof UnicutterItem) {
			NbtCompound nbt = EnchantmentHelper.createNbt(Registry.ENCHANTMENT.getId(Enchantments.SILK_TOUCH), 1);
			ci.getReturnValue().add(nbt);
		}
	}

	@Inject(at = @At("RETURN"), method = "addEnchantment", cancellable = true)
	public void addEnchantment(Enchantment enchantment, int level, CallbackInfo ci) {
		if (this.item == SpaceFactory.Items.FLAK_VEST && (enchantment instanceof ProtectionEnchantment || !enchantment.canCombine(Enchantments.BLAST_PROTECTION))) {
			ci.cancel();
		}
		if (this.item instanceof UnicutterItem && (enchantment == Enchantments.SILK_TOUCH || !enchantment.canCombine(Enchantments.SILK_TOUCH))) {
			ci.cancel();
		}
	}
}
