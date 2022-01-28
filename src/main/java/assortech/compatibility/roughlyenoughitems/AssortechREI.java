package assortech.compatibility.roughlyenoughitems;

import assortech.Assortech;
import assortech.recipe.CompressorRecipe;
import assortech.recipe.ExtractorRecipe;
import assortech.recipe.MaceratorRecipe;
import assortech.recipe.MolecularAssemblerRecipe;
import assortech.screen.client.CompressorScreen;
import assortech.screen.client.ElectricFurnaceScreen;
import assortech.screen.client.ExtractorScreen;
import assortech.screen.client.MaceratorScreen;
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

public class AssortechREI implements REIClientPlugin {
    public static final Identifier WIDGETS = Assortech.id("textures/gui/compatibility/rei_widgets.png");

    public static final CategoryIdentifier<MaceratingDisplay> MACERATING = CategoryIdentifier.of("assortech:macerating");
    public static final CategoryIdentifier<CompressingDisplay> COMPRESSING = CategoryIdentifier.of("assortech:compressing");
    public static final CategoryIdentifier<ExtractingDisplay> EXTRACTING = CategoryIdentifier.of("assortech:extracting");
    public static final CategoryIdentifier<MolecularAssemblyDisplay> MOLECULAR_ASSEMBLY = CategoryIdentifier.of("assortech:molecular_assembly");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CraftingMachineCategory(MACERATING, EntryStacks.of(Assortech.AtBlocks.MACERATOR), "category.assortech.macerating"));
        registry.add(new CraftingMachineCategory(COMPRESSING, EntryStacks.of(Assortech.AtBlocks.COMPRESSOR), "category.assortech.compressing"));
        registry.add(new CraftingMachineCategory(EXTRACTING, EntryStacks.of(Assortech.AtBlocks.EXTRACTOR), "category.assortech.extracting"));
        registry.add(new CraftingMachineCategory(MOLECULAR_ASSEMBLY, EntryStacks.of(Assortech.AtBlocks.MOLECULAR_RECONSTRUCTOR), "category.assortech.molecular_assembly"));

        registry.addWorkstations(CategoryIdentifier.of("minecraft", "plugins/smelting"), EntryStacks.of(Assortech.AtBlocks.ELECTRIC_FURNACE));
        registry.addWorkstations(MACERATING, EntryStacks.of(Assortech.AtBlocks.MACERATOR));
        registry.addWorkstations(COMPRESSING, EntryStacks.of(Assortech.AtBlocks.COMPRESSOR));
        registry.addWorkstations(EXTRACTING, EntryStacks.of(Assortech.AtBlocks.EXTRACTOR));
        registry.addWorkstations(MOLECULAR_ASSEMBLY, EntryStacks.of(Assortech.AtBlocks.MOLECULAR_RECONSTRUCTOR));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(MaceratorRecipe.class, Assortech.AtRecipeTypes.MACERATING, MaceratingDisplay::new);
        registry.registerRecipeFiller(CompressorRecipe.class, Assortech.AtRecipeTypes.COMPRESSING, CompressingDisplay::new);
        registry.registerRecipeFiller(ExtractorRecipe.class, Assortech.AtRecipeTypes.EXTRACTING, ExtractingDisplay::new);
        registry.registerRecipeFiller(MolecularAssemblerRecipe.class, Assortech.AtRecipeTypes.MOLECULAR_ASSEMBLY, MolecularAssemblyDisplay::new);
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ElectricFurnaceScreen.class, CategoryIdentifier.of("minecraft", "plugins/smelting"));
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), MaceratorScreen.class, MACERATING);
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
