package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.recipe.CompressorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class CompressingDisplay extends CraftingMachineDisplay {
    public CompressingDisplay(CompressorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.COMPRESSING;
    }
}
