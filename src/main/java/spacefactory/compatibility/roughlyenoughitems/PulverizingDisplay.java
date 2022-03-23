package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.features.pulverizer.PulverizerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class PulverizingDisplay extends SimpleMachineDisplay {
    public PulverizingDisplay(PulverizerRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.PULVERIZER;
    }
}
