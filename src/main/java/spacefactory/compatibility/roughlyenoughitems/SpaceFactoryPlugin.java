package spacefactory.compatibility.roughlyenoughitems;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import spacefactory.SpaceFactory;
import spacefactory.core.recipe.IngredientCount;
import spacefactory.features.atomic_reassembler.AtomicReassemblerRecipe;
import spacefactory.features.atomic_reassembler.AtomicReassemblerScreen;
import spacefactory.features.compressor.CompressorRecipe;
import spacefactory.features.compressor.CompressorScreen;
import spacefactory.features.electric_furnace.ElectricFurnaceScreen;
import spacefactory.features.extractor.ExtractorRecipe;
import spacefactory.features.extractor.ExtractorScreen;
import spacefactory.features.pulverizer.PulverizerRecipe;
import spacefactory.features.pulverizer.PulverizerScreen;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SpaceFactoryPlugin implements REIClientPlugin {
	public static final Identifier WIDGETS = SpaceFactory.id("textures/gui/compatibility/rei_widgets.png");

	public static final CategoryIdentifier<ElectricSmeltingDisplay> ELECTRIC_SMELTING = CategoryIdentifier.of("spacefactory:electric_smelting");
	public static final CategoryIdentifier<PulverizingDisplay> PULVERIZER = CategoryIdentifier.of("spacefactory:pulverizing");
	public static final CategoryIdentifier<CompressingDisplay> COMPRESSING = CategoryIdentifier.of("spacefactory:compressing");
	public static final CategoryIdentifier<ExtractingDisplay> EXTRACTING = CategoryIdentifier.of("spacefactory:extracting");
	public static final CategoryIdentifier<AtomicReassemblyDisplay> ATOMIC_REASSEMBLY = CategoryIdentifier.of("spacefactory:atomic_reassembly");

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new SimpleMachineCategory(ELECTRIC_SMELTING, EntryStacks.of(SpaceFactory.Blocks.ELECTRIC_FURNACE), "category.spacefactory.electric_smelting"));
		registry.add(new SimpleMachineCategory(PULVERIZER, EntryStacks.of(SpaceFactory.Blocks.PULVERIZER), "category.spacefactory.pulverizing"));
		registry.add(new SimpleMachineCategory(COMPRESSING, EntryStacks.of(SpaceFactory.Blocks.COMPRESSOR), "category.spacefactory.compressing"));
		registry.add(new SimpleMachineCategory(EXTRACTING, EntryStacks.of(SpaceFactory.Blocks.EXTRACTOR), "category.spacefactory.extracting"));
		registry.add(new AtomicReassemblyCategory(ATOMIC_REASSEMBLY, EntryStacks.of(SpaceFactory.Blocks.ATOMIC_REASSEMBLER), "category.spacefactory.atomic_reassembly"));

		registry.addWorkstations(ELECTRIC_SMELTING, EntryStacks.of(SpaceFactory.Blocks.ELECTRIC_FURNACE));
		registry.addWorkstations(PULVERIZER, EntryStacks.of(SpaceFactory.Blocks.PULVERIZER));
		registry.addWorkstations(COMPRESSING, EntryStacks.of(SpaceFactory.Blocks.COMPRESSOR));
		registry.addWorkstations(EXTRACTING, EntryStacks.of(SpaceFactory.Blocks.EXTRACTOR));
		registry.addWorkstations(ATOMIC_REASSEMBLY, EntryStacks.of(SpaceFactory.Blocks.ATOMIC_REASSEMBLER));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerRecipeFiller(SmeltingRecipe.class, RecipeType.SMELTING, ElectricSmeltingDisplay::new);
		registry.registerRecipeFiller(PulverizerRecipe.class, SpaceFactory.RecipeTypes.PULVERIZING, PulverizingDisplay::new);
		registry.registerRecipeFiller(CompressorRecipe.class, SpaceFactory.RecipeTypes.COMPRESSING, CompressingDisplay::new);
		registry.registerRecipeFiller(ExtractorRecipe.class, SpaceFactory.RecipeTypes.EXTRACTING, ExtractingDisplay::new);
		registry.registerRecipeFiller(AtomicReassemblerRecipe.class, SpaceFactory.RecipeTypes.ATOMIC_REASSEMBLY, AtomicReassemblyDisplay::new);

		registry.registerGlobalDisplayGenerator(new DynamicDisplayGenerator<DefaultInformationDisplay>() {
			@Override
			public Optional<List<DefaultInformationDisplay>> getUsageFor(EntryStack<?> entry) {
				return Optional.ofNullable(entry) //
						.filter(e -> e.getIdentifier() != null && e.getIdentifier().getNamespace().equals(SpaceFactory.MOD_ID)) //
						.map(e -> e.getValue() instanceof ItemStack stack ? Util.createTranslationKey("usage_info", Registry.ITEM.getId(stack.getItem())) : null) //
						.filter(I18n::hasTranslation) //
						.map(info -> DefaultInformationDisplay.createFromEntry(entry, entry.asFormatStrippedText()).line(translateWithNewLines(info)))
						.map(ImmutableList::of);
			}

			@Override
			public Optional<List<DefaultInformationDisplay>> getRecipeFor(EntryStack<?> entry) {
				return Optional.ofNullable(entry) //
						.filter(e -> e.getIdentifier() != null && e.getIdentifier().getNamespace().equals(SpaceFactory.MOD_ID)) //
						.map(e -> e.getValue() instanceof ItemStack stack ? Util.createTranslationKey("recipe_info", Registry.ITEM.getId(stack.getItem())) : null) //
						.filter(I18n::hasTranslation) //
						.map(info -> DefaultInformationDisplay.createFromEntry(entry, entry.asFormatStrippedText()).line(translateWithNewLines(info)))
						.map(ImmutableList::of);
			}
		});
	}

	@Override
	public void registerScreens(ScreenRegistry registry) {
		registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ElectricFurnaceScreen.class, CategoryIdentifier.of("minecraft", "plugins/smelting"));
		registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), PulverizerScreen.class, PULVERIZER);
		registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), CompressorScreen.class, COMPRESSING);
		registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ExtractorScreen.class, EXTRACTING);
		registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), AtomicReassemblerScreen.class, ATOMIC_REASSEMBLY);
	}

	public static List<EntryIngredient> toIngredientEntries(Ingredient ingredient, int count) {
		return ImmutableList.of(EntryIngredient.of(
				Arrays.stream(ingredient.getMatchingStacks())
						.peek(stack -> stack.setCount(count))
						.map(EntryStacks::of)
						.toList()
		));
	}

	public static List<EntryIngredient> toIngredientEntries(IngredientCount... inputs) {
		return Arrays.stream(inputs)
				.map(input -> EntryIngredient.of(
						Arrays.stream(input.ingredient.getMatchingStacks())
								.peek(stack -> stack.setCount(input.count))
								.map(EntryStacks::of)
								.toList()
				))
				.toList();
	}

	private static Text translateWithNewLines(String translationKey, Object... args) {
		return new LiteralText(I18n.translate(translationKey, args).replace("\\n", "\n"));
	}
}
