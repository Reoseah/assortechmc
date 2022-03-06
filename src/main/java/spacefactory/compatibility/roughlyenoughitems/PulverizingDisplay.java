package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.recipe.PulverizerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class PulverizingDisplay extends CraftingMachineDisplay {
    public PulverizingDisplay(PulverizerRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.PULVERIZER;
    }
}
