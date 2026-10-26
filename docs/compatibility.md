# Compatibility notes

## Minecraft and NeoForge versions

This documentation targets Minecraft 1.21.1. Use the NeoForge version configured by the IC2R checkout you build against. Do not assume a class compiled against another Minecraft or NeoForge version will be binary compatible.

## Public API vs implementation

Prefer documented `ic2.api.*` classes. The reusable block-entity bases currently extend classes in `ic2.core.*`; they are useful but coupled to IC2R internals and may change between revisions. Avoid depending on unrelated internal classes unless you accept that maintenance cost.

## Energy interop

IC2R's native energy system uses EU. Other mods may expose FE or another energy unit. Never assume a 1:1 mapping without checking the adapter. A robust bridge should document its conversion ratio, rounding behavior, transfer limits, and buffer policy, and should avoid creating energy through repeated rounding or duplicate transfers.

## Common problems

- **Missing IC2R classes:** make sure IC2R is on the add-on's compile classpath and present at runtime.
- **Content doesn't appear:** verify the mod ID/namespace, registry event bus, asset paths, and language keys.
- **Block entity crashes:** verify the registered `BlockEntityType` matches your subclass and block.
- **Wrong textures/models:** registration does not generate resource files.
- **Build fails on formatting:** run `spotlessApply`, then rerun `build` and inspect the first real error if it still fails.
