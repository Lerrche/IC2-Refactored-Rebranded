# IC2 Refactored (Rebranded)

An unofficial continuation of the IndustrialCraft 2 port for Minecraft 1.21.1 on NeoForge.

I started this project to build on the existing port with compatibility work, quality-of-life fixes, and a more approachable API for people who want to make add-ons. I did **not** create the original port and do not claim ownership of the original IC2 code or assets.

## Documentation & Add-on Creation

- **[Complete Add-on Creation Guide](docs/addon-development-guide.md)**: Master guide for creating custom machines, generators, energy storage, electric tools, and recipes.
- **[Online Web Documentation](https://lerrche.github.io/IC2-Refactored-Rebranded/)**: Full online docs and guides.
- **[Add-on API Reference](docs/api-reference.md)**: Concise index of all add-on API classes and helpers.
- **[Compilable Example Add-on](src/test/java/ic2/api/example/ExampleAddonMod.java)**: Live, fully working reference mod demonstrating blocks, tools, upgrades, and FE capabilities.

### Add-on Development Highlights
- **`Ic2AddonContent`**: 1-line registration for blocks, items, custom Creative Mode tabs, menus, and recipe types.
- **`AddonWrenchableBlock`**: Machine & battery block base with placement facing and IC2 Wrench rotation/dismantling.
- **`AddonMachineBlockEntity`**: EU-powered processing machine base with progress and operation tracking.
- **`AddonGeneratorBlockEntity` & `AddonEnergyStorageBlockEntity`**: Generator and battery bases with built-in Forge Energy (FE) bridge (`1 EU = 2 FE`).
- **`AddonElectricTool`**: Extensible electric tool base with automated discharge/charge management.
- **`AddonUpgradeItem`**: Fluent builder for creating machine upgrades (speed, storage, tier boost).
- **`AddonRecipeBuilder`**: Programmatic builder generating JSON recipes for all IC2 machine types (Macerator, Compressor, Centrifuge, Blast Furnace, etc.).

**[Read the IC2 Refactored Add-on API documentation](https://lerrche.github.io/IC2-Refactored-Rebranded/)**

The docs cover project setup, content registration, electric items, energy storage, generators, examples, and API notes. They're a work in progress, so check the actual source when something is marked experimental or incomplete.

## Project goals

- Improve compatibility between IC2 energy systems and other tech mods.
- Make it easier to create add-ons with custom items, machines, generators, and energy storage.
- Add quality-of-life improvements and fixes on top of the existing port.
- Keep integration examples and API notes in one place for other developers.

## Requirements

- Minecraft 1.21.1
- NeoForge (use the version configured by this repository)
- Java 21

## Build from source

Run these commands from the repository root in PowerShell:

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat build
```

On Linux or macOS, use `./gradlew spotlessApply` followed by `./gradlew build`.

`spotlessApply` formats the source files according to the project's Spotless rules. It does not replace compilation or tests; `build` is still required. When the build succeeds, look in `build/libs/` for the generated JAR. Check the filename and use the normal mod JAR rather than a sources or development artifact if multiple JARs are present.

To launch the development client on Windows:

```powershell
.\gradlew.bat runClient
```

## Credits and ownership

- Original port: [neo-industrial-mc/IC2R](https://github.com/neo-industrial-mc/IC2R)
- IndustrialCraft 2 and its original developers.

This is an unofficial community project and is not affiliated with or endorsed by the original IndustrialCraft 2 developers. The original code, assets, and their applicable licensing/ownership notices remain distinct from my own changes. See [`LICENSE`](LICENSE) and the [original repository](https://github.com/neo-industrial-mc/IC2R) for licensing context.

## Contributing

Bug reports and practical add-on API examples are welcome. When reporting a build or runtime problem, include the Minecraft/NeoForge versions, the command you ran, the relevant error output, and steps to reproduce it.
