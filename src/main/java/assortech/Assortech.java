package assortech;

import assortech.block.*;
import assortech.block.entity.*;
import assortech.feature.RubberFoliagePlacer;
import assortech.item.*;
import assortech.item.material.AssortechArmorMaterials;
import assortech.item.material.AssortechToolMaterials;
import assortech.mixin.FoliagePlacerTypeInvoker;
import assortech.recipe.*;
import assortech.screen.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import team.reborn.energy.api.EnergyStorage;

public class Assortech implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, id("rubber_log"), AtBlocks.RUBBER_LOG);
        Registry.register(Registry.BLOCK, id("resin_rubber_log"), AtBlocks.RESIN_RUBBER_LOG);
        // TODO stripped rubber log, rubber wood, stripped rubber wood
        Registry.register(Registry.BLOCK, id("rubber_leaves"), AtBlocks.RUBBER_LEAVES);
        Registry.register(Registry.BLOCK, id("rubber_sapling"), AtBlocks.RUBBER_SAPLING);
        Registry.register(Registry.BLOCK, id("tin_ore"), AtBlocks.TIN_ORE);
        Registry.register(Registry.BLOCK, id("deepslate_tin_ore"), AtBlocks.DEEPSLATE_TIN_ORE);
        Registry.register(Registry.BLOCK, id("tin_block"), AtBlocks.TIN_BLOCK);
        Registry.register(Registry.BLOCK, id("bronze_block"), AtBlocks.BRONZE_BLOCK);
        Registry.register(Registry.BLOCK, id("machine"), AtBlocks.MACHINE);
        Registry.register(Registry.BLOCK, id("advanced_machine"), AtBlocks.ADVANCED_MACHINE);
        Registry.register(Registry.BLOCK, id("alloy_smeltery"), AtBlocks.ALLOY_SMELTERY);
        Registry.register(Registry.BLOCK, id("generator"), AtBlocks.GENERATOR);
        Registry.register(Registry.BLOCK, id("solar_panel"), AtBlocks.SOLAR_PANEL);
        Registry.register(Registry.BLOCK, id("electric_furnace"), AtBlocks.ELECTRIC_FURNACE);
        Registry.register(Registry.BLOCK, id("macerator"), AtBlocks.MACERATOR);
        Registry.register(Registry.BLOCK, id("compressor"), AtBlocks.COMPRESSOR);
        Registry.register(Registry.BLOCK, id("extractor"), AtBlocks.EXTRACTOR);
        Registry.register(Registry.BLOCK, id("molecular_reconstructor"), AtBlocks.MOLECULAR_RECONSTRUCTOR);
        Registry.register(Registry.BLOCK, id("battery_box"), AtBlocks.BATTERY_BOX);
        Registry.register(Registry.BLOCK, id("copper_wire"), AtBlocks.COPPER_WIRE);
        Registry.register(Registry.BLOCK, id("copper_cable"), AtBlocks.COPPER_CABLE);

        FlammableBlockRegistry.getDefaultInstance().add(AtBlocks.RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(AtBlocks.RESIN_RUBBER_LOG, 5, 5);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("generator"), AtBlockEntityTypes.GENERATOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("solar_panel"), AtBlockEntityTypes.SOLAR_PANEL);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("electric_furnace"), AtBlockEntityTypes.ELECTRIC_FURNACE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("macerator"), AtBlockEntityTypes.MACERATOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("compressor"), AtBlockEntityTypes.COMPRESSOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("extractor"), AtBlockEntityTypes.EXTRACTOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("battery_box"), AtBlockEntityTypes.BATTERY_BOX);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("cable"), AtBlockEntityTypes.CABLE);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AtBlockEntityTypes.GENERATOR);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> SolarPanelBlockEntity.ENERGY, AtBlockEntityTypes.SOLAR_PANEL);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AtBlockEntityTypes.ELECTRIC_FURNACE);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AtBlockEntityTypes.MACERATOR);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AtBlockEntityTypes.COMPRESSOR);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AtBlockEntityTypes.EXTRACTOR);
        EnergyStorage.SIDED.registerForBlockEntity(BatteryBoxBlockEntity::getEnergyHandler, AtBlockEntityTypes.BATTERY_BOX);
        EnergyStorage.SIDED.registerForBlockEntity(CableBlockEntity::getEnergyHandler, AtBlockEntityTypes.CABLE);

        Registry.register(Registry.ITEM, id("rubber_log"), new BlockItem(AtBlocks.RUBBER_LOG, AtItems.settings()));
        Registry.register(Registry.ITEM, id("rubber_leaves"), new BlockItem(AtBlocks.RUBBER_LEAVES, AtItems.settings()));
        Registry.register(Registry.ITEM, id("rubber_sapling"), new BlockItem(AtBlocks.RUBBER_SAPLING, AtItems.settings()));
        Registry.register(Registry.ITEM, id("tin_ore"), new BlockItem(AtBlocks.TIN_ORE, AtItems.settings()));
        Registry.register(Registry.ITEM, id("deepslate_tin_ore"), new BlockItem(AtBlocks.DEEPSLATE_TIN_ORE, AtItems.settings()));
        Registry.register(Registry.ITEM, id("tin_block"), new BlockItem(AtBlocks.TIN_BLOCK, AtItems.settings()));
        Registry.register(Registry.ITEM, id("bronze_block"), new BlockItem(AtBlocks.BRONZE_BLOCK, AtItems.settings()));
        Registry.register(Registry.ITEM, id("machine"), new BlockItem(AtBlocks.MACHINE, AtItems.settings()));
        Registry.register(Registry.ITEM, id("advanced_machine"), new BlockItem(AtBlocks.ADVANCED_MACHINE, AtItems.settings().rarity(Rarity.RARE)));
        Registry.register(Registry.ITEM, id("alloy_smeltery"), new BlockItem(AtBlocks.ALLOY_SMELTERY, AtItems.settings().group(null)));
        Registry.register(Registry.ITEM, id("generator"), new BlockItem(AtBlocks.GENERATOR, AtItems.settings()));
        Registry.register(Registry.ITEM, id("solar_panel"), new BlockItem(AtBlocks.SOLAR_PANEL, AtItems.settings()));
        Registry.register(Registry.ITEM, id("electric_furnace"), new BlockItem(AtBlocks.ELECTRIC_FURNACE, AtItems.settings()));
        Registry.register(Registry.ITEM, id("macerator"), new BlockItem(AtBlocks.MACERATOR, AtItems.settings()));
        Registry.register(Registry.ITEM, id("compressor"), new BlockItem(AtBlocks.COMPRESSOR, AtItems.settings()));
        Registry.register(Registry.ITEM, id("extractor"), new BlockItem(AtBlocks.EXTRACTOR, AtItems.settings()));
        Registry.register(Registry.ITEM, id("molecular_reconstructor"), new BlockItem(AtBlocks.MOLECULAR_RECONSTRUCTOR, AtItems.settings()));
        Registry.register(Registry.ITEM, id("battery_box"), new BlockItem(AtBlocks.BATTERY_BOX, AtItems.settings()));
        Registry.register(Registry.ITEM, id("copper_wire"), new BlockItem(AtBlocks.COPPER_WIRE, AtItems.settings()));
        Registry.register(Registry.ITEM, id("copper_cable"), new BlockItem(AtBlocks.COPPER_CABLE, AtItems.settings()));

        Registry.register(Registry.ITEM, id("sticky_resin"), AtItems.STICKY_RESIN);
        Registry.register(Registry.ITEM, id("rubber"), AtItems.RUBBER);
        Registry.register(Registry.ITEM, id("raw_tin"), AtItems.RAW_TIN);
        Registry.register(Registry.ITEM, id("tin_ingot"), AtItems.TIN_INGOT);
        Registry.register(Registry.ITEM, id("bronze_ingot"), AtItems.BRONZE_INGOT);
        Registry.register(Registry.ITEM, id("refined_iron_ingot"), AtItems.REFINED_IRON_INGOT);
        Registry.register(Registry.ITEM, id("tin_nugget"), AtItems.TIN_NUGGET);
        Registry.register(Registry.ITEM, id("bronze_nugget"), AtItems.BRONZE_NUGGET);
        Registry.register(Registry.ITEM, id("stone_dust"), AtItems.STONE_DUST);
        Registry.register(Registry.ITEM, id("coal_dust"), AtItems.COAL_DUST);
        Registry.register(Registry.ITEM, id("iron_dust"), AtItems.IRON_DUST);
        Registry.register(Registry.ITEM, id("gold_dust"), AtItems.GOLD_DUST);
        Registry.register(Registry.ITEM, id("diamond_dust"), AtItems.DIAMOND_DUST);
        Registry.register(Registry.ITEM, id("copper_dust"), AtItems.COPPER_DUST);
        Registry.register(Registry.ITEM, id("tin_dust"), AtItems.TIN_DUST);
        Registry.register(Registry.ITEM, id("bronze_dust"), AtItems.BRONZE_DUST);
        Registry.register(Registry.ITEM, id("refined_iron_dust"), AtItems.REFINED_IRON_DUST);
        Registry.register(Registry.ITEM, id("energy_crystal_dust"), AtItems.ENERGY_CRYSTAL_DUST);
        Registry.register(Registry.ITEM, id("circuit"), AtItems.CIRCUIT);
        Registry.register(Registry.ITEM, id("advanced_alloy_compound"), AtItems.ADVANCED_ALLOY_COMPOUND);
        Registry.register(Registry.ITEM, id("advanced_alloy"), AtItems.ADVANCED_ALLOY);
        Registry.register(Registry.ITEM, id("arachnolactam"), AtItems.ARACHNOLACTAM);
        Registry.register(Registry.ITEM, id("hyperstrand"), AtItems.HYPERSRAND);
        Registry.register(Registry.ITEM, id("hyperweave"), AtItems.HYPERWEAVE);

        Registry.register(Registry.ITEM, id("bronze_sword"), AtItems.BRONZE_SWORD);
        Registry.register(Registry.ITEM, id("bronze_shovel"), AtItems.BRONZE_SHOVEL);
        Registry.register(Registry.ITEM, id("bronze_pickaxe"), AtItems.BRONZE_PICKAXE);
        Registry.register(Registry.ITEM, id("bronze_axe"), AtItems.BRONZE_AXE);
        Registry.register(Registry.ITEM, id("bronze_hoe"), AtItems.BRONZE_HOE);
        Registry.register(Registry.ITEM, id("bronze_helmet"), AtItems.BRONZE_HELMET);
        Registry.register(Registry.ITEM, id("bronze_chestplate"), AtItems.BRONZE_CHESTPLATE);
        Registry.register(Registry.ITEM, id("bronze_leggings"), AtItems.BRONZE_LEGGINGS);
        Registry.register(Registry.ITEM, id("bronze_boots"), AtItems.BRONZE_BOOTS);

        Registry.register(Registry.ITEM, id("rechargeable_battery"), AtItems.RECHARGEABLE_BATTERY);
        Registry.register(Registry.ITEM, id("energy_crystal"), AtItems.ENERGY_CRYSTAL);

        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(AtBlocks.RUBBER_SAPLING.asItem(), 0.3F);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(AtBlocks.RUBBER_LEAVES.asItem(), 0.3F);

        Registry.register(Registry.RECIPE_TYPE, id("dummy"), AtRecipeTypes.DUMMY);
        Registry.register(Registry.RECIPE_TYPE, id("macerating"), AtRecipeTypes.MACERATING);
        Registry.register(Registry.RECIPE_TYPE, id("compressing"), AtRecipeTypes.COMPRESSING);
        Registry.register(Registry.RECIPE_TYPE, id("extracting"), AtRecipeTypes.EXTRACTING);

        Registry.register(Registry.RECIPE_SERIALIZER, id("dummy"), AtRecipeSerializers.DUMMY);
        Registry.register(Registry.RECIPE_SERIALIZER, id("macerating"), AtRecipeSerializers.MACERATING);
        Registry.register(Registry.RECIPE_SERIALIZER, id("compressing"), AtRecipeSerializers.COMPRESSING);
        Registry.register(Registry.RECIPE_SERIALIZER, id("extracting"), AtRecipeSerializers.EXTRACTING);

        Registry.register(Registry.FOLIAGE_PLACER_TYPE, id("rubber"), AtFoliagePlacers.RUBBER);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id("rubber_tree"), AtFeatures.RUBBER_TREE);

        AtScreenHandlerTypes.init();
    }

    public static Identifier id(String path) {
        return new Identifier("assortech", path);
    }

    public static class AtMaterials {
        public static final Material MACHINE = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();
    }

    public static class AtBlocks {
        private static final Material UNMOVABLE_WOOD = new FabricMaterialBuilder(MapColor.OAK_TAN).blocksPistons().burnable().build();

        private static final AbstractBlock.Settings RUBBER_LOG_SETTINGS = FabricBlockSettings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.YELLOW : MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD);
        private static final AbstractBlock.Settings UNMOVABLE_RUBBER_LOG_SETTINGS = FabricBlockSettings.of(UNMOVABLE_WOOD, MapColor.YELLOW).ticksRandomly().strength(2F).sounds(BlockSoundGroup.WOOD);
        private static final AbstractBlock.Settings TIN_SETTINGS = FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL);
        private static final AbstractBlock.Settings BRONZE_SETTINGS = FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).strength(3F, 6F).sounds(BlockSoundGroup.METAL);
        private static final AbstractBlock.Settings MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool();
        private static final AbstractBlock.Settings ADVANCED_MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(4F, 10F).sounds(BlockSoundGroup.METAL).requiresTool();
        private static final AbstractBlock.Settings BRICK_DEVICE = FabricBlockSettings.of(Material.STONE, MapColor.RED).luminance(state -> state.get(Properties.LIT) ? 15 : 0).strength(3F, 6F).requiresTool();

        public static final Block RUBBER_LOG = new PillarBlock(RUBBER_LOG_SETTINGS);
        public static final Block RESIN_RUBBER_LOG = new RubberLogBlock(UNMOVABLE_RUBBER_LOG_SETTINGS);
        public static final Block RUBBER_LEAVES = new LeavesBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_LEAVES));
        public static final Block RUBBER_SAPLING = new RubberSaplingBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_SAPLING));
        public static final Block TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(3F));
        public static final Block DEEPSLATE_TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(4.5F, 3F));
        public static final Block TIN_BLOCK = new Block(TIN_SETTINGS);
        public static final Block BRONZE_BLOCK = new Block(BRONZE_SETTINGS);
        public static final Block MACHINE = new Block(MACHINE_SETTINGS);
        public static final Block ADVANCED_MACHINE = new Block(ADVANCED_MACHINE_SETTINGS);

        public static final Block ALLOY_SMELTERY = new AlloySmelteryBlock(FabricBlockSettings.copyOf(BRICK_DEVICE));

        public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE));
        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block MACERATOR = new MaceratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block COMPRESSOR = new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block MOLECULAR_RECONSTRUCTOR = new MolecularReconstructorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block BATTERY_BOX = new BatteryBoxBlock(FabricBlockSettings.copyOf(MACHINE).mapColor(MapColor.SPRUCE_BROWN));
        public static final Block COPPER_WIRE = new CableBlock(1, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL).breakByHand(true));
        public static final Block COPPER_CABLE = new CableBlock(2, FabricBlockSettings.of(Material.METAL).strength(0.5F).breakByHand(true));
    }

    public static class AtItems {
        public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(AtBlocks.MACHINE));

        private static Item.Settings settings() {
            return new Item.Settings().group(GROUP);
        }

        public static final Item STICKY_RESIN = new Item(settings());
        public static final Item RUBBER = new Item(settings());
        public static final Item RAW_TIN = new Item(settings());
        public static final Item TIN_INGOT = new Item(settings());
        public static final Item BRONZE_INGOT = new Item(settings());
        public static final Item REFINED_IRON_INGOT = new Item(settings());
        public static final Item TIN_NUGGET = new Item(settings());
        public static final Item BRONZE_NUGGET = new Item(settings());
        public static final Item STONE_DUST = new Item(settings());
        public static final Item COAL_DUST = new Item(settings());
        public static final Item IRON_DUST = new Item(settings());
        public static final Item GOLD_DUST = new Item(settings());
        public static final Item DIAMOND_DUST = new Item(settings());
        public static final Item COPPER_DUST = new Item(settings());
        public static final Item TIN_DUST = new Item(settings());
        public static final Item BRONZE_DUST = new Item(settings());
        public static final Item REFINED_IRON_DUST = new Item(settings());
        public static final Item ENERGY_CRYSTAL_DUST = new Item(settings());
        public static final Item CIRCUIT = new Item(settings());
        public static final Item ADVANCED_ALLOY_COMPOUND = new Item(settings());
        public static final Item ADVANCED_ALLOY = new Item(settings().rarity(Rarity.RARE));
        public static final Item ARACHNOLACTAM = new Item(settings());
        public static final Item HYPERSRAND = new Item(settings().rarity(Rarity.RARE));
        public static final Item HYPERWEAVE = new Item(settings().rarity(Rarity.RARE));
        public static final Item BRONZE_SWORD = new SwordItem(AssortechToolMaterials.BRONZE, 3, -2.4F, settings());
        public static final Item BRONZE_SHOVEL = new ShovelItem(AssortechToolMaterials.BRONZE, 1.5F, -3.0F, settings());
        public static final Item BRONZE_PICKAXE = new AccessiblePickaxeItem(AssortechToolMaterials.BRONZE, 1, -2.8F, settings());
        public static final Item BRONZE_AXE = new AccessibleAxeItem(AssortechToolMaterials.BRONZE, 6, -3.1F, settings());
        public static final Item BRONZE_HOE = new AccessibleAxeItem(AssortechToolMaterials.BRONZE, -2, -1.0F, settings());
        public static final Item BRONZE_HELMET = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.HEAD, settings());
        public static final Item BRONZE_CHESTPLATE = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.CHEST, settings());
        public static final Item BRONZE_LEGGINGS = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.LEGS, settings());
        public static final Item BRONZE_BOOTS = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.FEET, settings());
        public static final Item RECHARGEABLE_BATTERY = new RechargeableBatteryItem(settings().maxCount(1).rarity(Rarity.RARE));
        public static final Item ENERGY_CRYSTAL = new EnergyCrystalItem(settings().maxCount(1).rarity(Rarity.RARE));
    }

    public static class AtBlockEntityTypes {
        public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = FabricBlockEntityTypeBuilder.create(GeneratorBlockEntity::new, AtBlocks.GENERATOR).build();
        public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = FabricBlockEntityTypeBuilder.create(SolarPanelBlockEntity::new, AtBlocks.SOLAR_PANEL).build();
        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, AtBlocks.ELECTRIC_FURNACE).build();
        public static final BlockEntityType<MaceratorBlockEntity> MACERATOR = FabricBlockEntityTypeBuilder.create(MaceratorBlockEntity::new, AtBlocks.MACERATOR).build();
        public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = FabricBlockEntityTypeBuilder.create(CompressorBlockEntity::new, AtBlocks.COMPRESSOR).build();
        public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = FabricBlockEntityTypeBuilder.create(ExtractorBlockEntity::new, AtBlocks.EXTRACTOR).build();
        public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX = FabricBlockEntityTypeBuilder.create(BatteryBoxBlockEntity::new, AtBlocks.BATTERY_BOX).build();
        public static final BlockEntityType<CableBlockEntity> CABLE = FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, AtBlocks.COPPER_WIRE, AtBlocks.COPPER_CABLE).build();
    }

    public static class AtRecipeTypes {
        public static final RecipeType<DummyRecipe> DUMMY = new AtRecipeType<>();
        public static final RecipeType<MaceratorRecipe> MACERATING = new AtRecipeType<>();
        public static final RecipeType<CompressorRecipe> COMPRESSING = new AtRecipeType<>();
        public static final RecipeType<ExtractorRecipe> EXTRACTING = new AtRecipeType<>();

        private static class AtRecipeType<T extends Recipe<?>> implements RecipeType<T> {
            // TODO perhaps put here default EU/tick ?
        }
    }

    public static class AtRecipeSerializers {
        public static final RecipeSerializer<DummyRecipe> DUMMY = new DummyRecipe.Serializer();
        public static final RecipeSerializer<MaceratorRecipe> MACERATING = new CraftingMachineRecipe.Serializer<>(MaceratorRecipe::new, 300);
        public static final RecipeSerializer<CompressorRecipe> COMPRESSING = new CraftingMachineRecipe.Serializer<>(CompressorRecipe::new, 400);
        public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = new CraftingMachineRecipe.Serializer<>(ExtractorRecipe::new, 400);
    }

    public static class AtFoliagePlacers {
        public static final FoliagePlacerType<RubberFoliagePlacer> RUBBER = FoliagePlacerTypeInvoker.create(RubberFoliagePlacer.CODEC);
    }

    public static class AtFeatures {
        private static final WeightedBlockStateProvider RUBBER_LOGS = new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(AtBlocks.RUBBER_LOG.getDefaultState(), 4).add(AtBlocks.RESIN_RUBBER_LOG.getDefaultState(), 1).add(AtBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.SOUTH), 1).add(AtBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.WEST), 1).add(AtBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.EAST), 1));
        private static final SimpleBlockStateProvider RUBBER_LEAVES = BlockStateProvider.of(AtBlocks.RUBBER_LEAVES);

        public static final ConfiguredFeature<TreeFeatureConfig, ?> RUBBER_TREE = Feature.TREE.configure(new TreeFeatureConfig.Builder(RUBBER_LOGS, new StraightTrunkPlacer(5, 2, 0), RUBBER_LEAVES, new RubberFoliagePlacer(UniformIntProvider.create(2, 2), UniformIntProvider.create(1, 1), 5), new TwoLayersFeatureSize(1, 0, 1)).build());
    }

    public static class AtScreenHandlerTypes {
        public static final ScreenHandlerType<GeneratorScreenHandler> GENERATOR = ScreenHandlerRegistry.registerSimple(id("generator"), GeneratorScreenHandler::new);
        public static final ScreenHandlerType<SolarPanelScreenHandler> SOLAR_PANEL = ScreenHandlerRegistry.registerSimple(id("solar_panel"), SolarPanelScreenHandler::new);
        public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE = ScreenHandlerRegistry.registerSimple(id("electric_furnace"), ElectricFurnaceScreenHandler::new);
        public static final ScreenHandlerType<MaceratorScreenHandler> MACERATOR = ScreenHandlerRegistry.registerSimple(id("macerator"), MaceratorScreenHandler::new);
        public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR = ScreenHandlerRegistry.registerSimple(id("compressor"), CompressorScreenHandler::new);
        public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR = ScreenHandlerRegistry.registerSimple(id("extractor"), ExtractorScreenHandler::new);
        public static final ScreenHandlerType<BatteryBoxScreenHandler> BATTERY_BOX = ScreenHandlerRegistry.registerSimple(id("battery_box"), BatteryBoxScreenHandler::new);

        private static void init() {
            // just initializes static fields
        }
    }
}
