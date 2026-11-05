# IndustrialCraft 2: Refactored — Add-on API

Welcome to the documentation for **IC2 Refactored (IC2 Rebranded)** on **Minecraft 1.21.1** with **NeoForge**!

This documentation provides everything you need to create custom add-ons: custom machines, generators, energy storage, electric tools, nano/quantum armor, flying jetpacks, machine upgrades, and recipes.

---

## Quick Navigation

| Resource | Description |
|---|---|
| **[Add-on Creation Guide](addon-development-guide.md)** | Master overview and 8 numbered guide chapters covering registration, machines, generators, tools, armor, and upgrades. |
| **[Energy System & FE Interop](energy-system.md)** | Deep dive into Energy Units (EU), Voltage Tiers (LV to EV), cable loss, and the automatic Forge Energy (FE) cross-mod bridge. |
| **[Complete Example Add-on](example-addon.md)** | A full, copy-pasteable reference mod template (`ExampleAddonMod.java`) demonstrating all core features in one place. |
| **[API Reference](api-reference.md)** | A concise index of all verified helper classes, interfaces, and methods in `ic2.api.*`. |

---

## Guide Chapters at a Glance

1. **[Registration & Setup](guides/registration.md)**: 1-line content registration with `Ic2AddonContent`, creative tabs, and materials.
2. **[Custom Machines](guides/machines.md)**: Building EU-consuming machines with `AddonMachineBlockEntity` and `AddonWrenchableBlock`.
3. **[Custom Generators](guides/generators.md)**: Creating solid-fuel, fluid, and solar generators with dynamic block lighting (`litWhenActive`).
4. **[Energy Storage Blocks](guides/storage.md)**: Building battery accumulators (BatBox, CESU, MFE) with charge and discharge slots.
5. **[Electric Tools & Weapons](guides/tools.md)**: Drills, chainsaws, wrenches, and nanosabers using `AddonElectricTool` and `ElectricItem.manager`.
6. **[Electric Armor & Jetpacks](guides/armor.md)**: Quantum/Nano armor suits, wearable Lappacks, and flying jetpacks with hover and boost keys.
7. **[Machine Upgrades](guides/upgrades.md)**: Overclock/speed, storage buffer, and transformer voltage upgrades using `AddonUpgradeItem`.
8. **[Recipes & DataGen](guides/recipes.md)**: Programmatic recipe generation with `AddonRecipeBuilder` and circuit tag conventions.

---

## Why Develop for IC2 Rebranded?

- **1-Line Registration**: With `Ic2AddonContent`, you can register blocks, items, block entities, menus, recipes, and creative tabs without managing dozens of separate `DeferredRegister` objects.
- **Built-in Wrench Support**: `AddonWrenchableBlock` automatically provides rotation, dismantle drops, and facing states compatible with IC2 wrenches.
- **Dynamic Lighting**: Generators and furnaces automatically emit block light when actively operating with `AddonEnergyBlock.litWhenActive(...)`.
- **Automatic Forge Energy (FE) Bridge**: Every add-on energy machine and generator can connect seamlessly to modern tech mods (Mekanism, Thermal, Ender IO) with 1 line of code (`1 EU = 2 FE`).
- **Data-Driven Machine Recipes**: Generate recipes for Macerators, Compressors, Extractors, and Blast Furnaces programmatically using `AddonRecipeBuilder`.

---

## Getting Started in 3 Steps

1. Check out the **[Getting Started Guide](getting-started.md)** to configure your `build.gradle` and `neoforge.mods.toml`.
2. Follow **[Chapter 1: Registration with `Ic2AddonContent`](guides/registration.md)** to register your first items and blocks.
3. Explore the **[Complete Example Add-on Mod](example-addon.md)** to copy a full working template.

---

- **GitHub Repository**: [Lerrche/IC2-Refactored-Rebranded](https://github.com/Lerrche/IC2-Refactored-Rebranded)
- **Original IC2R Port**: [neo-industrial-mc/IC2R](https://github.com/neo-industrial-mc/IC2R)
