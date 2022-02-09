package spacefactory.mixin;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode}.
 */
@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Shadow
    @Final
    int width;
    @Shadow
    @Final
    int height;
    @Shadow
    @Final
    DefaultedList<Ingredient> input;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShapedRecipe that = (ShapedRecipe) o;

        if (width != that.getWidth()) return false;
        if (height != that.getHeight()) return false;
        return input.equals(that.getIngredients());
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + input.hashCode();
        return result;
    }
}
