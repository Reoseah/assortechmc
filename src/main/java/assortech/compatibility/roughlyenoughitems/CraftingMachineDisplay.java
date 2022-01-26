package assortech.compatibility.roughlyenoughitems;

import assortech.recipe.CraftingMachineRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.Optional;

public abstract class CraftingMachineDisplay extends BasicDisplay {
    private final CraftingMachineRecipe recipe;

    public CraftingMachineDisplay(CraftingMachineRecipe recipe) {
        super(AssortechREI.toIngredientEntries(recipe.getIngredient(), recipe.getIngredientCount()),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.of(recipe.getId()));
        this.recipe = recipe;
    }

    public int getDuration() {
        return this.recipe.getDuration();
    }
}
