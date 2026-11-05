# Chapter 1: Registration with `Ic2AddonContent`

`Ic2AddonContent` is the central registration manager for your add-on. Instead of creating and managing dozens of separate `DeferredRegister` objects for blocks, items, block entities, menus, recipes, and creative tabs, `Ic2AddonContent` wraps everything into a single, clean API.

---

## 1. Creating the Manager

In your main mod class, create a single `public static final` instance of `Ic2AddonContent` with your mod ID:

```java
package com.example.myaddon;

import ic2.api.registry.AddonEnergyCapabilities;
import ic2.api.registry.Ic2AddonContent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(MyAddon.MOD_ID)
public class MyAddon {
    public static final String MOD_ID = "myaddon";

    // 1. Create your add-on content manager
    public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

    public MyAddon(IEventBus modEventBus) {
        // 2. Register all deferred content to NeoForge in a single call
        CONTENT.register(modEventBus);

        // 3. Register Forge Energy (FE) capabilities
        modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Expose FE capability for any custom energy blocks
        // AddonEnergyCapabilities.registerEnergyStorage(event, MyContent.MY_MACHINE_BE.get());
    }
}
```

---

## 2. Registering a Creative Mode Tab

Create a dedicated tab in the Creative Mode inventory for your add-on:

```java
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MyContent {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
        MyAddon.CONTENT.registerCreativeModeTab(
            "main",
            () -> new ItemStack(MY_MACHINE.get()), // Tab icon
            (params, output) -> {
                output.accept(MY_MACHINE.get());
                output.accept(MY_DRILL.get());
                output.accept(MY_BATTERY.get());
            }
        );
}
```

---

## 3. Registering Standard Items & Materials

### Simple Items
```java
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<Item> REINFORCED_CIRCUIT =
    MyAddon.CONTENT.registerItem("reinforced_circuit", Item::new);
```

### Complete Material Sets (Ingot, Plate, Dust, Block)
If your add-on introduces a metal or resource, you can register all standard industrial forms in one call:

```java
import ic2.api.registry.AddonMaterialSet;

public static final AddonMaterialSet TITANIUM =
    MyAddon.CONTENT.registerMaterialSet(
        "titanium",
        builder -> builder
            .withIngot()
            .withDust()
            .withPlate()
            .withBlock(3.0F, 6.0F)
            .withRawMaterial()
    );
```
This registers:
- `myaddon:titanium_ingot`
- `myaddon:titanium_dust`
- `myaddon:titanium_plate`
- `myaddon:titanium_block`
- `myaddon:raw_titanium`

---

## 4. Summary of Registration Methods

| Method | Purpose | Returns |
|---|---|---|
| `CONTENT.registerItem(name, factory)` | Standard Minecraft item | `DeferredItem<T>` |
| `CONTENT.registerBlock(name, factory, props)` | Standard block with custom properties | `DeferredBlock<T>` |
| `CONTENT.registerWrenchableBlock(name, props, factory)` | Block with facing & IC2 wrench rotation/drops | `DeferredBlock<AddonWrenchableBlock>` |
| `CONTENT.registerBlockEntityType(name, factory, blocks...)` | Block Entity Type | `DeferredHolder<BlockEntityType<?>, ...>` |
| `CONTENT.registerCreativeModeTab(name, icon, items)` | Creative mode inventory tab | `DeferredHolder<CreativeModeTab, ...>` |
| `CONTENT.registerElectricItem(...)` | Rechargeable item / battery | `DeferredItem<AddonElectricItem>` |
| `CONTENT.registerElectricTool(...)` | Electric utility tool (wrench, shears) | `DeferredItem<AddonElectricTool>` |
| `CONTENT.registerElectricMiningTool(...)` | Electric drill or chainsaw | `DeferredItem<AddonElectricMiningTool>` |
| `CONTENT.registerElectricWeapon(...)` | Charge-powered sword / nanosaber | `DeferredItem<AddonElectricWeapon>` |
| `CONTENT.registerElectricArmor(...)` | Nano / Quantum suit armor piece | `DeferredItem<AddonElectricArmor>` |
| `CONTENT.registerJetpack(...)` | Chest-slot flying electric jetpack | `DeferredItem<AddonElectricJetpack>` |
| `CONTENT.registerUpgradeItem(...)` | Machine upgrade module (speed, storage) | `DeferredItem<AddonUpgradeItem>` |
| `CONTENT.registerMenuType(name, factory)` | GUI Container Menu | `DeferredHolder<MenuType<?>, ...>` |
