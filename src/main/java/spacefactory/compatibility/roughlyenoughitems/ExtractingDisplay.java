package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.recipe.ExtractorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class ExtractingDisplay extends CraftingMachineDisplay {
    public ExtractingDisplay(ExtractorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryREI.EXTRACTING;
    }
}
