package spacefactory.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implements {@link Object#equals} and {@link Object#hashCode} on item stack entries in {@link net.minecraft.recipe.Ingredient}.
 */
@Mixin(Ingredient.StackEntry.class)
public class IngredientStackEntryMixin {
    @Shadow
    @Final
    private ItemStack stack;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngredientStackEntryAccessor that = (IngredientStackEntryAccessor) o;

        return ItemStack.areItemsEqual(this.stack, that.getStack());
    }

    @Override
    public int hashCode() {
        return this.stack.hashCode();
    }
}
