package spacefactory;

import spacefactory.block.*;
import spacefactory.block.entity.*;
import spacefactory.feature.RubberFoliagePlacer;
import spacefactory.item.AccessibleAxeItem;
import spacefactory.item.AccessiblePickaxeItem;
import spacefactory.item.EnergyCrystalItem;
import spacefactory.item.RechargeableBatteryItem;
import spacefactory.item.material.AssortechArmorMaterials;
import spacefactory.item.material.AssortechToolMaterials;
import spacefactory.mixin.FoliagePlacerTypeInvoker;
import spacefactory.recipe.*;
import spacefactory.screen.*;
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

public class SpaceFactory implements ModInitializer {
    @Override
    public void onInitialize() {
        // Generators
        register(Registry.BLOCK, "generator", SFBlocks.GENERATOR);
        register(Registry.BLOCK, "solar_panel", SFBlocks.SOLAR_PANEL);
        // Energy Storage
        register(Registry.BLOCK, "battery_box", SFBlocks.BATTERY_BOX);
        // Crafting Machines
        register(Registry.BLOCK, "electric_furnace", SFBlocks.ELECTRIC_FURNACE);
        register(Registry.BLOCK, "macerator", SFBlocks.MACERATOR);
        register(Registry.BLOCK, "compressor", SFBlocks.COMPRESSOR);
        register(Registry.BLOCK, "extractor", SFBlocks.EXTRACTOR);
        register(Registry.BLOCK, "ore_washer", SFBlocks.ORE_WASHER);
        // Cables
        register(Registry.BLOCK, "copper_wire", SFBlocks.COPPER_WIRE);
        register(Registry.BLOCK, "copper_cable", SFBlocks.COPPER_CABLE);
        // Resources
        register(Registry.BLOCK, "tin_ore", SFBlocks.TIN_ORE);
        register(Registry.BLOCK, "deepslate_tin_ore", SFBlocks.DEEPSLATE_TIN_ORE);
        register(Registry.BLOCK, "tin_block", SFBlocks.TIN_BLOCK);
        register(Registry.BLOCK, "bronze_block", SFBlocks.BRONZE_BLOCK);
        register(Registry.BLOCK, "iridium_block", SFBlocks.IRIDIUM_BLOCK);
        register(Registry.BLOCK, "machine", SFBlocks.MACHINE);
        register(Registry.BLOCK, "advanced_machine", SFBlocks.ADVANCED_MACHINE);
        register(Registry.BLOCK, "rubber_log", SFBlocks.RUBBER_LOG);
        register(Registry.BLOCK, "resin_rubber_log", SFBlocks.RESIN_RUBBER_LOG);
        // TODO stripped rubber log, rubber wood, stripped rubber wood
        register(Registry.BLOCK, "rubber_leaves", SFBlocks.RUBBER_LEAVES);
        register(Registry.BLOCK, "rubber_sapling", SFBlocks.RUBBER_SAPLING);

        register(Registry.BLOCK, "alloy_smeltery", SFBlocks.ALLOY_SMELTERY);

        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.RESIN_RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.RUBBER_LEAVES, 30, 60);

