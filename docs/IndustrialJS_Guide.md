# IndustrialJS — KubeJS & Modpack Creator Guide for IndustrialCraft 2

Welcome to **IndustrialJS**, the official modpack creator and scripting integration for **IndustrialCraft 2 (IC2 Refactored / Rebranded)**!

IndustrialJS was built for modpack authors of all skill levels:
- **No-code / Zero-code mode**: Tweak machinery balance, cross-dimensional teleportation, drill speeds, combat weapons, battery capacities, wrench mechanics, and add custom machine recipes simply by editing simple `.json` files in `config/industrialjs/`!
- **KubeJS script mode**: Leverage the full power of JavaScript inside `kubejs/server_scripts/` to register custom machine recipes, dynamically adjust generator outputs, configure energy ratios, tune drills & quantum armor, and chain recipe pipelines with clean, readable syntax.

---

## Table of Contents
1. [Zero-Code Configuration (No Coding Needed!)](#1-zero-code-configuration-no-coding-needed)
   - [Configuring Multipliers & Balance (`multipliers.json`)](#configuring-multipliers--balance-multipliersjson)
   - [Adding Custom Recipes & Amplifiers (`recipes.json`)](#adding-custom-recipes--amplifiers-recipesjson)
2. [KubeJS Script Integration](#2-kubejs-script-integration)
   - [Accessing IndustrialJS in KubeJS](#accessing-industrialjs-in-kubejs)
   - [Recipe Creation Examples](#recipe-creation-examples)
   - [Dynamic Machine, Energy & Balance Tuning](#dynamic-machine-energy--balance-tuning)
3. [Special Systems & Mechanics](#3-special-systems--mechanics)
   - [Cross-Dimensional Teleportation](#cross-dimensional-teleportation)
   - [Electric Tools, Drills & Nano Saber Combat](#electric-tools-drills--nano-saber-combat)
   - [Quantum Suit Void Rescue & High-Speed Sprinting](#quantum-suit-void-rescue--high-speed-sprinting)
   - [Mining Laser Anti-Grief & Super-Heat Smelting](#mining-laser-anti-grief--super-heat-smelting)
   - [Energy Storage Blocks (BatBox, CESU, MFE, MFSU)](#energy-storage-blocks-batbox-cesu-mfe-mfsu)
   - [Alternative Machine Mute Tools](#alternative-machine-mute-tools)
   - [Lossless Wrench & Machine Retention](#lossless-wrench--machine-retention)
   - [EU to FE Conversion Ratio](#eu-to-fe-conversion-ratio)
   - [Recycler Scrap Tuning & Blacklists](#recycler-scrap-tuning--blacklists)
   - [Nuclear Reactor & Meltdown Safety](#nuclear-reactor--meltdown-safety)
   - [Matter Fabricator & Custom Amplifiers](#matter-fabricator--custom-amplifiers)
   - [Radiation Immunity & Custom Hazmat Gear](#radiation-immunity--custom-hazmat-gear)
4. [Supported Machine Types & Parameters](#4-supported-machine-types--parameters)
5. [In-Game Hot Reloading](#5-in-game-hot-reloading)

---

## 1. Zero-Code Configuration (No Coding Needed!)

If you do not know how to code, or simply prefer straightforward configuration files, IndustrialJS automatically creates a `config/industrialjs/` directory with easy-to-read JSON files when the game starts.

### Configuring Multipliers & Balance (`config/industrialjs/multipliers.json`)

You can alter energy output, consumption, speed, conversion rates, and safety rules across all machines and generators:

```json
{
  "_comment": "IndustrialJS Machine & Balance Multipliers. 1.0 = Default Vanilla IC2 Balance.",
  "generator_output_multiplier": 1.5,
  "solar_day_output_multiplier": 2.0,
  "solar_night_output_multiplier": 2.0,
  "machine_energy_consumption_multiplier": 0.8,
  "machine_speed_multiplier": 1.25,
  "molecular_transformer_cost_multiplier": 0.5,
  "nuclear_reactor_multiplier": 1.5,
  "reactor_explosion_multiplier": 1.0,
  "uu_energy_multiplier": 1.0,
  "matter_amplifier_multiplier": 1.0,
  "teleporter_cost_multiplier": 1.0,
  "teleporter_cross_dimension": true,
  "teleporter_cross_dimension_flat_cost": 1000000.0,
  "mining_speed_multiplier": 1.25,
  "armor_absorption_multiplier": 1.0,
  "armor_energy_cost_multiplier": 1.0,
  "nano_saber_active_damage": 28.0,
  "nano_saber_passive_damage": 4.0,
  "nano_saber_energy_per_hit": 150.0,
  "nano_saber_armor_bypass": 0.25,
  "laser_explosive_destruction": true,
  "quantum_void_protection": true,
  "quantum_sprint_multiplier": 1.5,
  "eu_to_fe_ratio": 2.0,
  "lossless_wrench": true,
  "disable_radiation": false,
  "recycler_scrap_chance": 8,
  "recycler_blacklist": [
    "minecraft:glass_bottle",
    "#c:stones"
  ],
  "hazmat_items": [
    "#c:armors/hazmat",
    "create:diving_helmet"
  ],
  "mute_tools": [
    "ic2:forge_hammer",
    "#c:tools/wrench"
  ],
  "drill_mining_speeds": {
    "ic2:iridium_drill": 45.0,
    "ic2:diamond_drill": 20.0
  },
  "drill_energy_costs": {
    "ic2:iridium_drill": 150,
    "ic2:diamond_drill": 60
  },
  "storage_capacities": {
    "batbox": 80000,
    "cesu": 500000,
    "mfe": 8000000,
    "mfsu": 100000000
  },
  "storage_outputs": {
    "batbox": 64,
    "cesu": 256,
    "mfe": 1024,
    "mfsu": 4096
  },
  "machine_multipliers": {
    "macerator": 1.0,
    "compressor": 1.0,
    "extractor": 1.0
  }
}
```

---

### Adding Custom Recipes & Amplifiers (`config/industrialjs/recipes.json`)

To add recipes without writing a single line of script, open `config/industrialjs/recipes.json` and add your entries:

```json
[
  {
    "type": "macerator",
    "input": "minecraft:granite",
    "output": "2x minecraft:sand"
  },
  {
    "type": "compressor",
    "input": "9x minecraft:dirt",
    "output": "minecraft:clay"
  },
  {
    "type": "extractor",
    "input": "minecraft:dandelion",
    "output": "2x ic2:rubber"
  },
  {
    "type": "ore_washer",
    "input": "minecraft:raw_iron",
    "output": [
      "ic2:purified_iron",
      "2x ic2:small_iron_dust",
      "ic2:stone_dust"
    ],
    "water": 1000
  },
  {
    "type": "centrifuge",
    "input": "ic2:purified_iron",
    "output": [
      "ic2:refined_iron",
      "ic2:small_gold_dust"
    ],
    "min_heat": 1500
  },
  {
    "type": "blast_furnace",
    "input": "minecraft:iron_ingot",
    "output": [
      "ic2:refined_iron_ingot",
      "ic2:slag"
    ],
    "duration": 6000,
    "min_heat": 1000
  },
  {
    "type": "block_cutter",
    "input": "ic2:tin_block",
    "output": "9x ic2:tin_plate",
    "hardness": 1
  },
  {
    "type": "rolling",
    "input": "minecraft:iron_ingot",
    "output": "ic2:iron_plate"
  },
  {
    "type": "cutting",
    "input": "ic2:iron_plate",
    "output": "3x ic2:iron_cable"
  },
  {
    "type": "extruding",
    "input": "ic2:iron_ingot",
    "output": "ic2:casing_iron"
  },
  {
    "type": "molecular_transformer",
    "input": "minecraft:iron_block",
    "output": "minecraft:gold_block",
    "eu_cost": 5000000.0
  },
  {
    "type": "scrapbox",
    "input": "minecraft:diamond",
    "weight": 0.05
  },
  {
    "type": "amplifier",
    "input": "minecraft:cobblestone",
    "amplification": 100
  },
  {
    "type": "laser_smelting",
    "input": "minecraft:sand",
    "output": "minecraft:glass"
  }
]
```

---

## 2. KubeJS Script Integration

If you have [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) installed in your modpack, IndustrialJS provides direct Java bindings and chainable builder methods.

### Accessing IndustrialJS in KubeJS

In any server script (e.g. `kubejs/server_scripts/industrial.js`):

```javascript
const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');
```

---

### Recipe Creation Examples

Inside `ServerEvents.recipes`:

```javascript
ServerEvents.recipes(event => {
  const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');

  // Macerator: output, input
  IndustrialJS.macerator('2x ic2:crushed_copper', '#c:raw_materials/copper');

  // Compressor: output, input
  IndustrialJS.compressor('minecraft:diamond', '64x minecraft:coal');
  IndustrialJS.compressor('ic2:scrap_box', '9x ic2:scrap');

  // Extractor: output, input
  IndustrialJS.extractor('3x ic2:rubber', 'ic2:resin');

  // Ore Washing Plant: outputs, input, water in mB (default 1000)
  IndustrialJS.oreWasher(
    ['ic2:purified_iron', '2x ic2:small_iron_dust', 'ic2:stone_dust'],
    'ic2:crushed_iron',
    1000
  );

  // Thermal Centrifuge: outputs, input, min heat (default 500)
  IndustrialJS.thermalCentrifuge(
    ['ic2:refined_iron', 'ic2:small_gold_dust'],
    'ic2:purified_iron',
    1500
  );

  // Molecular Transformer: output, input, total EU cost
  IndustrialJS.molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000);

  // Custom Super-Heat Mining Laser Smelting
  IndustrialJS.laserSmelting('minecraft:sand', 'minecraft:glass');
  IndustrialJS.laserSmelting('minecraft:raw_iron_block', 'minecraft:iron_block');

  // Custom Matter Fabricator Scrap Amplifiers: item, amplification value
  IndustrialJS.amplifier('minecraft:cobblestone', 100);
});
```

---

### Dynamic Machine, Energy & Balance Tuning

Modify game balance live from KubeJS:

```javascript
ServerEvents.recipes(event => {
  const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');

  // --- Teleporter ---
  IndustrialJS.setTeleporterCrossDimension(true);              // Cross-dimension jumps enabled
  IndustrialJS.setTeleporterCrossDimensionFlatCost(1000000);   // 1,000,000 EU to jump dimensions
  IndustrialJS.setTeleporterDistanceCostMultiplier(0.5);       // 50% cheaper distance energy

  // --- Tools & Drills ---
  IndustrialJS.setDrillMiningSpeed('ic2:iridium_drill', 45.0);
  IndustrialJS.setDrillEnergyPerBlock('ic2:diamond_drill', 60);

  // --- Nano Saber ---
  IndustrialJS.setNanoSaberStats({
    activeDamage: 28,             // Attack damage when turned ON
    passiveDamage: 4,             // Attack damage when turned OFF
    energyPerHit: 120,            // EU consumed per strike
    armorBypassPercentage: 0.5    // 50% true armor penetration
  });

  // --- Mining Laser ---
  IndustrialJS.setLaserExplosiveDestruction(false); // Explosions hurt mobs but protect blocks

  // --- Quantum Suit ---
  IndustrialJS.setQuantumSuitVoidProtection(true);  // Teleports players back up from the Void
  IndustrialJS.setQuantumSprintSpeed(2.5);          // 2.5x run speed

  // --- Energy Storage (BatBox, CESU, MFE, MFSU) ---
  IndustrialJS.setStorageCapacity('batbox', 80000);   // Double capacity
  IndustrialJS.setStorageCapacity('mfsu', 100000000); // 100M EU
  IndustrialJS.setStorageOutput('mfsu', 4096);        // 4096 EU/t output

  // --- Mute Tools ---
  IndustrialJS.addMuteTool('#c:tools/wrench');        // Right-clicking with any wrench mutes/unmutes machines

  // --- General Balance ---
  IndustrialJS.setLosslessWrench(true);              // Never lose machines when mined
  IndustrialJS.setEuToFeRatio(4.0);                  // 1 EU = 4 FE
  IndustrialJS.setRecyclerScrapChance(4);            // 25% scrap chance
  IndustrialJS.setReactorExplosionMultiplier(0.0);   // Safe nuclear meltdowns (no griefing)
  IndustrialJS.setRadiationDisabled(true);           // Disable radiation completely
});
```

---

## 3. Special Systems & Mechanics

### Cross-Dimensional Teleportation
Players can link IC2 Teleporters across different dimensions (e.g. Overworld to Nether, End, or space dimensions)!
1. Right-click Teleporter A in Dimension 1 with the Frequency Transmitter.
2. Travel to Dimension 2, right-click Teleporter B.
3. The Frequency Transmitter links them bidirectionally and displays the target dimension in its tooltip.
4. Step onto the Teleporter and supply redstone to jump across dimensions.
- **Config**: `teleporter_cross_dimension: true`
- **Flat Cost**: `teleporter_cross_dimension_flat_cost: 1000000.0` (scales with player inventory weight).

### Electric Tools, Drills & Nano Saber Combat
- Custom per-drill mining speed: `IndustrialJS.setDrillMiningSpeed('ic2:iridium_drill', 45.0)`.
- Custom energy per block: `IndustrialJS.setDrillEnergyPerBlock('ic2:diamond_drill', 60)`.
- Nano Saber attack damage, energy per hit, and armor penetration can be fine-tuned via `IndustrialJS.setNanoSaberStats(...)`.

### Quantum Suit Void Rescue & High-Speed Sprinting
- **Void Protection**: Falling into the void below the world's minimum build height automatically consumes suit EU to safely boost the player back into the world, resetting fall damage.
- **Sprinting**: Boost sprinting speed multiplier via `quantum_sprint_multiplier`.

### Mining Laser Anti-Grief & Super-Heat Smelting
- Disable block explosion griefing on servers while keeping mob explosion damage with `laser_explosive_destruction: false`.
- Register custom block transformations when struck by Super-Heat beams via `IndustrialJS.laserSmelting(...)` or in `recipes.json`.

### Energy Storage Blocks (BatBox, CESU, MFE, MFSU)
Directly configure maximum EU capacity and output rate for BatBox, CESU, MFE, and MFSU without editing complex code.

### Alternative Machine Mute Tools
Right-clicking any machine with a hammer toggles machine audio muting. You can add wrenches, screwdrivers, or any item/tag to `mute_tools` in `multipliers.json` or via `IndustrialJS.addMuteTool(itemOrTag)`.

### Lossless Wrench & Machine Retention
Setting `lossless_wrench: true` ensures that any tool or wrench retains the machine intact with 100% certainty, preventing machines from degrading into casings.

### EU to FE Conversion Ratio
The built-in energy bridge lets IC2 cables, machines, and storage seamlessly interface with Forge Energy / RF mods.
- Set `eu_to_fe_ratio` (default `2.0` FE per 1 EU).

### Nuclear Reactor & Meltdown Safety
- Tune reactor EU power output with `nuclear_reactor_multiplier`.
- Set `reactor_explosion_multiplier: 0.0` to disable meltdown terrain griefing!

---

## 4. Supported Machine Types & Parameters

| Machine | Method | Signature | Description |
| :--- | :--- | :--- | :--- |
| **Macerator** | `IndustrialJS.macerator` | `(output, input)` | Grinds blocks/items into crushed dusts |
| **Compressor** | `IndustrialJS.compressor` | `(output, input)` | Compresses scrap, materials, diamonds |
| **Extractor** | `IndustrialJS.extractor` | `(output, input)` | Extracts rubber, dyes, liquids |
| **Ore Washer** | `IndustrialJS.oreWasher` | `(outputs, input, water)` | Washes crushed ores into purified ores |
| **Thermal Centrifuge**| `IndustrialJS.thermalCentrifuge` | `(outputs, input, minHeat)` | Separates materials at temperature |
| **Blast Furnace** | `IndustrialJS.blastFurnace` | `(outputs, input, duration, heat)`| Refines iron to steel/refined iron with slag |
| **Block Cutter** | `IndustrialJS.blockCutter` | `(output, input, hardness)` | Slices blocks into plates with cutter blades |
| **Metal Former (Rolling)** | `IndustrialJS.metalFormerRolling` | `(output, input)` | Rolls ingots into plates |
| **Metal Former (Cutting)** | `IndustrialJS.metalFormerCutting` | `(output, input)` | Cuts plates into cables |
| **Metal Former (Extruding)**| `IndustrialJS.metalFormerExtruding` | `(output, input)` | Extrudes items into casings/rods |
| **Molecular Transformer** | `IndustrialJS.molecularTransformer` | `(output, input, euCost)` | Matter transmutation consuming high EU |
| **Scrapbox** | `IndustrialJS.scrapbox` | `(item, weight)` | Adds rare or surprise drops to scrap boxes |
| **Matter Amplifier** | `IndustrialJS.amplifier` | `(item, amplification)` | Registers custom items to amplify Matter Fabricators |
| **Laser Smelting** | `IndustrialJS.laserSmelting` | `(input, output)` | Adds custom block smelting to Mining Laser Super-Heat |

---

## 5. In-Game Hot Reloading

When you make changes to:
- `config/industrialjs/multipliers.json`
- `config/industrialjs/recipes.json`
- KubeJS `server_scripts/`

Simply run `/reload` in-game (with cheats or console enabled). IndustrialJS reloads all multipliers, sets, drill speeds, and re-injects all recipes immediately without needing to restart Minecraft!
