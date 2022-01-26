package assortech.compatibility.roughlyenoughitems;

import assortech.recipe.ExtractorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class ExtractingDisplay extends CraftingMachineDisplay {
    public ExtractingDisplay(ExtractorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AssortechREI.EXTRACTING;
    }
}
