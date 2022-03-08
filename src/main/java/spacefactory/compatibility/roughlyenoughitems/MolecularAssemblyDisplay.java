package spacefactory.compatibility.roughlyenoughitems;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import spacefactory.recipe.MolecularAssemblerRecipe;
import spacefactory.recipe.SimpleMachineRecipe;

import java.util.Collections;
import java.util.Optional;

public class MolecularAssemblyDisplay extends BasicDisplay {
    private final int duration;

    public MolecularAssemblyDisplay(MolecularAssemblerRecipe recipe) {
        super(SpaceFactoryPlugin.toIngredientEntries(recipe.input1, recipe.input2),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.of(recipe.getId()));
        this.duration = recipe.getDuration();
    }

    public int getDuration() {
        return this.duration;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SpaceFactoryPlugin.MOLECULAR_ASSEMBLY;
    }
}
