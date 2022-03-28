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
import spacefactory.core.item.MacheteItem;
import spacefactory.core.item.UnicutterItem;
import spacefactory.core.recipe.SimpleMachineRecipe;
import spacefactory.features.PotatoBatteryItem;
import spacefactory.features.atomic_reassembler.AtomicReassemblerBlock;
import spacefactory.features.atomic_reassembler.AtomicReassemblerBlockEntity;
import spacefactory.features.atomic_reassembler.AtomicReassemblerRecipe;
import spacefactory.features.atomic_reassembler.AtomicReassemblerScreenHandler;
import spacefactory.features.battery.BatteryBlock;
import spacefactory.features.battery.BatteryBlockEntity;
import spacefactory.features.battery.BatteryBoxScreenHandler;
import spacefactory.features.compressor.CompressorBlock;
import spacefactory.features.compressor.CompressorBlockEntity;
import spacefactory.features.compressor.CompressorRecipe;
import spacefactory.features.compressor.CompressorScreenHandler;
import spacefactory.features.conduit.ConduitBlock;
import spacefactory.features.conduit.ConduitBlockEntity;
import spacefactory.features.dragon_egg_siphon.DragonEggSiphonBlock;
import spacefactory.features.dragon_egg_siphon.DragonEggSiphonBlockEntity;
import spacefactory.features.electric_furnace.ElectricFurnaceBlock;
import spacefactory.features.electric_furnace.ElectricFurnaceBlockEntity;
import spacefactory.features.electric_furnace.ElectricFurnaceScreenHandler;
import spacefactory.features.extractor.ExtractorBlock;
import spacefactory.features.extractor.ExtractorBlockEntity;
import spacefactory.features.extractor.ExtractorRecipe;
import spacefactory.features.extractor.ExtractorScreenHandler;
import spacefactory.features.generator.GeneratorBlock;
import spacefactory.features.generator.GeneratorBlockEntity;
import spacefactory.features.generator.GeneratorScreenHandler;
import spacefactory.features.nano_steel.NanoSteelMacheteItem;
import spacefactory.features.nano_steel.NanoSteelUnicutterItem;
import spacefactory.features.pulverizer.PulverizerBlock;
import spacefactory.features.pulverizer.PulverizerBlockEntity;
import spacefactory.features.pulverizer.PulverizerRecipe;
import spacefactory.features.pulverizer.PulverizerScreenHandler;
import spacefactory.features.rubber_tree.AliveRubberLogBlock;
import spacefactory.features.rubber_tree.RubberFoliagePlacer;
import spacefactory.features.rubber_tree.RubberSaplingBlock;
import spacefactory.features.solar_panel.SolarPanelBlock;
import spacefactory.features.solar_panel.SolarPanelBlockEntity;
import spacefactory.features.solar_panel.SolarPanelScreenHandler;
import spacefactory.mixin.BlocksAccessor;
import spacefactory.mixin.FoliagePlacerTypeInvoker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
public class SpaceFactory implements ModInitializer {
	public static final String MOD_ID = "spacefactory";
	public static Config config = new Config();

	static {
		Config.load();
	}

