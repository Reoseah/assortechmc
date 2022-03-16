package spacefactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import spacefactory.screen.client.*;
import team.reborn.energy.api.base.SimpleBatteryItem;

@Environment(EnvType.CLIENT)
public class SpaceFactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), SpaceFactory.Blocks.RUBBER_LEAVES, SpaceFactory.Blocks.RUBBER_SAPLING);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            int color = world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor();
            return color;
        }, SpaceFactory.Blocks.RUBBER_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getDefaultColor(), SpaceFactory.Blocks.RUBBER_LEAVES);

        FabricModelPredicateProviderRegistry.register(SpaceFactory.id("energy"), new UnclampedModelPredicateProvider() {
            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                return SimpleBatteryItem.getStoredEnergyUnchecked(stack);
            }

            @Override
            public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                return this.unclampedCall(stack, world, entity, seed);
            }
        });

        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.GENERATOR, GeneratorScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.SOLAR_PANEL, SolarPanelScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.PULVERIZER, PulverizerScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.COMPRESSOR, CompressorScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.MOLECULAR_ASSEMBLER, MolecularAssemblerScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.EXTRACTOR, ExtractorScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.BATTERY_BOX, BatteryBoxScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(SpaceFactory.id("burnt_cable"), (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> {
                if (client.world != null) {
                    for (int i = 0; i < 8; i++) {
                        client.world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + client.world.random.nextFloat(), pos.getY() + client.world.random.nextFloat(), pos.getZ() + client.world.random.nextFloat(), 0, 0, 0);
                    }
                }
            });
        });
    }

}
