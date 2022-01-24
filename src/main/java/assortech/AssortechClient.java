package assortech;

import assortech.Assortech;
import assortech.screen.client.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleBatteryItem;

@Environment(EnvType.CLIENT)
public class AssortechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), Assortech.AtBlocks.RUBBER_LEAVES, Assortech.AtBlocks.RUBBER_SAPLING);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> applyRubberTreeTint(world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor()), Assortech.AtBlocks.RUBBER_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> applyRubberTreeTint(FoliageColors.getDefaultColor()), Assortech.AtBlocks.RUBBER_LEAVES);

        FabricModelPredicateProviderRegistry.register(Assortech.id("energy"), new UnclampedModelPredicateProvider() {
            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                return SimpleBatteryItem.getStoredEnergyUnchecked(stack);
            }

            @Override
            public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                return this.unclampedCall(stack, world, entity, seed);
            }
        });

        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.GENERATOR, GeneratorScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.SOLAR_PANEL, SolarPanelScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.MACERATOR, MaceratorScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.COMPRESSOR, CompressorScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.EXTRACTOR, ExtractorScreen::new);
        ScreenRegistry.register(Assortech.AtScreenHandlerTypes.BATTERY_BOX, BatteryBoxScreen::new);
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
