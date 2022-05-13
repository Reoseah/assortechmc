package spacefactory.compatibility.rei;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.SmeltingRecipe;
import spacefactory.recipe.MachineRecipe;

import java.util.Collections;
import java.util.Optional;

public abstract class SimpleMachineDisplay extends BasicDisplay {
	private final int duration;

	public SimpleMachineDisplay(MachineRecipe recipe) {
		super(SpaceFactoryPlugin.toIngredientEntries(recipe.getIngredient(), recipe.getIngredientCount()),
				Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
				Optional.of(recipe.getId()));
		this.duration = recipe.getDuration();
	}

	public SimpleMachineDisplay(SmeltingRecipe recipe) {
		super(EntryIngredients.ofIngredients(recipe.getIngredients()),
				Collections.singletonList(EntryIngredients.of(recipe.getOutput())),
				Optional.of(recipe.getId()));
		this.duration = recipe.getCookTime() * 130 / 200;
	}


	public int getDuration() {
		return this.duration;
	}
}
