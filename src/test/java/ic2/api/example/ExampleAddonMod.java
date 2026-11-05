package ic2.api.example;

import ic2.api.block.AddonWrenchableBlock;
import ic2.api.item.AddonElectricTool;
import ic2.api.registry.AddonEnergyCapabilities;
import ic2.api.registry.AddonMaterialSet;
import ic2.api.registry.Ic2AddonContent;
import ic2.api.upgrade.AddonUpgradeItem;
import ic2.api.upgrade.UpgradableProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Complete, compilable example showing how an add-on mod initializes and registers its content with
 * IC2 Rebranded on Minecraft 1.21.1 / NeoForge.
 */
public final class ExampleAddonMod {
  public static final String MOD_ID = "example_ic2_addon";
  public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

  // Wrenchable Generator Block and BlockEntity
  public static final DeferredBlock<AddonWrenchableBlock> GENERATOR_BLOCK =
      CONTENT.registerWrenchableBlock(
          "bio_generator",
          BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
          ExampleAddonMod::createGenerator);

  public static final DeferredItem<net.minecraft.world.item.BlockItem> GENERATOR_ITEM =
      CONTENT.registerBlockItem("bio_generator", GENERATOR_BLOCK);

  public static final DeferredHolder<
          BlockEntityType<?>, BlockEntityType<ExampleGeneratorBlockEntity>>
      GENERATOR_BE =
          CONTENT.registerBlockEntityType(
              "bio_generator", ExampleAddonMod::createGenerator, GENERATOR_BLOCK);

  // Wrenchable Processing Machine Block and BlockEntity
  public static final DeferredBlock<AddonWrenchableBlock> MACHINE_BLOCK =
      CONTENT.registerWrenchableBlock(
          "alloy_smelter",
          BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
          ExampleAddonMod::createMachine);

  public static final DeferredItem<net.minecraft.world.item.BlockItem> MACHINE_ITEM =
      CONTENT.registerBlockItem("alloy_smelter", MACHINE_BLOCK);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExampleMachineBlockEntity>>
      MACHINE_BE =
          CONTENT.registerBlockEntityType(
              "alloy_smelter", ExampleAddonMod::createMachine, MACHINE_BLOCK);

  // Electric Tool: 10,000 EU max charge, 100 EU/t transfer, Tier 1, 50 EU per use
  public static final DeferredItem<AddonElectricTool> ELECTRIC_WRENCH =
      CONTENT.registerElectricTool(
          "electric_wrench", new Item.Properties().stacksTo(1), 10000.0, 100.0, 1, 50.0);

  // Custom Machine Upgrade: 30% faster (0.7x time), 20% more power demand (1.2x EU)
  public static final DeferredItem<AddonUpgradeItem> SPEED_UPGRADE =
      CONTENT.registerUpgradeItem(
          "super_speed_upgrade",
          new Item.Properties().stacksTo(16),
          builder ->
              builder
                  .suitableFor(UpgradableProperty.Processing)
                  .processTimeMultiplier(0.7)
                  .energyDemandMultiplier(1.2));

  // Material set: auto-registers example_ic2_addon:titanium_ingot, dust, and plate
  public static final AddonMaterialSet TITANIUM =
      AddonMaterialSet.builder(CONTENT, "titanium")
          .itemForm("ingot")
          .itemForm("dust")
          .itemForm("plate")
          .build();

  // Creative Mode Tab
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
      CONTENT.registerCreativeModeTab(
          "main", () -> new ItemStack(Blocks.IRON_BLOCK), ExampleAddonMod::fillTab);

  private static ExampleGeneratorBlockEntity createGenerator(BlockPos pos, BlockState state) {
    return new ExampleGeneratorBlockEntity(GENERATOR_BE.get(), pos, state);
  }

  private static ExampleMachineBlockEntity createMachine(BlockPos pos, BlockState state) {
    return new ExampleMachineBlockEntity(MACHINE_BE.get(), pos, state);
  }

  private static void fillTab(
      CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
    output.accept(GENERATOR_BLOCK.get());
    output.accept(MACHINE_BLOCK.get());
    output.accept(ELECTRIC_WRENCH.get());
    output.accept(SPEED_UPGRADE.get());
  }

  /** Call this from your mod constructor. */
  public static void init(IEventBus modEventBus) {
    CONTENT.register(modEventBus);
    modEventBus.addListener(ExampleAddonMod::onRegisterCapabilities);
  }

  /** Exposes Forge Energy (FE) compatibility for your EU block entities. */
  public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
    AddonEnergyCapabilities.registerEnergyStorage(event, GENERATOR_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, MACHINE_BE.get());
  }
}
