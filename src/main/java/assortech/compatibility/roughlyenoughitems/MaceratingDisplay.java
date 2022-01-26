package assortech.compatibility.roughlyenoughitems;

import assortech.recipe.MaceratorRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class MaceratingDisplay extends CraftingMachineDisplay {
    public MaceratingDisplay(MaceratorRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AssortechREI.MACERATING;
    }
}
