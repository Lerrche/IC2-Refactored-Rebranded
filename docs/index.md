# IC2 Refactored Add-on API

Welcome! This site documents the add-on-facing helpers and reusable electric-content base classes available in this IC2 Refactored source snapshot for **Minecraft 1.21.1 and NeoForge**.

The aim is to help you build an add-on without having to reverse-engineer every implementation class. The API is still evolving, so treat the source as the final authority when behavior or signatures are unclear.

## Start here

- [Getting started](getting-started.md) — environment and dependency setup.
- [Registration](registration.md) — register blocks and items under your own mod ID.
- [Examples](examples/index.md) — small patterns you can adapt.
- [Energy API](energy/index.md) — storage and generators.
- [Electric items](items/electric-items.md) — chargeable items, weapons, armor, and jetpacks.
- [API reference](api-reference.md) — verified API entry points.
- [Building the mod](building.md) — format, build, and locate the JAR.

## A few rules of thumb

1. Use your own mod ID for add-on content and resources.
2. Prefer classes in `ic2.api.*` where they cover your use case. Some reusable base classes extend IC2R internals and are more version-sensitive.
3. Register your own blocks, items, and block-entity types. The helper classes do not automatically create all models, textures, menus, recipes, or loot tables.
4. Run `spotlessApply` before `build`, as described in [Building the mod](building.md).

[Open the source repository](https://github.com/Lerrche/IC2-Refactored-Rebranded) · [Original IC2R port](https://github.com/neo-industrial-mc/IC2R)
