package spacefactory.compatibility.roughlyenoughitems;

import spacefactory.SpaceFactory;
import spacefactory.recipe.CompressorRecipe;
import spacefactory.recipe.ExtractorRecipe;
import spacefactory.recipe.PulverizerRecipe;
import spacefactory.screen.client.CompressorScreen;
import spacefactory.screen.client.ElectricFurnaceScreen;
import spacefactory.screen.client.ExtractorScreen;
import spacefactory.screen.client.PulverizerScreen;
import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class SpaceFactoryPlugin implements REIClientPlugin {
    public static final Identifier WIDGETS = SpaceFactory.id("textures/gui/compatibility/rei_widgets.png");

    public static final CategoryIdentifier<PulverizingDisplay> PULVERIZER = CategoryIdentifier.of("spacefactory:pulverizing");
    public static final CategoryIdentifier<CompressingDisplay> COMPRESSING = CategoryIdentifier.of("spacefactory:compressing");
    public static final CategoryIdentifier<ExtractingDisplay> EXTRACTING = CategoryIdentifier.of("spacefactory:extracting");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CraftingMachineCategory(PULVERIZER, EntryStacks.of(SpaceFactory.SFBlocks.PULVERIZER), "category.spacefactory.pulverizing"));
        registry.add(new CraftingMachineCategory(COMPRESSING, EntryStacks.of(SpaceFactory.SFBlocks.COMPRESSOR), "category.spacefactory.compressing"));
        registry.add(new CraftingMachineCategory(EXTRACTING, EntryStacks.of(SpaceFactory.SFBlocks.EXTRACTOR), "category.spacefactory.extracting"));

        registry.addWorkstations(CategoryIdentifier.of("minecraft", "plugins/smelting"), EntryStacks.of(SpaceFactory.SFBlocks.ELECTRIC_FURNACE));
        registry.addWorkstations(PULVERIZER, EntryStacks.of(SpaceFactory.SFBlocks.PULVERIZER));
        registry.addWorkstations(COMPRESSING, EntryStacks.of(SpaceFactory.SFBlocks.COMPRESSOR));
        registry.addWorkstations(EXTRACTING, EntryStacks.of(SpaceFactory.SFBlocks.EXTRACTOR));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(PulverizerRecipe.class, SpaceFactory.SFRecipeTypes.PULVERIZING, PulverizingDisplay::new);
        registry.registerRecipeFiller(CompressorRecipe.class, SpaceFactory.SFRecipeTypes.COMPRESSING, CompressingDisplay::new);
        registry.registerRecipeFiller(ExtractorRecipe.class, SpaceFactory.SFRecipeTypes.EXTRACTING, ExtractingDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ElectricFurnaceScreen.class, CategoryIdentifier.of("minecraft", "plugins/smelting"));
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), PulverizerScreen.class, PULVERIZER);
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), CompressorScreen.class, COMPRESSING);
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ExtractorScreen.class, EXTRACTING);
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
