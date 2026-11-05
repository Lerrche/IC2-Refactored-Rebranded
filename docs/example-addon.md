# Complete Example Add-on Mod

Here is a complete, copy-pasteable reference add-on mod demonstrating the entire IC2 Rebranded add-on lifecycle:
- Content registration with `Ic2AddonContent`
- Wrenchable Generator block with active lighting
- Wrenchable Processing Machine block
- Electric Tool (Wrench)
- Custom Machine Upgrade
- Industrial Material Set (Ingot, Dust, Plate)
- Dedicated Creative Mode Tab
- Forge Energy (FE) capability registration

---

## `ExampleAddonMod.java`

```java
package com.example.myaddon;

import ic2.api.block.AddonEnergyBlock;
import ic2.api.block.AddonWrenchableBlock;
import ic2.api.item.AddonElectricTool;
import ic2.api.registry.AddonEnergyCapabilities;
import ic2.api.registry.AddonMaterialSet;
import ic2.api.registry.Ic2AddonContent;
import ic2.api.upgrade.AddonUpgradeItem;
import ic2.api.upgrade.UpgradableProperty;
import ic2.core.block.tileentity.Ic2TileEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

@Mod(ExampleAddonMod.MOD_ID)
public final class ExampleAddonMod {
    public static final String MOD_ID = "example_ic2_addon";

    // 1. Central registration manager
    public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

    // 2. Wrenchable Generator with Active Dynamic Lighting
    public static final DeferredBlock<AddonWrenchableBlock> GENERATOR_BLOCK =
        CONTENT.registerWrenchableBlock(
            "bio_generator",
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .lightLevel(AddonEnergyBlock.litWhenActive(Ic2TileEntityBlock.ACTIVE, 13)),
            ExampleAddonMod::createGenerator
        );

    public static final DeferredItem<net.minecraft.world.item.BlockItem> GENERATOR_ITEM =
        CONTENT.registerBlockItem("bio_generator", GENERATOR_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExampleGeneratorBlockEntity>>
        GENERATOR_BE = CONTENT.registerBlockEntityType(
            "bio_generator", ExampleAddonMod::createGenerator, GENERATOR_BLOCK
        );

    // 3. Wrenchable Processing Machine
    public static final DeferredBlock<AddonWrenchableBlock> MACHINE_BLOCK =
        CONTENT.registerWrenchableBlock(
            "alloy_smelter",
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
            ExampleAddonMod::createMachine
        );

    public static final DeferredItem<net.minecraft.world.item.BlockItem> MACHINE_ITEM =
        CONTENT.registerBlockItem("alloy_smelter", MACHINE_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExampleMachineBlockEntity>>
        MACHINE_BE = CONTENT.registerBlockEntityType(
            "alloy_smelter", ExampleAddonMod::createMachine, MACHINE_BLOCK
        );

    // 4. Electric Tool (10k EU capacity, 100 EU/t transfer, Tier 1, 50 EU per use)
    public static final DeferredItem<AddonElectricTool> ELECTRIC_WRENCH =
        CONTENT.registerElectricTool(
            "electric_wrench",
            new Item.Properties().stacksTo(1),
            10_000.0,
            100.0,
            1,
            50.0
        );

    // 5. Custom Machine Upgrade: 30% faster (0.7x time), 20% more power demand
    public static final DeferredItem<AddonUpgradeItem> SPEED_UPGRADE =
        CONTENT.registerUpgradeItem(
            "super_speed_upgrade",
            new Item.Properties().stacksTo(16),
            builder -> builder
                .suitableFor(UpgradableProperty.Processing)
                .processTimeMultiplier(0.7)
                .energyDemandMultiplier(1.2)
        );

    // 6. Complete Material Set (Ingot, Dust, Plate)
    public static final AddonMaterialSet TITANIUM =
        AddonMaterialSet.builder(CONTENT, "titanium")
            .itemForm("ingot")
            .itemForm("dust")
            .itemForm("plate")
            .build();

    // 7. Creative Mode Tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
        CONTENT.registerCreativeModeTab(
            "main",
            () -> new ItemStack(MACHINE_ITEM.get()),
            ExampleAddonMod::fillTab
        );

    // 8. Mod Constructor
    public ExampleAddonMod(IEventBus modEventBus) {
        // Register all content to NeoForge with 1 line
        CONTENT.register(modEventBus);

        // Register FE capabilities for machines & generators
        modEventBus.addListener(this::registerCapabilities);
    }

    private static ExampleGeneratorBlockEntity createGenerator(BlockPos pos, BlockState state) {
        return new ExampleGeneratorBlockEntity(GENERATOR_BE.get(), pos, state);
    }

    private static ExampleMachineBlockEntity createMachine(BlockPos pos, BlockState state) {
        return new ExampleMachineBlockEntity(MACHINE_BE.get(), pos, state);
    }

    private static void fillTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        output.accept(GENERATOR_ITEM.get());
        output.accept(MACHINE_ITEM.get());
        output.accept(ELECTRIC_WRENCH.get());
        output.accept(SPEED_UPGRADE.get());
        output.accept(TITANIUM.getItem("ingot"));
        output.accept(TITANIUM.getItem("dust"));
        output.accept(TITANIUM.getItem("plate"));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Expose Forge Energy (FE) to other mods: 1 EU = 2 FE
        AddonEnergyCapabilities.registerEnergyStorage(event, GENERATOR_BE.get());
        AddonEnergyCapabilities.registerEnergyStorage(event, MACHINE_BE.get());
    }
}
```
