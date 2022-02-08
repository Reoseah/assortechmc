package spacefactory.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(at = @At("HEAD"), method = "isNamespaceValid", cancellable = true)
    private static void isNamespaceValid(String namespace, CallbackInfoReturnable<Boolean> ci) {
        if ("*".equals(namespace)) {
            ci.setReturnValue(true);
        }
    }
}
