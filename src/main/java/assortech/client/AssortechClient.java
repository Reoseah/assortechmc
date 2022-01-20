package assortech.client;

import assortech.Assortech;
import assortech.client.screen.ElectricFurnaceScreen;
import assortech.client.screen.GeneratorScreen;
import assortech.client.screen.SolarPanelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class AssortechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), Assortech.AtBlocks.RUBBER_LEAVES, Assortech.AtBlocks.RUBBER_SAPLING);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> applyRubberTreeTint(world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor()), Assortech.AtBlocks.RUBBER_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> applyRubberTreeTint(FoliageColors.getDefaultColor()), Assortech.AtBlocks.RUBBER_LEAVES);

        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.GENERATOR, GeneratorScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.SOLAR_PANEL, SolarPanelScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
    }

    /**
     * Returns slightly yellower version of the passed argument, which is usually normal foliage color.
     */
    public static int applyRubberTreeTint(int color) {
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;

        return 0xFF000000 | (r * 240 / 255) << 16 | (g * 235 / 255) << 8 | (b * 220 / 255);
    }
}
