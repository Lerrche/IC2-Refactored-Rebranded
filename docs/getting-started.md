# Getting started

## Requirements

- Java 21
- Minecraft 1.21.1
- NeoForge for Minecraft 1.21.1
- A Java IDE or editor
- IC2 Refactored installed as a development/runtime dependency for your add-on

Use the NeoForge version expected by the IC2R build you are targeting. Do not assume every IC2R fork or release uses the same NeoForge patch version.

## Create an add-on project

Start with a NeoForge 1.21.1 mod project, give it its own unique mod ID (for example `example_ic2_addon`), and add IC2 Refactored to the development environment using the dependency approach your project uses. The add-on's `neoforge.mods.toml` should declare IC2R as a dependency if your code calls its API during initialization.

The exact Maven coordinate or local dependency setup can vary by how you distribute/build IC2R; this repository's `publishing` block alone does not guarantee a published artifact is available to everyone. If you are developing against source locally, use a local project dependency or the built JAR.

## Recommended package layout

```text
src/main/java/example/addon/
  ExampleIc2Addon.java
  content/AddonContent.java
  item/AlloyCell.java
  item/AlloyWeapon.java
  block/energy/ExampleGeneratorBlockEntity.java
src/main/resources/
  META-INF/neoforge.mods.toml
  assets/example_ic2_addon/lang/en_us.json
  assets/example_ic2_addon/models/item/...
  assets/example_ic2_addon/textures/item/...
  data/example_ic2_addon/recipe/...
```

This is an example layout, not a required API structure.

## Build IC2R

From the repository root, run:

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat build
```

See [Building the mod](building.md) for Linux/macOS commands and where to find the JAR.
