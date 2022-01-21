package assortech.recipe;

import assortech.Assortech;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class MaceratorRecipe extends CraftingMachineRecipe {
    public MaceratorRecipe(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience) {
        super(id, input, count, output, duration, experience);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Assortech.AtRecipeSerializers.MACERATING;
    }

    @Override
    public RecipeType<?> getType() {
        return Assortech.AtRecipeTypes.MACERATING;
    }
}
