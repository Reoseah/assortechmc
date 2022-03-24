package spacefactory.compatibility.roughlyenoughitems;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.recipe.SmeltingRecipe;
import spacefactory.features.extractor.ExtractorRecipe;

public class ElectricSmeltingDisplay extends SimpleMachineDisplay {
    public ElectricSmeltingDisplay(SmeltingRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.ELECTRIC_SMELTING;
    }
}