	@Override
	public void onInitialize() {
		Blocks.init();
		BlockEntityTypes.init();
		ScreenHandlerTypes.init();
		Items.init();
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
		BiomeModifications.create(id("overworld_ores")) //
				.add(ModificationPhase.ADDITIONS, BiomeSelectors.foundInOverworld(), context -> {
					if (config.generateOres) {
						RegistryKey<PlacedFeature> tinOre = getKey(BiomePlacedFeatures.TIN_ORE);
						context.getGenerationSettings().addFeature(GenerationStep.Feature.UNDERGROUND_ORES, tinOre);
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

	public static <T> T register(Registry<? super T> registry, String name, T entry) {
		return Registry.register(registry, id(name), entry);
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
		@SerializedName("rubber_tree_tries")
		public int rubberTreesTries = 6;

		@SerializedName("generate_ores")
		public boolean generateOres = true;

		@SerializedName("tin_veins_per_chunk")
		public int tinVeinsPerChunk = 16;
		@SerializedName("tin_vein_size")
		public int tinVeinSize = 8;
		@SerializedName("tin_min_height")
		public int tinMinHeight = 25;
		@SerializedName("tin_max_height")
		public int tinMaxHeight = 80;

		@SerializedName("iridium_veins_per_chunk")
		public int iridiumVeinsPerChunk = 4;
		@SerializedName("iridium_vein_size")
		public int iridiumVeinSize = 4;
		@SerializedName("iridium_min_height")
		public int iridiumMinHeight = 0;
		@SerializedName("iridium_max_height")
		public int iridiumMaxHeight = 64;

		public static void load() {
			Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");

			try {
				if (Files.exists(configPath)) {
					BufferedReader reader = Files.newBufferedReader(configPath);
					config = GSON.fromJson(reader, Config.class);
					reader.close();
				} else {
					write();
				}
			} catch (IOException e) {
				e.printStackTrace();
				config = new Config();
			}
		}

		public static void write() {
			Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");
			try {
				BufferedWriter writer = Files.newBufferedWriter(configPath);
				GSON.toJson(config, writer);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				config = new Config();
			}
		}
	}

	public static class Materials {
		public static final Material MACHINE = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();
	}

	public static class Blocks {
		private static final Material UNMOVABLE_WOOD = new FabricMaterialBuilder(MapColor.OAK_TAN).blocksPistons().burnable().build();

		private static final AbstractBlock.Settings MACHINE_SETTINGS = Settings.of(Materials.MACHINE).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(BlocksAccessor::invokeNever);
		private static final AbstractBlock.Settings ADVANCED_MACHINE_SETTINGS = Settings.of(Materials.MACHINE).strength(10F, 30F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(BlocksAccessor::invokeNever);

		public static final Block RUBBER_LOG = register("rubber_log", new PillarBlock(Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.YELLOW : MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block ALIVE_RUBBER_LOG = register("alive_rubber_log", new AliveRubberLogBlock(Settings.of(UNMOVABLE_WOOD, MapColor.YELLOW).ticksRandomly().strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block RUBBER_WOOD = register("rubber_wood", new PillarBlock(Settings.of(Material.WOOD, MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block STRIPPED_RUBBER_LOG = register("stripped_rubber_log", new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block STRIPPED_RUBBER_WOOD = register("stripped_rubber_wood", new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block RUBBER_LEAVES = register("rubber_leaves", new LeavesBlock(Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(BlocksAccessor::invokeCanSpawnOnLeaves).suffocates(BlocksAccessor::invokeNever).blockVision(BlocksAccessor::invokeNever)));
		public static final Block RUBBER_SAPLING = register("rubber_sapling", new RubberSaplingBlock(Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)));

		public static final Block TIN_ORE = register("tin_ore", new Block(FabricBlockSettings.of(Material.STONE).strength(3F)));
		public static final Block DEEPSLATE_TIN_ORE = register("deepslate_tin_ore", new Block(FabricBlockSettings.of(Material.STONE).strength(4.5F, 3F)));
		public static final Block RAW_TIN_BLOCK = register("raw_tin_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.LIGHT_GRAY).requiresTool().strength(5.0f, 6.0f)));
		public static final Block TIN_BLOCK = register("tin_block", new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL)));


		public static final Block COPPER_WIRE = register("copper_wire", new ConduitBlock(1, 128, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL)));
		public static final Block COPPER_CABLE = register("copper_cable", new ConduitBlock(2, 128, FabricBlockSettings.of(Material.METAL).strength(0.5F)));

		public static final Block GENERATOR = register("generator", new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0)));
		public static final Block ELECTRIC_FURNACE = register("electric_furnace", new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0)));
		public static final Block PULVERIZER = register("pulverizer", new PulverizerBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block COMPRESSOR = register("compressor", new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block EXTRACTOR = register("extractor", new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block SOLAR_PANEL = register("solar_panel", new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE)));

		public static final Block BATTERY_BOX = register("battery_box", new BatteryBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.WOOD)));
		public static final Block COPPER_BUS_BAR = register("copper_bus_bar", new ConduitBlock(3, 1024, FabricBlockSettings.of(Material.METAL).strength(2F, 5F)));
		public static final Block ENERGY_CONDUIT = register("reinforced_energy_conduit", new ConduitBlock(4, 1024, FabricBlockSettings.of(Material.METAL).strength(5F, 20F)));

		public static final Block CRYSTALITE_BLOCK = register("crystalite_block", new Block(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE).luminance(15).strength(5F, 20F).slipperiness(0.98F).sounds(BlockSoundGroup.GLASS).allowsSpawning(BlocksAccessor::invokeNever)));

		public static final Block ATOMIC_REASSEMBLER = register("atomic_reassembler", new AtomicReassemblerBlock(FabricBlockSettings.copyOf(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0))));

		public static final Block NANO_STEEL_BLOCK = register("nano_steel_block", new Block(FabricBlockSettings.of(Material.METAL, MapColor.DARK_AQUA).strength(12F, 20F)));
		public static final Block REINFORCED_STONE = register("reinforced_stone", new Block(FabricBlockSettings.of(Material.STONE, MapColor.DARK_AQUA).strength(10F, 15F)));
		public static final Block REINFORCED_GLASS = register("reinforced_glass", new GlassBlock(FabricBlockSettings.of(Material.GLASS).strength(7F, 10F).nonOpaque().sounds(BlockSoundGroup.GLASS)));

		public static final Block END_STONE_IRIDIUM_ORE = register("end_stone_iridium_ore", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(3F, 9F)));
		public static final Block RAW_IRIDIUM_BLOCK = register("raw_iridium_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).requiresTool().strength(7.0f, 10.0f)));
		public static final Block IRIDIUM_BLOCK = register("iridium_block", new Block(FabricBlockSettings.of(Material.METAL).strength(5F, 20F).allowsSpawning(BlocksAccessor::invokeNever)));

		public static final Block DRAGON_ENERGY_ABSORBER = register("dragon_energy_absorber", new DragonEggSiphonBlock(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0)));

		public static <T extends Block> T register(String name, T entry) {
			return Registry.register(Registry.BLOCK, id(name), entry);
		}

		public static void init() {
			for (Block block : Registry.BLOCK) {
				if (Registry.BLOCK.getId(block).getNamespace().equals(MOD_ID)) {
					for (BlockState state : block.getStateManager().getStates()) {
						Block.STATE_IDS.add(state);
					}
				}
			}

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

		public static final Item COPPER_NUGGET = register("copper_nugget", new Item(settings()));

		public static final Item STONE_DUST = register("stone_dust", new Item(settings()));
		public static final Item COAL_DUST = register("coal_dust", new Item(settings()));
		public static final Item COPPER_DUST = register("copper_dust", new Item(settings()));
		public static final Item IRON_DUST = register("iron_dust", new Item(settings()));
		public static final Item GOLD_DUST = register("gold_dust", new Item(settings()));
		public static final Item DIAMOND_DUST = register("diamond_dust", new Item(settings()));

		public static final Item RUBBER_LOG = register("rubber_log", new BlockItem(Blocks.RUBBER_LOG, settings()));
		public static final Item STRIPPED_RUBBER_LOG = register("stripped_rubber_log", new BlockItem(Blocks.STRIPPED_RUBBER_LOG, settings()));
		public static final Item RUBBER_WOOD = register("rubber_wood", new BlockItem(Blocks.RUBBER_WOOD, settings()));
		public static final Item STRIPPED_RUBBER_WOOD = register("stripped_rubber_wood", new BlockItem(Blocks.STRIPPED_RUBBER_WOOD, settings()));
		public static final Item RUBBER_LEAVES = register("rubber_leaves", new BlockItem(Blocks.RUBBER_LEAVES, settings()));
		public static final Item RUBBER_SAPLING = register("rubber_sapling", new BlockItem(Blocks.RUBBER_SAPLING, settings()));
		public static final Item RAW_RUBBER = register("raw_rubber", new Item(settings()));
		public static final Item RUBBER = register("rubber", new Item(settings()));

		public static final Item TIN_ORE = register("tin_ore", new BlockItem(Blocks.TIN_ORE, settings()));
		public static final Item DEEPSLATE_TIN_ORE = register("deepslate_tin_ore", new BlockItem(Blocks.DEEPSLATE_TIN_ORE, settings()));
		public static final Item RAW_TIN_BLOCK = register("raw_tin_block", new BlockItem(Blocks.RAW_TIN_BLOCK, settings()));
		public static final Item TIN_BLOCK = register("tin_block", new BlockItem(Blocks.TIN_BLOCK, settings()));
		public static final Item RAW_TIN = register("raw_tin", new Item(settings()));
		public static final Item TIN_INGOT = register("tin_ingot", new Item(settings()));
		public static final Item TIN_NUGGET = register("tin_nugget", new Item(settings()));
		public static final Item TIN_DUST = register("tin_dust", new Item(settings()));

		public static final Item COPPER_WIRE = register("copper_wire", new BlockItem(Blocks.COPPER_WIRE, settings()));
		public static final Item COPPER_CABLE = register("copper_cable", new BlockItem(Blocks.COPPER_CABLE, settings()));
		public static final Item CIRCUIT = register("circuit", new Item(settings()));

		public static final Item GENERATOR = register("generator", new BlockItem(Blocks.GENERATOR, settings()));

		public static final Item ELECTRIC_FURNACE = register("electric_furnace", new BlockItem(Blocks.ELECTRIC_FURNACE, settings()));
		public static final Item PULVERIZER = register("pulverizer", new BlockItem(Blocks.PULVERIZER, settings()));
		public static final Item COMPRESSOR = register("compressor", new BlockItem(Blocks.COMPRESSOR, settings()));
		public static final Item EXTRACTOR = register("extractor", new BlockItem(Blocks.EXTRACTOR, settings()));

		public static final Item NETHER_QUARTZ_DUST = register("nether_quartz_dust", new Item(settings()));
		public static final Item SILICON_INGOT = register("silicon_ingot", new Item(settings()));
		public static final Item SOLAR_PANEL = register("solar_panel", new BlockItem(Blocks.SOLAR_PANEL, settings()));

		public static final Item BATTERY_BOX = register("battery_box", new BlockItem(Blocks.BATTERY_BOX, settings()));
		public static final Item COPPER_BUS_BAR = register("copper_bus_bar", new BlockItem(Blocks.COPPER_BUS_BAR, settings()));
		public static final Item REINFORCED_ENERGY_CONDUIT = register("reinforced_energy_conduit", new BlockItem(Blocks.ENERGY_CONDUIT, settings()));

		public static final Item REFINED_IRON_INGOT = register("refined_iron_ingot", new Item(settings().rarity(Rarity.RARE)));
		public static final Item REFINED_IRON_DUST = register("refined_iron_dust", new Item(settings()));
		public static final Item REFINED_IRON_MACHETE = register("refined_iron_machete", new MacheteItem(ToolMaterials.REFINED_IRON, 2, -2.2F, settings()));
		public static final Item REFINED_IRON_UNICUTTER = register("refined_iron_unicutter", new UnicutterItem(ToolMaterials.REFINED_IRON, -1, -1F, settings().maxDamage(250)));

		public static final Item CAMOUFLAGE_CLOTH = register("camouflage_cloth", new Item(settings()));
		public static final Item FLAK_VEST = register("flak_vest", new ArmorItem(ArmorMaterials.FLAK, EquipmentSlot.CHEST, settings().rarity(Rarity.UNCOMMON)));

		public static final Item CRYSTALITE_BLOCK = register("crystalite_block", new BlockItem(Blocks.CRYSTALITE_BLOCK, settings().rarity(Rarity.RARE)));
		public static final Item CRYSTALITE_MATRIX = register("crystalite_matrix", new Item(settings().rarity(Rarity.RARE).maxCount(16)));
		public static final Item CRYSTALITE_DUST = register("crystalite_dust", new Item(settings().rarity(Rarity.RARE)));
		public static final Item RAW_CRYSTALITE_DUST = register("raw_crystalite_dust", new Item(settings()));
		public static final Item QUANTUM_CIRCUIT = register("quantum_circuit", new Item(settings().rarity(Rarity.UNCOMMON)));

		public static final Item ATOMIC_REASSEMBLER = register("atomic_reassembler", new BlockItem(Blocks.ATOMIC_REASSEMBLER, settings().rarity(Rarity.RARE)));

		public static final Item NANO_STEEL_BLOCK = register("nano_steel_block", new BlockItem(Blocks.NANO_STEEL_BLOCK, settings().rarity(Rarity.RARE)));
		public static final Item NANO_STEEL_INGOT = register("nano_steel_ingot", new Item(settings().rarity(Rarity.RARE)));
		public static final Item NANO_STEEL_MACHETE = register("nano_steel_machete", new NanoSteelMacheteItem(ToolMaterials.NANO_STRUCTURED_STEEL, 2, -2.2F, settings().rarity(Rarity.RARE)));
		public static final Item NANO_STEEL_UNICUTTER = register("nano_steel_unicutter", new NanoSteelUnicutterItem(ToolMaterials.NANO_STRUCTURED_STEEL, -1, -1F, settings().maxDamage(700).rarity(Rarity.RARE)));
		public static final Item REINFORCED_STONE = register("reinforced_stone", new BlockItem(Blocks.REINFORCED_STONE, settings()));
		public static final Item REINFORCED_GLASS = register("reinforced_glass", new BlockItem(Blocks.REINFORCED_GLASS, settings()));

		public static final Item WARP_PRISM = register("warp_prism", new Item(settings().rarity(Rarity.RARE).maxCount(16)));

		public static final Item END_STONE_IRIDIUM_ORE = register("end_stone_iridium_ore", new BlockItem(Blocks.END_STONE_IRIDIUM_ORE, settings().rarity(Rarity.UNCOMMON)));
		public static final Item RAW_IRIDIUM_BLOCK = register("raw_iridium_block", new BlockItem(Blocks.RAW_IRIDIUM_BLOCK, settings().rarity(Rarity.UNCOMMON)));
		public static final Item IRIDIUM_BLOCK = register("iridium_block", new BlockItem(Blocks.IRIDIUM_BLOCK, settings().rarity(Rarity.UNCOMMON)));
		public static final Item RAW_IRIDIUM = register("raw_iridium", new Item(settings().rarity(Rarity.UNCOMMON)));
		public static final Item IRIDIUM_INGOT = register("iridium_ingot", new Item(settings().rarity(Rarity.UNCOMMON)));
		public static final Item IRIDIUM_NUGGET = register("iridium_nugget", new Item(settings().rarity(Rarity.UNCOMMON)));
		public static final Item IRIDIUM_DUST = register("iridium_dust", new Item(settings().rarity(Rarity.UNCOMMON)));
		public static final Item SMALL_IRIDIUM_DUST = register("small_iridium_dust", new Item(settings().rarity(Rarity.UNCOMMON)));
		public static final Item NETHERITE_SCRAP_DUST = register("netherite_scrap_dust", new Item(settings()));


		public static final Item DRAGON_ENERGY_ABSORBER = register("dragon_energy_absorber", new BlockItem(Blocks.DRAGON_ENERGY_ABSORBER, settings().rarity(Rarity.RARE)));

		public static final Item POTATO_BATTERY = register("potato_battery", new PotatoBatteryItem(settings().maxCount(1)));

		public static final TagKey<Item> RUBBERS = TagKey.of(Registry.ITEM_KEY, new Identifier("c:rubbers"));

		private static Item.Settings settings() {
			return new Item.Settings().group(MAIN);
		}

		public static <T extends Item> T register(String name, T entry) {
			return Registry.register(Registry.ITEM, id(name), entry);
		}

		public static void init() {
			FuelRegistry.INSTANCE.add(RUBBERS, 100);

			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(RUBBER_SAPLING, 0.3F);
			ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(RUBBER_LEAVES, 0.3F);
		}
	}

	public static class BlockEntityTypes {
		public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = create("generator", GeneratorBlockEntity::new, Blocks.GENERATOR);
		public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = create("solar_panel", SolarPanelBlockEntity::new, Blocks.SOLAR_PANEL);
		public static final BlockEntityType<DragonEggSiphonBlockEntity> DRAGON_ENERGY_ABSORBER = create("dragon_energy_absorber", DragonEggSiphonBlockEntity::new, Blocks.DRAGON_ENERGY_ABSORBER);

		public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = create("electric_furnace", ElectricFurnaceBlockEntity::new, Blocks.ELECTRIC_FURNACE);
		public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER = create("pulverizer", PulverizerBlockEntity::new, Blocks.PULVERIZER);
		public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = create("compressor", CompressorBlockEntity::new, Blocks.COMPRESSOR);
		public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = create("extractor", ExtractorBlockEntity::new, Blocks.EXTRACTOR);
		public static final BlockEntityType<AtomicReassemblerBlockEntity> MOLECULAR_ASSEMBLER = create("atomic_reassembler", AtomicReassemblerBlockEntity::new, Blocks.ATOMIC_REASSEMBLER);
		public static final BlockEntityType<BatteryBlockEntity> BATTERY_BOX = create("battery_box", BatteryBlockEntity::new, Blocks.BATTERY_BOX);
		public static final BlockEntityType<ConduitBlockEntity> CONDUIT = create("conduit", ConduitBlockEntity::new, Blocks.COPPER_WIRE, Blocks.COPPER_CABLE, Blocks.COPPER_BUS_BAR, Blocks.ENERGY_CONDUIT);

		public static <T extends BlockEntity> BlockEntityType<T> create(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
			BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, blocks).build();
			return Registry.register(Registry.BLOCK_ENTITY_TYPE, id(name), type);
		}

		public static void init() {
		}
	}

	public static class RecipeTypes {
		public static final RecipeType<PulverizerRecipe> PULVERIZING = create("pulverizing");
		public static final RecipeType<CompressorRecipe> COMPRESSING = create("compressing");
		public static final RecipeType<ExtractorRecipe> EXTRACTING = create("extracting");
		public static final RecipeType<AtomicReassemblerRecipe> ATOMIC_REASSEMBLY = create("atomic_reassembly");

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
		public static final RecipeSerializer<PulverizerRecipe> PULVERIZING = register("pulverizing", new SimpleMachineRecipe.Serializer<>(PulverizerRecipe::new, 300));
		public static final RecipeSerializer<CompressorRecipe> COMPRESSING = register("compressing", new SimpleMachineRecipe.Serializer<>(CompressorRecipe::new, 400));
		public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = register("extracting", new SimpleMachineRecipe.Serializer<>(ExtractorRecipe::new, 400));
		public static final RecipeSerializer<AtomicReassemblerRecipe> ATOMIC_REASSEMBLY = register("atomic_reassembly", new AtomicReassemblerRecipe.Serializer(1000));

		public static <T extends RecipeSerializer<?>> T register(String name, T entry) {
			return Registry.register(Registry.RECIPE_SERIALIZER, id(name), entry);
		}

		public static void init() {
		}
	}

	public static class FoliagePlacers {
		public static final FoliagePlacerType<RubberFoliagePlacer> RUBBER = create("rubber_foliage_placer", RubberFoliagePlacer.CODEC);

		public static <T extends FoliagePlacer> FoliagePlacerType<T> create(String name, Codec<T> codec) {
			return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id(name), FoliagePlacerTypeInvoker.create(codec));
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

		private static final OreFeatureConfig TIN_ORE_CONFIG = new OreFeatureConfig(List.of( //
				OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, Blocks.TIN_ORE.getDefaultState()), //
				OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_TIN_ORE.getDefaultState())
		), config.tinVeinSize);

		private static final OreFeatureConfig IRIDIUM_ORE_CONFIG = new OreFeatureConfig(List.of( //
				OreFeatureConfig.createTarget(new BlockStateMatchRuleTest(net.minecraft.block.Blocks.END_STONE.getDefaultState()), Blocks.END_STONE_IRIDIUM_ORE.getDefaultState())), config.iridiumVeinSize);


		public static final ConfiguredFeature<TreeFeatureConfig, ?> RUBBER_TREE = register("rubber_tree", new ConfiguredFeature<>(Feature.TREE, RUBBER_TREE_CONFIG));

		public static final ConfiguredFeature<OreFeatureConfig, ?> TIN_ORE = register("tin_ore", new ConfiguredFeature<>(Feature.ORE, TIN_ORE_CONFIG));
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
						RarityFilterPlacementModifier.of(4), //
						SquarePlacementModifier.of(), //
						net.minecraft.world.gen.feature.PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, //
						BiomePlacementModifier.of())));

		public static final PlacedFeature TIN_ORE = PlacedFeatures.register("tin_ore", new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeatures.TIN_ORE), //
				List.of( //
						CountPlacementModifier.of(config.tinVeinsPerChunk), //
						SquarePlacementModifier.of(), //
						HeightRangePlacementModifier.uniform( //
								YOffset.fixed(config.tinMinHeight), //
								YOffset.fixed(config.tinMaxHeight)), //
						BiomePlacementModifier.of()
				)));
		public static final PlacedFeature IRIDIUM_ORE = PlacedFeatures.register("iridium_ore",
				new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeatures.IRIDIUM_ORE), //
				List.of( //
						CountPlacementModifier.of(config.iridiumVeinsPerChunk), //
						SquarePlacementModifier.of(), //
						HeightRangePlacementModifier.uniform( //
								YOffset.fixed(config.iridiumMinHeight), //
								YOffset.fixed(config.iridiumMaxHeight)), //
						BiomePlacementModifier.of()
				)));

		public static void init() {
		}
	}

	public static class ScreenHandlerTypes {
		public static final ScreenHandlerType<GeneratorScreenHandler> GENERATOR = register("generator", new ScreenHandlerType<>(GeneratorScreenHandler::new));
		public static final ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL = register("solar_panel", new ScreenHandlerType<>(SolarPanelScreenHandler::new));
		public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE = register("electric_furnace", new ScreenHandlerType<>(ElectricFurnaceScreenHandler::new));
		public static final ScreenHandlerType<PulverizerScreenHandler> PULVERIZER = register("pulverizer", new ScreenHandlerType<>(PulverizerScreenHandler::new));
		public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR = register("compressor", new ScreenHandlerType<>(CompressorScreenHandler::new));
		public static final ScreenHandlerType<AtomicReassemblerScreenHandler> MOLECULAR_ASSEMBLER = register("atomic_reassembler", new ScreenHandlerType<>(AtomicReassemblerScreenHandler::new));
		public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR = register("extractor", new ScreenHandlerType<>(ExtractorScreenHandler::new));
		public static final ScreenHandlerType<BatteryBoxScreenHandler> BATTERY_BOX = register("battery_box", new ScreenHandlerType<>(BatteryBoxScreenHandler::new));

		private static void init() {
		}

		public static <T extends ScreenHandlerType<?>> T register(String name, T entry) {
			return Registry.register(Registry.SCREEN_HANDLER, id(name), entry);
		}
	}

	public static class Constants {
		public static final int GENERATOR_OUTPUT = 10;
		public static final int GENERATOR_CONSUMPTION = 4;

		public static final float SOLAR_PANEL_OUTPUT = 1;

		public static final int DRAGON_EGG_SIPHON_OUTPUT = 256;

		public static final int VANOVOLTAIC_CELL_GENERATION = 10;
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
		REFINED_IRON(2, 750, 6.5F, 3F, 8, Ingredient.ofItems(Items.REFINED_IRON_INGOT)),
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
