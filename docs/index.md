# IC2 Refactored Add-on API

Documentation for building add-ons for **IndustrialCraft 2: Refactored** on **Minecraft 1.21.1 + NeoForge**.

This site documents the API present in the project source snapshot used to prepare these pages. The project is evolving, so check the source for your exact revision before relying on an example in a released add-on.

## Start here

- [Getting started](getting-started.md) — environment, dependency, and project setup.
- [Content registration](registration.md) — register items and blocks under your own mod ID.
- [Energy API](energy/index.md) — batteries, generators, and EU tiers.
- [Electric items](items/electric-items.md) — chargeable items and melee weapons.
- [Armor and jetpacks](items/armor.md) — electric armor support.
- [API reference](api-reference.md) — classes and verified constructor contracts.

## Supported baseline

| Component | Version in the inspected project snapshot |
| --- | --- |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.234` in `gradle.properties` |
| Java toolchain | `21` |
| Mod ID | `ic2` |

The repository's `gradle.properties` is the source of truth for versions. If your checkout differs, use its values instead.

## Scope and attribution

This is community documentation for an unofficial add-on API. The original IC2R port and underlying IC2 code/assets have their own authorship and licensing history; see the repository's `LICENSE` and upstream project for details.
