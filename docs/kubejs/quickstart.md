# KubeJS Quick Start & Syntax

## 2. KubeJS Quick Start & Fluent Chaining

If you have [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) installed, IndustrialJS provides direct Java bindings with static helpers, getters, setters, checkers, and a fluent builder API.

### Accessing IndustrialJS in KubeJS

In any server script (e.g. `kubejs/server_scripts/industrial.js`):

```javascript
const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');
```

### Fluent Chaining (`IndustrialJS.recipes()`)

`IndustrialJS.recipes()` returns a chainable singleton instance allowing you to register multiple machine recipes across various systems cleanly:

```javascript
ServerEvents.recipes(event => {
  const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');

  IndustrialJS.recipes()
    .macerator('2x ic2:crushed_copper', 'minecraft:raw_copper')
    .compressor('minecraft:diamond', '64x minecraft:coal')
    .extractor('3x ic2:rubber', 'ic2:resin')
    .oreWasher(['ic2:purified_copper', '2x ic2:small_copper_dust', 'ic2:stone_dust'], 'ic2:crushed_copper', 1000)
    .thermalCentrifuge(['ic2:refined_copper', 'ic2:small_gold_dust'], 'ic2:purified_copper', 1200)
    .blastFurnace(['ic2:refined_iron_ingot', 'ic2:slag'], 'minecraft:iron_ingot', 6000, 1000)
    .blockCutter('9x ic2:copper_plate', 'minecraft:copper_block', 1)
    .metalFormerRolling('ic2:copper_plate', 'minecraft:copper_ingot')
    .metalFormerCutting('3x ic2:copper_cable', 'ic2:copper_plate')
    .metalFormerExtruding('ic2:casing_copper', 'minecraft:copper_ingot')
    .molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000.0)
    .cannerEnrich('1000mB ic2:coolant', '1000mB minecraft:water', 'ic2:water_drop')
    .cannerBottle('minecraft:potion', 'minecraft:glass_bottle', '250mB ic2:biomass')
    .scrapbox('minecraft:netherite_scrap', 0.02)
    .amplifier('minecraft:obsidian', 250);
});
```

---


---

## 10. Item, Fluid & Recipe Input/Output Syntax

IndustrialJS features an intelligent `ItemParser` that resolves string identifiers, item tags, fluid strings, stack counts, and native Minecraft/KubeJS objects:

| Syntax Format | Example | Description |
| :--- | :--- | :--- |
| **Simple Item ID** | `'minecraft:iron_ingot'` | Single item (count = 1) |
| **Count Prefix (`x`)** | `'2x minecraft:sand'` | Stack with quantity 2 |
| **Count Prefix (`*`)** | `'64* minecraft:coal'` | Stack with quantity 64 |
| **Item Tag (`#`)** | `'#c:raw_materials/copper'` | Any item matching the item tag |
| **Counted Item Tag** | `'9x #c:ingots/tin'` | Requires 9 of any item matching the tag |
| **Fluid String** | `'1000mB minecraft:water'` | 1000 mB of water fluid |
| **Short Fluid String** | `'1000 ic2:coolant'` | 1000 mB of coolant fluid |
| **Array of Outputs** | `['ic2:purified_iron', '2x ic2:small_iron_dust']` | Multiple outputs (Ore Washer, Centrifuge, Blast Furnace) |
| **Native Objects** | `ItemStack`, `Ingredient`, `ItemLike`, `FluidStack` | Full compatibility with native Java / KubeJS objects |

---
