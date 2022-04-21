package spacefactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spacefactory.features.cables.CableBlockEntity;
import spacefactory.features.cables.NormalCableBlock;
import spacefactory.features.cables.HiddenCableBlock;
import spacefactory.features.generator.dragon_egg_energy_absorber.DragonEggEnergyAbsorberBlockEntity;
import spacefactory.features.generator.dragon_egg_energy_absorber.DragonEggSiphonBlock;
import spacefactory.features.generator.fuel_generator.GeneratorBlock;
import spacefactory.features.generator.fuel_generator.GeneratorBlockEntity;
import spacefactory.features.generator.fuel_generator.GeneratorScreenHandler;
import spacefactory.features.generator.solar_panel.SolarPanelBlock;
import spacefactory.features.generator.solar_panel.SolarPanelBlockEntity;
import spacefactory.features.generator.solar_panel.SolarPanelScreenHandler;
import spacefactory.features.machine.MachineRecipe;
import spacefactory.features.machine.atomic_reconstructor.AtomicReconstructorRecipe;
import spacefactory.features.machine.atomic_reconstructor.AtomicReconstructorBlock;
import spacefactory.features.machine.atomic_reconstructor.AtomicReconstructorBlockEntity;
import spacefactory.features.machine.atomic_reconstructor.AtomicReconstructorScreenHandler;
import spacefactory.features.machine.compressor.CompressorBlock;
import spacefactory.features.machine.compressor.CompressorBlockEntity;
import spacefactory.features.machine.compressor.CompressorRecipe;
import spacefactory.features.machine.compressor.CompressorScreenHandler;
import spacefactory.features.machine.electric_furnace.ElectricFurnaceBlock;
import spacefactory.features.machine.electric_furnace.ElectricFurnaceBlockEntity;
import spacefactory.features.machine.electric_furnace.ElectricFurnaceScreenHandler;
import spacefactory.features.machine.extractor.ExtractorBlock;
import spacefactory.features.machine.extractor.ExtractorBlockEntity;
import spacefactory.features.machine.extractor.ExtractorRecipe;
import spacefactory.features.machine.extractor.ExtractorScreenHandler;
import spacefactory.features.machine.fabricator_ai.AIFabricationRecipe;
import spacefactory.features.machine.fabricator_ai.FabricatorAIBlock;
import spacefactory.features.machine.fabricator_ai.FabricatorAIBlockEntity;
import spacefactory.features.machine.fabricator_ai.FabricatorAIScreenHandler;
import spacefactory.features.machine.macerator.MaceratorBlock;
import spacefactory.features.machine.macerator.MaceratorBlockEntity;
import spacefactory.features.machine.macerator.MaceratorRecipe;
import spacefactory.features.machine.macerator.MaceratorScreenHandler;
import spacefactory.features.nano_steel.NanoSteelMacheteItem;
import spacefactory.features.nano_steel.NanoSteelUnicutterItem;
import spacefactory.features.nano_steel.NanoSteelWrenchItem;
import spacefactory.features.rubber_tree.AliveRubberLogBlock;
import spacefactory.features.rubber_tree.RubberSaplingBlock;
import spacefactory.core.item.MacheteItem;
import spacefactory.core.item.UnicutterItem;
import spacefactory.core.item.WrenchItem;
import spacefactory.features.rubber_tree.RubberFoliagePlacer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SpaceFactory implements ModInitializer {
    public static final String MOD_ID = "spacefactory";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Config config = Config.load();

    @Override
    public void onInitialize() {
        Blocks.register();
        BlockEntityTypes.init();
        ScreenHandlerTypes.init();
        Items.register();
        RecipeTypes.init();
        RecipeSerializers.init();
        FoliagePlacers.init();
        ConfiguredFeatures.init();
        PlacedFeatures.init();
        BiomeConfiguredFeatures.init();
        BiomePlacedFeatures.init();

        BiomeModifications.create(id("rubber_trees")) //
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.categories(Biome.Category.SWAMP, Biome.Category.JUNGLE, Biome.Category.FOREST, Biome.Category.RIVER), context -> {
                    if (config.generateRubberTrees) {
                        RegistryKey<PlacedFeature> rubberTreePatch = getKey(BiomePlacedFeatures.RUBBER_TREE_PATCH);
                        context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, rubberTreePatch);
                    }
                });
        BiomeModifications.create(id("the_end_ores")) //
                .add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInTheEnd(), context -> {
                    if (config.generateOres) {
                        RegistryKey<PlacedFeature> iridiumOre = getKey(BiomePlacedFeatures.IRIDIUM_ORE);
                        context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, iridiumOre);
                    }
                });
    }

    public static Identifier id(String path) {
        return new Identifier("spacefactory", path);
    }

    public static <T> RegistryEntry<T> getEntry(Registry<T> registry, T entry) {
        return registry.getEntry(registry.getKey(entry).orElseThrow()).orElseThrow();
    }

    private static RegistryKey<PlacedFeature> getKey(PlacedFeature feature) {
        return BuiltinRegistries.PLACED_FEATURE.getKey(feature).orElseThrow();
    }

    public static class Config {
        public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        @SerializedName("generate_rubber_trees")
        public boolean generateRubberTrees = true;
        @SerializedName("rubber_tree_rarity")
        public int rubberTreesRarity = 4;
        @SerializedName("rubber_tree_tries")
        public int rubberTreesTries = 6;

        @SerializedName("generate_ores")
        public boolean generateOres = true;

        @SerializedName("iridium_veins_per_chunk")
        public int iridiumVeinsPerChunk = 4;
        @SerializedName("iridium_vein_size")
        public int iridiumVeinSize = 4;
        @SerializedName("iridium_min_height")
        public int iridiumMinHeight = 0;
        @SerializedName("iridium_max_height")
        public int iridiumMaxHeight = 64;

        @SerializedName("generator_production")
        public int generatorProduction = 10;
        @SerializedName("generator_burn_rate")
        public int generatorBurnRate = 4;

        @SerializedName("solar_panel_production")
        public int solarPanelProduction = 1;

        @SerializedName("dragon_egg_siphon_production")
        public int dragonEggSiphonProduction = 250;

        @SerializedName("copper_wire_transfer_rate")
        public int copperWireTransferRate = 100;
        @SerializedName("copper_bus_transfer_rate")
        public int copperBusTransferRate = 1000;

        @SerializedName("minimum_shock_current")
        public int minShockCurrent = 10;
        @SerializedName("shock_current_to_damage_coefficient")
        public int shockCurrentToDamageCoefficient = 15;

        @SerializedName("crafting_machine_energy_buffer")
        public int craftingMachineCapacity = 100;
        @SerializedName("crafting_machine_receive_rate")
        public int craftingMachineRate = 10;

        @SerializedName("electric_furnace_consumption")
        public int electricFurnaceConsumption = 3;
        @SerializedName("macerator_consumption")
        public int maceratorConsumption = 2;
        @SerializedName("compressor_consumption")
        public int compressorConsumption = 2;
        @SerializedName("extractor_consumption")
        public int extractorConsumption = 2;
        @SerializedName("atomic_reconstructor_consumption")
        public int atomicReassemblerConsumption = 10;
        @SerializedName("fabricator_ai_consumption")
        public int fabricatorAIConsumption = 10;

        public static Config load() {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");

            if (Files.exists(configPath)) {
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    return GSON.fromJson(reader, Config.class);
                } catch (IOException e) {
                    LOGGER.warn("Error while reading config, using defaults", e);
                    return new Config();
                }
            } else {
                LOGGER.info("Missing config file, creating default");
                Config config = new Config();
                write(config);
                return config;
            }
        }

        public static void write(Config config) {
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            } catch (IOException e) {
                LOGGER.warn("Error while writing config", e);
            }
        }
    }

    public static class Materials {
        public static final Material MACHINE = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();
        public static final Material UNMOVABLE_WOOD = new FabricMaterialBuilder(MapColor.OAK_TAN).blocksPistons().burnable().build();
    }

    public static class Blocks {

        private static final AbstractBlock.Settings MACHINE_SETTINGS = Settings.of(Materials.MACHINE).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(net.minecraft.block.Blocks::never);
        private static final AbstractBlock.Settings ADVANCED_MACHINE_SETTINGS = Settings.of(Materials.MACHINE).strength(10F, 30F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(net.minecraft.block.Blocks::never);

        public static final Block RUBBER_LOG = new PillarBlock(Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.YELLOW : MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD));
        public static final Block ALIVE_RUBBER_LOG = new AliveRubberLogBlock(Settings.of(Materials.UNMOVABLE_WOOD, MapColor.YELLOW).ticksRandomly().strength(2F).sounds(BlockSoundGroup.WOOD));
        public static final Block RUBBER_WOOD = new PillarBlock(Settings.of(Material.WOOD, MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD));
        public static final Block STRIPPED_RUBBER_LOG = new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD));
        public static final Block STRIPPED_RUBBER_WOOD = new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD));
        public static final Block RUBBER_LEAVES = new LeavesBlock(Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(net.minecraft.block.Blocks::canSpawnOnLeaves).suffocates(net.minecraft.block.Blocks::never).blockVision(net.minecraft.block.Blocks::never));
        public static final Block RUBBER_SAPLING = new RubberSaplingBlock(Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS));

        public static final Block END_STONE_IRIDIUM_ORE = new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(3F, 9F));
        public static final Block RAW_IRIDIUM_BLOCK = new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).requiresTool().strength(7.0f, 10.0f));

        public static final Block CRYSTALITE_BLOCK = new Block(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE).luminance(15).strength(5F, 20F).slipperiness(0.98F).sounds(BlockSoundGroup.GLASS).allowsSpawning(net.minecraft.block.Blocks::never));
        public static final Block NANO_STEEL_BLOCK = new Block(FabricBlockSettings.of(Material.METAL, MapColor.DARK_AQUA).strength(12F, 20F));
        public static final Block IRIDIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5F, 20F).allowsSpawning(net.minecraft.block.Blocks::never));

        public static final Block COPPER_WIRE = new NormalCableBlock(1, false, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL));
        public static final Block COPPER_CABLE = new NormalCableBlock(2, true, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL));
        public static final Block COPPER_BUS_BAR = new NormalCableBlock(3, false, FabricBlockSettings.of(Material.METAL).strength(2F, 5F));
        public static final Block ENERGY_CONDUIT = new NormalCableBlock(4, true, FabricBlockSettings.of(Material.METAL).strength(5F, 20F));

        public static final Block MACHINE_FRAME = new Block(MACHINE_SETTINGS);

        public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE));
        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0));
        public static final Block MACERATOR = new MaceratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block COMPRESSOR = new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0));
        public static final Block ATOMIC_RECONSTRUCTOR = new AtomicReconstructorBlock(FabricBlockSettings.copyOf(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0)));
        public static final Block DRAGON_EGG_ENERGY_ABSORBER = new DragonEggSiphonBlock(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));
        public static final Block FABRICATOR_AI = new FabricatorAIBlock(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));

        private static final AbstractBlock.Settings REINFORCED_STONE_SETTINGS = FabricBlockSettings.of(Material.STONE, MapColor.DARK_AQUA).strength(10F, 15F);
        public static final Block REINFORCED_STONE = new Block(REINFORCED_STONE_SETTINGS);
        public static final Block REINFORCED_STONE_TILES = new Block(REINFORCED_STONE_SETTINGS);
        public static final Block REINFORCED_STONE_STAIRS = new StairsBlock(REINFORCED_STONE_TILES.getDefaultState(), REINFORCED_STONE_SETTINGS);
        public static final Block REINFORCED_STONE_SLAB = new SlabBlock(REINFORCED_STONE_SETTINGS);
        public static final Block REINFORCED_STONE_COVERED_CONDUIT = new HiddenCableBlock(REINFORCED_STONE_SETTINGS);
        public static final Block REINFORCED_GLASS = new GlassBlock(FabricBlockSettings.of(Material.GLASS).strength(7F, 10F).nonOpaque().sounds(BlockSoundGroup.GLASS));

        private static void register(String name, Block entry) {
            Registry.register(Registry.BLOCK, id(name), entry);
        }

        public static void register() {
            register("rubber_log", RUBBER_LOG);
            register("alive_rubber_log", ALIVE_RUBBER_LOG);
            register("rubber_wood", RUBBER_WOOD);
            register("stripped_rubber_log", STRIPPED_RUBBER_LOG);
            register("stripped_rubber_wood", STRIPPED_RUBBER_WOOD);
            register("rubber_leaves", RUBBER_LEAVES);
            register("rubber_sapling", RUBBER_SAPLING);

            register("end_stone_iridium_ore", END_STONE_IRIDIUM_ORE);
            register("raw_iridium_block", RAW_IRIDIUM_BLOCK);
            register("iridium_block", IRIDIUM_BLOCK);
            register("nano_steel_block", NANO_STEEL_BLOCK);
            register("crystalite_block", CRYSTALITE_BLOCK);

            register("copper_wire", COPPER_WIRE);
            register("copper_cable", COPPER_CABLE);
            register("copper_bus_bar", COPPER_BUS_BAR);
            register("energy_conduit", ENERGY_CONDUIT);

            register("machine_frame", MACHINE_FRAME);

            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);
            register("electric_furnace", ELECTRIC_FURNACE);
            register("macerator", MACERATOR);
            register("compressor", COMPRESSOR);
            register("extractor", EXTRACTOR);
            register("atomic_reassembler", ATOMIC_RECONSTRUCTOR);
            register("dragon_energy_absorber", DRAGON_EGG_ENERGY_ABSORBER);
            register("fabricator_ai", FABRICATOR_AI);

            register("reinforced_stone", REINFORCED_STONE);
            register("reinforced_stone_tiles", REINFORCED_STONE_TILES);
            register("reinforced_stone_stairs", REINFORCED_STONE_STAIRS);
            register("reinforced_stone_slab", REINFORCED_STONE_SLAB);
            register("reinforced_stone_covered_conduit", REINFORCED_STONE_COVERED_CONDUIT);
            register("reinforced_glass", REINFORCED_GLASS);

            FlammableBlockRegistry.getDefaultInstance().add(Blocks.RUBBER_LOG, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(Blocks.ALIVE_RUBBER_LOG, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(Blocks.STRIPPED_RUBBER_LOG, 5, 5);
            FlammableBlockRegistry.getDefaultInstance().add(Blocks.RUBBER_LEAVES, 30, 60);

            StrippableBlockRegistry.register(Blocks.RUBBER_LOG, Blocks.STRIPPED_RUBBER_LOG);
            StrippableBlockRegistry.register(Blocks.RUBBER_WOOD, Blocks.STRIPPED_RUBBER_WOOD);
        }
    }

    public static class Items {
        public static final ItemGroup MAIN = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(SpaceFactory.Items.QUANTUM_CIRCUIT));

        public static final Item RUBBER_LOG = new BlockItem(Blocks.RUBBER_LOG, settings());
        public static final Item STRIPPED_RUBBER_LOG = new BlockItem(Blocks.STRIPPED_RUBBER_LOG, settings());
        public static final Item RUBBER_WOOD = new BlockItem(Blocks.RUBBER_WOOD, settings());
        public static final Item STRIPPED_RUBBER_WOOD = new BlockItem(Blocks.STRIPPED_RUBBER_WOOD, settings());
        public static final Item RUBBER_LEAVES = new BlockItem(Blocks.RUBBER_LEAVES, settings());
        public static final Item RUBBER_SAPLING = new BlockItem(Blocks.RUBBER_SAPLING, settings());

        public static final Item END_STONE_IRIDIUM_ORE = new BlockItem(Blocks.END_STONE_IRIDIUM_ORE, settings().rarity(Rarity.UNCOMMON));
        public static final Item RAW_IRIDIUM_BLOCK = new BlockItem(Blocks.RAW_IRIDIUM_BLOCK, settings().rarity(Rarity.UNCOMMON));

        public static final Item CRYSTALITE_BLOCK = new BlockItem(Blocks.CRYSTALITE_BLOCK, settings().rarity(Rarity.RARE));
        public static final Item NANO_STEEL_BLOCK = new BlockItem(Blocks.NANO_STEEL_BLOCK, settings().rarity(Rarity.RARE));
        public static final Item IRIDIUM_BLOCK = new BlockItem(Blocks.IRIDIUM_BLOCK, settings().rarity(Rarity.UNCOMMON));

        public static final Item RAW_RUBBER = new Item(settings());
        public static final Item RUBBER = new Item(settings());
        public static final Item CAMOUFLAGE_CLOTH = new Item(settings());

        public static final Item REFINED_IRON_INGOT = new Item(settings().rarity(Rarity.RARE));
        public static final Item NANO_STEEL_INGOT = new Item(settings().rarity(Rarity.RARE));
        public static final Item IRIDIUM_INGOT = new Item(settings().rarity(Rarity.UNCOMMON));

        public static final Item RAW_IRIDIUM = new Item(settings().rarity(Rarity.UNCOMMON));

        public static final Item COPPER_NUGGET = new Item(settings());
        public static final Item IRIDIUM_NUGGET = new Item(settings().rarity(Rarity.UNCOMMON));

        public static final Item STONE_DUST = new Item(settings());
        public static final Item COAL_DUST = new Item(settings());
        public static final Item COPPER_DUST = new Item(settings());
        public static final Item IRON_DUST = new Item(settings());
        public static final Item GOLD_DUST = new Item(settings());
        public static final Item DIAMOND_DUST = new Item(settings());
        public static final Item ENDER_PEARL_DUST = new Item(settings());
        public static final Item NETHERITE_SCRAP_DUST = new Item(settings());
        public static final Item NETHER_QUARTZ_DUST = new Item(settings());
        public static final Item REFINED_IRON_DUST = new Item(settings());
        public static final Item IRIDIUM_DUST = new Item(settings().rarity(Rarity.UNCOMMON));

        public static final Item SMALL_IRIDIUM_DUST = new Item(settings().rarity(Rarity.UNCOMMON));

        public static final Item CIRCUIT = new Item(settings());
        public static final Item QUANTUM_CIRCUIT = new Item(settings().rarity(Rarity.UNCOMMON));
        public static final Item SILICON_INGOT = new Item(settings());
        public static final Item RAW_CRYSTALITE_DUST = new Item(settings());
        public static final Item CRYSTALITE_DUST = new Item(settings().rarity(Rarity.RARE));
        public static final Item CRYSTALITE_MATRIX = new Item(settings().rarity(Rarity.RARE).maxCount(16));
        public static final Item WARP_PRISM = new Item(settings().rarity(Rarity.EPIC).maxCount(16));
        public static final Item HYPER_AI_CORE = new Item(settings().rarity(Rarity.EPIC).maxCount(1));

        public static final Item COPPER_WIRE = new BlockItem(Blocks.COPPER_WIRE, settings());
        public static final Item COPPER_CABLE = new BlockItem(Blocks.COPPER_CABLE, settings());
        public static final Item COPPER_BUS_BAR = new BlockItem(Blocks.COPPER_BUS_BAR, settings());
        public static final Item ENERGY_CONDUIT = new BlockItem(Blocks.ENERGY_CONDUIT, settings());

        public static final Item MACHINE_FRAME = new BlockItem(Blocks.MACHINE_FRAME, settings());

        public static final Item GENERATOR = new BlockItem(Blocks.GENERATOR, settings());
        public static final Item SOLAR_PANEL = new BlockItem(Blocks.SOLAR_PANEL, settings());

        public static final Item ELECTRIC_FURNACE = new BlockItem(Blocks.ELECTRIC_FURNACE, settings());
        public static final Item MACERATOR = new BlockItem(Blocks.MACERATOR, settings());
        public static final Item COMPRESSOR = new BlockItem(Blocks.COMPRESSOR, settings());
        public static final Item EXTRACTOR = new BlockItem(Blocks.EXTRACTOR, settings());

        public static final Item ATOMIC_RECONSTRUCTOR = new BlockItem(Blocks.ATOMIC_RECONSTRUCTOR, settings().rarity(Rarity.RARE));
        public static final Item DRAGON_EGG_ENERGY_ABSORBER = new BlockItem(Blocks.DRAGON_EGG_ENERGY_ABSORBER, settings().rarity(Rarity.RARE));
        public static final Item FABRICATOR_AI = new BlockItem(Blocks.FABRICATOR_AI, settings().rarity(Rarity.RARE));

        public static final Item REINFORCED_STONE = new BlockItem(Blocks.REINFORCED_STONE, settings());
        public static final Item REINFORCED_STONE_TILES = new BlockItem(Blocks.REINFORCED_STONE_TILES, settings());
        public static final Item REINFORCED_STONE_STAIRS = new BlockItem(Blocks.REINFORCED_STONE_STAIRS, settings());
        public static final Item REINFORCED_STONE_SLAB = new BlockItem(Blocks.REINFORCED_STONE_SLAB, settings());
        public static final Item REINFORCED_STONE_COVERED_CONDUIT = new BlockItem(Blocks.REINFORCED_STONE_COVERED_CONDUIT, settings());
        public static final Item REINFORCED_GLASS = new BlockItem(Blocks.REINFORCED_GLASS, settings());

        public static final Item FLAK_VEST = new ArmorItem(ArmorMaterials.FLAK, EquipmentSlot.CHEST, settings().rarity(Rarity.UNCOMMON));
        public static final Item REFINED_IRON_MACHETE = new MacheteItem(ToolMaterials.REFINED_IRON, 2, -2.2F, settings());
        public static final Item REFINED_IRON_WRENCH = new WrenchItem(ToolMaterials.REFINED_IRON, 0, -1.8F, settings());
        public static final Item REFINED_IRON_UNICUTTER = new UnicutterItem(ToolMaterials.REFINED_IRON, -1, -1F, settings().maxDamage(250));

        public static final Item NANO_STEEL_MACHETE = new NanoSteelMacheteItem(ToolMaterials.NANO_STRUCTURED_STEEL, 2, -2.2F, settings().rarity(Rarity.RARE));
        public static final Item NANO_STEEL_WRENCH = new NanoSteelWrenchItem(ToolMaterials.NANO_STRUCTURED_STEEL, 0, -1.8F, settings().maxDamage(700).rarity(Rarity.RARE));
        public static final Item NANO_STEEL_UNICUTTER = new NanoSteelUnicutterItem(ToolMaterials.NANO_STRUCTURED_STEEL, -1, -1F, settings().maxDamage(700).rarity(Rarity.RARE));

        public static final TagKey<Item> RUBBERS = TagKey.of(Registry.ITEM_KEY, new Identifier("c:rubbers"));

        private static Item.Settings settings() {
            return new Item.Settings().group(MAIN);
        }

        private static void register(String name, Item entry) {
            Registry.register(Registry.ITEM, id(name), entry);
        }

        public static void register() {
            register("rubber_log", RUBBER_LOG);
            register("stripped_rubber_log", STRIPPED_RUBBER_LOG);
            register("rubber_wood", RUBBER_WOOD);
            register("stripped_rubber_wood", STRIPPED_RUBBER_WOOD);
            register("rubber_leaves", RUBBER_LEAVES);
            register("rubber_sapling", RUBBER_SAPLING);

            register("end_stone_iridium_ore", END_STONE_IRIDIUM_ORE);
            register("raw_iridium_block", RAW_IRIDIUM_BLOCK);

            register("nano_steel_block", NANO_STEEL_BLOCK);
            register("crystalite_block", CRYSTALITE_BLOCK);
            register("iridium_block", IRIDIUM_BLOCK);

            register("raw_rubber", RAW_RUBBER);
            register("rubber", RUBBER);
            register("camouflage_cloth", CAMOUFLAGE_CLOTH);

            register("refined_iron_ingot", REFINED_IRON_INGOT);
            register("nano_steel_ingot", NANO_STEEL_INGOT);
            register("iridium_ingot", IRIDIUM_INGOT);

            register("raw_iridium", RAW_IRIDIUM);

            register("copper_nugget", COPPER_NUGGET);
            register("iridium_nugget", IRIDIUM_NUGGET);

            register("stone_dust", STONE_DUST);
            register("coal_dust", COAL_DUST);
            register("copper_dust", COPPER_DUST);
            register("iron_dust", IRON_DUST);
            register("gold_dust", GOLD_DUST);
            register("diamond_dust", DIAMOND_DUST);
            register("ender_pearl_dust", ENDER_PEARL_DUST);
            register("netherite_scrap_dust", NETHERITE_SCRAP_DUST);
            register("nether_quartz_dust", NETHER_QUARTZ_DUST);
            register("refined_iron_dust", REFINED_IRON_DUST);
            register("iridium_dust", IRIDIUM_DUST);

            register("small_iridium_dust", SMALL_IRIDIUM_DUST);

            register("circuit", CIRCUIT);
            register("quantum_circuit", QUANTUM_CIRCUIT);
            register("silicon_ingot", SILICON_INGOT);
            register("raw_crystalite_dust", RAW_CRYSTALITE_DUST);
            register("crystalite_dust", CRYSTALITE_DUST);
            register("crystalite_matrix", CRYSTALITE_MATRIX);
            register("warp_prism", WARP_PRISM);
            register("hyper_ai_core", HYPER_AI_CORE);

            register("copper_wire", COPPER_WIRE);
            register("copper_cable", COPPER_CABLE);
            register("copper_bus_bar", COPPER_BUS_BAR);
            register("energy_conduit", ENERGY_CONDUIT);

            register("machine_frame", MACHINE_FRAME);

            register("generator", GENERATOR);
            register("solar_panel", SOLAR_PANEL);

            register("electric_furnace", ELECTRIC_FURNACE);
            register("macerator", MACERATOR);
            register("compressor", COMPRESSOR);
            register("extractor", EXTRACTOR);

            register("atomic_reassembler", ATOMIC_RECONSTRUCTOR);
            register("dragon_energy_absorber", DRAGON_EGG_ENERGY_ABSORBER);
            register("fabricator_ai", FABRICATOR_AI);

            register("reinforced_stone", REINFORCED_STONE);
            register("reinforced_stone_tiles", REINFORCED_STONE_TILES);
            register("reinforced_stone_stairs", REINFORCED_STONE_STAIRS);
            register("reinforced_stone_slab", REINFORCED_STONE_SLAB);
            register("reinforced_stone_covered_conduit", REINFORCED_STONE_COVERED_CONDUIT);
            register("reinforced_glass", REINFORCED_GLASS);

            register("flak_vest", FLAK_VEST);
            register("refined_iron_machete", REFINED_IRON_MACHETE);
            register("refined_iron_wrench", REFINED_IRON_WRENCH);
            register("refined_iron_unicutter", REFINED_IRON_UNICUTTER);
            register("nano_steel_machete", NANO_STEEL_MACHETE);
            register("nano_steel_wrench", NANO_STEEL_WRENCH);
            register("nano_steel_unicutter", NANO_STEEL_UNICUTTER);

            FuelRegistry.INSTANCE.add(RUBBERS, 100);

            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(RUBBER_SAPLING, 0.3F);
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(RUBBER_LEAVES, 0.3F);
        }
    }

    public static class BlockEntityTypes {
        public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = create("generator", GeneratorBlockEntity::new, Blocks.GENERATOR);
        public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = create("solar_panel", SolarPanelBlockEntity::new, Blocks.SOLAR_PANEL);
        public static final BlockEntityType<DragonEggEnergyAbsorberBlockEntity> DRAGON_EGG_ENERGY_ABSORBER = create("dragon_egg_energy_absorber", DragonEggEnergyAbsorberBlockEntity::new, Blocks.DRAGON_EGG_ENERGY_ABSORBER);

        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = create("electric_furnace", ElectricFurnaceBlockEntity::new, Blocks.ELECTRIC_FURNACE);
        public static final BlockEntityType<MaceratorBlockEntity> MACERATOR = create("macerator", MaceratorBlockEntity::new, Blocks.MACERATOR);
        public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = create("compressor", CompressorBlockEntity::new, Blocks.COMPRESSOR);
        public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = create("extractor", ExtractorBlockEntity::new, Blocks.EXTRACTOR);
        public static final BlockEntityType<AtomicReconstructorBlockEntity> ATOMIC_RECONSTRUCTOR = create("atomic_reconstructor", AtomicReconstructorBlockEntity::new, Blocks.ATOMIC_RECONSTRUCTOR);
        public static final BlockEntityType<FabricatorAIBlockEntity> FABRICATOR_AI = create("fabricator_ai", FabricatorAIBlockEntity::new, Blocks.FABRICATOR_AI);
        public static final BlockEntityType<CableBlockEntity> CABLE = create("cable", CableBlockEntity::new, Blocks.COPPER_WIRE, Blocks.COPPER_CABLE, Blocks.COPPER_BUS_BAR, Blocks.ENERGY_CONDUIT, Blocks.REINFORCED_STONE_COVERED_CONDUIT);

        public static <T extends BlockEntity> BlockEntityType<T> create(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
            BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, blocks).build();
            return Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), type);
        }

        public static void init() {
        }
    }

    public static class RecipeTypes {
        public static final RecipeType<MaceratorRecipe> MACERATING = create("macerating");
        public static final RecipeType<CompressorRecipe> COMPRESSING = create("compressing");
        public static final RecipeType<ExtractorRecipe> EXTRACTING = create("extracting");
        public static final RecipeType<AtomicReconstructorRecipe> ATOMIC_REASSEMBLY = create("atomic_reassembly");
        public static final RecipeType<AIFabricationRecipe> AI_FABRICATION = create("ai_fabrication");

        public static <T extends Recipe<?>> RecipeType<T> create(String name) {
            return Registry.register(Registry.RECIPE_TYPE, id(name), new RecipeType<T>() {
                @Override
                public String toString() {
                    return MOD_ID + ":" + name;
                }
            });
        }

        public static void init() {
        }
    }

    public static class RecipeSerializers {
        public static final RecipeSerializer<MaceratorRecipe> MACERATING = register("macerating", new MachineRecipe.Serializer<>(MaceratorRecipe::new, 300));
        public static final RecipeSerializer<CompressorRecipe> COMPRESSING = register("compressing", new MachineRecipe.Serializer<>(CompressorRecipe::new, 400));
        public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = register("extracting", new MachineRecipe.Serializer<>(ExtractorRecipe::new, 400));
        public static final RecipeSerializer<AtomicReconstructorRecipe> ATOMIC_REASSEMBLY = register("atomic_reassembly", new AtomicReconstructorRecipe.Serializer(1000));
        public static final RecipeSerializer<AIFabricationRecipe> AI_FABRICATION = register("ai_fabrication", new AIFabricationRecipe.Serializer());

        public static <T extends RecipeSerializer<?>> T register(String name, T entry) {
            return Registry.register(Registry.RECIPE_SERIALIZER, id(name), entry);
        }

        public static void init() {
        }
    }

    public static class FoliagePlacers {
        public static final FoliagePlacerType<RubberFoliagePlacer> RUBBER = create("rubber_foliage_placer", RubberFoliagePlacer.CODEC);

        public static <T extends FoliagePlacer> FoliagePlacerType<T> create(String name, Codec<T> codec) {
            return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id(name), new FoliagePlacerType<>(codec));
        }

        public static void init() {
        }
    }

    public static class ConfiguredFeatures {
        private static final BlockStateProvider RUBBER_LOGS = SimpleBlockStateProvider.of(Blocks.ALIVE_RUBBER_LOG);
        private static final BlockStateProvider RUBBER_LEAVES = SimpleBlockStateProvider.of(Blocks.RUBBER_LEAVES);
        public static final TreeFeatureConfig RUBBER_TREE_CONFIG = new TreeFeatureConfig.Builder( //
                RUBBER_LOGS, new StraightTrunkPlacer(5, 2, 0), //
                RUBBER_LEAVES, new RubberFoliagePlacer(UniformIntProvider.create(2, 2), UniformIntProvider.create(1, 1), 5), //
                new TwoLayersFeatureSize(1, 0, 1)).build();

        private static final OreFeatureConfig IRIDIUM_ORE_CONFIG = new OreFeatureConfig(List.of( //
                OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(net.minecraft.block.Blocks.END_STONE.getDefaultState()), Blocks.END_STONE_IRIDIUM_ORE.getDefaultState())), config.iridiumVeinSize);


        public static final ConfiguredFeature<TreeFeatureConfig, ?> RUBBER_TREE = register("rubber_tree", new ConfiguredFeature<>(Feature.TREE, RUBBER_TREE_CONFIG));

        public static final ConfiguredFeature<OreFeatureConfig, ?> IRIDIUM_ORE = register("iridium_ore", new ConfiguredFeature<>(Feature.ORE, IRIDIUM_ORE_CONFIG));

        public static <T extends ConfiguredFeature<?, ?>> T register(String name, T entry) {
            return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id(name), entry);
        }

        public static void init() {
        }
    }

    public static class PlacedFeatures {
        public static final PlacedFeature RUBBER_TREE = register("rubber_tree", new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeatures.RUBBER_TREE), List.of(net.minecraft.world.gen.feature.PlacedFeatures.wouldSurvive(Blocks.RUBBER_SAPLING))));

        public static <T extends PlacedFeature> T register(String name, T entry) {
            return Registry.register(BuiltinRegistries.PLACED_FEATURE, id(name), entry);
        }

        public static void init() {
        }
    }

    public static class BiomeConfiguredFeatures {
        public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> RUBBER_TREE_PATCH = ConfiguredFeatures.register("rubber_tree_patch", //
                new ConfiguredFeature<>(Feature.RANDOM_PATCH, //
                        net.minecraft.world.gen.feature.ConfiguredFeatures.createRandomPatchFeatureConfig(config.rubberTreesTries, getEntry(BuiltinRegistries.PLACED_FEATURE, PlacedFeatures.RUBBER_TREE))));

        public static void init() {
        }
    }

    public static class BiomePlacedFeatures {
        public static final PlacedFeature RUBBER_TREE_PATCH = PlacedFeatures.register("rubber_tree_patch", new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, BiomeConfiguredFeatures.RUBBER_TREE_PATCH), //
                List.of( //
                        RarityFilterPlacementModifier.of(config.rubberTreesRarity), //
                        SquarePlacementModifier.of(), //
                        net.minecraft.world.gen.feature.PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, //
                        BiomePlacementModifier.of())));


        public static final PlacedFeature IRIDIUM_ORE = PlacedFeatures.register("iridium_ore", new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeatures.IRIDIUM_ORE), //
                List.of( //
                        CountPlacementModifier.of(config.iridiumVeinsPerChunk), //
                        SquarePlacementModifier.of(), //
                        HeightRangePlacementModifier.uniform( //
                                YOffset.fixed(config.iridiumMinHeight), //
                                YOffset.fixed(config.iridiumMaxHeight)), //
                        BiomePlacementModifier.of())));

        public static void init() {
        }
    }

    public static class ScreenHandlerTypes {
        public static final ScreenHandlerType<GeneratorScreenHandler> GENERATOR = register("generator", new ScreenHandlerType<>(GeneratorScreenHandler::new));
        public static final ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL = register("solar_panel", new ScreenHandlerType<>(SolarPanelScreenHandler::new));
        public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE = register("electric_furnace", new ScreenHandlerType<>(ElectricFurnaceScreenHandler::new));
        public static final ScreenHandlerType<MaceratorScreenHandler> MACERATOR = register("macerator", new ScreenHandlerType<>(MaceratorScreenHandler::new));
        public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR = register("compressor", new ScreenHandlerType<>(CompressorScreenHandler::new));
        public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR = register("extractor", new ScreenHandlerType<>(ExtractorScreenHandler::new));
        public static final ScreenHandlerType<AtomicReconstructorScreenHandler> ATOMIC_RECONSTRUCTOR = register("atomic_reassembler", new ScreenHandlerType<>(AtomicReconstructorScreenHandler::new));
        public static final ScreenHandlerType<FabricatorAIScreenHandler> FABRICATOR_AI = register("fabricator_ai", new ScreenHandlerType<>(FabricatorAIScreenHandler.Client::new));

        private static void init() {
        }

        public static <T extends ScreenHandlerType<?>> T register(String name, T entry) {
            return Registry.register(Registry.SCREEN_HANDLER, id(name), entry);
        }
    }

    public static class DamageSources {
        public static final DamageSource UNINSULATED_WIRE_SHOCK = new DamageSource("spacefactory.uninsulated_wire_shock") {

        };
    }

    public enum ArmorMaterials implements ArmorMaterial {
        FLAK("flak", 15, new int[]{2, 5, 6, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, Ingredient.empty());

        private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

        private final String name;
        private final int durabilityMultiplier;
        private final int[] protectionAmounts;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Ingredient repairIngredient;

        ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Ingredient repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protectionAmounts = protectionAmounts;
            this.enchantability = enchantability;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurability(EquipmentSlot slot) {
            return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
        }

        @Override
        public int getProtectionAmount(EquipmentSlot slot) {
            return this.protectionAmounts[slot.getEntitySlotId()];
        }

        @Override
        public int getEnchantability() {
            return this.enchantability;
        }

        @Override
        public SoundEvent getEquipSound() {
            return this.equipSound;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }

        @Override
        @Environment(EnvType.CLIENT)
        public String getName() {
            return this.name;
        }

        @Override
        public float getToughness() {
            return this.toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }

    public enum ToolMaterials implements ToolMaterial {
        REFINED_IRON(2, 750, 6.5F, 3F, 8, Ingredient.ofItems(Items.REFINED_IRON_INGOT)), //
        NANO_STRUCTURED_STEEL(3, 1500, 7.5F, 4F, 0, Ingredient.ofItems(Items.NANO_STEEL_INGOT));

        private final int miningLevel;
        private final int itemDurability;
        private final float miningSpeed;
        private final float attackDamage;
        private final int enchantability;
        private final Ingredient repairIngredient;

        ToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Ingredient repairIngredient) {
            this.miningLevel = miningLevel;
            this.itemDurability = itemDurability;
            this.miningSpeed = miningSpeed;
            this.attackDamage = attackDamage;
            this.enchantability = enchantability;
            this.repairIngredient = repairIngredient;
        }

        @Override
        public int getDurability() {
            return this.itemDurability;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return this.miningSpeed;
        }

        @Override
        public float getAttackDamage() {
            return this.attackDamage;
        }

        @Override
        public int getMiningLevel() {
            return this.miningLevel;
        }

        @Override
        public int getEnchantability() {
            return this.enchantability;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient;
        }
    }
}
