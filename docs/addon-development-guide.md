# IC2 Rebranded Add-on Development Guide

Welcome to the definitive guide for creating add-ons for **IndustrialCraft 2: Refactored (IC2 Rebranded)** on **Minecraft 1.21.1** with **NeoForge**.

This guide covers everything you need to build, register, test, and ship custom machines, generators, energy storage, electric tools, armors, machine upgrades, and recipes.

---

## Table of Contents
1. [Architecture Overview](#1-architecture-overview)
2. [Setting Up Your Add-on Project](#2-setting-up-your-add-on-project)
3. [Registration with `Ic2AddonContent`](#3-registration-with-ic2addoncontent)
4. [Creating Custom Machines](#4-creating-custom-machines)
5. [Creating Custom Generators](#5-creating-custom-generators)
6. [Creating Energy Storage Blocks](#6-creating-energy-storage-blocks)
7. [Creating Energy Cables](#7-creating-energy-cables)
8. [Electric Tools and Weapons](#8-electric-tools-and-weapons)
9. [Custom Machine Upgrades](#9-custom-machine-upgrades)
10. [Machine Recipes and Data Generation](#10-machine-recipes-and-data-generation)
11. [Testing Your Add-on](#11-testing-your-add-on)

---

## 1. Architecture Overview

### Energy Units (EU) and Voltage Tiers
IC2 Rebranded operates on **Energy Units (EU)**. The system is split into four standard voltage tiers:

| Tier | Name | Max Voltage | Default Cable / Machine |
|:---:|:---:|:---:|:---|
| `0` | Micro / Ultra Low | 8 EU/t | Tin cable / small devices |
| `1` | Low Voltage (LV) | 32 EU/t | Copper cable, standard machines, basic generators |
| `2` | Medium Voltage (MV) | 128 EU/t | Gold cable, advanced machines, CESU |
| `3` | High Voltage (HV) | 512 EU/t | HV cable, MFE, large generators |

### Forge Energy (FE) Interoperability
All IC2R add-on energy bases (`AddonEnergyBlockEntity`, `AddonMachineBlockEntity`, `AddonGeneratorBlockEntity`, `AddonEnergyStorageBlockEntity`, `AddonEnergyCableBlockEntity`) include a built-in bidirectional Forge Energy bridge:
- **Conversion Ratio**: `1 EU = 2 FE` (`AddonEnergyBlockEntity.FE_PER_EU = 2.0`).
- To expose FE capabilities to other mods in NeoForge, simply register your BlockEntityType with `AddonEnergyCapabilities.registerEnergyStorage` during `RegisterCapabilitiesEvent`.

---

## 2. Setting Up Your Add-on Project

### Recommended Gradle Setup (`build.gradle`)
In your add-on mod project:

```groovy
plugins {
    id 'java-library'
    id 'net.neoforged.moddev' version '2.0.141'
}

dependencies {
    // Depend on IC2 Rebranded (Maven or flat-dir)
    implementation "me.halfcooler.ic2r.core:ic2r-neoforge:1.0.3"
}
```

### Mod Metadata (`neoforge.mods.toml`)
Declare IC2 as a dependency so NeoForge loads it first:

```toml
[[dependencies.your_addon_id]]
modId = "ic2"
type = "required"
versionRange = "[1.0.3,)"
ordering = "AFTER"
side = "BOTH"
```

---

## 3. Registration with `Ic2AddonContent`

`Ic2AddonContent` is the central registration manager for your add-on. Create a single instance per mod and register it once during your mod constructor:

```java
package com.example.addon;

import ic2.api.registry.Ic2AddonContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ExampleAddon.MOD_ID)
public class ExampleAddon {
    public static final String MOD_ID = "example_addon";
    public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

    public ExampleAddon(IEventBus modEventBus) {
        // Register all items, blocks, menus, recipes, and creative tabs to NeoForge
        CONTENT.register(modEventBus);
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // Expose FE energy bridge for custom block entities
        AddonEnergyCapabilities.registerEnergyStorage(event, MyContent.MY_MACHINE_BE.get());
    }
}
```

### Custom Creative Mode Tab
Create a dedicated creative tab in one line:

```java
public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
    CONTENT.registerCreativeModeTab(
        "main",
        () -> new ItemStack(MyContent.MY_MACHINE_BLOCK.get()),
        (parameters, output) -> {
            output.accept(MyContent.MY_MACHINE_BLOCK.get());
            output.accept(MyContent.MY_ELECTRIC_TOOL.get());
        });
```

### Material Sets
Quickly register a set of resources (e.g. ingots, plates, dusts, blocks) with consistent naming:

```java
public static final AddonMaterialSet TITANIUM =
    AddonMaterialSet.builder(CONTENT, "titanium")
        .itemForm("ingot")
        .itemForm("dust")
        .itemForm("plate")
        .blockForm("block", BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))
        .build();
```

---

## 4. Creating Custom Machines

Extend `AddonMachineBlockEntity` for processing machines and pair it with `AddonWrenchableBlock`:

### Machine BlockEntity
```java
package com.example.addon.block;

import ic2.api.block.AddonMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AlloySmelterBlockEntity extends AddonMachineBlockEntity {

    public AlloySmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        // Tier 1 (LV: 32V), 10,000 EU buffer, 32 EU/t max input
        super(type, pos, state, 1, 10000.0, 32.0);
    }

    @Override
    public int getProcessingTime() {
        return 100; // 5 seconds (100 ticks)
    }

    @Override
    public int getEnergyPerTick() {
        return 4; // 4 EU/t
    }

    @Override
    public boolean canProcess() {
        // Return true if input inventory has items and output slot is not full
        return true;
    }

    @Override
    public void processOperation() {
        // Consume input items and place product into output slot
    }
}
```

### Registering the Wrenchable Block
`AddonWrenchableBlock` automatically provides:
- Horizontal rotation matching player facing on placement.
- Full compatibility with IC2 Wrench and Electric Wrench.
- Safe dismantling with item drops.

```java
public static final DeferredBlock<AddonWrenchableBlock> ALLOY_SMELTER =
    CONTENT.registerWrenchableBlock(
        "alloy_smelter",
        BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK),
        (pos, state) -> new AlloySmelterBlockEntity(ALLOY_SMELTER_BE.get(), pos, state));

public static final DeferredItem<BlockItem> ALLOY_SMELTER_ITEM =
    CONTENT.registerBlockItem("alloy_smelter", ALLOY_SMELTER);

public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloySmelterBlockEntity>>
    ALLOY_SMELTER_BE =
        CONTENT.registerBlockEntityType(
            "alloy_smelter",
            (pos, state) -> new AlloySmelterBlockEntity(ALLOY_SMELTER_BE.get(), pos, state),
            ALLOY_SMELTER);
```

---

## 5. Creating Custom Generators

Extend `AddonGeneratorBlockEntity` to create custom generators (solar, bio, geothermal, kinetic):

```java
package com.example.addon.block;

import ic2.api.block.AddonGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BioGeneratorBlockEntity extends AddonGeneratorBlockEntity {

    public BioGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        // 32 EU/t output, Tier 1, 40,000 EU buffer, 32 EU/t max output
        super(type, pos, state, 32.0, 1, 40000, 32.0);
    }

    @Override
    protected boolean gainFuel() {
        // Check for fuel item/fluid in inventory and burn ticks
        // Return true if generator should produce EU this tick
        return true;
    }
}
```

---

## 6. Creating Energy Storage Blocks

Extend `AddonEnergyStorageBlockEntity` for batteries and power accumulators:

```java
public class SuperBatteryBlockEntity extends AddonEnergyStorageBlockEntity {
    public SuperBatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        // Tier 2 (MV: 128V), 1,000,000 EU capacity, 128 EU/t input, 128 EU/t output
        super(type, pos, state, 2, 1000000.0, 128.0, 128.0);
    }
}
```

---

## 7. Creating Energy Cables

Extend `AddonEnergyCableBlockEntity` to create custom conductors:

```java
public class SuperconductorCableBlockEntity extends AddonEnergyCableBlockEntity {
    public SuperconductorCableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        // capacity, transferLimit, tier, conductionLoss, insulationAbsorption, insulationBreakdown, conductorBreakdown
        super(type, pos, state, 10000.0, 512.0, 3, 0.05, 100.0, 512.0, 2048.0);
    }
}
```

---

## 8. Electric Tools and Weapons

### Electric Tool (`AddonElectricTool`)
Create tools powered by EU that integrate with battery chargers, charge pads, and tooltips:

```java
public static final DeferredItem<AddonElectricTool> ELECTRIC_WRENCH =
    CONTENT.registerElectricTool(
        "electric_wrench",
        new Item.Properties().stacksTo(1),
        10000.0,  // Max charge (10,000 EU)
        100.0,    // Recharge rate (100 EU/t)
        1,        // Tier 1
        50.0      // Cost per operation (50 EU)
    );
```

### Mining Tools & Weapons
- `CONTENT.registerElectricMiningTool(...)`: Configurable drills and chainsaws.
- `CONTENT.registerElectricWeapon(...)`: Charge-powered weapons like nanosabers.
- `CONTENT.registerElectricArmor(...)`: Nano / Quantum suit armor pieces.
- `CONTENT.registerJetpack(...)`: Configurable flying jetpacks.

---

## 9. Custom Machine Upgrades

Create custom machine modules using `AddonUpgradeItem` or attach `UpgradeDefinition` to your own item:

```java
// Overclock / Speed Upgrade: 30% faster (0.7x time), 25% higher power demand (1.25x EU)
public static final DeferredItem<AddonUpgradeItem> ADVANCED_SPEED_UPGRADE =
    CONTENT.registerUpgradeItem(
        "advanced_speed_upgrade",
        new Item.Properties().stacksTo(16),
        builder -> builder
            .suitableFor(UpgradableProperty.Processing)
            .processTimeMultiplier(0.7)
            .energyDemandMultiplier(1.25)
    );

// Energy Buffer Upgrade: Adds +20,000 EU capacity to machines
public static final DeferredItem<AddonUpgradeItem> ENERGY_BUFFER_UPGRADE =
    CONTENT.registerUpgradeItem(
        "energy_buffer_upgrade",
        new Item.Properties().stacksTo(16),
        builder -> builder
            .suitableFor(UpgradableProperty.EnergyStorage)
            .extraEnergyStorage(20000)
    );
```

You can also create standalone definitions with `UpgradeDefinition`:
```java
UpgradeDefinition speedDef = UpgradeDefinition.speed(0.75, 1.3);
```

---

## 10. Machine Recipes and Data Generation

### Recipe JSON Format
IC2 Rebranded machines load recipes directly from standard JSON files in `data/<modid>/recipe/<machine>/`.

#### Macerator (`data/your_addon/recipe/macerator/titanium_dust.json`)
```json
{
  "type": "ic2:macerator",
  "ingredient": {
    "item": "example_addon:titanium_ingot"
  },
  "result": {
    "item": "example_addon:titanium_dust"
  }
}
```

#### Compressor (`data/your_addon/recipe/compressor/dense_plate.json`)
```json
{
  "type": "ic2:compressor",
  "ingredient": {
    "item": "example_addon:titanium_plate",
    "count": 9
  },
  "result": {
    "item": "example_addon:dense_titanium_plate"
  }
}
```

#### Centrifuge (`data/your_addon/recipe/centrifuge/refined_dust.json`)
```json
{
  "type": "ic2:centrifuge",
  "ingredient": {
    "item": "example_addon:crude_dust"
  },
  "result": {
    "item": "example_addon:titanium_dust"
  },
  "minHeat": 250
}
```

#### Blast Furnace (`data/your_addon/recipe/blast_furnace/titanium_alloy.json`)
```json
{
  "type": "ic2:blast_furnace",
  "ingredient": {
    "item": "example_addon:titanium_dust"
  },
  "result": [
    { "item": "example_addon:titanium_ingot" },
    { "item": "ic2:slag" }
  ],
  "fluid": 1,
  "duration": 6000
}
```

### Fluent Recipe Generation (`AddonRecipeBuilder`)
Generate recipe JSON programmatically in your NeoForge datagen providers or tests:

```java
JsonObject maceratorRecipe =
    AddonRecipeBuilder.macerator()
        .inputItem("example_addon:titanium_ingot")
        .output("example_addon:titanium_dust")
        .build();

JsonObject blastFurnaceRecipe =
    AddonRecipeBuilder.blastFurnace()
        .inputItem("example_addon:crushed_ore")
        .output("example_addon:titanium_ingot")
        .output("ic2:slag")
        .fluid(1)
        .duration(4000)
        .build();
```

---

## 11. Testing Your Add-on

1. **Unit Testing**: You can test machine progress math, energy ratios, and recipe JSON outputs in lightweight JUnit 5 tests.
2. **GameTests**: Register game tests under your mod namespace to verify block entity ticking and item charge in simulated game environments.
3. **Reference Implementation**: Inspect the complete, working test addon in `src/test/java/ic2/api/example/ExampleAddonMod.java` for copy-pasteable patterns!
