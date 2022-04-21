package spacefactory.compatibility.rei;

import spacefactory.features.machine.extractor.ExtractorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class ExtractingDisplay extends SimpleMachineDisplay {
    public ExtractingDisplay(ExtractorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.EXTRACTING;
    }
}
