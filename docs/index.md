# IndustrialCraft 2: Refactored — Add-on API

Welcome to the documentation for **IC2 Refactored (IC2 Rebranded)** on **Minecraft 1.21.1** with **NeoForge**!

This documentation provides everything you need to create custom add-ons: custom machines, generators, energy storage, electric tools, nano/quantum armor, flying jetpacks, machine upgrades, and recipes.

---

## Quick Navigation

<div class="grid cards" markdown>

-   :material-book-open-page-variant: **[Add-on Creation Guide](addon-development-guide.md)**

    ---

    Step-by-step numbered guides covering registration, machines, generators, tools, armor, and upgrades.

-   :material-flash: **[Energy System & FE Interop](energy-system.md)**

    ---

    Energy Units (EU), Voltage Tiers (LV to EV), cable loss, and automatic Forge Energy (FE) cross-mod bridge.

-   :material-code-tags: **[Complete Example Add-on](example-addon.md)**

    ---

    A full, copy-pasteable reference mod template demonstrating all core features in one place.

-   :material-database-search: **[API Reference](api-reference.md)**

    ---

    A concise index of all verified helper classes, interfaces, and methods in `ic2.api.*`.

</div>

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
2. Follow **[Chapter 1: Registration with `Ic2AddonContent`](guides/registration.md)**.
3. Explore the **[Complete Example Add-on Mod](example-addon.md)** to copy a working template.

[View the Source Code on GitHub](https://github.com/Lerrche/IC2-Refactored-Rebranded){ .md-button .md-button--primary }
[Original IC2R Port by neo-industrial-mc](https://github.com/neo-industrial-mc/IC2R){ .md-button }
