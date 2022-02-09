package spacefactory.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode}.
 */
@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
    @Shadow
    @Final
    DefaultedList<Ingredient> input;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShapelessRecipe that = (ShapelessRecipe) o;

        return input.equals(that.getIngredients());
    }

    @Override
    public int hashCode() {
        return input.hashCode();
    }
}
