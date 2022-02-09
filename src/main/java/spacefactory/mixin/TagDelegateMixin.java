package spacefactory.mixin;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode} on normal tags.
 */
@Mixin(TagDelegate.class)
public class TagDelegateMixin {
    @Shadow
    @Final
    private Identifier id;

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagDelegate that = (TagDelegate) o;

        return id.equals(that.getId());
    }
}
