package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.recipe.CraftingMachineRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;
import java.util.Optional;

public abstract class CraftingMachineDisplay extends BasicDisplay {
    private final int duration;

    public CraftingMachineDisplay(CraftingMachineRecipe recipe) {
        super(SpaceFactoryPlugin.toIngredientEntries(recipe.getIngredient(), recipe.getIngredientCount()),
                Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
                Optional.of(recipe.getId()));
        this.duration = recipe.getDuration();
    }

    public int getDuration() {
        return this.duration;
    }
}
