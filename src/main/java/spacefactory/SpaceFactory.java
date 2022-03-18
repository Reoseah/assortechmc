package spacefactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.Codec;
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
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import spacefactory.api.EnergyTier;
import spacefactory.block.ConduitBlock;
import spacefactory.block.*;
import spacefactory.block.entity.*;
import spacefactory.block.entity.conduit.ConduitBlockEntity;
import spacefactory.feature.RubberFoliagePlacer;
import spacefactory.item.*;
import spacefactory.item.material.AssortechArmorMaterials;
import spacefactory.item.material.AssortechToolMaterials;
import spacefactory.mixin.BlocksAccessor;
import spacefactory.mixin.FoliagePlacerTypeInvoker;
import spacefactory.recipe.*;
import spacefactory.screen.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.minecraft.block.Blocks.JUNGLE_LEAVES;

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
		ConfiguredFeaturesDependent.init();
		PlacedFeaturesDependent.init();

		BiomeModifications.create(id("features")) //
				.add(ModificationPhase.ADDITIONS, BiomeSelectors.categories(Biome.Category.SWAMP, Biome.Category.JUNGLE, Biome.Category.FOREST, Biome.Category.RIVER), (context) -> {
					if (config.generateRubberTrees) {
						RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> key = BuiltinRegistries.PLACED_FEATURE.getKey(PlacedFeaturesDependent.RUBBER_TREE_PATCH).orElseThrow();
						context.getGenerationSettings().addFeature(GenerationStep.Feature.VEGETAL_DECORATION, key);
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

	public static <T> RegistryEntry<T> getEntry(Registry<T> registry, String name) {
		return registry.entryOf(RegistryKey.of(registry.getKey(), id(name)));
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
		public static final Block STRIPPED_RUBBER_WOOD = register("stripped_rubber_wood", new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD)));
		public static final Block RUBBER_LEAVES = register("rubber_leaves", new LeavesBlock(Settings.copy(JUNGLE_LEAVES)));
		public static final Block RUBBER_SAPLING = register("rubber_sapling", new RubberSaplingBlock(Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS)));
		public static final Block STRIPPED_RUBBER_LOG = register("stripped_rubber_log", new PillarBlock(Settings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD)));

		public static final Block TIN_ORE = register("tin_ore", new Block(FabricBlockSettings.of(Material.STONE).strength(3F)));
		public static final Block DEEPSLATE_TIN_ORE = register("deepslate_tin_ore", new Block(FabricBlockSettings.of(Material.STONE).strength(4.5F, 3F)));
		public static final Block RAW_TIN_BLOCK = register("raw_tin_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.LIGHT_GRAY).requiresTool().strength(5.0f, 6.0f)));
		public static final Block TIN_BLOCK = register("tin_block", new Block(FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL)));
		public static final Block BRONZE_BLOCK = register("bronze_block", new Block(FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).strength(3F, 6F).sounds(BlockSoundGroup.METAL)));

		public static final Block COPPER_WIRE = register("copper_wire", new ConduitBlock(1, EnergyTier.MEDIUM, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL)));
		public static final Block COPPER_CABLE = register("copper_cable", new ConduitBlock(2, EnergyTier.MEDIUM, FabricBlockSettings.of(Material.METAL).strength(0.5F)));

		public static final Block GENERATOR = register("generator", new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0)));
		public static final Block SOLAR_PANEL = register("solar_panel", new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE)));
		public static final Block ELECTRIC_FURNACE = register("electric_furnace", new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 14 : 0)));
		public static final Block PULVERIZER = register("pulverizer", new PulverizerBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block COMPRESSOR = register("compressor", new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block EXTRACTOR = register("extractor", new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 12 : 0)));
		public static final Block BATTERY_BOX = register("battery_box", new BatteryBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.WOOD)));
		public static final Block COPPER_BUS_BAR = register("copper_bus_bar", new ConduitBlock(3, EnergyTier.EXTREME, FabricBlockSettings.of(Material.METAL).strength(2F, 5F)));
		public static final Block ENERGY_CONDUIT = register("reinforced_energy_conduit", new ConduitBlock(4, EnergyTier.EXTREME, FabricBlockSettings.of(Material.METAL).strength(5F, 20F)));

		public static final Block CRYSTALITE_BLOCK = register("crystalite_block", new Block(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE).luminance(15).strength(5F, 20F).sounds(BlockSoundGroup.GLASS).allowsSpawning(BlocksAccessor::invokeNever)));
		public static final Block END_STONE_IRIDIUM_ORE = register("end_stone_iridium_ore", new Block(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(3F, 9F)));
		public static final Block RAW_IRIDIUM_BLOCK = register("raw_iridium_block", new Block(FabricBlockSettings.of(Material.STONE, MapColor.WHITE).requiresTool().strength(7.0f, 10.0f)));
		public static final Block IRIDIUM_BLOCK = register("iridium_block", new Block(FabricBlockSettings.of(Material.METAL).strength(5F, 20F).allowsSpawning(BlocksAccessor::invokeNever)));

		public static final Block MOLECULAR_ASSEMBLER = register("molecular_assembler", new MolecularAssemblerBlock(FabricBlockSettings.copyOf(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0))));
		public static final Block DRAGON_EGG_SIPHON = register("dragon_egg_siphon", new DragonEggSiphonBlock(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0)));

		public static <T extends Block> T register(String name, T entry) {
			return Registry.register(Registry.BLOCK, id(name), entry);
		}

		public static void init() {
			FlammableBlockRegistry.getDefaultInstance().add(Blocks.RUBBER_LOG, 5, 5);
			FlammableBlockRegistry.getDefaultInstance().add(Blocks.ALIVE_RUBBER_LOG, 5, 5);
			FlammableBlockRegistry.getDefaultInstance().add(Blocks.STRIPPED_RUBBER_LOG, 5, 5);
			FlammableBlockRegistry.getDefaultInstance().add(Blocks.RUBBER_LEAVES, 30, 60);

			StrippableBlockRegistry.register(Blocks.RUBBER_LOG, Blocks.STRIPPED_RUBBER_LOG);
			StrippableBlockRegistry.register(Blocks.RUBBER_WOOD, Blocks.STRIPPED_RUBBER_WOOD);
		}
	}

	public static class Items {
		public static final ItemGroup MAIN = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(Blocks.SOLAR_PANEL));

		public static final Item COPPER_NUGGET = register("copper_nugget", new Item(settings()));

		public static final Item STONE_DUST = register("stone_dust", new Item(settings()));
		public static final Item COAL_DUST = register("coal_dust", new Item(settings()));
		public static final Item COPPER_DUST = register("copper_dust", new Item(settings()));
		public static final Item IRON_DUST = register("iron_dust", new Item(settings()));
		public static final Item GOLD_DUST = register("gold_dust", new Item(settings()));
		public static final Item DIAMOND_DUST = register("diamond_dust", new Item(settings()));
		public static final Item NETHER_QUARTZ_DUST = register("nether_quartz_dust", new Item(settings()));

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

		public static final Item BRONZE_BLOCK = register("bronze_block", new BlockItem(Blocks.BRONZE_BLOCK, settings()));
		public static final Item BRONZE_INGOT = register("bronze_ingot", new Item(settings()));
		public static final Item BRONZE_NUGGET = register("bronze_nugget", new Item(settings()));
		public static final Item BRONZE_DUST = register("bronze_dust", new Item(settings()));
		public static final Item SILICON_INGOT = register("silicon_ingot", new Item(settings()));

		public static final Item COPPER_WIRE = register("copper_wire", new BlockItem(Blocks.COPPER_WIRE, settings()));
		public static final Item COPPER_CABLE = register("copper_cable", new BlockItem(Blocks.COPPER_CABLE, settings()));
		public static final Item CIRCUIT = register("circuit", new Item(settings()));

		public static final Item GENERATOR = register("generator", new BlockItem(Blocks.GENERATOR, settings()));
		public static final Item SOLAR_PANEL = register("solar_panel", new BlockItem(Blocks.SOLAR_PANEL, settings()));

		public static final Item ELECTRIC_FURNACE = register("electric_furnace", new BlockItem(Blocks.ELECTRIC_FURNACE, settings()));
		public static final Item PULVERIZER = register("pulverizer", new BlockItem(Blocks.PULVERIZER, settings()));
		public static final Item COMPRESSOR = register("compressor", new BlockItem(Blocks.COMPRESSOR, settings()));
		public static final Item EXTRACTOR = register("extractor", new BlockItem(Blocks.EXTRACTOR, settings()));

		public static final Item BATTERY_BOX = register("battery_box", new BlockItem(Blocks.BATTERY_BOX, settings()));
		public static final Item COPPER_BUS_BAR = register("copper_bus_bar", new BlockItem(Blocks.COPPER_BUS_BAR, settings()));
		public static final Item REINFORCED_ENERGY_CONDUIT = register("reinforced_energy_conduit", new BlockItem(Blocks.ENERGY_CONDUIT, settings()));


		public static final Item REFINED_IRON_INGOT = register("refined_iron_ingot", new Item(settings().rarity(Rarity.RARE)));
		public static final Item REFINED_IRON_DUST = register("refined_iron_dust", new Item(settings()));
		public static final Item SMALL_REFINED_IRON_DUST = register("small_refined_iron_dust", new Item(settings()));

		public static final Item RAW_CRYSTALITE_DUST = register("raw_crystalite_dust", new Item(settings()));
		public static final Item CRYSTALITE = register("crystalite", new Item(settings().rarity(Rarity.RARE)));
		public static final Item QUANTUM_CIRCUIT = register("quantum_circuit", new Item(settings().rarity(Rarity.UNCOMMON)));

		public static final Item END_STONE_IRIDIUM_ORE = register("end_stone_iridium_ore", new BlockItem(Blocks.END_STONE_IRIDIUM_ORE, settings()));
		public static final Item RAW_IRIDIUM_BLOCK = register("raw_iridium_block", new BlockItem(Blocks.RAW_IRIDIUM_BLOCK, settings().rarity(Rarity.EPIC)));
		public static final Item IRIDIUM_BLOCK = register("iridium_block", new BlockItem(Blocks.IRIDIUM_BLOCK, settings().rarity(Rarity.EPIC)));
		public static final Item RAW_IRIDIUM = register("raw_iridium", new Item(settings().rarity(Rarity.EPIC)));
		public static final Item IRIDIUM_INGOT = register("iridium_ingot", new Item(settings().rarity(Rarity.EPIC)));
		public static final Item IRIDIUM_DUST = register("iridium_dust", new Item(settings().rarity(Rarity.RARE)));
		public static final Item SMALL_IRIDIUM_DUST = register("small_iridium_dust", new Item(settings().rarity(Rarity.RARE)));
		public static final Item NETHERITE_SCRAP_DUST = register("netherite_scrap_dust", new Item(settings()));

		public static final Item MOLECULAR_ASSEMBLER = register("molecular_assembler", new BlockItem(Blocks.MOLECULAR_ASSEMBLER, settings().rarity(Rarity.RARE)));
		public static final Item CRYSTALITE_BLOCK = register("crystalite_block", new BlockItem(Blocks.CRYSTALITE_BLOCK, settings().rarity(Rarity.UNCOMMON)));
		public static final Item CRYSTALITE_MATRIX = register("crystalite_matrix", new Item(settings().rarity(Rarity.RARE).maxCount(16)));
		public static final Item WARP_PRISM = register("warp_prism", new Item(settings().rarity(Rarity.RARE).maxCount(16)));
		public static final Item DRAGON_EGG_SIPHON = register("dragon_egg_siphon", new BlockItem(Blocks.DRAGON_EGG_SIPHON, settings().rarity(Rarity.EPIC)));


		public static final Item ARACHNOLACTAM = register("arachnolactam", new Item(settings()));
		public static final Item HYPERSTRAND = register("hyperstrand", new Item(settings().rarity(Rarity.RARE)));
		public static final Item HYPERWEAVE = register("hyperweave", new Item(settings().rarity(Rarity.RARE)));
		public static final Item ENERGY_CRYSTAL = register("energy_crystal", new EnergyCrystalItem(settings().maxCount(1).rarity(Rarity.RARE)));
		public static final Item VANOVOLTAIC_CELL = register("vanovoltaic_cell", new VanovoltaicCellItem(settings().maxCount(1).rarity(Rarity.EPIC)));


		public static final Item BRONZE_SWORD = register("bronze_sword", new SwordItem(AssortechToolMaterials.BRONZE, 3, -2.4F, settings()));
		public static final Item BRONZE_SHOVEL = register("bronze_shovel", new ShovelItem(AssortechToolMaterials.BRONZE, 1.5F, -3.0F, settings()));
		public static final Item BRONZE_PICKAXE = register("bronze_pickaxe", new AccessiblePickaxeItem(AssortechToolMaterials.BRONZE, 1, -2.8F, settings()));
		public static final Item BRONZE_AXE = register("bronze_axe", new AccessibleAxeItem(AssortechToolMaterials.BRONZE, 6, -3.1F, settings()));
		public static final Item BRONZE_HOE = register("bronze_hoe", new AccessibleAxeItem(AssortechToolMaterials.BRONZE, -2, -1.0F, settings()));
		public static final Item BRONZE_HELMET = register("bronze_helmet", new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.HEAD, settings()));
		public static final Item BRONZE_CHESTPLATE = register("bronze_chestplate", new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.CHEST, settings()));
		public static final Item BRONZE_LEGGINGS = register("bronze_leggings", new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.LEGS, settings()));
		public static final Item BRONZE_BOOTS = register("bronze_boots", new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.FEET, settings()));
		public static final Item WIRE_CUTTERS = register("wire_cutters", new WireCuttersItem(settings().maxDamage(256)));
		public static final Item POTATO_BATTERY = register("potato_battery", new PotatoBatteryItem(settings().maxCount(1)));

		public static final TagKey<Item> RUBBERS = TagKey.of(Registry.ITEM_KEY, new Identifier("c:rubbers"));
		public static final TagKey<Item> BRONZE_INGOTS = TagKey.of(Registry.ITEM_KEY, new Identifier("c:bronze_ingots"));

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
		public static final BlockEntityType<DragonEggSiphonBlockEntity> DRAGON_EGG_SIPHON = create("dragon_egg_siphon", DragonEggSiphonBlockEntity::new, Blocks.DRAGON_EGG_SIPHON);

		public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = create("electric_furnace", ElectricFurnaceBlockEntity::new, Blocks.ELECTRIC_FURNACE);
		public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER = create("pulverizer", PulverizerBlockEntity::new, Blocks.PULVERIZER);
		public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = create("compressor", CompressorBlockEntity::new, Blocks.COMPRESSOR);
		public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = create("extractor", ExtractorBlockEntity::new, Blocks.EXTRACTOR);
		public static final BlockEntityType<MolecularAssemblerBlockEntity> MOLECULAR_ASSEMBLER = create("molecular_assembler", MolecularAssemblerBlockEntity::new, Blocks.MOLECULAR_ASSEMBLER);
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
		public static final RecipeType<EmptyRecipe> EMPTY = create("empty");
		public static final RecipeType<PulverizerRecipe> PULVERIZING = create("pulverizing");
		public static final RecipeType<CompressorRecipe> COMPRESSING = create("compressing");
		public static final RecipeType<ExtractorRecipe> EXTRACTING = create("extracting");
		public static final RecipeType<MolecularAssemblerRecipe> MOLECULAR_ASSEMBLY = create("molecular_assembly");

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
		public static final RecipeSerializer<EmptyRecipe> EMPTY = register("empty", new EmptyRecipe.Serializer());
		public static final RecipeSerializer<?> CONDITIONAL = register("conditional", new ConditionalRecipeSerializer());
		public static final RecipeSerializer<PulverizerRecipe> PULVERIZING = register("pulverizing", new SimpleMachineRecipe.Serializer<>(PulverizerRecipe::new, 300));
		public static final RecipeSerializer<CompressorRecipe> COMPRESSING = register("compressing", new SimpleMachineRecipe.Serializer<>(CompressorRecipe::new, 400));
		public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = register("extracting", new SimpleMachineRecipe.Serializer<>(ExtractorRecipe::new, 400));
		public static final RecipeSerializer<MolecularAssemblerRecipe> MOLECULAR_ASSEMBLY = register("molecular_assembly", new MolecularAssemblerRecipe.Serializer(1000));

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

	public static class SFFeatures {

	}

	public static class ConfiguredFeatures {
		private static final BlockStateProvider RUBBER_LOGS = SimpleBlockStateProvider.of(Blocks.ALIVE_RUBBER_LOG);
		private static final BlockStateProvider RUBBER_LEAVES = SimpleBlockStateProvider.of(Blocks.RUBBER_LEAVES);

		public static final ConfiguredFeature<TreeFeatureConfig, ?> RUBBER_TREE = register("rubber_tree", new ConfiguredFeature<>(Feature.TREE, new TreeFeatureConfig.Builder(RUBBER_LOGS, new StraightTrunkPlacer(5, 2, 0), RUBBER_LEAVES, new RubberFoliagePlacer(UniformIntProvider.create(2, 2), UniformIntProvider.create(1, 1), 5), new TwoLayersFeatureSize(1, 0, 1)).build()));

		public static <T extends ConfiguredFeature<?, ?>> T register(String name, T entry) {
			return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id(name), entry);
		}

		public static void init() {
		}
	}

	public static class PlacedFeatures {
		public static final net.minecraft.world.gen.feature.PlacedFeature RUBBER_TREE = register("rubber_tree", new net.minecraft.world.gen.feature.PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeatures.RUBBER_TREE), List.of(net.minecraft.world.gen.feature.PlacedFeatures.wouldSurvive(Blocks.RUBBER_SAPLING))));

		public static <T extends net.minecraft.world.gen.feature.PlacedFeature> T register(String name, T entry) {
			return Registry.register(BuiltinRegistries.PLACED_FEATURE, id(name), entry);
		}

		public static void init() {
		}
	}

	public static class ConfiguredFeaturesDependent {
		public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> RUBBER_TREE_PATCH = ConfiguredFeatures.register("rubber_tree_patch", //
				new ConfiguredFeature<>(Feature.RANDOM_PATCH, //
						net.minecraft.world.gen.feature.ConfiguredFeatures.createRandomPatchFeatureConfig(6, getEntry(BuiltinRegistries.PLACED_FEATURE, PlacedFeatures.RUBBER_TREE))));

		public static void init() {
		}
	}

	public static class PlacedFeaturesDependent {
		public static final net.minecraft.world.gen.feature.PlacedFeature RUBBER_TREE_PATCH = PlacedFeatures.register("rubber_tree_patch", new net.minecraft.world.gen.feature.PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, ConfiguredFeaturesDependent.RUBBER_TREE_PATCH), //
				List.of( //
						RarityFilterPlacementModifier.of(4), //
						SquarePlacementModifier.of(), //
						net.minecraft.world.gen.feature.PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, //
						BiomePlacementModifier.of())));

		public static void init() {
		}
	}

	public static class ScreenHandlerTypes {
		public static final ScreenHandlerType<GeneratorScreenHandler> GENERATOR = ScreenHandlerRegistry.registerSimple(id("generator"), GeneratorScreenHandler::new);
		public static final ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL = ScreenHandlerRegistry.registerSimple(id("solar_panel"), SolarPanelScreenHandler::new);
		public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE = ScreenHandlerRegistry.registerSimple(id("electric_furnace"), ElectricFurnaceScreenHandler::new);
		public static final ScreenHandlerType<PulverizerScreenHandler> PULVERIZER = ScreenHandlerRegistry.registerSimple(id("pulverizer"), PulverizerScreenHandler::new);
		public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR = ScreenHandlerRegistry.registerSimple(id("compressor"), CompressorScreenHandler::new);
		public static final ScreenHandlerType<MolecularAssemblerScreenHandler> MOLECULAR_ASSEMBLER = ScreenHandlerRegistry.registerSimple(id("molecular_assembler"), MolecularAssemblerScreenHandler::new);
		public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR = ScreenHandlerRegistry.registerSimple(id("extractor"), ExtractorScreenHandler::new);
		public static final ScreenHandlerType<BatteryBoxScreenHandler> BATTERY_BOX = ScreenHandlerRegistry.registerSimple(id("battery_box"), BatteryBoxScreenHandler::new);

		private static void init() {
			// just initializes static fields
		}
	}

	public static class Config {
		@SerializedName("preferred_namespaces")
		public List<String> preferredNamespaces = Lists.newArrayList("minecraft", "spacefactory");

		@SerializedName("generate_rubber_trees")
		public boolean generateRubberTrees = true;

		public void reset() {
			this.preferredNamespaces = Lists.newArrayList("minecraft", "spacefactory");
			this.generateRubberTrees = true;
		}

		public static void load() {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");

			try {
				if (Files.exists(configPath)) {
					BufferedReader reader = Files.newBufferedReader(configPath);
					config = gson.fromJson(reader, Config.class);
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
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");
			try {
				BufferedWriter writer = Files.newBufferedWriter(configPath);
				gson.toJson(config, writer);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				config = new Config();
			}
		}
	}

	public static class Constants {
		public static final int GENERATOR_OUTPUT = 10;
		public static final int GENERATOR_CONSUMPTION = 4;

		public static final float SOLAR_PANEL_OUTPUT = 1;

		public static final int DRAGON_EGG_SYPHON_OUTPUT = 256;

		public static final int VANOVOLTAIC_CELL_GENERATION = 10;
	}
}
