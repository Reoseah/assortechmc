package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.features.compressor.CompressorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class CompressingDisplay extends SimpleMachineDisplay {
    public CompressingDisplay(CompressorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.COMPRESSING;
    }
}
