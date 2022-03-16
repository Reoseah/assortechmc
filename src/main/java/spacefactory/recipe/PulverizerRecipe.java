package spacefactory.recipe;

import spacefactory.SpaceFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class PulverizerRecipe extends SimpleMachineRecipe {
    public PulverizerRecipe(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience) {
        super(id, input, count, output, duration, experience);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.RecipeSerializers.PULVERIZING;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.RecipeTypes.PULVERIZING;
    }
}
