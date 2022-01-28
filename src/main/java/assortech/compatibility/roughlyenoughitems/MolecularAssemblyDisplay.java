package assortech.compatibility.roughlyenoughitems;

import assortech.recipe.MaceratorRecipe;
import assortech.recipe.MolecularAssemblerRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public class MolecularAssemblyDisplay extends CraftingMachineDisplay {
    public MolecularAssemblyDisplay(MolecularAssemblerRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AssortechREI.MOLECULAR_ASSEMBLY;
    }
}
