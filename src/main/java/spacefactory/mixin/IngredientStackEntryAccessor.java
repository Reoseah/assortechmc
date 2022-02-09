package spacefactory.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.StackEntry.class)
public interface IngredientStackEntryAccessor {
    @Accessor("stack")
    ItemStack getStack();
}
