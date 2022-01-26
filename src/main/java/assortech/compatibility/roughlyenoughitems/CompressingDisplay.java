package assortech.compatibility.roughlyenoughitems;

import assortech.recipe.CompressorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class CompressingDisplay extends CraftingMachineDisplay {
    public CompressingDisplay(CompressorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AssortechREI.COMPRESSING;
    }
}
