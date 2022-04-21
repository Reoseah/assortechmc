package spacefactory.compatibility.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.recipe.SmeltingRecipe;

public class ElectricSmeltingDisplay extends SimpleMachineDisplay {
    public ElectricSmeltingDisplay(SmeltingRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.ELECTRIC_SMELTING;
    }
}
