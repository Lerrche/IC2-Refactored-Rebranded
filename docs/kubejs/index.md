# IndustrialJS & KubeJS Integration

Welcome to the **IndustrialJS** documentation for **IndustrialCraft 2: Refactored**!

IndustrialJS is a high-performance, native integration layer designed specifically for **KubeJS** and **modpack developers**. It provides deep, real-time control over IC2's recipes, machine processing speeds, generators, nuclear reactor physics, radiation mechanics, electric armor, flying jetpacks, audio muting, crop breeding, and cross-mod energy conversion.

---

## Key Features

- **Zero-Code Balancing**: Configure global energy ratios, machine speed multipliers, generator outputs, and custom recipes using simple JSON files (`config/industrialjs/`) without writing a single line of JavaScript.
- **Fluent KubeJS API**: Create recipes and tweak machine parameters directly from `server_scripts` with autocomplete-friendly chaining (`IndustrialJS.recipes().macerator(...)`).
- **Complete Nuclear & Reactor Physics**: Adjust reactor heat generation, output multipliers, explosion mechanics, and add custom Uranium and MOX fuel rods.
- **Dynamic Radiation & Hazmat System**: Turn any mod item into a radioactive hazard and register custom armor sets as full hazmat suits with 1 line of code.
- **In-Game Hot Reloading**: Edit configs or scripts and run `/reload` — all recipes, multipliers, and behaviors update immediately without restarting Minecraft!

---

## Documentation Categories

| Page | Description |
|---|---|
| **[Zero-Code JSON Configuration](zero-code.md)** | Balance machines, generators, and add recipes via `config/industrialjs/` JSON files. |
| **[Quick Start & Syntax](quickstart.md)** | Setting up KubeJS scripts, accessing the `IndustrialJS` global, and input/output syntax. |
| **[Recipe Creation API](recipes.md)** | Detailed guide for all 13 machine recipe types (Macerator, Blast Furnace, Canning, etc.). |
| **[Machinery & Generators](machinery.md)** | Machine speeds, EU per tick, Solar Panel outputs, audio muting, and Batch Crafter stats. |
| **[Energy Grid & Storage](energy-grid.md)** | Custom EU-to-FE conversion ratios, BatBox/CESU/MFE/MFSU capacity, and transfer limits. |
| **[Tools, Drills & Jetpacks](tools-jetpacks.md)** | Drill speeds, Chainsaw power, Nano Saber damage, Mining Laser grief prevention, and jetpack physics. |
| **[Armor, Hazmat & Radiation](armor-radiation.md)** | Electric armor damage absorption, Quantum Suit jump/sprint/void rescue, Hazmat gear, and radiation checks. |
| **[Nuclear Reactors & Mechanics](nuclear-crops.md)** | Reactor output and meltdown safety, Kinetic Rotors, Upgrades, Crop Breeding, and Teleportation. |
| **[Complete Script Example](examples.md)** | Full, copy-pasteable `server_scripts/ic2_custom.js` template and Master API table. |
| **[Full Single-Page Guide](../IndustrialJS_Guide.md)** | The complete, unedited guide on a single page for offline reading or quick Ctrl+F searching. |
