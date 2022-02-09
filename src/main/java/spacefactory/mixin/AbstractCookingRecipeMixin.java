package spacefactory.mixin;

import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode} on all cooking recipes.
 */
@Mixin(AbstractCookingRecipe.class)
public class AbstractCookingRecipeMixin {
    @Shadow
    @Final
    protected Ingredient input;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCookingRecipe that = (AbstractCookingRecipe) o;

        boolean equals = this.input.equals(that.getIngredients().get(0));
        System.out.println((equals ? "equal" : "different") + this.input + " " + that.getIngredients().get(0));
        return equals;
    }

    @Override
    public int hashCode() {
        return this.input.hashCode();
    }
}
