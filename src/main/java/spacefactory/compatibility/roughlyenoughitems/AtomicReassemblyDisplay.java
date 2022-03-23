package spacefactory.compatibility.roughlyenoughitems;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import spacefactory.features.atomic_reassembler.AtomicReassemblerRecipe;

import java.util.Collections;
import java.util.Optional;

public class AtomicReassemblyDisplay extends BasicDisplay {
    private final int duration;

    public AtomicReassemblyDisplay(AtomicReassemblerRecipe recipe) {
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
        return SpaceFactoryPlugin.ATOMIC_REASSEMBLY;
    }
}
