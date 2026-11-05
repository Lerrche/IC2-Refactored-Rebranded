# IC2 Rebranded Add-on Development Guide

Welcome to the official developer guide for creating add-ons for **IndustrialCraft 2: Refactored (IC2 Rebranded)** on **Minecraft 1.21.1** with **NeoForge**.

This guide is designed to be straightforward, practical, and easy to follow. You don't need to reverse-engineer old decompiled code or struggle with complex NBT structures. Everything you need is organized into 8 focused chapters below.

---

## Guide Chapters

| Chapter | Topic | What You Will Learn |
|:---:|:---|:---|
| **[1. Registration & Setup](guides/registration.md)** | `Ic2AddonContent` | Setting up your mod, registering items, blocks, and creative mode tabs in 1 line. |
| **[2. Custom Machines](guides/machines.md)** | `AddonMachineBlockEntity` | Building electric machines that consume EU to process items with IC2 Wrench rotation. |
| **[3. Custom Generators](guides/generators.md)** | `AddonGeneratorBlockEntity` | Creating fuel-burning and solar generators with dynamic block lighting when active. |
| **[4. Energy Storage](guides/storage.md)** | `AddonEnergyStorageBlockEntity` | Making battery accumulators (BatBox / CESU / MFE style) with charge/discharge slots. |
| **[5. Electric Tools](guides/tools.md)** | `AddonElectricTool` | Crafting electric drills, chainsaws, wrenches, and nanosabers powered by EU. |
| **[6. Armor & Jetpacks](guides/armor.md)** | `AddonElectricArmor` | Developing Nano/Quantum suits, battery backpacks (Lappacks), and flying jetpacks. |
| **[7. Machine Upgrades](guides/upgrades.md)** | `AddonUpgradeItem` | Creating Overclock / Speed, Energy Storage, and Transformer upgrades for machines. |
| **[8. Recipes & DataGen](guides/recipes.md)** | `AddonRecipeBuilder` | Generating recipes for Macerators, Compressors, Extractors, Furnaces, and more. |

---

## Core Systems & References

- **[Energy System & Interoperability](energy-system.md)**: Understanding Energy Units (EU), Voltage Tiers (LV to EV), cable loss, and automatic Forge Energy (FE) cross-mod compatibility.
- **[Complete Example Add-on Mod](example-addon.md)**: A single, fully working, copy-pasteable Java mod demonstrating all concepts together.
- **[API Reference](api-reference.md)**: Quick reference table of all classes and methods in `ic2.api.*`.
- **[Building from Source](building.md)**: How to compile IC2 Rebranded and run the development test client.

---

## Quick Architecture Summary

```
                      +-----------------------------------+
                      |         Your Add-on Mod           |
                      |   (ExampleAddon / MyContent)      |
                      +-----------------+-----------------+
                                        |
               +------------------------+------------------------+
               |                        |                        |
               v                        v                        v
     +-------------------+    +--------------------+   +-------------------+
     |  Ic2AddonContent  |    | AddonEnergyBlock   |   | ElectricItem      |
     | (Unified Registry)|    | (Wrench, Lighting) |   | .manager          |
     +---------+---------+    +---------+----------+   +---------+---------+
               |                        |                        |
               v                        v                        v
     +---------------------------------------------------------------------+
     |                     IC2 Rebranded Core Energy Net                   |
     |          (Energy Units [EU], Voltage Tiers 0-5, Cable Loss)         |
     +----------------------------------+----------------------------------+
                                        |
                                        v
     +---------------------------------------------------------------------+
     |                      Forge Energy (FE) Bridge                       |
     |               (1 EU = 2 FE, Mekanism / Thermal Interop)             |
     +---------------------------------------------------------------------+
```

Ready to start? Jump straight into **[Chapter 1: Registration with `Ic2AddonContent`](guides/registration.md)**!
