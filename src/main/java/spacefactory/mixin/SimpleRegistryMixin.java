package spacefactory.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * Resolves identifiers that with "*" in namespace, like "*:silver_ingot", to whatever entry occurs first.
 * <p>
 * (Should only happen when loading recipes, otherwise it's not very fast)
 */
@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> {
    @Shadow
    @Final
    private BiMap<Identifier, T> idToEntry;

    @Inject(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", cancellable = true)
    public void get(@Nullable Identifier id, CallbackInfoReturnable<T> ci) {
        if (id != null && id.getNamespace().equals("*")) {
            for (Map.Entry<Identifier, T> entry : this.idToEntry.entrySet()) {
                if (entry.getKey().getPath().equals(id.getPath())) {
                    ci.setReturnValue(entry.getValue());
                }
            }
        }
    }
}
