package spacefactory.features.extractor;

import spacefactory.SpaceFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import spacefactory.core.recipe.SimpleMachineRecipe;

public class ExtractorRecipe extends SimpleMachineRecipe {
    public ExtractorRecipe(Identifier id, Ingredient input, int count, ItemStack output, int duration, float experience) {
        super(id, input, count, output, duration, experience);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.RecipeSerializers.EXTRACTING;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.RecipeTypes.EXTRACTING;
    }
}