        register(Registry.BLOCK_ENTITY_TYPE, "generator", SFBlockEntityTypes.GENERATOR);
        register(Registry.BLOCK_ENTITY_TYPE, "solar_panel", SFBlockEntityTypes.SOLAR_PANEL);
        register(Registry.BLOCK_ENTITY_TYPE, "battery_box", SFBlockEntityTypes.BATTERY_BOX);
        register(Registry.BLOCK_ENTITY_TYPE, "electric_furnace", SFBlockEntityTypes.ELECTRIC_FURNACE);
        register(Registry.BLOCK_ENTITY_TYPE, "macerator", SFBlockEntityTypes.MACERATOR);
        register(Registry.BLOCK_ENTITY_TYPE, "compressor", SFBlockEntityTypes.COMPRESSOR);
        register(Registry.BLOCK_ENTITY_TYPE, "extractor", SFBlockEntityTypes.EXTRACTOR);
        register(Registry.BLOCK_ENTITY_TYPE, "cable", SFBlockEntityTypes.CABLE);

        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.GENERATOR);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> SolarPanelBlockEntity.ENERGY, SFBlockEntityTypes.SOLAR_PANEL);
        EnergyStorage.SIDED.registerForBlockEntity(BatteryBoxBlockEntity::getEnergyHandler, SFBlockEntityTypes.BATTERY_BOX);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.ELECTRIC_FURNACE);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.MACERATOR);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.COMPRESSOR);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.EXTRACTOR);
        EnergyStorage.SIDED.registerForBlockEntity(CableBlockEntity::getEnergyHandler, SFBlockEntityTypes.CABLE);

        // Generators
        register(Registry.ITEM, "generator", new BlockItem(SFBlocks.GENERATOR, SFItems.settings()));
        register(Registry.ITEM, "solar_panel", new BlockItem(SFBlocks.SOLAR_PANEL, SFItems.settings()));
        // Energy Storage
        register(Registry.ITEM, "battery_box", new BlockItem(SFBlocks.BATTERY_BOX, SFItems.settings()));
        // Crafting Machines
        register(Registry.ITEM, "electric_furnace", new BlockItem(SFBlocks.ELECTRIC_FURNACE, SFItems.settings()));
        register(Registry.ITEM, "macerator", new BlockItem(SFBlocks.MACERATOR, SFItems.settings()));
        register(Registry.ITEM, "compressor", new BlockItem(SFBlocks.COMPRESSOR, SFItems.settings()));
        register(Registry.ITEM, "extractor", new BlockItem(SFBlocks.EXTRACTOR, SFItems.settings()));
        register(Registry.ITEM, "ore_washer", new BlockItem(SFBlocks.ORE_WASHER, SFItems.settings()));
        // Cables
        register(Registry.ITEM, "copper_wire", new BlockItem(SFBlocks.COPPER_WIRE, SFItems.settings()));
        register(Registry.ITEM, "copper_cable", new BlockItem(SFBlocks.COPPER_CABLE, SFItems.settings()));
        // Resources
        register(Registry.ITEM, "tin_ore", new BlockItem(SFBlocks.TIN_ORE, SFItems.settings()));
        register(Registry.ITEM, "deepslate_tin_ore", new BlockItem(SFBlocks.DEEPSLATE_TIN_ORE, SFItems.settings()));
        register(Registry.ITEM, "tin_block", new BlockItem(SFBlocks.TIN_BLOCK, SFItems.settings()));
        register(Registry.ITEM, "bronze_block", new BlockItem(SFBlocks.BRONZE_BLOCK, SFItems.settings()));
        register(Registry.ITEM, "iridium_block", new BlockItem(SFBlocks.IRIDIUM_BLOCK, SFItems.settings().rarity(Rarity.EPIC)));
        register(Registry.ITEM, "machine", new BlockItem(SFBlocks.MACHINE, SFItems.settings()));
        register(Registry.ITEM, "advanced_machine", new BlockItem(SFBlocks.ADVANCED_MACHINE, SFItems.settings().rarity(Rarity.RARE)));

        register(Registry.ITEM, "rubber_log", new BlockItem(SFBlocks.RUBBER_LOG, SFItems.settings()));
        register(Registry.ITEM, "rubber_leaves", new BlockItem(SFBlocks.RUBBER_LEAVES, SFItems.settings()));
        register(Registry.ITEM, "rubber_sapling", new BlockItem(SFBlocks.RUBBER_SAPLING, SFItems.settings()));

        register(Registry.ITEM, "alloy_smeltery", new BlockItem(SFBlocks.ALLOY_SMELTERY, SFItems.settings().group(null)));

        register(Registry.ITEM, "sticky_resin", SFItems.STICKY_RESIN);
        register(Registry.ITEM, "rubber", SFItems.RUBBER);
        register(Registry.ITEM, "circuit", SFItems.CIRCUIT);
        register(Registry.ITEM, "advanced_alloy", SFItems.ADVANCED_ALLOY);
        register(Registry.ITEM, "arachnolactam", SFItems.ARACHNOLACTAM);
        register(Registry.ITEM, "hyperstrand", SFItems.HYPERSRAND);
        register(Registry.ITEM, "hyperweave", SFItems.HYPERWEAVE);

        register(Registry.ITEM, "tin_ingot", SFItems.TIN_INGOT);
        register(Registry.ITEM, "bronze_ingot", SFItems.BRONZE_INGOT);
        register(Registry.ITEM, "refined_iron_ingot", SFItems.REFINED_IRON_INGOT);
        register(Registry.ITEM, "iridium_ingot", SFItems.IRIDIUM_INGOT);
        register(Registry.ITEM, "advanced_alloy_compound", SFItems.ADVANCED_ALLOY_COMPOUND);

        register(Registry.ITEM, "copper_nugget", SFItems.COPPER_NUGGET);
        register(Registry.ITEM, "tin_nugget", SFItems.TIN_NUGGET);
        register(Registry.ITEM, "bronze_nugget", SFItems.BRONZE_NUGGET);

        register(Registry.ITEM, "stone_dust", SFItems.STONE_DUST);
        register(Registry.ITEM, "coal_dust", SFItems.COAL_DUST);
        register(Registry.ITEM, "copper_dust", SFItems.COPPER_DUST);
        register(Registry.ITEM, "iron_dust", SFItems.IRON_DUST);
        register(Registry.ITEM, "gold_dust", SFItems.GOLD_DUST);
        register(Registry.ITEM, "diamond_dust", SFItems.DIAMOND_DUST);
        register(Registry.ITEM, "tin_dust", SFItems.TIN_DUST);
        register(Registry.ITEM, "bronze_dust", SFItems.BRONZE_DUST);
        register(Registry.ITEM, "refined_iron_dust", SFItems.REFINED_IRON_DUST);
        register(Registry.ITEM, "iridium_dust", SFItems.IRIDIUM_DUST);
        register(Registry.ITEM, "energy_crystal_dust", SFItems.ENERGY_CRYSTAL_DUST);
        register(Registry.ITEM, "netherite_scrap_dust", SFItems.NETHERITE_SCRAP_DUST);

        register(Registry.ITEM, "small_refined_iron_dust", SFItems.SMALL_REFINED_IRON_DUST);
        register(Registry.ITEM, "small_iridium_dust", SFItems.SMALL_IRIDIUM_DUST);

        register(Registry.ITEM, "raw_tin", SFItems.RAW_TIN);

        register(Registry.ITEM, "bronze_sword", SFItems.BRONZE_SWORD);
        register(Registry.ITEM, "bronze_shovel", SFItems.BRONZE_SHOVEL);
        register(Registry.ITEM, "bronze_pickaxe", SFItems.BRONZE_PICKAXE);
        register(Registry.ITEM, "bronze_axe", SFItems.BRONZE_AXE);
        register(Registry.ITEM, "bronze_hoe", SFItems.BRONZE_HOE);
        register(Registry.ITEM, "bronze_helmet", SFItems.BRONZE_HELMET);
        register(Registry.ITEM, "bronze_chestplate", SFItems.BRONZE_CHESTPLATE);
        register(Registry.ITEM, "bronze_leggings", SFItems.BRONZE_LEGGINGS);
        register(Registry.ITEM, "bronze_boots", SFItems.BRONZE_BOOTS);

        register(Registry.ITEM, "rechargeable_battery", SFItems.RECHARGEABLE_BATTERY);
        register(Registry.ITEM, "energy_crystal", SFItems.ENERGY_CRYSTAL);

        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(SFBlocks.RUBBER_SAPLING.asItem(), 0.3F);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(SFBlocks.RUBBER_LEAVES.asItem(), 0.3F);

        register(Registry.RECIPE_TYPE, "dummy", SFRecipeTypes.DUMMY);
        register(Registry.RECIPE_TYPE, "macerating", SFRecipeTypes.MACERATING);
        register(Registry.RECIPE_TYPE, "compressing", SFRecipeTypes.COMPRESSING);
        register(Registry.RECIPE_TYPE, "extracting", SFRecipeTypes.EXTRACTING);

        register(Registry.RECIPE_SERIALIZER, "dummy", SFRecipeSerializers.DUMMY);
        register(Registry.RECIPE_SERIALIZER, "macerating", SFRecipeSerializers.MACERATING);
        register(Registry.RECIPE_SERIALIZER, "compressing", SFRecipeSerializers.COMPRESSING);
        register(Registry.RECIPE_SERIALIZER, "extracting", SFRecipeSerializers.EXTRACTING);

        register(Registry.FOLIAGE_PLACER_TYPE, "rubber", SFFoliagePlacers.RUBBER);

        register(BuiltinRegistries.CONFIGURED_FEATURE, "rubber_tree", SFFeatures.RUBBER_TREE);

        SFScreenHandlerTypes.init();
    }

    public static Identifier id(String path) {
        return new Identifier("spacefactory", path);
    }

    public static <T> T register(Registry<? super T> registry, String name, T entry) {
        return Registry.register(registry, id(name), entry);
    }

    public static class AtMaterials {
        public static final Material MACHINE = new FabricMaterialBuilder(MapColor.IRON_GRAY).build();
    }

    public static class SFBlocks {
        private static final Material UNMOVABLE_WOOD = new FabricMaterialBuilder(MapColor.OAK_TAN).blocksPistons().burnable().build();

        private static final AbstractBlock.Settings RUBBER_LOG_SETTINGS = FabricBlockSettings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? MapColor.YELLOW : MapColor.BROWN).strength(2F).sounds(BlockSoundGroup.WOOD);
        private static final AbstractBlock.Settings UNMOVABLE_RUBBER_LOG_SETTINGS = FabricBlockSettings.of(UNMOVABLE_WOOD, MapColor.YELLOW).ticksRandomly().strength(2F).sounds(BlockSoundGroup.WOOD);
        private static final AbstractBlock.Settings TIN_SETTINGS = FabricBlockSettings.of(Material.METAL, MapColor.LIGHT_GRAY).strength(3F, 6F).sounds(BlockSoundGroup.METAL);
        private static final AbstractBlock.Settings BRONZE_SETTINGS = FabricBlockSettings.of(Material.METAL, MapColor.ORANGE).strength(3F, 6F).sounds(BlockSoundGroup.METAL);
        private static final AbstractBlock.Settings MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool();
        private static final AbstractBlock.Settings ADVANCED_MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(4F, 10F).sounds(BlockSoundGroup.METAL).requiresTool();
        private static final AbstractBlock.Settings BRICK_DEVICE = FabricBlockSettings.of(Material.STONE, MapColor.RED).luminance(state -> state.get(Properties.LIT) ? 15 : 0).strength(3F, 6F).requiresTool();

        // Generators
        public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE));
        // Energy Storage
        public static final Block BATTERY_BOX = new BatteryBoxBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.WOOD));
        // Crafting Machines
        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block MACERATOR = new MaceratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block COMPRESSOR = new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block ORE_WASHER = new OreWasherBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        // Cables
        public static final Block COPPER_WIRE = new CableBlock(1, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL).breakByHand(true));
        public static final Block COPPER_CABLE = new CableBlock(2, FabricBlockSettings.of(Material.METAL).strength(0.5F).breakByHand(true));

        // Resources
        public static final Block TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(3F));
        public static final Block DEEPSLATE_TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(4.5F, 3F));
        public static final Block TIN_BLOCK = new Block(TIN_SETTINGS);
        public static final Block BRONZE_BLOCK = new Block(BRONZE_SETTINGS);
        public static final Block IRIDIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5F, 20F));
        public static final Block MACHINE = new Block(MACHINE_SETTINGS);
        public static final Block ADVANCED_MACHINE = new Block(ADVANCED_MACHINE_SETTINGS);
        public static final Block RUBBER_LOG = new PillarBlock(RUBBER_LOG_SETTINGS);
        public static final Block RESIN_RUBBER_LOG = new RubberLogBlock(UNMOVABLE_RUBBER_LOG_SETTINGS);
        public static final Block RUBBER_LEAVES = new LeavesBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_LEAVES));
        public static final Block RUBBER_SAPLING = new RubberSaplingBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_SAPLING));

        public static final Block ALLOY_SMELTERY = new AlloySmelteryBlock(FabricBlockSettings.copyOf(BRICK_DEVICE));
    }

    public static class SFItems {
        public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(SFBlocks.MACHINE));

        private static Item.Settings settings() {
            return new Item.Settings().group(GROUP);
        }

        public static final Item STICKY_RESIN = new Item(settings());
        public static final Item RUBBER = new Item(settings());
        public static final Item CIRCUIT = new Item(settings());
        public static final Item ADVANCED_ALLOY = new Item(settings().rarity(Rarity.RARE));
        public static final Item ARACHNOLACTAM = new Item(settings());
        public static final Item HYPERSRAND = new Item(settings().rarity(Rarity.RARE));
        public static final Item HYPERWEAVE = new Item(settings().rarity(Rarity.RARE));

        public static final Item TIN_INGOT = new Item(settings());
        public static final Item BRONZE_INGOT = new Item(settings());
        public static final Item REFINED_IRON_INGOT = new Item(settings());
        public static final Item IRIDIUM_INGOT = new Item(settings().rarity(Rarity.EPIC));
        public static final Item ADVANCED_ALLOY_COMPOUND = new Item(settings());

        public static final Item COPPER_NUGGET = new Item(settings());
        public static final Item TIN_NUGGET = new Item(settings());
        public static final Item BRONZE_NUGGET = new Item(settings());

        public static final Item STONE_DUST = new Item(settings());
        public static final Item COAL_DUST = new Item(settings());
        public static final Item COPPER_DUST = new Item(settings());
        public static final Item IRON_DUST = new Item(settings());
        public static final Item GOLD_DUST = new Item(settings());
        public static final Item DIAMOND_DUST = new Item(settings());
        public static final Item TIN_DUST = new Item(settings());
        public static final Item BRONZE_DUST = new Item(settings());
        public static final Item REFINED_IRON_DUST = new Item(settings());
        public static final Item IRIDIUM_DUST = new Item(settings().rarity(Rarity.EPIC));
        public static final Item ENERGY_CRYSTAL_DUST = new Item(settings());
        public static final Item NETHERITE_SCRAP_DUST = new Item(settings());

        public static final Item SMALL_REFINED_IRON_DUST = new Item(settings());
        public static final Item SMALL_IRIDIUM_DUST = new Item(settings().rarity(Rarity.EPIC));

        public static final Item RAW_TIN = new Item(settings());

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

    public static class SFBlockEntityTypes {
        public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = FabricBlockEntityTypeBuilder.create(GeneratorBlockEntity::new, SFBlocks.GENERATOR).build();
        public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = FabricBlockEntityTypeBuilder.create(SolarPanelBlockEntity::new, SFBlocks.SOLAR_PANEL).build();
        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, SFBlocks.ELECTRIC_FURNACE).build();
        public static final BlockEntityType<MaceratorBlockEntity> MACERATOR = FabricBlockEntityTypeBuilder.create(MaceratorBlockEntity::new, SFBlocks.MACERATOR).build();
        public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = FabricBlockEntityTypeBuilder.create(CompressorBlockEntity::new, SFBlocks.COMPRESSOR).build();
        public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = FabricBlockEntityTypeBuilder.create(ExtractorBlockEntity::new, SFBlocks.EXTRACTOR).build();
        public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX = FabricBlockEntityTypeBuilder.create(BatteryBoxBlockEntity::new, SFBlocks.BATTERY_BOX).build();
        public static final BlockEntityType<CableBlockEntity> CABLE = FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, SFBlocks.COPPER_WIRE, SFBlocks.COPPER_CABLE).build();
    }

    public static class SFRecipeTypes {
        public static final RecipeType<DummyRecipe> DUMMY = new AtRecipeType<>();
        public static final RecipeType<MaceratorRecipe> MACERATING = new AtRecipeType<>();
        public static final RecipeType<CompressorRecipe> COMPRESSING = new AtRecipeType<>();
        public static final RecipeType<ExtractorRecipe> EXTRACTING = new AtRecipeType<>();

        private static class AtRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        }
    }

    public static class SFRecipeSerializers {
        public static final RecipeSerializer<DummyRecipe> DUMMY = new DummyRecipe.Serializer();
        public static final RecipeSerializer<MaceratorRecipe> MACERATING = new CraftingMachineRecipe.Serializer<>(MaceratorRecipe::new, 300);
        public static final RecipeSerializer<CompressorRecipe> COMPRESSING = new CraftingMachineRecipe.Serializer<>(CompressorRecipe::new, 400);
        public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = new CraftingMachineRecipe.Serializer<>(ExtractorRecipe::new, 400);
    }

    public static class SFFoliagePlacers {
        public static final FoliagePlacerType<RubberFoliagePlacer> RUBBER = FoliagePlacerTypeInvoker.create(RubberFoliagePlacer.CODEC);
    }

    public static class SFFeatures {
        private static final WeightedBlockStateProvider RUBBER_LOGS = new WeightedBlockStateProvider(new DataPool.Builder<BlockState>().add(SFBlocks.RUBBER_LOG.getDefaultState(), 4).add(SFBlocks.RESIN_RUBBER_LOG.getDefaultState(), 1).add(SFBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.SOUTH), 1).add(SFBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.WEST), 1).add(SFBlocks.RESIN_RUBBER_LOG.getDefaultState().with(RubberLogBlock.FACING, Direction.EAST), 1));
        private static final SimpleBlockStateProvider RUBBER_LEAVES = BlockStateProvider.of(SFBlocks.RUBBER_LEAVES);

        public static final ConfiguredFeature<TreeFeatureConfig, ?> RUBBER_TREE = Feature.TREE.configure(new TreeFeatureConfig.Builder(RUBBER_LOGS, new StraightTrunkPlacer(5, 2, 0), RUBBER_LEAVES, new RubberFoliagePlacer(UniformIntProvider.create(2, 2), UniformIntProvider.create(1, 1), 5), new TwoLayersFeatureSize(1, 0, 1)).build());
    }

    public static class SFScreenHandlerTypes {
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
