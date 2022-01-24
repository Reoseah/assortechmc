package assortech.compatibility.roughlyenoughitems;

import assortech.Assortech;
import assortech.screen.client.ElectricFurnaceScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class AssortechREI implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.addWorkstations(CategoryIdentifier.of("minecraft", "plugins/smelting"), EntryStacks.of(Assortech.AtBlocks.ELECTRIC_FURNACE));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(78, 31, 28, 23), ElectricFurnaceScreen.class, CategoryIdentifier.of("minecraft", "plugins/smelting"));
    }
}
