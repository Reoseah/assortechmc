package spacefactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import spacefactory.api.EU;
import spacefactory.api.Wrenchable;
import spacefactory.features.atomic_reassembler.AtomicReassemblerScreen;
import spacefactory.features.battery.BatteryBoxScreen;
import spacefactory.features.compressor.CompressorScreen;
import spacefactory.features.electric_furnace.ElectricFurnaceScreen;
import spacefactory.features.extractor.ExtractorScreen;
import spacefactory.features.generator.GeneratorScreen;
import spacefactory.features.pulverizer.PulverizerScreen;
import spacefactory.features.solar_panel.SolarPanelScreen;

@Environment(EnvType.CLIENT)
public class SpaceFactoryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), SpaceFactory.Blocks.RUBBER_LEAVES, SpaceFactory.Blocks.RUBBER_SAPLING);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), SpaceFactory.Blocks.REINFORCED_GLASS);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(), SpaceFactory.Blocks.RUBBER_LEAVES);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getDefaultColor(), SpaceFactory.Blocks.RUBBER_LEAVES);

        ModelPredicateProviderRegistry.register(SpaceFactory.id("energy"), new UnclampedModelPredicateProvider() {
            @Override
            public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                if (stack.getItem() instanceof EU.ElectricItem item) {
                    return item.getEnergy(stack);
                }
                return 0;
            }

            @Override
            public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
                return this.unclampedCall(stack, world, entity, seed);
            }
        });
        UnclampedModelPredicateProvider wrenchOpenPredicate = (ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int i) -> {
            if (entity instanceof PlayerEntity player) {
                if ((player.getMainHandStack() == stack) || (player.getOffHandStack() == stack)) {
                    @SuppressWarnings("resource")
                    float reachDistance = MinecraftClient.getInstance().interactionManager.getReachDistance();
                    HitResult hit = player.raycast(reachDistance, 0, false);
                    if (hit instanceof BlockHitResult blockhit) {
                        BlockPos pos = blockhit.getBlockPos();
                        if (entity.getEntityWorld().getBlockState(pos).getBlock() instanceof Wrenchable) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        };
        ModelPredicateProviderRegistry.register(SpaceFactory.Items.REFINED_IRON_WRENCH, SpaceFactory.id("open"),
                wrenchOpenPredicate);

        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.GENERATOR, GeneratorScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.SOLAR_PANEL, SolarPanelScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.ELECTRIC_FURNACE, ElectricFurnaceScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.PULVERIZER, PulverizerScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.COMPRESSOR, CompressorScreen::new);
        ScreenRegistry.register(SpaceFactory.ScreenHandlerTypes.MOLECULAR_ASSEMBLER, AtomicReassemblerScreen::new);
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
