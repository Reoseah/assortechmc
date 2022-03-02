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
import spacefactory.SpaceFactory;

import java.util.Map;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> {
    @Shadow
    @Final
    private BiMap<Identifier, T> idToEntry;

    @Shadow
    public abstract boolean containsId(Identifier id);

    @Shadow
    public abstract @Nullable T get(@Nullable Identifier id);

    @Inject(at = @At("HEAD"), method = "get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;", cancellable = true)
    public void get(@Nullable Identifier id, CallbackInfoReturnable<T> ci) {
        if (id != null && id.getNamespace().equals("*")) {
            for (String namespace : SpaceFactory.config.preferredNamespaces) {
                Identifier resolvedId = new Identifier(namespace, id.getPath());
                if (this.containsId(resolvedId)) {
                    ci.setReturnValue(this.get(resolvedId));
                    return;
                }
            }
            for (Map.Entry<Identifier, T> entry : this.idToEntry.entrySet()) {
                if (entry.getKey().getPath().equals(id.getPath())) {
                    ci.setReturnValue(entry.getValue());
                }
            }
        }
    }
}
