package assortech.recipe;

import assortech.Assortech;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class MolecularAssemblerRecipe extends CraftingMachineRecipe {
    /**
     * @param duration actually it is energy cost here, not ticks
     */
    public MolecularAssemblerRecipe(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience) {
        super(id, input, count, output, duration, experience);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Assortech.AtRecipeSerializers.MOLECULAR_ASSEMBLY;
    }

    @Override
    public RecipeType<?> getType() {
        return Assortech.AtRecipeTypes.MOLECULAR_ASSEMBLY;
    }
}
