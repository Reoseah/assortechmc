package spacefactory.mixin;


import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode} on item tag entries in {@link net.minecraft.recipe.Ingredient}.
 */
@Mixin(Ingredient.TagEntry.class)
public class IngredientTagEntryMixin {
    @Shadow
    @Final
    private Tag<Item> tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngredientTagEntryAccessor that = (IngredientTagEntryAccessor) o;

        return tag.equals(that.getTag());
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }
}
