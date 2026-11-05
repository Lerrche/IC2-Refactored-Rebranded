package ic2.advsolar;

import ic2.advsolar.blocks.MolecularTransformerBlock;
import ic2.advsolar.blocks.SolarPanelBlock;
import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
import ic2.advsolar.blocks.entity.QuantumGeneratorBlockEntity;
import ic2.advsolar.blocks.entity.SolarPanelBlockEntity;
import ic2.advsolar.client.AdvancedSolarPanelsClient;
import ic2.advsolar.gui.MolecularTransformerMenu;
import ic2.advsolar.gui.QuantumGeneratorMenu;
import ic2.advsolar.gui.SolarPanelMenu;
import ic2.advsolar.items.MolecularTransformerBlockItem;
import ic2.advsolar.items.QuantumGeneratorBlockItem;
import ic2.advsolar.items.SolarHelmetItem;
import ic2.advsolar.items.SolarPanelBlockItem;
import ic2.api.registry.AddonEnergyCapabilities;
import ic2.api.registry.Ic2AddonContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AdvancedSolarPanelsMod.MOD_ID)
public class AdvancedSolarPanelsMod {
  public static final String MOD_ID = "advanced_solar_panels";
  public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);
  public static final DeferredRegister<MenuType<?>> MENUS =
      DeferredRegister.create(Registries.MENU, MOD_ID);

  public static final DeferredHolder<MenuType<?>, MenuType<SolarPanelMenu>> SOLAR_PANEL_MENU =
      MENUS.register("solar_panel", () -> IMenuTypeExtension.create(SolarPanelMenu::new));

  public static final DeferredHolder<MenuType<?>, MenuType<QuantumGeneratorMenu>>
      QUANTUM_GENERATOR_MENU =
          MENUS.register(
              "quantum_generator", () -> IMenuTypeExtension.create(QuantumGeneratorMenu::new));

  public static final DeferredHolder<MenuType<?>, MenuType<MolecularTransformerMenu>>
      MOLECULAR_TRANSFORMER_MENU =
          MENUS.register(
              "molecular_transformer",
              () -> IMenuTypeExtension.create(MolecularTransformerMenu::new));

  private static BlockBehaviour.Properties panelProps() {
    return BlockBehaviour.Properties.of()
        .strength(3.0F, 6.0F)
        .sound(SoundType.METAL)
        .noOcclusion()
        .requiresCorrectToolForDrops();
  }

  private static SolarPanelBlockEntity createAdvanced(BlockPos pos, BlockState state) {
    return new SolarPanelBlockEntity(
        ADVANCED_SOLAR_PANEL_BE.get(), pos, state, 8.0, 1.0, 32_000.0, 1, 32.0);
  }

  private static SolarPanelBlockEntity createHybrid(BlockPos pos, BlockState state) {
    return new SolarPanelBlockEntity(
        HYBRID_SOLAR_PANEL_BE.get(), pos, state, 64.0, 8.0, 100_000.0, 2, 128.0);
  }

  private static SolarPanelBlockEntity createUltimate(BlockPos pos, BlockState state) {
    return new SolarPanelBlockEntity(
        ULTIMATE_HYBRID_SOLAR_PANEL_BE.get(), pos, state, 512.0, 64.0, 1_000_000.0, 3, 512.0);
  }

  private static SolarPanelBlockEntity createQuantum(BlockPos pos, BlockState state) {
    return new SolarPanelBlockEntity(
        QUANTUM_SOLAR_PANEL_BE.get(), pos, state, 4_096.0, 2_048.0, 10_000_000.0, 4, 4_096.0);
  }

  private static QuantumGeneratorBlockEntity createQuantumGen(BlockPos pos, BlockState state) {
    return new QuantumGeneratorBlockEntity(QUANTUM_GENERATOR_BE.get(), pos, state);
  }

  private static MolecularTransformerBlockEntity createMolecular(BlockPos pos, BlockState state) {
    return new MolecularTransformerBlockEntity(MOLECULAR_TRANSFORMER_BE.get(), pos, state);
  }

  // Blocks
  public static final DeferredBlock<SolarPanelBlock> ADVANCED_SOLAR_PANEL =
      CONTENT.registerBlock(
          "advanced_solar_panel",
          props -> new SolarPanelBlock(props, AdvancedSolarPanelsMod::createAdvanced),
          panelProps());

  public static final DeferredBlock<SolarPanelBlock> HYBRID_SOLAR_PANEL =
      CONTENT.registerBlock(
          "hybrid_solar_panel",
          props -> new SolarPanelBlock(props, AdvancedSolarPanelsMod::createHybrid),
          panelProps());

  public static final DeferredBlock<SolarPanelBlock> ULTIMATE_HYBRID_SOLAR_PANEL =
      CONTENT.registerBlock(
          "ultimate_hybrid_solar_panel",
          props -> new SolarPanelBlock(props, AdvancedSolarPanelsMod::createUltimate),
          panelProps());

  public static final DeferredBlock<SolarPanelBlock> QUANTUM_SOLAR_PANEL =
      CONTENT.registerBlock(
          "quantum_solar_panel",
          props -> new SolarPanelBlock(props, AdvancedSolarPanelsMod::createQuantum),
          panelProps());

  public static final DeferredBlock<SolarPanelBlock> QUANTUM_GENERATOR =
      CONTENT.registerBlock(
          "quantum_generator",
          props -> new SolarPanelBlock(props, AdvancedSolarPanelsMod::createQuantumGen),
          panelProps().lightLevel(state -> 15));

  public static final DeferredBlock<MolecularTransformerBlock> MOLECULAR_TRANSFORMER =
      CONTENT.registerBlock(
          "molecular_transformer",
          props -> new MolecularTransformerBlock(props, AdvancedSolarPanelsMod::createMolecular),
          panelProps().strength(4.0F, 10.0F).lightLevel(state -> 7));

  // Block Entities
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>
      ADVANCED_SOLAR_PANEL_BE =
          CONTENT.registerBlockEntityType(
              "advanced_solar_panel", AdvancedSolarPanelsMod::createAdvanced, ADVANCED_SOLAR_PANEL);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>
      HYBRID_SOLAR_PANEL_BE =
          CONTENT.registerBlockEntityType(
              "hybrid_solar_panel", AdvancedSolarPanelsMod::createHybrid, HYBRID_SOLAR_PANEL);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>
      ULTIMATE_HYBRID_SOLAR_PANEL_BE =
          CONTENT.registerBlockEntityType(
              "ultimate_hybrid_solar_panel",
              AdvancedSolarPanelsMod::createUltimate,
              ULTIMATE_HYBRID_SOLAR_PANEL);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>>
      QUANTUM_SOLAR_PANEL_BE =
          CONTENT.registerBlockEntityType(
              "quantum_solar_panel", AdvancedSolarPanelsMod::createQuantum, QUANTUM_SOLAR_PANEL);

  public static final DeferredHolder<
          BlockEntityType<?>, BlockEntityType<QuantumGeneratorBlockEntity>>
      QUANTUM_GENERATOR_BE =
          CONTENT.registerBlockEntityType(
              "quantum_generator", AdvancedSolarPanelsMod::createQuantumGen, QUANTUM_GENERATOR);

  public static final DeferredHolder<
          BlockEntityType<?>, BlockEntityType<MolecularTransformerBlockEntity>>
      MOLECULAR_TRANSFORMER_BE =
          CONTENT.registerBlockEntityType(
              "molecular_transformer",
              AdvancedSolarPanelsMod::createMolecular,
              MOLECULAR_TRANSFORMER);

  // Block Items with Power Tier and Statistics Tooltips
  public static final DeferredItem<SolarPanelBlockItem> ADVANCED_SOLAR_PANEL_ITEM =
      CONTENT.registerItem(
          "advanced_solar_panel",
          () ->
              new SolarPanelBlockItem(
                  ADVANCED_SOLAR_PANEL.get(),
                  new Item.Properties().rarity(Rarity.UNCOMMON),
                  1,
                  8,
                  1,
                  32,
                  32_000));
  public static final DeferredItem<SolarPanelBlockItem> HYBRID_SOLAR_PANEL_ITEM =
      CONTENT.registerItem(
          "hybrid_solar_panel",
          () ->
              new SolarPanelBlockItem(
                  HYBRID_SOLAR_PANEL.get(),
                  new Item.Properties().rarity(Rarity.RARE),
                  2,
                  64,
                  8,
                  128,
                  100_000));
  public static final DeferredItem<SolarPanelBlockItem> ULTIMATE_HYBRID_SOLAR_PANEL_ITEM =
      CONTENT.registerItem(
          "ultimate_hybrid_solar_panel",
          () ->
              new SolarPanelBlockItem(
                  ULTIMATE_HYBRID_SOLAR_PANEL.get(),
                  new Item.Properties().rarity(Rarity.RARE),
                  3,
                  512,
                  64,
                  512,
                  1_000_000));
  public static final DeferredItem<SolarPanelBlockItem> QUANTUM_SOLAR_PANEL_ITEM =
      CONTENT.registerItem(
          "quantum_solar_panel",
          () ->
              new SolarPanelBlockItem(
                  QUANTUM_SOLAR_PANEL.get(),
                  new Item.Properties().rarity(Rarity.EPIC),
                  4,
                  4_096,
                  2_048,
                  4_096,
                  10_000_000));
  public static final DeferredItem<QuantumGeneratorBlockItem> QUANTUM_GENERATOR_ITEM =
      CONTENT.registerItem(
          "quantum_generator",
          () ->
              new QuantumGeneratorBlockItem(
                  QUANTUM_GENERATOR.get(), new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<MolecularTransformerBlockItem> MOLECULAR_TRANSFORMER_ITEM =
      CONTENT.registerItem(
          "molecular_transformer",
          () ->
              new MolecularTransformerBlockItem(
                  MOLECULAR_TRANSFORMER.get(), new Item.Properties().rarity(Rarity.EPIC)));

  // Solar Helmets
  public static final DeferredItem<SolarHelmetItem> ADVANCED_SOLAR_HELMET =
      CONTENT.registerItem("advanced_solar_helmet", SolarHelmetItem::createAdvanced);
  public static final DeferredItem<SolarHelmetItem> HYBRID_SOLAR_HELMET =
      CONTENT.registerItem("hybrid_solar_helmet", SolarHelmetItem::createHybrid);
  public static final DeferredItem<SolarHelmetItem> ULTIMATE_SOLAR_HELMET =
      CONTENT.registerItem("ultimate_solar_helmet", SolarHelmetItem::createUltimate);

  // Crafting Items
  public static final DeferredItem<Item> SUNNARIUM =
      CONTENT.registerItem(
          "sunnarium", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
  public static final DeferredItem<Item> SUNNARIUM_PART =
      CONTENT.registerItem(
          "sunnarium_part", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
  public static final DeferredItem<Item> SUNNARIUM_ALLOY =
      CONTENT.registerItem(
          "sunnarium_alloy", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> ENRICHED_SUNNARIUM =
      CONTENT.registerItem(
          "enriched_sunnarium", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> ENRICHED_SUNNARIUM_ALLOY =
      CONTENT.registerItem(
          "enriched_sunnarium_alloy", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<Item> IRRADIANT_URANIUM =
      CONTENT.registerItem(
          "irradiant_uranium", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> IRRADIANT_GLASS_PANE =
      CONTENT.registerItem(
          "irradiant_glass_pane", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
  public static final DeferredItem<Item> IRIDIUM_IRON_PLATE =
      CONTENT.registerItem(
          "iridium_iron_plate", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> REINFORCED_IRIDIUM_IRON_PLATE =
      CONTENT.registerItem(
          "reinforced_iridium_iron_plate",
          () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<Item> IRRADIANT_REINFORCED_PLATE =
      CONTENT.registerItem(
          "irradiant_reinforced_plate", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<Item> MT_CORE =
      CONTENT.registerItem(
          "mt_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
  public static final DeferredItem<Item> QUANTUM_CORE =
      CONTENT.registerItem(
          "quantum_core", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

  // Creative Tab
  static {
    CONTENT.registerCreativeModeTab(
        "main",
        () -> new ItemStack(ULTIMATE_HYBRID_SOLAR_PANEL_ITEM.get()),
        (params, output) -> {
          output.accept(ADVANCED_SOLAR_PANEL_ITEM.get());
          output.accept(HYBRID_SOLAR_PANEL_ITEM.get());
          output.accept(ULTIMATE_HYBRID_SOLAR_PANEL_ITEM.get());
          output.accept(QUANTUM_SOLAR_PANEL_ITEM.get());
          output.accept(QUANTUM_GENERATOR_ITEM.get());
          output.accept(MOLECULAR_TRANSFORMER_ITEM.get());

          output.accept(ADVANCED_SOLAR_HELMET.get());
          output.accept(HYBRID_SOLAR_HELMET.get());
          output.accept(ULTIMATE_SOLAR_HELMET.get());

          output.accept(SUNNARIUM.get());
          output.accept(SUNNARIUM_PART.get());
          output.accept(SUNNARIUM_ALLOY.get());
          output.accept(ENRICHED_SUNNARIUM.get());
          output.accept(ENRICHED_SUNNARIUM_ALLOY.get());
          output.accept(IRRADIANT_URANIUM.get());
          output.accept(IRRADIANT_GLASS_PANE.get());
          output.accept(IRIDIUM_IRON_PLATE.get());
          output.accept(REINFORCED_IRIDIUM_IRON_PLATE.get());
          output.accept(IRRADIANT_REINFORCED_PLATE.get());
          output.accept(MT_CORE.get());
          output.accept(QUANTUM_CORE.get());
        });
  }

  public AdvancedSolarPanelsMod(IEventBus modEventBus) {
    CONTENT.register(modEventBus);
    MENUS.register(modEventBus);
    modEventBus.addListener(this::registerCapabilities);
    modEventBus.addListener(this::commonSetup);
    if (FMLEnvironment.dist.isClient()) {
      modEventBus.addListener(AdvancedSolarPanelsClient::registerScreens);
      modEventBus.addListener(AdvancedSolarPanelsClient::registerRenderers);
    }
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(
        () -> {
          MolecularTransformerBlockEntity.registerRecipe(
              Ingredient.of(SUNNARIUM.get()), new ItemStack(Items.NETHER_STAR), 250_000_000.0);
          MolecularTransformerBlockEntity.registerRecipe(
              Ingredient.of(Blocks.GLOWSTONE), new ItemStack(SUNNARIUM.get()), 1_000_000.0);
          MolecularTransformerBlockEntity.registerRecipe(
              Ingredient.of(Items.GLOWSTONE_DUST), new ItemStack(SUNNARIUM_PART.get()), 120_000.0);
        });
  }

  private void registerCapabilities(RegisterCapabilitiesEvent event) {
    AddonEnergyCapabilities.registerEnergyStorage(event, ADVANCED_SOLAR_PANEL_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, HYBRID_SOLAR_PANEL_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, ULTIMATE_HYBRID_SOLAR_PANEL_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, QUANTUM_SOLAR_PANEL_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, QUANTUM_GENERATOR_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, MOLECULAR_TRANSFORMER_BE.get());

    event.registerBlockEntity(
        Capabilities.ItemHandler.BLOCK,
        MOLECULAR_TRANSFORMER_BE.get(),
        (be, side) -> be.getItemHandler());
  }
}
