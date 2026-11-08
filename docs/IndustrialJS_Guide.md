# IndustrialJS — KubeJS & Modpack Creator Guide for IndustrialCraft 2

Welcome to **IndustrialJS**, the official modpack creator and scripting integration for **IndustrialCraft 2 (IC2 Refactored / Rebranded)**!

IndustrialJS was built for modpack authors of all skill levels:
- **No-code / Zero-code mode**: Tweak machinery balance, nuclear reactors, custom fuel rods, radioactive items, hazmat gear, jetpacks, quantum abilities, kinetic rotors, upgrades, terraformers, crop breeding, cross-dimensional teleportation, drill speeds, combat weapons, battery capacities, wrench mechanics, structure loot, and add custom machine recipes simply by editing simple `.json` files in `config/industrialjs/`!
- **KubeJS script mode**: Leverage the full power of JavaScript inside `kubejs/server_scripts/` to register custom machine recipes, dynamically adjust generator outputs, configure energy ratios, tune drills & quantum armor, inspect values via getters, and chain recipe pipelines with clean, fluent syntax.

---

## Table of Contents
1. [Zero-Code Configuration (No Coding Needed!)](#1-zero-code-configuration-no-coding-needed)
   - [Configuring Multipliers & Balance (`multipliers.json`)](#configuring-multipliers--balance-multipliersjson)
   - [Adding Custom Recipes & Amplifiers (`recipes.json`)](#adding-custom-recipes--amplifiers-recipesjson)
2. [KubeJS Quick Start & Fluent Chaining](#2-kubejs-quick-start--fluent-chaining)
   - [Accessing IndustrialJS in KubeJS](#accessing-industrialjs-in-kubejs)
   - [Fluent Chaining (`IndustrialJS.recipes()`)](#fluent-chaining-industrialjsrecipes)
3. [Recipe Creation API (All Machines & Overloads)](#3-recipe-creation-api-all-machines--overloads)
   - [Macerator (`macerator`)](#macerator)
   - [Compressor (`compressor`)](#compressor)
   - [Extractor (`extractor`)](#extractor)
   - [Ore Washing Plant (`oreWasher`)](#ore-washing-plant)
   - [Thermal Centrifuge (`thermalCentrifuge`)](#thermal-centrifuge)
   - [Blast Furnace (`blastFurnace`)](#blast-furnace)
   - [Block Cutter (`blockCutter`)](#block-cutter)
   - [Metal Former (`metalFormerRolling`, `metalFormerCutting`, `metalFormerExtruding`)](#metal-former)
   - [Molecular Transformer (`molecularTransformer`)](#molecular-transformer)
   - [Canning Machine: Fluid Enrichment (`cannerEnrich`)](#canning-machine-fluid-enrichment)
   - [Canning Machine: Fluid Bottling / Filling (`cannerBottle`)](#canning-machine-fluid-bottling--filling)
   - [Scrapbox Drops (`scrapbox`)](#scrapbox-drops)
   - [Matter Fabricator Amplifiers (`amplifier`)](#matter-fabricator-amplifiers)
   - [Mining Laser Super-Heat Smelting (`laserSmelting`)](#mining-laser-super-heat-smelting)
4. [Machinery, Batch Crafter & Generator Balance API](#4-machinery-batch-crafter--generator-balance-api)
   - [Generators & Solar Panels](#generators--solar-panels)
   - [Machine Speed & Power Consumption](#machine-speed--power-consumption)
   - [Per-Machine Multipliers](#per-machine-multipliers)
   - [Batch Crafter Customization](#batch-crafter-customization)
   - [Solar Helmet Multipliers](#solar-helmet-multipliers)
   - [Machine Sound & Audio Muting](#machine-sound--audio-muting)
5. [Energy Grid, Storage & Electric Armor Batteries API](#5-energy-grid-storage--electric-armor-batteries-api)
   - [EU to FE Conversion Ratio](#eu-to-fe-conversion-ratio)
   - [Energy Storage Blocks (BatBox, CESU, MFE, MFSU)](#energy-storage-blocks-batbox-cesu-mfe-mfsu)
   - [Electric Armor EU Capacities & Transfer Limits](#electric-armor-eu-capacities--transfer-limits)
6. [Tools, Drills, Combat & Jetpack API](#6-tools-drills-combat--jetpack-api)
   - [Global Mining Speed](#global-mining-speed)
   - [Custom Drill Speeds & Energy Costs](#custom-drill-speeds--energy-costs)
   - [Nano Saber Combat Stats](#nano-saber-combat-stats)
   - [Mining Laser Anti-Griefing](#mining-laser-anti-griefing)
   - [Custom Jetpack Flight Dynamics](#custom-jetpack-flight-dynamics)
7. [Armor, Hazmat, Radiation & Quantum Boosts API](#7-armor-hazmat-radiation--quantum-boosts-api)
   - [Electric Armor Absorption & Energy Costs](#electric-armor-absorption--energy-costs)
   - [Quantum Suit Void Rescue, Sprint & Jump Boosts](#quantum-suit-void-rescue-sprint--jump-boosts)
   - [Custom Hazmat Gear & Standalone Full Suits](#custom-hazmat-gear--standalone-full-suits)
   - [Custom Radioactive Items & Radiation Checks](#custom-radioactive-items--radiation-checks)
8. [Nuclear Reactors, Rotors, Upgrades, Crops & World Mechanics API](#8-nuclear-reactors-rotors-upgrades-crops--world-mechanics-api)
   - [Nuclear Reactor Output, Heat & Meltdown Safety](#nuclear-reactor-output-heat--meltdown-safety)
   - [Custom Uranium & MOX Fuel Rods](#custom-uranium--mox-fuel-rods)
   - [Kinetic Wind & Water Rotors](#kinetic-wind--water-rotors)
   - [Machine Upgrades (Overclockers, Storage, Transformer)](#machine-upgrades-overclockers-storage-transformer)
   - [Terraformer Energy & Range Multipliers](#terraformer-energy--range-multipliers)
   - [Crop Breeding & Growth Multipliers](#crop-breeding--growth-multipliers)
   - [Cross-Dimensional Teleportation](#cross-dimensional-teleportation)
   - [Matter Fabricator & UU-Matter Tuning](#matter-fabricator--uu-matter-tuning)
   - [Recycler Scrap Rates & Custom Blacklists](#recycler-scrap-rates--custom-blacklists)
   - [Lossless Wrench & Machine Retention](#lossless-wrench--machine-retention)
   - [Alternative Machine Mute Tools](#alternative-machine-mute-tools)
   - [Structure Loot Injection](#structure-loot-injection)
9. [System & State Lifecycle API (`reset()`)](#9-system--state-lifecycle-api-reset)
10. [Item, Fluid & Recipe Input/Output Syntax](#10-item-fluid--recipe-inputoutput-syntax)
11. [Complete Master API Reference Table](#11-complete-master-api-reference-table)
12. [Full KubeJS Script Example](#12-full-kubejs-script-example)
13. [In-Game Hot Reloading](#13-in-game-hot-reloading)

---

## 1. Zero-Code Configuration (No Coding Needed!)

If you do not know how to code, or simply prefer straightforward configuration files, IndustrialJS automatically creates a `config/industrialjs/` directory with easy-to-read JSON files when the game starts.

### Configuring Multipliers & Balance (`config/industrialjs/multipliers.json`)

You can alter energy output, consumption, speed, conversion rates, audio, jetpacks, reactor dynamics, crops, rotors, and safety rules across all machines and generators:

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
  "reactor_heat_multiplier": 1.0,
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
  "quantum_jump_multiplier": 1.0,
  "eu_to_fe_ratio": 2.0,
  "lossless_wrench": true,
  "disable_radiation": false,
  "recycler_scrap_chance": 8,
  "machine_volume_multiplier": 1.0,
  "audio_disabled": false,
  "batch_crafter_speed_multiplier": 1.0,
  "batch_crafter_energy_multiplier": 1.0,
  "batch_crafter_capacity": 10000,
  "solar_helmet_multiplier": 1.0,
  "jetpack_thrust_multiplier": 1.0,
  "jetpack_height_multiplier": 1.0,
  "jetpack_energy_cost_multiplier": 1.0,
  "jetpack_boost_multiplier": 1.0,
  "rotor_efficiency_multiplier": 1.0,
  "overclocker_speed_multiplier": 1.0,
  "overclocker_energy_multiplier": 1.0,
  "energy_storage_upgrade_bonus": 10000,
  "transformer_upgrade_tier": 1,
  "terraformer_energy_multiplier": 1.0,
  "terraformer_range_multiplier": 1.0,
  "crop_growth_multiplier": 1.0,
  "crop_stat_gain_multiplier": 1.0,
  "crop_crossing_chance": 1.0,
  "recycler_blacklist": [
    "minecraft:glass_bottle",
    "#c:stones"
  ],
  "hazmat_items": [
    "#c:armors/hazmat",
    "create:diving_helmet"
  ],
  "hazmat_suits": [
    "mekanism:hazmat_suit",
    "pneumaticcraft:pneumatic_armor"
  ],
  "mute_tools": [
    "ic2:forge_hammer",
    "#c:tools/wrench"
  ],
  "radioactive_items": {
    "minecraft:poisonous_potato": { "duration": 10, "amplifier": 0 },
    "ic2:uranium_ore": { "duration": 30, "amplifier": 1 }
  },
  "fuel_rods": {
    "ic2:uranium_fuel_rod": { "power": 1.5, "heat": 1.0, "lifespan": 20000 },
    "ic2:mox_fuel_rod": { "power": 2.0, "heat": 1.5, "lifespan": 10000 }
  },
  "rotors": {
    "ic2:rotor_carbon": { "efficiency": 1.25, "min_wind": 10, "max_wind": 120 }
  },
  "solar_helmet_multipliers": {
    "ic2:solar_helmet": 1.5,
    "advsolar:ultimate_solar_helmet": 2.0
  },
  "armor_capacities": {
    "ic2:nano_chestplate": 1500000,
    "ic2:quantum_chestplate": 20000000
  },
  "armor_transfer_limits": {
    "ic2:nano_chestplate": 2000,
    "ic2:quantum_chestplate": 20000
  },
  "structure_loot": [
    {
      "pattern": "chests/desert_pyramid",
      "item": "ic2:iridium_ore",
      "chance": 0.25,
      "min": 1,
      "max": 3
    },
    {
      "pattern": "chests/simple_dungeon",
      "item": "ic2:uranium_ore",
      "chance": 0.40,
      "min": 1,
      "max": 2
    }
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
    "input": "minecraft:iron_ingot",
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
  },
  {
    "type": "canner_enrich",
    "input": "1000mB minecraft:water",
    "additive": "ic2:sugar",
    "output": "1000mB ic2:biomass"
  },
  {
    "type": "canner_bottle",
    "container": "minecraft:glass_bottle",
    "fluid": "250mB ic2:biomass",
    "output": "minecraft:potion"
  }
]
```

---

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

## 3. Recipe Creation API (All Machines & Overloads)

### Macerator
Grinds items and ores into crushed/powdered forms.
- **Signature**: `IndustrialJS.macerator(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.macerator('2x ic2:crushed_iron', 'minecraft:raw_iron');
IndustrialJS.macerator('4x minecraft:bone_meal', 'minecraft:bone');
```

### Compressor
Compresses materials under extreme hydraulic pressure.
- **Signature**: `IndustrialJS.compressor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.compressor('minecraft:diamond', '64x minecraft:coal');
IndustrialJS.compressor('ic2:dense_copper_plate', '9x ic2:copper_plate');
```

### Extractor
Extracts rubber, dyes, and biochemical fluids from raw organic materials.
- **Signature**: `IndustrialJS.extractor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.extractor('3x ic2:rubber', 'ic2:resin');
IndustrialJS.extractor('ic2:rubber', 'ic2:rubber_wood');
```

### Ore Washing Plant
Cleans crushed ores using water to separate stone dust and valuable sub-products.
- **Overload 1**: `IndustrialJS.oreWasher(outputs, input, waterAmount)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.oreWasher(outputs, input)` -> `IndustrialJS` *(defaults to 1000 mB water)*
```javascript
// Outputs array: [Primary, Secondary, Tertiary]
IndustrialJS.oreWasher(
  ['ic2:purified_iron', '2x ic2:small_iron_dust', 'ic2:stone_dust'],
  'ic2:crushed_iron',
  1000 // 1000 mB water
);
```

### Thermal Centrifuge
Spins purified ores under high rotational speed and temperature to isolate refined metals and rare dusts.
- **Overload 1**: `IndustrialJS.thermalCentrifuge(outputs, input, minHeat)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.thermalCentrifuge(outputs, input)` -> `IndustrialJS` *(defaults to 500 minHeat)*
```javascript
// Requires 1500 degrees minimum temperature:
IndustrialJS.thermalCentrifuge(
  ['ic2:refined_iron', 'ic2:small_gold_dust'],
  'ic2:purified_iron',
  1500
);
```

### Blast Furnace
Refines iron into refined iron and slag using heat and compressed air.
- **Overload 1**: `IndustrialJS.blastFurnace(outputs, input, duration, minHeat)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.blastFurnace(outputs, input)` -> `IndustrialJS` *(defaults to 6000 ticks, 1000 minHeat)*
```javascript
IndustrialJS.blastFurnace(
  ['ic2:refined_iron_ingot', 'ic2:slag'],
  'minecraft:iron_ingot',
  6000, // Duration in ticks
  1000  // Minimum operational heat
);
```

### Block Cutter
Slices dense blocks and ingots into precision plates with minimal waste.
- **Overload 1**: `IndustrialJS.blockCutter(output, input, hardness)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.blockCutter(output, input)` -> `IndustrialJS` *(defaults to hardness 1)*
```javascript
// Standard plate cutting (hardness 1):
IndustrialJS.blockCutter('9x ic2:tin_plate', 'ic2:tin_block');

// Custom required hardness (3 for tough obsidian/steel blocks):
IndustrialJS.blockCutter('9x ic2:steel_plate', 'ic2:steel_block', 3);
```

### Metal Former
The Metal Former operates in three distinct modes: Rolling, Cutting, and Extruding.
- **Rolling**: `IndustrialJS.metalFormerRolling(output, input)` -> `IndustrialJS` (Rolls ingots into dense plates)
- **Cutting**: `IndustrialJS.metalFormerCutting(output, input)` -> `IndustrialJS` (Cuts plates into insulated or uninsulated cables)
- **Extruding**: `IndustrialJS.metalFormerExtruding(output, input)` -> `IndustrialJS` (Extrudes ingots/plates into casings, rods, and tubes)
```javascript
IndustrialJS.metalFormerRolling('ic2:bronze_plate', 'ic2:bronze_ingot');
IndustrialJS.metalFormerCutting('3x ic2:copper_cable', 'ic2:copper_plate');
IndustrialJS.metalFormerExtruding('ic2:casing_bronze', 'ic2:bronze_ingot');
```

### Molecular Transformer
Matter transmutation device that consumes immense amounts of EU energy to transform elements.
- **Signature**: `IndustrialJS.molecularTransformer(output, input, euCost)` -> `IndustrialJS`
```javascript
// Transmute Wither Skeleton Skull into a Nether Star for 250,000,000 EU:
IndustrialJS.molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000.0);

// Transmute Iron Block into Gold Block for 5,000,000 EU:
IndustrialJS.molecularTransformer('minecraft:gold_block', 'minecraft:iron_block', 5000000.0);
```

### Canning Machine: Fluid Enrichment
Enriches or chemically alters a base fluid by mixing it with an item/additive in the Canning Machine.
- **Signature**: `IndustrialJS.cannerEnrich(outputFluid, inputFluid, additive)` -> `IndustrialJS`
```javascript
// Enrich 1000 mB water with coolant dust into 1000 mB coolant fluid:
IndustrialJS.cannerEnrich('1000mB ic2:coolant', '1000mB minecraft:water', 'ic2:coolant_drop');
```

### Canning Machine: Fluid Bottling / Filling
Bottles liquids from the Canning Machine's internal tank into containers (bottles, tin cans, fuel cans).
- **Signature**: `IndustrialJS.cannerBottle(output, container, fillFluid)` -> `IndustrialJS`
```javascript
// Fill glass bottle with 250 mB biomass to make a potion:
IndustrialJS.cannerBottle('minecraft:potion', 'minecraft:glass_bottle', '250mB ic2:biomass');
```

### Scrapbox Drops
Registers new surprise items that can randomly drop when opening an IC2 Scrap Box.
- **Signature**: `IndustrialJS.scrapbox(item, weight)` -> `IndustrialJS`
```javascript
IndustrialJS.scrapbox('minecraft:diamond', 0.05);   // 5% relative weight
IndustrialJS.scrapbox('minecraft:emerald', 0.10);   // 10% relative weight
IndustrialJS.scrapbox('ic2:iridium_ore', 0.01);     // 1% rare drop
```

### Matter Fabricator Amplifiers
Registers custom items or blocks as scrap amplifiers in the Matter Fabricator, accelerating UU-Matter production and drastically reducing EU energy demands.
- **Signature**: `IndustrialJS.amplifier(item, amplification)` -> `IndustrialJS`
```javascript
IndustrialJS.amplifier('minecraft:cobblestone', 100);
IndustrialJS.amplifier('minecraft:netherrack', 250);
IndustrialJS.amplifier('ic2:scrap', 5000);
IndustrialJS.amplifier('ic2:scrap_box', 45000);
```

### Mining Laser Super-Heat Smelting
Registers custom in-world block transformations when struck by the Mining Laser in **Super-Heat** beam mode.
- **Signature**: `IndustrialJS.laserSmelting(input, output)` -> `void`
- **Query**: `IndustrialJS.getLaserSmelting(inputStack)` -> `ItemStack`
```javascript
IndustrialJS.laserSmelting('minecraft:sand', 'minecraft:glass');
IndustrialJS.laserSmelting('minecraft:cobblestone', 'minecraft:stone');
IndustrialJS.laserSmelting('minecraft:raw_iron_block', 'minecraft:iron_block');
```

---

## 4. Machinery, Batch Crafter & Generator Balance API

Every multiplier has both a **getter** and a **setter**. You can query current values to scale them dynamically.

### Generators & Solar Panels
- `setGeneratorMultiplier(mult)` / `getGeneratorMultiplier()`: Multiplier for standard fuel/geothermal/water generators.
- `setSolarDayMultiplier(mult)` / `getSolarDayMultiplier()`: Daytime generation multiplier for standard and Advanced Solar Panels.
- `setSolarNightMultiplier(mult)` / `getSolarNightMultiplier()`: Nighttime/lunar generation multiplier.
- `setSolarMultiplier(mult)`: Convenience method that sets **both** day and night multipliers simultaneously.

```javascript
// Query and double existing generator output:
let currentGen = IndustrialJS.getGeneratorMultiplier();
IndustrialJS.setGeneratorMultiplier(currentGen * 2.0);

// Set both solar day and night generation:
IndustrialJS.setSolarMultiplier(2.5);
```

### Machine Speed & Power Consumption
- `setMachineConsumptionMultiplier(mult)` / `getMachineConsumptionMultiplier()`: Scales EU/tick energy usage across all standard machines.
- `setMachineSpeedMultiplier(mult)` / `getMachineSpeedMultiplier()`: Scales processing speed across all machines (minimum `0.01`).
- `setMolecularTransformerMultiplier(mult)` / `getMolecularTransformerMultiplier()`: Multiplier applied to all Molecular Transformer recipes (e.g. `0.5` cuts all transmutation costs in half).

```javascript
IndustrialJS.setMachineConsumptionMultiplier(0.75); // 25% energy savings
IndustrialJS.setMachineSpeedMultiplier(1.5);        // 50% faster processing
IndustrialJS.setMolecularTransformerMultiplier(0.5);// 50% cheaper transmutations
```

### Per-Machine Multipliers
Tune processing speed or energy consumption for specific machines by machine ID:
- `setMachineMultiplier(machineId, mult)`: Sets multiplier for machine ID (e.g. `'macerator'`, `'compressor'`, `'extractor'`).
- `getMachineMultiplier(machineId)`: Returns current multiplier (defaults to `1.0` if unset).

```javascript
IndustrialJS.setMachineMultiplier('macerator', 2.0);  // Double macerator speed/throughput
IndustrialJS.setMachineMultiplier('extractor', 1.25);
console.log('Macerator multiplier: ' + IndustrialJS.getMachineMultiplier('macerator'));
```

### Batch Crafter Customization
Control processing speed, operational EU energy cost, and internal battery buffer for the automated Batch Crafter:
- `setBatchCrafterSpeedMultiplier(mult)` / `getBatchCrafterSpeedMultiplier()`: Scales crafting progress speed.
- `setBatchCrafterEnergyMultiplier(mult)` / `getBatchCrafterEnergyMultiplier()`: Scales EU consumed per crafting recipe cycle.
- `setBatchCrafterCapacity(capacity)` / `getBatchCrafterCapacity(default)`: Sets internal EU energy capacity buffer (default 10,000 EU).

```javascript
IndustrialJS.setBatchCrafterSpeedMultiplier(2.0);   // 2x faster batch crafting
IndustrialJS.setBatchCrafterEnergyMultiplier(0.5);  // 50% less energy consumed
IndustrialJS.setBatchCrafterCapacity(50000);        // 50,000 EU buffer
```

### Solar Helmet Multipliers
Modify energy generated by worn solar helmets from IC2 and Advanced Solar Panels:
- `setSolarHelmetMultiplier(mult)` / `getSolarHelmetMultiplier()`: Global multiplier for all solar helmets.
- `setSolarHelmetMultiplier(helmetId, mult)`: Multiplier targeting a specific solar helmet item (e.g. `'ic2:solar_helmet'`, `'advsolar:ultimate_solar_helmet'`).
- `getSolarHelmetMultiplier(itemStack)`: Inspects active multiplier for the given helmet item.

```javascript
IndustrialJS.setSolarHelmetMultiplier(2.0); // 2x EU for all solar helmets
IndustrialJS.setSolarHelmetMultiplier('advsolar:ultimate_solar_helmet', 3.0);
```

### Machine Sound & Audio Muting
- `setMachineVolumeMultiplier(mult)` / `getMachineVolumeMultiplier()`: Scales volume for all IC2 machine operational audio sounds (`1.0` = default, `0.5` = half volume).
- `setAudioDisabled(disabled)` / `isAudioDisabled()`: Master toggle to completely silence all IC2 machine and tool sounds.

```javascript
IndustrialJS.setMachineVolumeMultiplier(0.3); // 30% volume
// IndustrialJS.setAudioDisabled(true);       // Completely mute machine noises
```

---

## 5. Energy Grid, Storage & Electric Armor Batteries API

### EU to FE Conversion Ratio
IndustrialCraft 2 Refactored features a built-in energy bridge connecting EU cables, machines, and storage directly to Forge Energy / RF / Mekanism networks.
- `setEuToFeRatio(ratio)`: Sets FE delivered per 1 EU (default `2.0`). Must be finite and `> 0.0`.
- `getEuToFeRatio()`: Returns current conversion ratio.

```javascript
IndustrialJS.setEuToFeRatio(4.0); // 1 EU = 4 FE (classic 1:4 standard)
console.log('Current EU/FE ratio: ' + IndustrialJS.getEuToFeRatio());
```

### Energy Storage Blocks (BatBox, CESU, MFE, MFSU)
Modify maximum storage capacity and EU/t output rate for any energy storage unit without editing Java tile entities:
- `setStorageCapacity(id, capacity)`: Sets storage capacity in EU for `'batbox'`, `'cesu'`, `'mfe'`, or `'mfsu'`.
- `getStorageCapacity(blockEntityType, defaultCapacity)`: Inspects storage capacity.
- `setStorageOutput(id, outputRate)`: Sets output rate in EU/t for `'batbox'`, `'cesu'`, `'mfe'`, or `'mfsu'`.
- `getStorageOutput(blockEntityType, defaultOutput)`: Inspects output rate.

```javascript
// Expand BatBox to 80,000 EU with 64 EU/t output:
IndustrialJS.setStorageCapacity('batbox', 80000);
IndustrialJS.setStorageOutput('batbox', 64);

// Supercharge MFSU to 100,000,000 EU with 4,096 EU/t output:
IndustrialJS.setStorageCapacity('mfsu', 100000000);
IndustrialJS.setStorageOutput('mfsu', 4096);
```

### Electric Armor EU Capacities & Transfer Limits
Modify battery storage capacity and recharge/transfer rates for electric armor pieces (Nano Suit, Quantum Suit, Hazmat, Jetpacks, etc.):
- `setArmorCapacity(itemOrId, capacity)`: Overrides maximum EU stored in the armor piece.
- `getArmorCapacity(itemStack, defaultCapacity)`: Inspects armor EU capacity.
- `setArmorTransferLimit(itemOrId, transferLimit)`: Overrides maximum EU/t charge/discharge rate.
- `getArmorTransferLimit(itemStack, defaultLimit)`: Inspects armor transfer limit.

```javascript
// Expand Nano Suit Chestplate to 2,000,000 EU with 2,500 EU/t transfer rate:
IndustrialJS.setArmorCapacity('ic2:nano_chestplate', 2000000);
IndustrialJS.setArmorTransferLimit('ic2:nano_chestplate', 2500);

// Supercharge Quantum Suit Body to 50,000,000 EU:
IndustrialJS.setArmorCapacity('ic2:quantum_chestplate', 50000000);
```

---

## 6. Tools, Drills, Combat & Jetpack API

### Global Mining Speed
- `setMiningSpeedMultiplier(mult)` / `getMiningSpeedMultiplier()`: Scales block-breaking speed across all electric tools.

```javascript
IndustrialJS.setMiningSpeedMultiplier(1.25); // 25% faster global mining
```

### Custom Drill Speeds & Energy Costs
Fine-tune individual drill tiers (Mining Drill, Diamond Drill, Iridium Drill):
- `setDrillMiningSpeed(drillId, speed)`: Custom base mining speed for the specified drill item.
- `hasDrillMiningSpeed(drillId)`: Returns `true` if a custom speed is set.
- `getDrillMiningSpeed(drillId, defaultSpeed)`: Returns configured speed, or `defaultSpeed` if unset.
- `setDrillEnergyPerBlock(drillId, energy)`: Custom EU energy drained per block mined.
- `hasDrillEnergyPerBlock(drillId)`: Returns `true` if a custom energy cost is set.
- `getDrillEnergyPerBlock(drillId, defaultCost)`: Returns configured energy cost, or `defaultCost` if unset.

```javascript
IndustrialJS.setDrillMiningSpeed('ic2:iridium_drill', 45.0);
IndustrialJS.setDrillEnergyPerBlock('ic2:iridium_drill', 150);

IndustrialJS.setDrillMiningSpeed('ic2:diamond_drill', 20.0);
IndustrialJS.setDrillEnergyPerBlock('ic2:diamond_drill', 60);

if (IndustrialJS.hasDrillMiningSpeed('ic2:iridium_drill')) {
  console.log('Iridium drill speed is customized: ' + IndustrialJS.getDrillMiningSpeed('ic2:iridium_drill', 35.0));
}
```

### Nano Saber Combat Stats
Fine-tune Nano Saber combat mechanics, energy draw, and true armor penetration:
- `setNanoSaberActiveDamage(damage)` / `getNanoSaberActiveDamage()`: Attack damage when turned **ON** (default `20.0`).
- `setNanoSaberPassiveDamage(damage)` / `getNanoSaberPassiveDamage()`: Attack damage when turned **OFF** (default `4.0`).
- `setNanoSaberEnergyPerHit(energy)` / `getNanoSaberEnergyPerHit()`: EU consumed per strike (default `400.0`).
- `setNanoSaberArmorBypass(ratio)` / `getNanoSaberArmorBypass()`: Armor bypass percentage (`0.0` to `1.0`, e.g. `0.25` = 25% true penetration).
- `setNanoSaberStats(map)`: Convenience method to configure all combat stats in one object call.

```javascript
IndustrialJS.setNanoSaberStats({
  activeDamage: 28.0,
  passiveDamage: 4.0,
  energyPerHit: 150.0,
  armorBypassPercentage: 0.35 // 35% armor bypass
});
```

### Mining Laser Anti-Griefing
- `setLaserExplosiveDestruction(enable)` / `isLaserExplosiveDestruction()`:
  - When `true` (default): Mining Laser explosive mode destroys blocks in world.
  - When `false`: Protects blocks against griefing on multiplayer servers while still damaging mobs!

```javascript
IndustrialJS.setLaserExplosiveDestruction(false); // Server-safe mining laser
```

### Custom Jetpack Flight Dynamics
Tune thrust, flight ceiling, energy drain, and forward boost for electric and combustion jetpacks:
- `setJetpackThrustMultiplier(mult)` / `getJetpackThrustMultiplier()`: Scales upward acceleration and vertical thrust.
- `setJetpackHeightMultiplier(mult)` / `getJetpackHeightMultiplier()`: Scales maximum flight ceiling altitude.
- `setJetpackEnergyCostMultiplier(mult)` / `getJetpackEnergyCostMultiplier()`: Scales EU or fuel consumed per tick of flight.
- `setJetpackBoostMultiplier(mult)` / `getJetpackBoostMultiplier()`: Scales forward boost velocity when holding sprint/boost keys.

```javascript
IndustrialJS.setJetpackThrustMultiplier(1.3);     // 30% more vertical thrust
IndustrialJS.setJetpackHeightMultiplier(1.5);     // 50% higher flight ceiling
IndustrialJS.setJetpackEnergyCostMultiplier(0.7); // 30% less battery drain
IndustrialJS.setJetpackBoostMultiplier(1.5);      // 50% faster horizontal sprint
```

---

## 7. Armor, Hazmat, Radiation & Quantum Boosts API

### Electric Armor Absorption & Energy Costs
- `setArmorAbsorptionMultiplier(mult)` / `getArmorAbsorptionMultiplier()`: Scales percentage of damage absorbed by electric armor.
- `setArmorEnergyCostMultiplier(mult)` / `getArmorEnergyCostMultiplier()`: Scales EU energy drained from armor when mitigating damage.

```javascript
IndustrialJS.setArmorAbsorptionMultiplier(1.2);   // 20% stronger protection
IndustrialJS.setArmorEnergyCostMultiplier(0.8);   // 20% longer battery life under fire
```

### Quantum Suit Void Rescue, Sprint & Jump Boosts
- `setQuantumSuitVoidProtection(enable)` / `isQuantumSuitVoidProtection()`: When `true`, falling into the void below the world's minimum build height automatically consumes suit EU to safely boost the player back upward, resetting fall damage.
- `setQuantumSprintMultiplier(mult)` / `setQuantumSprintSpeed(speed)` / `getQuantumSprintMultiplier()`: Speed multiplier applied when sprinting with Quantum Leggings.
- `setQuantumJumpMultiplier(mult)` / `getQuantumJumpMultiplier()`: Multiplier for Quantum Boots high jump boost velocity.

```javascript
IndustrialJS.setQuantumSuitVoidProtection(true);  // Never lose items to the Void!
IndustrialJS.setQuantumSprintSpeed(2.5);          // Super-speed sprint
IndustrialJS.setQuantumJumpMultiplier(1.5);       // 50% higher jumps
```

### Custom Hazmat Gear & Standalone Full Suits
- `addHazmatItem(itemOrTag)`: Registers an individual item or tag as a certified hazmat piece (requires matching slot).
- `addHazmatSuit(itemOrTag)`: Registers a standalone item or tag that grants **100% full radiation immunity** on its own (e.g. Mekanism Hazmat, PneumaticCraft suit, Diving suit)!
- `isHazmatPiece(itemStack, equipmentSlot)`: Tests whether an item stack qualifies as hazmat armor for the given slot.
- `isFullHazmatSuit(itemStack)`: Tests whether an item stack provides complete radiation immunity.
- `setRadiationDisabled(disabled)` / `isRadiationDisabled()`: Completely disables nuclear radiation damage and environmental effects across the entire game.

```javascript
// Register individual pieces:
IndustrialJS.addHazmatItem('create:diving_helmet');
IndustrialJS.addHazmatItem('create:diving_boots');

// Register 1-piece standalone suits that offer 100% full radiation immunity:
IndustrialJS.addHazmatSuit('mekanism:hazmat_suit');
IndustrialJS.addHazmatSuit('pneumaticcraft:pneumatic_armor');

// Disable radiation entirely for casual modpacks:
IndustrialJS.setRadiationDisabled(true);
```

### Custom Radioactive Items & Radiation Checks
Make any item or block in Minecraft radioactive! If a player carries it in their inventory without certified Hazmat gear, they suffer nuclear radiation sickness:
- `addRadioactiveItem(itemOrTag, durationSeconds, amplifier)`: Registers an item or tag as radioactive with custom effect duration and amplifier level.
- `addRadioactiveItem(itemOrTag)`: Registers an item with standard radiation stats (15 seconds, amplifier 0).
- `isRadioactive(itemStack)`: Checks whether an item stack is radioactive.
- `getRadiationDuration(itemStack)`: Returns radiation effect duration in seconds.
- `getRadiationAmplifier(itemStack)`: Returns radiation effect amplifier.
- `checkRadioactiveInventory(player)`: Automatically called on player tick, or can be triggered manually in scripts.

```javascript
// Make Poisonous Potatoes give mild radiation (10s, level 1):
IndustrialJS.addRadioactiveItem('minecraft:poisonous_potato', 10, 0);

// Make Netherite Scrap give severe radiation (30s, level 2):
IndustrialJS.addRadioactiveItem('minecraft:netherite_scrap', 30, 1);
```

---

## 8. Nuclear Reactors, Rotors, Upgrades, Crops & World Mechanics API

### Nuclear Reactor Output, Heat & Meltdown Safety
- `setNuclearReactorMultiplier(mult)` / `getNuclearReactorMultiplier()`: Scales total EU power output produced by nuclear reactor fuel rods.
- `setReactorHeatMultiplier(mult)` / `getReactorHeatMultiplier()`: Scales total heat produced by reactor components and fuel rods (`0.5` = half heat, `0.0` = completely heatless reactors).
- `setReactorExplosionMultiplier(mult)` / `getReactorExplosionMultiplier()`: Blast radius and explosion multiplier for reactor meltdowns. Set to `0.0` to disable meltdown terrain destruction completely!

```javascript
IndustrialJS.setNuclearReactorMultiplier(1.5);   // 50% more reactor power
IndustrialJS.setReactorHeatMultiplier(0.8);      // 20% cooler reactors
IndustrialJS.setReactorExplosionMultiplier(0.0); // Safe nuclear reactors (no crater griefing)
```

### Custom Uranium & MOX Fuel Rods
Customize power output, heat generation, and lifespan (maximum durability) for single, dual, and quad Uranium/MOX fuel rods, or register third-party mod items as custom nuclear fuel rods!
- `fuelRod(itemOrId, powerMultiplier, heatMultiplier, lifespan)` -> `IndustrialJS` (Fluent builder method)
- `setFuelRodStats(itemOrId, powerMultiplier, heatMultiplier, lifespan)` -> `void`
- `setFuelRodPowerMultiplier(itemOrId, mult)`
- `setFuelRodHeatMultiplier(itemOrId, mult)`
- `setFuelRodLifespan(itemOrId, lifespan)`
- `getFuelRodPowerMultiplier(itemStack)`
- `getFuelRodHeatMultiplier(itemStack)`
- `getFuelRodLifespan(itemStack, defaultLifespan)`

```javascript
// Fluent builder registration:
IndustrialJS.recipes()
  .fuelRod('ic2:uranium_fuel_rod', 2.0, 1.0, 20000)
  .fuelRod('ic2:mox_fuel_rod', 3.0, 1.5, 15000);

// Direct static setter:
IndustrialJS.setFuelRodStats('ic2:dual_uranium_fuel_rod', 2.0, 1.2, 20000);
```

### Kinetic Wind & Water Rotors
Customize efficiency and wind speed boundaries for kinetic wind and water rotors (Wood, Iron, Steel, Carbon):
- `setRotorEfficiencyMultiplier(mult)` / `getRotorEfficiencyMultiplier()`: Global efficiency multiplier for all kinetic rotors.
- `setRotorStats(rotorId, efficiencyMultiplier, minWind, maxWind)`: Custom stats for a specific rotor item.
- `getRotorEfficiencyMultiplier(itemStack)`
- `getRotorMinWind(itemStack, defaultMin)`
- `getRotorMaxWind(itemStack, defaultMax)`

```javascript
IndustrialJS.setRotorEfficiencyMultiplier(1.5); // Global 50% more kinetic energy
IndustrialJS.setRotorStats('ic2:rotor_carbon', 2.0, 8, 150); // High efficiency carbon rotor
```

### Machine Upgrades (Overclockers, Storage, Transformer)
Customize how IC2 machine upgrades behave when installed inside machines:
- `setOverclockerSpeedMultiplier(mult)` / `getOverclockerSpeedMultiplier()`: Scales processing time reduction per Overclocker upgrade (default `1.0`).
- `setOverclockerEnergyMultiplier(mult)` / `getOverclockerEnergyMultiplier()`: Scales energy demand increase per Overclocker upgrade (default `1.0`).
- `setEnergyStorageUpgradeBonus(bonus)` / `getEnergyStorageUpgradeBonus()`: Bonus internal EU storage added per Energy Storage upgrade (default `10,000` EU).
- `setTransformerUpgradeTier(tierBonus)` / `getTransformerUpgradeTier()`: Voltage tier increase provided per Transformer upgrade (default `1`).

```javascript
IndustrialJS.setOverclockerSpeedMultiplier(1.2);   // Overclockers speed up machines 20% more
IndustrialJS.setOverclockerEnergyMultiplier(0.8);  // Overclockers use 20% less energy penalty
IndustrialJS.setEnergyStorageUpgradeBonus(25000);  // 25,000 EU per storage upgrade
```

### Terraformer Energy & Range Multipliers
Control the power demands and geographical range of the IC2 Terraformer:
- `setTerraformerEnergyMultiplier(mult)` / `getTerraformerEnergyMultiplier()`: Scales EU energy consumed per terraforming cycle.
- `setTerraformerRangeMultiplier(mult)` / `getTerraformerRangeMultiplier()`: Scales block radius and operational reach.

```javascript
IndustrialJS.setTerraformerEnergyMultiplier(0.5); // 50% cheaper terraforming
IndustrialJS.setTerraformerRangeMultiplier(2.0);  // Double terraforming area
```

### Crop Breeding & Growth Multipliers
Fine-tune IC2 Agricultural crop breeding, crossbreeding mutation rates, and growth speeds:
- `setCropGrowthMultiplier(mult)` / `getCropGrowthMultiplier()`: Scales crop growth point accumulation per tick.
- `setCropStatGainMultiplier(mult)` / `getCropStatGainMultiplier()`: Scales stat gains (Growth, Gain, Resistance) during crossbreeding.
- `setCropCrossingChance(mult)` / `getCropCrossingChance()`: Scales chance for adjacent crop sticks to breed new plant varieties.

```javascript
IndustrialJS.setCropGrowthMultiplier(2.0);   // 2x faster crop growth
IndustrialJS.setCropCrossingChance(1.5);    // 50% higher crossbreeding success
IndustrialJS.setCropStatGainMultiplier(1.5); // Boost stat mutation speed
```

### Cross-Dimensional Teleportation
- `setTeleporterCrossDimension(enable)` / `isTeleporterCrossDimension()`: Enables or disables cross-dimensional teleportation.
- `setTeleporterCrossDimensionFlatCost(cost)` / `getTeleporterCrossDimensionFlatCost()`: Flat base EU energy cost for jumping across dimensions (default `1,000,000.0` EU).
- `setTeleporterCostMultiplier(mult)` / `setTeleporterDistanceCostMultiplier(mult)` / `getTeleporterCostMultiplier()`: Multiplier applied to intra-dimensional distance energy costs.

```javascript
IndustrialJS.setTeleporterCrossDimension(true);
IndustrialJS.setTeleporterCrossDimensionFlatCost(500000.0); // 500k EU flat cost
IndustrialJS.setTeleporterDistanceCostMultiplier(0.5);      // 50% cheaper distance cost
```

### Matter Fabricator & UU-Matter Tuning
- `setUuEnergyMultiplier(mult)` / `getUuEnergyMultiplier()`: Multiplier for EU energy required to generate 1 mB of UU-Matter (default `1.0`).
- `setMatterAmplifierMultiplier(mult)` / `getMatterAmplifierMultiplier()`: Multiplier for scrap amplification points (default `1.0`).

```javascript
IndustrialJS.setUuEnergyMultiplier(0.5);        // 50% less energy required for UU-Matter
IndustrialJS.setMatterAmplifierMultiplier(2.0); // Scrap amplifies twice as much
```

### Recycler Scrap Rates & Custom Blacklists
- `setRecyclerScrapChance(chance)` / `getRecyclerScrapChance()`: Sets scrap drop probability as `1 in N` operations (default `8` = 12.5% chance).
- `addRecyclerBlacklist(itemOrTag)`: Blacklists items or item tags from producing scrap in the Recycler.
- `isRecyclerBlacklisted(itemStack)`: Checks whether an item stack is blacklisted from recycling.

```javascript
IndustrialJS.setRecyclerScrapChance(4); // 25% scrap chance (1 in 4)
IndustrialJS.addRecyclerBlacklist('minecraft:glass_bottle');
IndustrialJS.addRecyclerBlacklist('#c:stones');
```

### Lossless Wrench & Machine Retention
- `setLosslessWrench(lossless)` / `isLosslessWrench()`: When `true`, dismantling any IC2 machine with an electric or manual wrench guarantees 100% chance to recover the intact machine instead of degrading into an empty machine casing.

```javascript
IndustrialJS.setLosslessWrench(true); // 100% safe machine dismantling
```

### Alternative Machine Mute Tools
- `addMuteTool(itemOrTag)`: Registers custom items or tags that can mute/unmute machines upon right-clicking.
- `isMuteTool(itemStack)`: Checks whether an item stack is configured as an audio mute tool.

```javascript
IndustrialJS.addMuteTool('#c:tools/wrench');
IndustrialJS.addMuteTool('minecraft:stick');
```

### Structure Loot Injection
Inject IC2 items, ores, or custom mod items directly into vanilla or modded dungeon and structure chests without complex JSON loot tables!
- `addStructureLoot(tablePattern, itemOrStack, chance, minCount, maxCount)`
- `addStructureLoot(tablePattern, itemOrStack, chance)` *(defaults min/max to 1)*

```javascript
// Add 1-3 Iridium Ores to desert pyramids with 25% probability:
IndustrialJS.addStructureLoot('chests/desert_pyramid', 'ic2:iridium_ore', 0.25, 1, 3);

// Add Uranium Ore to all dungeon chests with 50% probability:
IndustrialJS.addStructureLoot('chests/simple_dungeon', 'ic2:uranium_ore', 0.50, 1, 2);

// Inject into all Nether fortress chests:
IndustrialJS.addStructureLoot('chests/nether_bridge', 'ic2:energy_crystal', 0.15, 1, 1);
```

---

## 9. System & State Lifecycle API (`reset()`)

- `IndustrialJS.reset()`: Resets all multipliers, scrap chances, blacklists, mute tools, hazmat sets, drill speeds, laser recipes, storage capacities, output rates, rotors, upgrades, crops, and structure loot back to vanilla IC2 defaults.
- Useful when writing test scripts or reloading dynamic modpack states.

```javascript
IndustrialJS.reset();
```

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

## 11. Complete Master API Reference Table

Here is the complete list of all **120+ methods** available in `ic2.industrialjs.IndustrialJS`:

| Category | Method | Return Type | Arguments / Description | Default |
| :--- | :--- | :--- | :--- | :--- |
| **Lifecycle** | `reset()` | `void` | Resets all multipliers, configurations, and caches to defaults | — |
| **Lifecycle** | `recipes()` | `IndustrialJS` | Returns chainable recipe builder instance | Instance |
| **Lifecycle** | `recipes(event)` | `IndustrialJS` | Returns chainable recipe builder instance (event ignored) | Instance |
| **Generators** | `setGeneratorMultiplier(mult)` | `void` | Sets standard generator output multiplier | `1.0` |
| **Generators** | `getGeneratorMultiplier()` | `double` | Gets standard generator output multiplier | `1.0` |
| **Generators** | `setSolarDayMultiplier(mult)` | `void` | Sets daytime solar panel output multiplier | `1.0` |
| **Generators** | `getSolarDayMultiplier()` | `double` | Gets daytime solar panel output multiplier | `1.0` |
| **Generators** | `setSolarNightMultiplier(mult)` | `void` | Sets nighttime/lunar solar panel output multiplier | `1.0` |
| **Generators** | `getSolarNightMultiplier()` | `double` | Gets nighttime/lunar solar panel output multiplier | `1.0` |
| **Generators** | `setSolarMultiplier(mult)` | `void` | Sets both day and night solar output multipliers | — |
| **Machines** | `setMachineConsumptionMultiplier(mult)` | `void` | Sets global machine energy consumption multiplier | `1.0` |
| **Machines** | `getMachineConsumptionMultiplier()` | `double` | Gets global machine energy consumption multiplier | `1.0` |
| **Machines** | `setMachineSpeedMultiplier(mult)` | `void` | Sets global machine processing speed multiplier | `1.0` |
| **Machines** | `getMachineSpeedMultiplier()` | `double` | Gets global machine processing speed multiplier | `1.0` |
| **Machines** | `setMolecularTransformerMultiplier(mult)` | `void` | Sets Molecular Transformer EU cost multiplier | `1.0` |
| **Machines** | `getMolecularTransformerMultiplier()` | `double` | Gets Molecular Transformer EU cost multiplier | `1.0` |
| **Machines** | `setMachineMultiplier(id, mult)` | `void` | Sets multiplier for specific machine (`'macerator'`, etc.) | `1.0` |
| **Machines** | `getMachineMultiplier(id)` | `double` | Gets multiplier for specific machine | `1.0` |
| **Batch Crafter** | `setBatchCrafterSpeedMultiplier(mult)` | `void` | Sets batch crafter operational speed multiplier | `1.0` |
| **Batch Crafter** | `getBatchCrafterSpeedMultiplier()` | `double` | Gets batch crafter operational speed multiplier | `1.0` |
| **Batch Crafter** | `setBatchCrafterEnergyMultiplier(mult)`| `void` | Sets batch crafter operational energy multiplier | `1.0` |
| **Batch Crafter** | `getBatchCrafterEnergyMultiplier()` | `double` | Gets batch crafter operational energy multiplier | `1.0` |
| **Batch Crafter** | `setBatchCrafterCapacity(capacity)` | `void` | Sets batch crafter internal EU buffer capacity | `10,000` |
| **Batch Crafter** | `getBatchCrafterCapacity(default)` | `int` | Gets batch crafter internal EU buffer capacity | `10,000` |
| **Solar Helmets**| `setSolarHelmetMultiplier(mult)` | `void` | Sets global solar helmet EU generation multiplier | `1.0` |
| **Solar Helmets**| `setSolarHelmetMultiplier(id, mult)` | `void` | Sets generation multiplier for specific solar helmet | `1.0` |
| **Solar Helmets**| `getSolarHelmetMultiplier()` | `double` | Gets global solar helmet generation multiplier | `1.0` |
| **Solar Helmets**| `getSolarHelmetMultiplier(stack)` | `double` | Gets generation multiplier for specific helmet item stack | `1.0` |
| **Audio** | `setMachineVolumeMultiplier(mult)` | `void` | Sets machine audio volume multiplier (`0.0` to `2.0`) | `1.0` |
| **Audio** | `getMachineVolumeMultiplier()` | `double` | Gets machine audio volume multiplier | `1.0` |
| **Audio** | `setAudioDisabled(disabled)` | `void` | Completely silences all IC2 machine/tool sound effects | `false` |
| **Audio** | `isAudioDisabled()` | `boolean` | Checks if IC2 sound effects are disabled | `false` |
| **Energy Bridge** | `setEuToFeRatio(ratio)` | `void` | Sets FE conversion ratio per 1 EU | `2.0` |
| **Energy Bridge** | `getEuToFeRatio()` | `double` | Gets FE conversion ratio per 1 EU | `2.0` |
| **Energy Storage**| `setStorageCapacity(id, cap)` | `void` | Sets storage capacity in EU (`'batbox'`, `'mfsu'`) | — |
| **Energy Storage**| `getStorageCapacity(type, def)` | `int` | Gets storage capacity for block entity type | — |
| **Energy Storage**| `setStorageOutput(id, out)` | `void` | Sets output rate in EU/t (`'batbox'`, `'mfsu'`) | — |
| **Energy Storage**| `getStorageOutput(type, def)` | `int` | Gets output rate for block entity type | — |
| **Electric Armor**| `setArmorCapacity(item, cap)` | `void` | Overrides maximum EU stored in electric armor piece | — |
| **Electric Armor**| `getArmorCapacity(stack, def)` | `double` | Gets maximum EU stored in armor piece | — |
| **Electric Armor**| `setArmorTransferLimit(item, limit)` | `void` | Overrides max EU/t charge rate for armor piece | — |
| **Electric Armor**| `getArmorTransferLimit(stack, def)` | `double` | Gets max EU/t charge rate for armor piece | — |
| **Electric Armor**| `setArmorAbsorptionMultiplier(mult)` | `void` | Sets electric armor damage absorption multiplier | `1.0` |
| **Electric Armor**| `getArmorAbsorptionMultiplier()` | `double` | Gets electric armor damage absorption multiplier | `1.0` |
| **Electric Armor**| `setArmorEnergyCostMultiplier(mult)` | `void` | Sets electric armor energy drain multiplier | `1.0` |
| **Electric Armor**| `getArmorEnergyCostMultiplier()` | `double` | Gets electric armor energy drain multiplier | `1.0` |
| **Quantum Suit** | `setQuantumSuitVoidProtection(enable)` | `void` | Toggles void rescue teleportation safety | `true` |
| **Quantum Suit** | `isQuantumSuitVoidProtection()` | `boolean` | Checks if void rescue protection is enabled | `true` |
| **Quantum Suit** | `setQuantumSprintMultiplier(mult)` | `void` | Sets sprinting speed multiplier for Quantum Leggings| `1.0` |
| **Quantum Suit** | `setQuantumSprintSpeed(speed)` | `void` | Alias for `setQuantumSprintMultiplier` | `1.0` |
| **Quantum Suit** | `getQuantumSprintMultiplier()` | `double` | Gets sprinting speed multiplier for Quantum Leggings| `1.0` |
| **Quantum Suit** | `setQuantumJumpMultiplier(mult)` | `void` | Sets high-jump velocity multiplier for Quantum Boots | `1.0` |
| **Quantum Suit** | `getQuantumJumpMultiplier()` | `double` | Gets high-jump velocity multiplier for Quantum Boots | `1.0` |
| **Jetpack** | `setJetpackThrustMultiplier(mult)` | `void` | Sets vertical thrust multiplier for jetpacks | `1.0` |
| **Jetpack** | `getJetpackThrustMultiplier()` | `double` | Gets vertical thrust multiplier for jetpacks | `1.0` |
| **Jetpack** | `setJetpackHeightMultiplier(mult)` | `void` | Sets maximum flight ceiling altitude multiplier | `1.0` |
| **Jetpack** | `getJetpackHeightMultiplier()` | `double` | Gets maximum flight ceiling altitude multiplier | `1.0` |
| **Jetpack** | `setJetpackEnergyCostMultiplier(mult)` | `void` | Sets flight fuel/energy drain multiplier | `1.0` |
| **Jetpack** | `getJetpackEnergyCostMultiplier()` | `double` | Gets flight fuel/energy drain multiplier | `1.0` |
| **Jetpack** | `setJetpackBoostMultiplier(mult)` | `void` | Sets forward boost acceleration multiplier | `1.0` |
| **Jetpack** | `getJetpackBoostMultiplier()` | `double` | Gets forward boost acceleration multiplier | `1.0` |
| **Mining Tools** | `setMiningSpeedMultiplier(mult)` | `void` | Sets global electric tool mining speed multiplier | `1.0` |
| **Mining Tools** | `getMiningSpeedMultiplier()` | `double` | Gets global electric tool mining speed multiplier | `1.0` |
| **Drills** | `setDrillMiningSpeed(id, speed)` | `void` | Sets custom base mining speed for a drill item | — |
| **Drills** | `hasDrillMiningSpeed(id)` | `boolean` | Checks if a custom mining speed is set for a drill | — |
| **Drills** | `getDrillMiningSpeed(id, def)` | `double` | Gets drill mining speed, or `def` if unset | — |
| **Drills** | `setDrillEnergyPerBlock(id, cost)` | `void` | Sets EU drained per block mined for a drill | — |
| **Drills** | `hasDrillEnergyPerBlock(id)` | `boolean` | Checks if custom energy cost is set for a drill | — |
| **Drills** | `getDrillEnergyPerBlock(id, def)` | `int` | Gets drill energy cost, or `def` if unset | — |
| **Nano Saber** | `setNanoSaberActiveDamage(damage)` | `void` | Sets attack damage when turned ON | `20.0` |
| **Nano Saber** | `getNanoSaberActiveDamage()` | `double` | Gets attack damage when turned ON | `20.0` |
| **Nano Saber** | `setNanoSaberPassiveDamage(damage)` | `void` | Sets attack damage when turned OFF | `4.0` |
| **Nano Saber** | `getNanoSaberPassiveDamage()` | `double` | Gets attack damage when turned OFF | `4.0` |
| **Nano Saber** | `setNanoSaberEnergyPerHit(energy)` | `void` | Sets EU consumed per hit | `400.0` |
| **Nano Saber** | `getNanoSaberEnergyPerHit()` | `double` | Gets EU consumed per hit | `400.0` |
| **Nano Saber** | `setNanoSaberArmorBypass(ratio)` | `void` | Sets true armor bypass ratio (`0.0` to `1.0`) | `0.0` |
| **Nano Saber** | `getNanoSaberArmorBypass()` | `double` | Gets true armor bypass ratio | `0.0` |
| **Nano Saber** | `setNanoSaberStats(map)` | `void` | Batch sets active/passive damage, energy, bypass | — |
| **Mining Laser** | `setLaserExplosiveDestruction(enable)` | `void` | Toggles block destruction in explosive mode | `true` |
| **Mining Laser** | `isLaserExplosiveDestruction()` | `boolean` | Checks if laser block destruction is enabled | `true` |
| **Mining Laser** | `laserSmelting(input, output)` | `void` | Registers in-world Super-Heat block conversion | — |
| **Mining Laser** | `getLaserSmelting(inputStack)` | `ItemStack` | Queries in-world Super-Heat conversion result | — |
| **Hazmat** | `addHazmatItem(itemOrTag)` | `void` | Registers custom item or tag as radiation hazmat gear | — |
| **Hazmat** | `addHazmatSuit(itemOrTag)` | `void` | Registers standalone 100% full radiation immunity suit | — |
| **Hazmat** | `isHazmatPiece(stack, slot)` | `boolean` | Checks if an ItemStack is certified hazmat gear | — |
| **Hazmat** | `isFullHazmatSuit(stack)` | `boolean` | Checks if an ItemStack is a standalone 100% full suit | — |
| **Radiation** | `addRadioactiveItem(item, dur, amp)` | `void` | Registers radioactive item with duration and amplifier | — |
| **Radiation** | `addRadioactiveItem(item)` | `void` | Registers radioactive item (15s duration, amplifier 0) | — |
| **Radiation** | `isRadioactive(stack)` | `boolean` | Checks if an ItemStack is radioactive | — |
| **Radiation** | `getRadiationDuration(stack)` | `int` | Gets radiation sickness duration in seconds | `0` |
| **Radiation** | `getRadiationAmplifier(stack)` | `int` | Gets radiation sickness amplifier level | `0` |
| **Radiation** | `checkRadioactiveInventory(player)` | `void` | Checks player inventory and applies radiation if unshielded | — |
| **Radiation** | `setRadiationDisabled(disabled)` | `void` | Completely turns off nuclear radiation damage | `false` |
| **Radiation** | `isRadiationDisabled()` | `boolean` | Checks if nuclear radiation damage is disabled | `false` |
| **Nuclear** | `fuelRod(id, power, heat, life)`| `IndustrialJS` | Fluent builder: registers fuel rod multipliers & life | Instance |
| **Nuclear** | `setFuelRodStats(id, power, heat, life)`| `void` | Registers fuel rod power, heat, and max lifespan | — |
| **Nuclear** | `setFuelRodPowerMultiplier(id, mult)` | `void` | Sets power multiplier for fuel rod item | `1.0` |
| **Nuclear** | `setFuelRodHeatMultiplier(id, mult)` | `void` | Sets heat multiplier for fuel rod item | `1.0` |
| **Nuclear** | `setFuelRodLifespan(id, life)` | `void` | Sets max lifespan duration for fuel rod item | — |
| **Nuclear** | `getFuelRodPowerMultiplier(stack)` | `double` | Gets power multiplier for fuel rod stack | `1.0` |
| **Nuclear** | `getFuelRodHeatMultiplier(stack)` | `double` | Gets heat multiplier for fuel rod stack | `1.0` |
| **Nuclear** | `getFuelRodLifespan(stack, def)` | `int` | Gets max lifespan for fuel rod stack | `def` |
| **Nuclear** | `setNuclearReactorMultiplier(mult)` | `void` | Sets reactor EU power output multiplier | `1.0` |
| **Nuclear** | `getNuclearReactorMultiplier()` | `double` | Gets reactor EU power output multiplier | `1.0` |
| **Nuclear** | `setReactorHeatMultiplier(mult)` | `void` | Sets reactor heat generation multiplier | `1.0` |
| **Nuclear** | `getReactorHeatMultiplier()` | `double` | Gets reactor heat generation multiplier | `1.0` |
| **Nuclear** | `setReactorExplosionMultiplier(mult)` | `void` | Sets meltdown explosion blast multiplier (`0.0` = safe) | `1.0` |
| **Nuclear** | `getReactorExplosionMultiplier()` | `double` | Gets meltdown explosion blast multiplier | `1.0` |
| **Kinetic Rotors**| `setRotorEfficiencyMultiplier(mult)`| `void` | Sets global rotor efficiency multiplier | `1.0` |
| **Kinetic Rotors**| `getRotorEfficiencyMultiplier()` | `double` | Gets global rotor efficiency multiplier | `1.0` |
| **Kinetic Rotors**| `setRotorStats(id, eff, minW, maxW)`| `void` | Sets efficiency and min/max wind speeds for rotor item | — |
| **Kinetic Rotors**| `getRotorEfficiencyMultiplier(stack)` | `double` | Gets efficiency multiplier for rotor stack | `1.0` |
| **Kinetic Rotors**| `getRotorMinWind(stack, def)` | `int` | Gets minimum wind speed for rotor stack | `def` |
| **Kinetic Rotors**| `getRotorMaxWind(stack, def)` | `int` | Gets maximum wind speed for rotor stack | `def` |
| **Upgrades** | `setOverclockerSpeedMultiplier(mult)` | `void` | Sets Overclocker time reduction multiplier | `1.0` |
| **Upgrades** | `getOverclockerSpeedMultiplier()` | `double` | Gets Overclocker time reduction multiplier | `1.0` |
| **Upgrades** | `setOverclockerEnergyMultiplier(mult)`| `void` | Sets Overclocker energy penalty multiplier | `1.0` |
| **Upgrades** | `getOverclockerEnergyMultiplier()`| `double` | Gets Overclocker energy penalty multiplier | `1.0` |
| **Upgrades** | `setEnergyStorageUpgradeBonus(bonus)`| `void` | Sets bonus EU storage per Energy Storage upgrade | `10,000` |
| **Upgrades** | `getEnergyStorageUpgradeBonus()` | `int` | Gets bonus EU storage per Energy Storage upgrade | `10,000` |
| **Upgrades** | `setTransformerUpgradeTier(tier)` | `void` | Sets voltage tier increase per Transformer upgrade | `1` |
| **Upgrades** | `getTransformerUpgradeTier()` | `int` | Gets voltage tier increase per Transformer upgrade | `1` |
| **Terraformer** | `setTerraformerEnergyMultiplier(mult)`| `void` | Sets Terraformer EU energy consumption multiplier | `1.0` |
| **Terraformer** | `getTerraformerEnergyMultiplier()`| `double` | Gets Terraformer EU energy consumption multiplier | `1.0` |
| **Terraformer** | `setTerraformerRangeMultiplier(mult)` | `void` | Sets Terraformer operational block radius multiplier | `1.0` |
| **Terraformer** | `getTerraformerRangeMultiplier()` | `double` | Gets Terraformer operational block radius multiplier | `1.0` |
| **Crop Breeding** | `setCropGrowthMultiplier(mult)` | `void` | Sets agricultural crop growth speed multiplier | `1.0` |
| **Crop Breeding** | `getCropGrowthMultiplier()` | `double` | Gets agricultural crop growth speed multiplier | `1.0` |
| **Crop Breeding** | `setCropStatGainMultiplier(mult)` | `void` | Sets stat gain multiplier during crossbreeding | `1.0` |
| **Crop Breeding** | `getCropStatGainMultiplier()` | `double` | Gets stat gain multiplier during crossbreeding | `1.0` |
| **Crop Breeding** | `setCropCrossingChance(mult)` | `void` | Sets crossbreeding mutation chance multiplier | `1.0` |
| **Crop Breeding** | `getCropCrossingChance()` | `double` | Gets crossbreeding mutation chance multiplier | `1.0` |
| **Teleporter** | `setTeleporterCrossDimension(enable)` | `void` | Enables cross-dimensional teleportation | `true` |
| **Teleporter** | `isTeleporterCrossDimension()` | `boolean` | Checks if cross-dimensional teleportation is enabled | `true` |
| **Teleporter** | `setTeleporterCrossDimensionFlatCost(cost)`| `void` | Sets flat base EU cost for dimensional jumps | `1,000,000.0` |
| **Teleporter** | `getTeleporterCrossDimensionFlatCost()` | `double` | Gets flat base EU cost for dimensional jumps | `1,000,000.0` |
| **Teleporter** | `setTeleporterCostMultiplier(mult)` | `void` | Sets distance cost multiplier for teleporter | `1.0` |
| **Teleporter** | `setTeleporterDistanceCostMultiplier(mult)`| `void` | Alias for `setTeleporterCostMultiplier` | `1.0` |
| **Teleporter** | `getTeleporterCostMultiplier()` | `double` | Gets distance cost multiplier for teleporter | `1.0` |
| **UU / Matter** | `setUuEnergyMultiplier(mult)` | `void` | Sets EU cost multiplier for UU-Matter | `1.0` |
| **UU / Matter** | `getUuEnergyMultiplier()` | `double` | Gets EU cost multiplier for UU-Matter | `1.0` |
| **UU / Matter** | `setMatterAmplifierMultiplier(mult)` | `void` | Sets scrap amplifier point multiplier | `1.0` |
| **UU / Matter** | `getMatterAmplifierMultiplier()` | `double` | Gets scrap amplifier point multiplier | `1.0` |
| **Wrench** | `setLosslessWrench(lossless)` | `void` | Enables 100% intact machine retention on wrenching | `false` |
| **Wrench** | `isLosslessWrench()` | `boolean` | Checks if lossless wrenching is enabled | `false` |
| **Recycler** | `setRecyclerScrapChance(chance)` | `void` | Sets 1-in-N scrap drop chance | `8` (12.5%) |
| **Recycler** | `getRecyclerScrapChance()` | `int` | Gets 1-in-N scrap drop chance | `8` |
| **Recycler** | `addRecyclerBlacklist(itemOrTag)` | `void` | Blacklists item or `#tag` from producing scrap | — |
| **Recycler** | `isRecyclerBlacklisted(stack)` | `boolean` | Checks if an ItemStack is blacklisted from recycling | — |
| **Mute Tools** | `addMuteTool(itemOrTag)` | `void` | Registers item or tag to toggle machine audio muting | — |
| **Mute Tools** | `isMuteTool(stack)` | `boolean` | Checks if an ItemStack is a registered mute tool | — |
| **Structure Loot**| `addStructureLoot(pattern, item, chance, min, max)` | `void` | Injects item into dungeon/structure chests matching regex pattern | — |
| **Structure Loot**| `addStructureLoot(pattern, item, chance)` | `void` | Injects single item into matching structure chests | — |
| **Recipes** | `macerator(output, input)` | `IndustrialJS` | Registers Macerator recipe | Instance |
| **Recipes** | `compressor(output, input)` | `IndustrialJS` | Registers Compressor recipe | Instance |
| **Recipes** | `extractor(output, input)` | `IndustrialJS` | Registers Extractor recipe | Instance |
| **Recipes** | `oreWasher(outputs, input, water)` | `IndustrialJS` | Registers Ore Washer recipe with custom water | Instance |
| **Recipes** | `oreWasher(outputs, input)` | `IndustrialJS` | Registers Ore Washer recipe (1000 mB water) | Instance |
| **Recipes** | `thermalCentrifuge(out, in, heat)` | `IndustrialJS` | Registers Thermal Centrifuge recipe with minHeat | Instance |
| **Recipes** | `thermalCentrifuge(out, in)` | `IndustrialJS` | Registers Thermal Centrifuge recipe (500 minHeat)| Instance |
| **Recipes** | `blastFurnace(out, in, dur, heat)` | `IndustrialJS` | Registers Blast Furnace recipe with duration & heat | Instance |
| **Recipes** | `blastFurnace(out, in)` | `IndustrialJS` | Registers Blast Furnace recipe (6000 dur, 1000 heat)| Instance |
| **Recipes** | `blockCutter(output, input, hard)` | `IndustrialJS` | Registers Block Cutter recipe with hardness | Instance |
| **Recipes** | `blockCutter(output, input)` | `IndustrialJS` | Registers Block Cutter recipe (hardness 1) | Instance |
| **Recipes** | `metalFormerRolling(output, input)` | `IndustrialJS` | Registers Metal Former (Rolling) recipe | Instance |
| **Recipes** | `metalFormerCutting(output, input)` | `IndustrialJS` | Registers Metal Former (Cutting) recipe | Instance |
| **Recipes** | `metalFormerExtruding(output, input)`| `IndustrialJS` | Registers Metal Former (Extruding) recipe | Instance |
| **Recipes** | `molecularTransformer(out, in, eu)`| `IndustrialJS` | Registers Molecular Transformer transmutation recipe | Instance |
| **Recipes** | `cannerEnrich(outFluid, inFluid, add)`| `IndustrialJS`| Registers Canning Machine fluid enrichment recipe | Instance |
| **Recipes** | `cannerBottle(out, container, fill)` | `IndustrialJS`| Registers Canning Machine fluid bottling recipe | Instance |
| **Recipes** | `scrapbox(item, weight)` | `IndustrialJS` | Registers surprise drop from Scrap Box | Instance |
| **Recipes** | `amplifier(item, amplification)` | `IndustrialJS` | Registers custom Matter Fabricator scrap amplifier | Instance |

---

## 12. Full KubeJS Script Example

Save this example into `kubejs/server_scripts/ic2_overhaul.js`:

```javascript
ServerEvents.recipes(event => {
  const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');

  // ==========================================
  // 1. Fluent Machine Recipe Registrations
  // ==========================================
  IndustrialJS.recipes()
    // Ore processing
    .macerator('2x ic2:crushed_iron', 'minecraft:raw_iron')
    .oreWasher(['ic2:purified_iron', '2x ic2:small_iron_dust', 'ic2:stone_dust'], 'ic2:crushed_iron', 1000)
    .thermalCentrifuge(['ic2:refined_iron', 'ic2:small_gold_dust'], 'ic2:purified_iron', 1500)

    // Metal forming & compression
    .compressor('minecraft:diamond', '64x minecraft:coal')
    .extractor('3x ic2:rubber', 'ic2:resin')
    .blockCutter('9x ic2:tin_plate', 'ic2:tin_block', 1)
    .metalFormerRolling('ic2:iron_plate', 'minecraft:iron_ingot')
    .metalFormerCutting('3x ic2:iron_cable', 'ic2:iron_plate')
    .metalFormerExtruding('ic2:casing_iron', 'minecraft:iron_ingot')

    // Fluid enrichment & bottling
    .cannerEnrich('1000mB ic2:coolant', '1000mB minecraft:water', 'ic2:coolant_drop')
    .cannerBottle('minecraft:potion', 'minecraft:glass_bottle', '250mB ic2:biomass')

    // High-tech matter & transmutations
    .molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000.0)
    .molecularTransformer('minecraft:emerald_block', 'minecraft:diamond_block', 25000000.0)
    .scrapbox('minecraft:diamond', 0.05)
    .scrapbox('ic2:iridium_ore', 0.01)
    .amplifier('minecraft:cobblestone', 100)
    .amplifier('minecraft:netherrack', 350)

    // Custom nuclear fuel rods
    .fuelRod('ic2:uranium_fuel_rod', 2.0, 1.0, 20000)
    .fuelRod('ic2:mox_fuel_rod', 3.0, 1.5, 10000);

  // In-world Mining Laser Super-Heat conversions
  IndustrialJS.laserSmelting('minecraft:sand', 'minecraft:glass');
  IndustrialJS.laserSmelting('minecraft:raw_iron_block', 'minecraft:iron_block');

  // ==========================================
  // 2. Machine, Batch Crafter & Generator Balances
  // ==========================================
  IndustrialJS.setGeneratorMultiplier(1.5);
  IndustrialJS.setSolarMultiplier(2.0); // Sets both day and night to 2.0x
  IndustrialJS.setMachineSpeedMultiplier(1.25);
  IndustrialJS.setMachineConsumptionMultiplier(0.8);
  IndustrialJS.setMachineMultiplier('macerator', 1.5);

  IndustrialJS.setBatchCrafterSpeedMultiplier(2.0);
  IndustrialJS.setBatchCrafterEnergyMultiplier(0.5);
  IndustrialJS.setBatchCrafterCapacity(50000);

  IndustrialJS.setSolarHelmetMultiplier(1.5);
  IndustrialJS.setSolarHelmetMultiplier('advsolar:ultimate_solar_helmet', 2.5);

  // Audio volume tuning
  IndustrialJS.setMachineVolumeMultiplier(0.5); // 50% volume

  // ==========================================
  // 3. Energy Grid, Storage & Armor Batteries
  // ==========================================
  IndustrialJS.setEuToFeRatio(4.0); // 1 EU = 4 FE
  IndustrialJS.setStorageCapacity('batbox', 80000);
  IndustrialJS.setStorageOutput('batbox', 64);
  IndustrialJS.setStorageCapacity('mfsu', 100000000);
  IndustrialJS.setStorageOutput('mfsu', 4096);

  IndustrialJS.setArmorCapacity('ic2:nano_chestplate', 2000000);
  IndustrialJS.setArmorTransferLimit('ic2:nano_chestplate', 2500);
  IndustrialJS.setArmorCapacity('ic2:quantum_chestplate', 50000000);

  // ==========================================
  // 4. Tools, Nano Saber, Drills & Jetpack
  // ==========================================
  IndustrialJS.setDrillMiningSpeed('ic2:iridium_drill', 50.0);
  IndustrialJS.setDrillEnergyPerBlock('ic2:iridium_drill', 120);
  IndustrialJS.setDrillMiningSpeed('ic2:diamond_drill', 22.0);
  IndustrialJS.setDrillEnergyPerBlock('ic2:diamond_drill', 50);

  IndustrialJS.setNanoSaberStats({
    activeDamage: 28.0,
    passiveDamage: 4.0,
    energyPerHit: 120.0,
    armorBypassPercentage: 0.35 // 35% true armor bypass
  });

  IndustrialJS.setLaserExplosiveDestruction(false); // Server-safe mining laser

  IndustrialJS.setJetpackThrustMultiplier(1.2);
  IndustrialJS.setJetpackHeightMultiplier(1.5);
  IndustrialJS.setJetpackEnergyCostMultiplier(0.7);
  IndustrialJS.setJetpackBoostMultiplier(1.4);

  // ==========================================
  // 5. Quantum Suit, Hazmat & Radiation
  // ==========================================
  IndustrialJS.setQuantumSuitVoidProtection(true);
  IndustrialJS.setQuantumSprintSpeed(2.0);
  IndustrialJS.setQuantumJumpMultiplier(1.5);
  IndustrialJS.setArmorAbsorptionMultiplier(1.2);
  IndustrialJS.setArmorEnergyCostMultiplier(0.9);

  // Custom Hazmat pieces & standalone suits
  IndustrialJS.addHazmatItem('create:diving_helmet');
  IndustrialJS.addHazmatItem('create:diving_boots');
  IndustrialJS.addHazmatSuit('mekanism:hazmat_suit');

  // Custom radioactive items
  IndustrialJS.addRadioactiveItem('minecraft:poisonous_potato', 10, 0);
  IndustrialJS.addRadioactiveItem('minecraft:netherite_scrap', 30, 1);

  // ==========================================
  // 6. Nuclear, Rotors, Upgrades & Crops
  // ==========================================
  IndustrialJS.setNuclearReactorMultiplier(1.5);
  IndustrialJS.setReactorHeatMultiplier(0.8);      // 20% cooler
  IndustrialJS.setReactorExplosionMultiplier(0.0); // Safe meltdown (no grief)

  IndustrialJS.setRotorEfficiencyMultiplier(1.25);
  IndustrialJS.setRotorStats('ic2:rotor_carbon', 1.8, 8, 150);

  IndustrialJS.setOverclockerSpeedMultiplier(1.25);
  IndustrialJS.setOverclockerEnergyMultiplier(0.75);
  IndustrialJS.setEnergyStorageUpgradeBonus(25000);
  IndustrialJS.setTransformerUpgradeTier(1);

  IndustrialJS.setCropGrowthMultiplier(2.0);
  IndustrialJS.setCropCrossingChance(1.5);
  IndustrialJS.setCropStatGainMultiplier(1.5);

  IndustrialJS.setTerraformerEnergyMultiplier(0.5);
  IndustrialJS.setTerraformerRangeMultiplier(1.5);

  // ==========================================
  // 7. Teleporters, Loot & Utility
  // ==========================================
  IndustrialJS.setTeleporterCrossDimension(true);
  IndustrialJS.setTeleporterCrossDimensionFlatCost(1000000.0);
  IndustrialJS.setTeleporterDistanceCostMultiplier(0.5);

  IndustrialJS.setLosslessWrench(true);
  IndustrialJS.setRecyclerScrapChance(4); // 25% chance (1 in 4)
  IndustrialJS.addRecyclerBlacklist('minecraft:glass_bottle');
  IndustrialJS.addRecyclerBlacklist('#c:stones');
  IndustrialJS.addMuteTool('#c:tools/wrench');

  // Structure loot injection
  IndustrialJS.addStructureLoot('chests/desert_pyramid', 'ic2:iridium_ore', 0.25, 1, 3);
  IndustrialJS.addStructureLoot('chests/simple_dungeon', 'ic2:uranium_ore', 0.50, 1, 2);
});
```

---

## 13. In-Game Hot Reloading

When you make changes to:
- `config/industrialjs/multipliers.json`
- `config/industrialjs/recipes.json`
- KubeJS `server_scripts/`

Simply run `/reload` in-game (with cheats or console enabled). IndustrialJS reloads all multipliers, sets, drill speeds, rotors, upgrades, crops, jetpack dynamics, and re-injects all recipes immediately without needing to restart Minecraft!
