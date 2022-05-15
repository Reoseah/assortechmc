package spacefactory.compatibility.rei;

import spacefactory.recipe.MaceratorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class MaceratingDisplay extends SimpleMachineDisplay {
    public MaceratingDisplay(MaceratorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.MACERATING;
    }
}
