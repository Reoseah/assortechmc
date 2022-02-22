package spacefactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagFactory;
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
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
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
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import spacefactory.block.*;
import spacefactory.block.entity.*;
import spacefactory.feature.RubberFoliagePlacer;
import spacefactory.item.AccessibleAxeItem;
import spacefactory.item.AccessiblePickaxeItem;
import spacefactory.item.VanovoltaicCellItem;
import spacefactory.item.WireCuttersItem;
import spacefactory.item.material.AssortechArmorMaterials;
import spacefactory.item.material.AssortechToolMaterials;
import spacefactory.mixin.BlocksAccessor;
import spacefactory.mixin.FoliagePlacerTypeInvoker;
import spacefactory.recipe.*;
import spacefactory.screen.*;
import team.reborn.energy.api.EnergyStorage;

public class SpaceFactory implements ModInitializer {
    @Override
    public void onInitialize() {
        register(Registry.BLOCK, "machine_block", SFBlocks.MACHINE_BLOCK);
        // Generators
        register(Registry.BLOCK, "generator", SFBlocks.GENERATOR);
        register(Registry.BLOCK, "solar_panel", SFBlocks.SOLAR_PANEL);
        register(Registry.BLOCK, "dragon_egg_siphon", SFBlocks.DRAGON_EGG_SIPHON);
        // Energy Storage
        register(Registry.BLOCK, "battery_box", SFBlocks.BATTERY_BOX);
        // Crafting Machines
        register(Registry.BLOCK, "electric_furnace", SFBlocks.ELECTRIC_FURNACE);
        register(Registry.BLOCK, "macerator", SFBlocks.MACERATOR);
        register(Registry.BLOCK, "compressor", SFBlocks.COMPRESSOR);
        register(Registry.BLOCK, "extractor", SFBlocks.EXTRACTOR);
        // Cables
        register(Registry.BLOCK, "copper_wire", SFBlocks.COPPER_WIRE);
        register(Registry.BLOCK, "copper_cable", SFBlocks.COPPER_CABLE);
        // Resources
        register(Registry.BLOCK, "tin_ore", SFBlocks.TIN_ORE);
        register(Registry.BLOCK, "deepslate_tin_ore", SFBlocks.DEEPSLATE_TIN_ORE);
        register(Registry.BLOCK, "tin_block", SFBlocks.TIN_BLOCK);
        register(Registry.BLOCK, "bronze_block", SFBlocks.BRONZE_BLOCK);
        register(Registry.BLOCK, "iridium_block", SFBlocks.IRIDIUM_BLOCK);
        register(Registry.BLOCK, "crystalite_block", SFBlocks.CRYSTALITE_BLOCK);
        register(Registry.BLOCK, "raw_tin_block", SFBlocks.RAW_TIN_BLOCK);
        register(Registry.BLOCK, "raw_iridium_block", SFBlocks.RAW_IRIDIUM_BLOCK);

        register(Registry.BLOCK, "rubber_log", SFBlocks.RUBBER_LOG);
        register(Registry.BLOCK, "alive_rubber_log", SFBlocks.ALIVE_RUBBER_LOG);
        register(Registry.BLOCK, "stripped_rubber_log", SFBlocks.STRIPPED_RUBBER_LOG);
        // TODO rubber wood, stripped rubber wood
        register(Registry.BLOCK, "rubber_leaves", SFBlocks.RUBBER_LEAVES);
        register(Registry.BLOCK, "rubber_sapling", SFBlocks.RUBBER_SAPLING);
        // TODO iridium ores
        // TODO raw iridium block

        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.ALIVE_RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.STRIPPED_RUBBER_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(SFBlocks.RUBBER_LEAVES, 30, 60);

        register(Registry.BLOCK_ENTITY_TYPE, "generator", SFBlockEntityTypes.GENERATOR);
        register(Registry.BLOCK_ENTITY_TYPE, "solar_panel", SFBlockEntityTypes.SOLAR_PANEL);
        register(Registry.BLOCK_ENTITY_TYPE, "dragon_egg_siphon", SFBlockEntityTypes.DRAGON_EGG_SIPHON);
        register(Registry.BLOCK_ENTITY_TYPE, "battery_box", SFBlockEntityTypes.BATTERY_BOX);
        register(Registry.BLOCK_ENTITY_TYPE, "electric_furnace", SFBlockEntityTypes.ELECTRIC_FURNACE);
        register(Registry.BLOCK_ENTITY_TYPE, "macerator", SFBlockEntityTypes.MACERATOR);
        register(Registry.BLOCK_ENTITY_TYPE, "compressor", SFBlockEntityTypes.COMPRESSOR);
        register(Registry.BLOCK_ENTITY_TYPE, "extractor", SFBlockEntityTypes.EXTRACTOR);
        register(Registry.BLOCK_ENTITY_TYPE, "cable", SFBlockEntityTypes.CABLE);

        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.GENERATOR);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> SolarPanelBlockEntity.ENERGY, SFBlockEntityTypes.SOLAR_PANEL);
        EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> DragonEggSiphonBlockEntity.ENERGY, SFBlockEntityTypes.DRAGON_EGG_SIPHON);
        EnergyStorage.SIDED.registerForBlockEntity(BatteryBlockEntity::getEnergyHandler, SFBlockEntityTypes.BATTERY_BOX);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.ELECTRIC_FURNACE);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.MACERATOR);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.COMPRESSOR);
        EnergyStorage.SIDED.registerForBlockEntity(ElectricInventoryBlockEntity::getEnergyHandler, SFBlockEntityTypes.EXTRACTOR);
        EnergyStorage.SIDED.registerForBlockEntity(CableBlockEntity::getEnergyHandler, SFBlockEntityTypes.CABLE);

        register(Registry.ITEM, "machine_block", new BlockItem(SFBlocks.MACHINE_BLOCK, SFItems.settings()));
        // Generators
        register(Registry.ITEM, "generator", new BlockItem(SFBlocks.GENERATOR, SFItems.settings()));
        register(Registry.ITEM, "solar_panel", new BlockItem(SFBlocks.SOLAR_PANEL, SFItems.settings()));
        register(Registry.ITEM, "dragon_egg_siphon", new BlockItem(SFBlocks.DRAGON_EGG_SIPHON, SFItems.settings().rarity(Rarity.EPIC)));
        // Energy Storage
        register(Registry.ITEM, "battery_box", new BlockItem(SFBlocks.BATTERY_BOX, SFItems.settings()));
        // Crafting Machines
        register(Registry.ITEM, "electric_furnace", new BlockItem(SFBlocks.ELECTRIC_FURNACE, SFItems.settings()));
        register(Registry.ITEM, "macerator", new BlockItem(SFBlocks.MACERATOR, SFItems.settings()));
        register(Registry.ITEM, "compressor", new BlockItem(SFBlocks.COMPRESSOR, SFItems.settings()));
        register(Registry.ITEM, "extractor", new BlockItem(SFBlocks.EXTRACTOR, SFItems.settings()));
        // Cables
        register(Registry.ITEM, "copper_wire", new BlockItem(SFBlocks.COPPER_WIRE, SFItems.settings()));
        register(Registry.ITEM, "copper_cable", new BlockItem(SFBlocks.COPPER_CABLE, SFItems.settings()));
        // Resources
        register(Registry.ITEM, "tin_ore", new BlockItem(SFBlocks.TIN_ORE, SFItems.settings()));
        register(Registry.ITEM, "deepslate_tin_ore", new BlockItem(SFBlocks.DEEPSLATE_TIN_ORE, SFItems.settings()));
        register(Registry.ITEM, "tin_block", new BlockItem(SFBlocks.TIN_BLOCK, SFItems.settings()));
        register(Registry.ITEM, "bronze_block", new BlockItem(SFBlocks.BRONZE_BLOCK, SFItems.settings()));
        register(Registry.ITEM, "iridium_block", new BlockItem(SFBlocks.IRIDIUM_BLOCK, SFItems.settings().rarity(Rarity.EPIC)));
        register(Registry.ITEM, "crystalite_block", new BlockItem(SFBlocks.CRYSTALITE_BLOCK, SFItems.settings().rarity(Rarity.UNCOMMON)));
        register(Registry.ITEM, "raw_tin_block", new BlockItem(SFBlocks.RAW_TIN_BLOCK, SFItems.settings()));
        register(Registry.ITEM, "raw_iridium_block", new BlockItem(SFBlocks.RAW_IRIDIUM_BLOCK, SFItems.settings().rarity(Rarity.EPIC)));

        register(Registry.ITEM, "rubber_log", new BlockItem(SFBlocks.RUBBER_LOG, SFItems.settings()));
        register(Registry.ITEM, "stripped_rubber_log", new BlockItem(SFBlocks.STRIPPED_RUBBER_LOG, SFItems.settings()));
        register(Registry.ITEM, "rubber_leaves", new BlockItem(SFBlocks.RUBBER_LEAVES, SFItems.settings()));
        register(Registry.ITEM, "rubber_sapling", new BlockItem(SFBlocks.RUBBER_SAPLING, SFItems.settings()));

        // Crafting Items
        register(Registry.ITEM, "sticky_resin", SFItems.STICKY_RESIN);
        register(Registry.ITEM, "rubber", SFItems.RUBBER);
        register(Registry.ITEM, "circuit", SFItems.CIRCUIT);
        register(Registry.ITEM, "arachnolactam", SFItems.ARACHNOLACTAM);
        register(Registry.ITEM, "hyperstrand", SFItems.HYPERSRAND);
        register(Registry.ITEM, "hyperweave", SFItems.HYPERWEAVE);
        register(Registry.ITEM, "crystalite", SFItems.CRYSTALITE);
        register(Registry.ITEM, "crystalite_matrix", SFItems.CRYSTALITE_MATRIX);
        register(Registry.ITEM, "ascended_circuit", SFItems.ASCENDED_CIRCUIT);
        register(Registry.ITEM, "vanovoltaic_cell", SFItems.VANOVOLTAIC_CELL);

        register(Registry.ITEM, "wire_cutters", SFItems.WIRE_CUTTERS);

        register(Registry.ITEM, "tin_ingot", SFItems.TIN_INGOT);
        register(Registry.ITEM, "bronze_ingot", SFItems.BRONZE_INGOT);
        register(Registry.ITEM, "refined_iron_ingot", SFItems.REFINED_IRON_INGOT);
        register(Registry.ITEM, "iridium_ingot", SFItems.IRIDIUM_INGOT);

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
        register(Registry.ITEM, "netherite_scrap_dust", SFItems.NETHERITE_SCRAP_DUST);
        register(Registry.ITEM, "raw_crystalite_dust", SFItems.RAW_CRYSTALITE_DUST);

        register(Registry.ITEM, "small_refined_iron_dust", SFItems.SMALL_REFINED_IRON_DUST);
        register(Registry.ITEM, "small_iridium_dust", SFItems.SMALL_IRIDIUM_DUST);

        register(Registry.ITEM, "raw_tin", SFItems.RAW_TIN);
        register(Registry.ITEM, "raw_iridium", SFItems.RAW_IRIDIUM);

        // Other
        register(Registry.ITEM, "bronze_sword", SFItems.BRONZE_SWORD);
        register(Registry.ITEM, "bronze_shovel", SFItems.BRONZE_SHOVEL);
        register(Registry.ITEM, "bronze_pickaxe", SFItems.BRONZE_PICKAXE);
        register(Registry.ITEM, "bronze_axe", SFItems.BRONZE_AXE);
        register(Registry.ITEM, "bronze_hoe", SFItems.BRONZE_HOE);
        register(Registry.ITEM, "bronze_helmet", SFItems.BRONZE_HELMET);
        register(Registry.ITEM, "bronze_chestplate", SFItems.BRONZE_CHESTPLATE);
        register(Registry.ITEM, "bronze_leggings", SFItems.BRONZE_LEGGINGS);
        register(Registry.ITEM, "bronze_boots", SFItems.BRONZE_BOOTS);

        FuelRegistry.INSTANCE.add(SFItems.RUBBERS, 100);

        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(SFBlocks.RUBBER_SAPLING.asItem(), 0.3F);
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(SFBlocks.RUBBER_LEAVES.asItem(), 0.3F);

        register(Registry.RECIPE_TYPE, "empty", SFRecipeTypes.EMPTY);
        register(Registry.RECIPE_TYPE, "macerating", SFRecipeTypes.MACERATING);
        register(Registry.RECIPE_TYPE, "compressing", SFRecipeTypes.COMPRESSING);
        register(Registry.RECIPE_TYPE, "extracting", SFRecipeTypes.EXTRACTING);

        register(Registry.RECIPE_SERIALIZER, "empty", SFRecipeSerializers.EMPTY);
        register(Registry.RECIPE_SERIALIZER, "conditional", SFRecipeSerializers.CONDITIONAL);
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
        private static final AbstractBlock.Settings MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(3F, 6F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(BlocksAccessor::invokeNever);
        private static final AbstractBlock.Settings ADVANCED_MACHINE_SETTINGS = FabricBlockSettings.of(AtMaterials.MACHINE).strength(10F, 30F).sounds(BlockSoundGroup.METAL).requiresTool().allowsSpawning(BlocksAccessor::invokeNever);

        public static final Block MACHINE_BLOCK = new Block(MACHINE_SETTINGS);
        // Generators
        public static final Block GENERATOR = new GeneratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));
        public static final Block SOLAR_PANEL = new SolarPanelBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.LAPIS_BLUE));
        public static final Block DRAGON_EGG_SIPHON = new DragonEggSiphonBlock(FabricBlockSettings.copyOf(ADVANCED_MACHINE_SETTINGS).luminance(state -> state.get(Properties.LIT) ? 15 : 0));
        // Energy Storage
        public static final Block BATTERY_BOX = new BatteryBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS).mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.WOOD));
        // Crafting Machines
        public static final Block ELECTRIC_FURNACE = new ElectricFurnaceBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block MACERATOR = new MaceratorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block COMPRESSOR = new CompressorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        public static final Block EXTRACTOR = new ExtractorBlock(FabricBlockSettings.copyOf(MACHINE_SETTINGS));
        // Cables
        public static final Block COPPER_WIRE = new CableBlock(1, FabricBlockSettings.of(Material.METAL).strength(0.5F).sounds(BlockSoundGroup.WOOL).breakByHand(true));
        public static final Block COPPER_CABLE = new CableBlock(2, FabricBlockSettings.of(Material.METAL).strength(0.5F).breakByHand(true));

        // Resources
        public static final Block TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(3F));
        public static final Block DEEPSLATE_TIN_ORE = new Block(FabricBlockSettings.of(Material.STONE).strength(4.5F, 3F));
        public static final Block TIN_BLOCK = new Block(TIN_SETTINGS);
        public static final Block BRONZE_BLOCK = new Block(BRONZE_SETTINGS);
        public static final Block IRIDIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5F, 20F).allowsSpawning(BlocksAccessor::invokeNever));
        public static final Block CRYSTALITE_BLOCK = new Block(FabricBlockSettings.of(Material.GLASS, MapColor.LIGHT_BLUE).luminance(15).strength(5F, 20F).sounds(BlockSoundGroup.GLASS).allowsSpawning(BlocksAccessor::invokeNever));
        public static final Block RAW_TIN_BLOCK = new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.LIGHT_GRAY).requiresTool().strength(5.0f, 6.0f));
        public static final Block RAW_IRIDIUM_BLOCK = new Block(AbstractBlock.Settings.of(Material.STONE, MapColor.WHITE).requiresTool().strength(7.0f, 10.0f));
        public static final Block RUBBER_LOG = new PillarBlock(RUBBER_LOG_SETTINGS);
        public static final Block ALIVE_RUBBER_LOG = new AliveRubberLogBlock(UNMOVABLE_RUBBER_LOG_SETTINGS);
        public static final Block RUBBER_LEAVES = new LeavesBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_LEAVES));
        public static final Block RUBBER_SAPLING = new RubberSaplingBlock(FabricBlockSettings.copyOf(Blocks.JUNGLE_SAPLING));
        public static final Block STRIPPED_RUBBER_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.YELLOW).strength(2F).sounds(BlockSoundGroup.WOOD));
    }

    public static class SFItems {
        public static final ItemGroup MACHINERY = FabricItemGroupBuilder.build(id("main"), () -> new ItemStack(SFBlocks.SOLAR_PANEL));

        private static Item.Settings settings() {
            return new Item.Settings().group(MACHINERY);
        }

        public static final Item STICKY_RESIN = new Item(settings());
        public static final Item RUBBER = new Item(settings());
        public static final Item CIRCUIT = new Item(settings());
        public static final Item ARACHNOLACTAM = new Item(settings());
        public static final Item HYPERSRAND = new Item(settings().rarity(Rarity.RARE));
        public static final Item HYPERWEAVE = new Item(settings().rarity(Rarity.RARE));
        public static final Item RAW_CRYSTALITE_DUST = new Item(settings());
        public static final Item CRYSTALITE = new Item(settings().rarity(Rarity.UNCOMMON));
        public static final Item CRYSTALITE_MATRIX = new Item(settings().rarity(Rarity.UNCOMMON));
        public static final Item ASCENDED_CIRCUIT = new Item(settings().rarity(Rarity.UNCOMMON));
        public static final Item VANOVOLTAIC_CELL = new VanovoltaicCellItem(settings().rarity(Rarity.EPIC));

        public static final Item TIN_INGOT = new Item(settings());
        public static final Item BRONZE_INGOT = new Item(settings());
        public static final Item REFINED_IRON_INGOT = new Item(settings());
        public static final Item IRIDIUM_INGOT = new Item(settings().rarity(Rarity.EPIC));

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
        public static final Item IRIDIUM_DUST = new Item(settings().rarity(Rarity.RARE));
        public static final Item NETHERITE_SCRAP_DUST = new Item(settings());

        public static final Item SMALL_REFINED_IRON_DUST = new Item(settings());
        public static final Item SMALL_IRIDIUM_DUST = new Item(settings().rarity(Rarity.RARE));

        public static final Item RAW_TIN = new Item(settings());
        public static final Item RAW_IRIDIUM = new Item(settings().rarity(Rarity.RARE));

        public static final Item WIRE_CUTTERS = new WireCuttersItem(settings().maxDamage(256));

        public static final Item BRONZE_SWORD = new SwordItem(AssortechToolMaterials.BRONZE, 3, -2.4F, settings());
        public static final Item BRONZE_SHOVEL = new ShovelItem(AssortechToolMaterials.BRONZE, 1.5F, -3.0F, settings());
        public static final Item BRONZE_PICKAXE = new AccessiblePickaxeItem(AssortechToolMaterials.BRONZE, 1, -2.8F, settings());
        public static final Item BRONZE_AXE = new AccessibleAxeItem(AssortechToolMaterials.BRONZE, 6, -3.1F, settings());
        public static final Item BRONZE_HOE = new AccessibleAxeItem(AssortechToolMaterials.BRONZE, -2, -1.0F, settings());
        public static final Item BRONZE_HELMET = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.HEAD, settings());
        public static final Item BRONZE_CHESTPLATE = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.CHEST, settings());
        public static final Item BRONZE_LEGGINGS = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.LEGS, settings());
        public static final Item BRONZE_BOOTS = new ArmorItem(AssortechArmorMaterials.BRONZE, EquipmentSlot.FEET, settings());

        public static final Tag<Item> RUBBERS = TagFactory.ITEM.create(new Identifier("c:rubbers"));
        public static final Tag<Item> BRONZE_INGOTS = TagFactory.ITEM.create(new Identifier("c:bronze_ingots"));
    }

    public static class SFBlockEntityTypes {
        public static final BlockEntityType<GeneratorBlockEntity> GENERATOR = FabricBlockEntityTypeBuilder.create(GeneratorBlockEntity::new, SFBlocks.GENERATOR).build();
        public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = FabricBlockEntityTypeBuilder.create(SolarPanelBlockEntity::new, SFBlocks.SOLAR_PANEL).build();
        public static final BlockEntityType<DragonEggSiphonBlockEntity> DRAGON_EGG_SIPHON = FabricBlockEntityTypeBuilder.create(DragonEggSiphonBlockEntity::new, SFBlocks.DRAGON_EGG_SIPHON).build();

        public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, SFBlocks.ELECTRIC_FURNACE).build();
        public static final BlockEntityType<MaceratorBlockEntity> MACERATOR = FabricBlockEntityTypeBuilder.create(MaceratorBlockEntity::new, SFBlocks.MACERATOR).build();
        public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = FabricBlockEntityTypeBuilder.create(CompressorBlockEntity::new, SFBlocks.COMPRESSOR).build();
        public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = FabricBlockEntityTypeBuilder.create(ExtractorBlockEntity::new, SFBlocks.EXTRACTOR).build();
        public static final BlockEntityType<BatteryBlockEntity> BATTERY_BOX = FabricBlockEntityTypeBuilder.create(BatteryBlockEntity::new, SFBlocks.BATTERY_BOX).build();
        public static final BlockEntityType<CableBlockEntity> CABLE = FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, SFBlocks.COPPER_WIRE, SFBlocks.COPPER_CABLE).build();
    }

    public static class SFRecipeTypes {
        public static final RecipeType<EmptyRecipe> EMPTY = new AtRecipeType<>();
        public static final RecipeType<MaceratorRecipe> MACERATING = new AtRecipeType<>();
        public static final RecipeType<CompressorRecipe> COMPRESSING = new AtRecipeType<>();
        public static final RecipeType<ExtractorRecipe> EXTRACTING = new AtRecipeType<>();

        private static class AtRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        }
    }

    public static class SFRecipeSerializers {
        public static final RecipeSerializer<EmptyRecipe> EMPTY = new EmptyRecipe.Serializer();
        public static final RecipeSerializer<?> CONDITIONAL = new ConditionalRecipeSerializer();
        public static final RecipeSerializer<MaceratorRecipe> MACERATING = new CraftingMachineRecipe.Serializer<>(MaceratorRecipe::new, 300);
        public static final RecipeSerializer<CompressorRecipe> COMPRESSING = new CraftingMachineRecipe.Serializer<>(CompressorRecipe::new, 400);
        public static final RecipeSerializer<ExtractorRecipe> EXTRACTING = new CraftingMachineRecipe.Serializer<>(ExtractorRecipe::new, 400);
    }

    public static class SFFoliagePlacers {
        public static final FoliagePlacerType<RubberFoliagePlacer> RUBBER = FoliagePlacerTypeInvoker.create(RubberFoliagePlacer.CODEC);
    }

    public static class SFFeatures {
        private static final BlockStateProvider RUBBER_LOGS = SimpleBlockStateProvider.of(SFBlocks.ALIVE_RUBBER_LOG);
        private static final BlockStateProvider RUBBER_LEAVES = SimpleBlockStateProvider.of(SFBlocks.RUBBER_LEAVES);

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
