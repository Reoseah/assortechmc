package assortech.compatibility.roughlyenoughitems;

import assortech.Assortech;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class AssortechREI implements REIClientPlugin {
	public static final Identifier WIDGETS = Assortech.id("textures/gui/compatibility/rei_widgets.png");

	@Override
	public void registerCategories(CategoryRegistry registry) {
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
	}

	@Override
	public void registerScreens(ScreenRegistry registry) {
	}

	public static List<EntryIngredient> toIngredientEntries(Ingredient ingredient, int count) {
		return ImmutableList.of(EntryIngredient.of(
				Arrays.stream(ingredient.getMatchingStacks())
						.peek(stack -> stack.setCount(count))
						.map(EntryStacks::of)
						.toList()
		));
	}
}
