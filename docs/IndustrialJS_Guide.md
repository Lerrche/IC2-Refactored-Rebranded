# IndustrialJS — KubeJS & Modpack Creator Guide for IndustrialCraft 2

Welcome to **IndustrialJS**, the official modpack creator and scripting integration for **IndustrialCraft 2 (IC2 Refactored / Rebranded)**!

IndustrialJS was built for modpack authors of all skill levels:
- **No-code / Zero-code mode**: Tweak machinery balance, cross-dimensional teleportation, drill speeds, combat weapons, battery capacities, wrench mechanics, and add custom machine recipes simply by editing simple `.json` files in `config/industrialjs/`!
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
   - [Scrapbox Drops (`scrapbox`)](#scrapbox-drops)
   - [Matter Fabricator Amplifiers (`amplifier`)](#matter-fabricator-amplifiers)
   - [Mining Laser Super-Heat Smelting (`laserSmelting`)](#mining-laser-super-heat-smelting)
4. [Machinery & Generator Balance API](#4-machinery--generator-balance-api)
   - [Generators & Solar Panels](#generators--solar-panels)
   - [Machine Speed & Power Consumption](#machine-speed--power-consumption)
   - [Per-Machine Multipliers](#per-machine-multipliers)
5. [Energy Grid, Storage & Energy Bridge API](#5-energy-grid-storage--energy-bridge-api)
   - [EU to FE Conversion Ratio](#eu-to-fe-conversion-ratio)
   - [Energy Storage Blocks (BatBox, CESU, MFE, MFSU)](#energy-storage-blocks-batbox-cesu-mfe-mfsu)
6. [Tools, Drills & Combat API](#6-tools-drills--combat-api)
   - [Global Mining Speed](#global-mining-speed)
   - [Custom Drill Speeds & Energy Costs](#custom-drill-speeds--energy-costs)
   - [Nano Saber Combat Stats](#nano-saber-combat-stats)
   - [Mining Laser Anti-Griefing](#mining-laser-anti-griefing)
7. [Armor, Hazmat & Void Protection API](#7-armor-hazmat--void-protection-api)
   - [Electric Armor Absorption & Energy Costs](#electric-armor-absorption--energy-costs)
   - [Quantum Suit Void Rescue & Sprinting](#quantum-suit-void-rescue--sprinting)
   - [Custom Hazmat Gear & Radiation Immunity](#custom-hazmat-gear--radiation-immunity)
8. [Special Mechanics API](#8-special-mechanics-api)
   - [Cross-Dimensional Teleportation](#cross-dimensional-teleportation)
   - [Nuclear Reactor & Meltdown Safety](#nuclear-reactor--meltdown-safety)
   - [Matter Fabricator & UU-Matter Tuning](#matter-fabricator--uu-matter-tuning)
   - [Recycler Scrap Rates & Custom Blacklists](#recycler-scrap-rates--custom-blacklists)
   - [Lossless Wrench & Machine Retention](#lossless-wrench--machine-retention)
   - [Alternative Machine Mute Tools](#alternative-machine-mute-tools)
9. [System & State Lifecycle API (`reset()`)](#9-system--state-lifecycle-api-reset)
10. [Item & Recipe Input/Output Syntax](#10-item--recipe-inputoutput-syntax)
11. [Complete Master API Reference Table](#11-complete-master-api-reference-table)
12. [Full KubeJS Script Example](#12-full-kubejs-script-example)
13. [In-Game Hot Reloading](#13-in-game-hot-reloading)

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

## 2. KubeJS Quick Start & Fluent Chaining

If you have [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) installed, IndustrialJS provides direct Java bindings with static helpers, getters, setters, checkers, and a fluent builder API.

### Accessing IndustrialJS in KubeJS

In any server script (e.g. `kubejs/server_scripts/industrial.js`):

```javascript
const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');
```

### Fluent Chaining (`IndustrialJS.recipes()`)

All recipe registration methods return the `IndustrialJS` singleton instance. This allows chaining multiple recipe declarations in a single expression:

```javascript
ServerEvents.recipes(event => {
  const IndustrialJS = Java.loadClass('ic2.industrialjs.IndustrialJS');

  // Both forms are valid:
  IndustrialJS.recipes()
    .macerator('2x ic2:crushed_copper', '#c:raw_materials/copper')
    .compressor('minecraft:diamond', '64x minecraft:coal')
    .extractor('3x ic2:rubber', 'ic2:resin');

  // Passing the event object is also supported:
  IndustrialJS.recipes(event)
    .blockCutter('9x ic2:copper_plate', 'ic2:copper_block', 1)
    .molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000);
});
```

---

## 3. Recipe Creation API (All Machines & Overloads)

IndustrialJS provides full registration support for every IC2 processing machine, complete with optional parameter overloads.

### Macerator
Grinds ores, blocks, and items into crushed dusts or powdered forms.
- **Signature**: `IndustrialJS.macerator(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.macerator('2x ic2:crushed_iron', 'minecraft:raw_iron');
IndustrialJS.macerator('4x minecraft:sand', '#c:sandstone');
```

### Compressor
Compresses items under immense pressure (e.g. scrap into scrap boxes, coal into diamond dust).
- **Signature**: `IndustrialJS.compressor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.compressor('ic2:scrap_box', '9x ic2:scrap');
IndustrialJS.compressor('minecraft:diamond', '64x minecraft:coal');
```

### Extractor
Extracts rubber from resin, fuels, dyes, and specialized components.
- **Signature**: `IndustrialJS.extractor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.extractor('3x ic2:rubber', 'ic2:resin');
IndustrialJS.extractor('ic2:bio_chaff', '16x #minecraft:leaves');
```

### Ore Washing Plant
Washes crushed ores using water to produce purified ores, small secondary dusts, and stone dust.
- **Signatures**:
  - `IndustrialJS.oreWasher(outputs, input, waterAmount)` -> `IndustrialJS`
  - `IndustrialJS.oreWasher(outputs, input)` -> `IndustrialJS` *(defaults to 1000 mB of water)*
```javascript
// Default 1000 mB of water:
IndustrialJS.oreWasher(['ic2:purified_copper', '2x ic2:small_tin_dust', 'ic2:stone_dust'], 'ic2:crushed_copper');

// Custom water consumption (500 mB):
IndustrialJS.oreWasher(['ic2:purified_gold', '2x ic2:small_silver_dust'], 'ic2:crushed_gold', 500);
```

### Thermal Centrifuge
Separates purified and crushed ores at high temperatures into refined metals and byproduct dusts.
- **Signatures**:
  - `IndustrialJS.thermalCentrifuge(outputs, input, minHeat)` -> `IndustrialJS`
  - `IndustrialJS.thermalCentrifuge(outputs, input)` -> `IndustrialJS` *(defaults to minHeat: 500)*
```javascript
// Default minimum heat (500):
IndustrialJS.thermalCentrifuge(['ic2:refined_iron', 'ic2:small_gold_dust'], 'ic2:purified_iron');

// Custom minimum heat requirement (1500):
IndustrialJS.thermalCentrifuge(['ic2:refined_copper', 'ic2:small_tin_dust'], 'ic2:purified_copper', 1500);
```

### Blast Furnace
Refines iron into refined iron/steel with slag byproducts using compressed air and heat over time.
- **Signatures**:
  - `IndustrialJS.blastFurnace(outputs, input, duration, minHeat)` -> `IndustrialJS`
  - `IndustrialJS.blastFurnace(outputs, input)` -> `IndustrialJS` *(defaults to duration: 6000 ticks, minHeat: 1000)*
```javascript
// Default duration (6000 ticks = 5 min) and minHeat (1000):
IndustrialJS.blastFurnace(['ic2:refined_iron_ingot', 'ic2:slag'], 'minecraft:iron_ingot');

// Custom duration (3000 ticks) and minHeat (1200):
IndustrialJS.blastFurnace(['ic2:refined_iron_ingot', '2x ic2:slag'], 'minecraft:iron_block', 3000, 1200);
```

### Block Cutter
Slices whole metal or composite blocks into plates using cutter blades. Higher blade hardness is required for tougher materials.
- **Signatures**:
  - `IndustrialJS.blockCutter(output, input, hardness)` -> `IndustrialJS`
  - `IndustrialJS.blockCutter(output, input)` -> `IndustrialJS` *(defaults to hardness: 1)*
```javascript
// Default hardness (1):
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

## 4. Machinery & Generator Balance API

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

---

## 5. Energy Grid, Storage & Energy Bridge API

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

---

## 6. Tools, Drills & Combat API

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
// Method 1: Single object configuration
IndustrialJS.setNanoSaberStats({
  activeDamage: 28.0,
  passiveDamage: 4.0,
  energyPerHit: 150.0,
  armorBypassPercentage: 0.35 // 35% armor bypass
});

// Method 2: Individual setters
IndustrialJS.setNanoSaberActiveDamage(32.0);
IndustrialJS.setNanoSaberArmorBypass(0.5);
```

### Mining Laser Anti-Griefing
- `setLaserExplosiveDestruction(enable)` / `isLaserExplosiveDestruction()`:
  - When `true` (default): Mining Laser explosive mode destroys blocks in world.
  - When `false`: Protects blocks against griefing on multiplayer servers while still damaging mobs!

```javascript
IndustrialJS.setLaserExplosiveDestruction(false); // Server-safe mining laser
```

---

## 7. Armor, Hazmat & Void Protection API

### Electric Armor Absorption & Energy Costs
- `setArmorAbsorptionMultiplier(mult)` / `getArmorAbsorptionMultiplier()`: Scales percentage of damage absorbed by electric armor.
- `setArmorEnergyCostMultiplier(mult)` / `getArmorEnergyCostMultiplier()`: Scales EU energy drained from armor when mitigating damage.

```javascript
IndustrialJS.setArmorAbsorptionMultiplier(1.2);   // 20% stronger protection
IndustrialJS.setArmorEnergyCostMultiplier(0.8);   // 20% longer battery life under fire
```

### Quantum Suit Void Rescue & Sprinting
- `setQuantumSuitVoidProtection(enable)` / `isQuantumSuitVoidProtection()`: When `true`, falling into the void below the world's minimum build height automatically consumes suit EU to safely boost the player back upward, resetting fall damage.
- `setQuantumSprintMultiplier(mult)` / `setQuantumSprintSpeed(speed)` / `getQuantumSprintMultiplier()`: Speed multiplier applied when sprinting with Quantum Leggings.

```javascript
IndustrialJS.setQuantumSuitVoidProtection(true);  // Never lose items to the Void!
IndustrialJS.setQuantumSprintSpeed(2.5);          // Super-speed sprint
```

### Custom Hazmat Gear & Radiation Immunity
- `addHazmatItem(itemOrTag)`: Registers any third-party item or item tag as a certified radiation protection piece.
- `isHazmatPiece(itemStack, equipmentSlot)`: Tests whether an item stack qualifies as hazmat armor for the given slot.
- `setRadiationDisabled(disabled)` / `isRadiationDisabled()`: Completely disables nuclear radiation damage and environmental effects across the entire game.

```javascript
// Register third-party diving gear and modded hazmat suits:
IndustrialJS.addHazmatItem('create:diving_helmet');
IndustrialJS.addHazmatItem('create:diving_boots');
IndustrialJS.addHazmatItem('#c:armors/hazmat');

// Disable radiation entirely for casual modpacks:
IndustrialJS.setRadiationDisabled(true);
```

---

## 8. Special Mechanics API

### Cross-Dimensional Teleportation
Players can link IC2 Teleporters across different dimensions (e.g. Overworld to Nether, End, or space dimensions)!
1. Right-click Teleporter A in Dimension 1 with the Frequency Transmitter.
2. Travel to Dimension 2, right-click Teleporter B.
3. The Frequency Transmitter links them bidirectionally and displays the target dimension in its tooltip.
4. Step onto the Teleporter and supply redstone to jump across dimensions.

- `setTeleporterCrossDimension(enable)` / `isTeleporterCrossDimension()`: Enables or disables cross-dimensional teleportation.
- `setTeleporterCrossDimensionFlatCost(cost)` / `getTeleporterCrossDimensionFlatCost()`: Flat base EU energy cost for jumping across dimensions (default `1,000,000.0` EU, scales with player inventory weight).
- `setTeleporterCostMultiplier(mult)` / `setTeleporterDistanceCostMultiplier(mult)` / `getTeleporterCostMultiplier()`: Multiplier applied to intra-dimensional distance energy costs.

```javascript
IndustrialJS.setTeleporterCrossDimension(true);
IndustrialJS.setTeleporterCrossDimensionFlatCost(500000.0); // 500k EU flat cost
IndustrialJS.setTeleporterDistanceCostMultiplier(0.5);      // 50% cheaper distance cost
```

### Nuclear Reactor & Meltdown Safety
- `setNuclearReactorMultiplier(mult)` / `getNuclearReactorMultiplier()`: Scales total EU power output produced by nuclear reactor fuel rods.
- `setReactorExplosionMultiplier(mult)` / `getReactorExplosionMultiplier()`: Blast radius and explosion multiplier for reactor meltdowns. Set to `0.0` to disable meltdown terrain destruction completely!

```javascript
IndustrialJS.setNuclearReactorMultiplier(1.5);     // 50% more reactor power
IndustrialJS.setReactorExplosionMultiplier(0.0);   // Safe nuclear reactors (no crater griefing)
```

### Matter Fabricator & UU-Matter Tuning
- `setUuEnergyMultiplier(mult)` / `getUuEnergyMultiplier()`: Multiplier for EU energy required to generate 1 mB of UU-Matter (default `1.0`).
- `setMatterAmplifierMultiplier(mult)` / `getMatterAmplifierMultiplier()`: Multiplier for scrap amplification points (default `1.0`).

```javascript
IndustrialJS.setUuEnergyMultiplier(0.5);        // 50% less energy required for UU-Matter
IndustrialJS.setMatterAmplifierMultiplier(2.0); // Scrap amplifies twice as much
```

### Recycler Scrap Rates & Custom Blacklists
- `setRecyclerScrapChance(chance)` / `getRecyclerScrapChance()`: Sets scrap drop probability as `1 in N` operations (default `8` = 12.5% chance. Set to `4` for 25%, `1` for 100%).
- `addRecyclerBlacklist(itemOrTag)`: Blacklists items or item tags from producing scrap in the Recycler.
- `isRecyclerBlacklisted(itemStack)`: Checks whether an item stack is blacklisted from recycling.

```javascript
IndustrialJS.setRecyclerScrapChance(4); // 25% scrap chance (1 in 4)
IndustrialJS.addRecyclerBlacklist('minecraft:glass_bottle');
IndustrialJS.addRecyclerBlacklist('#c:stones');
IndustrialJS.addRecyclerBlacklist('#c:cobblestones');
```

### Lossless Wrench & Machine Retention
- `setLosslessWrench(lossless)` / `isLosslessWrench()`: When `true`, dismantling any IC2 machine with an electric or manual wrench guarantees 100% chance to recover the intact machine instead of degrading into an empty machine casing.

```javascript
IndustrialJS.setLosslessWrench(true); // 100% safe machine dismantling
```

### Alternative Machine Mute Tools
Right-clicking any IC2 machine with a tool toggles machine audio sound muting.
- `addMuteTool(itemOrTag)`: Registers custom items or tags that can mute/unmute machines.
- `isMuteTool(itemStack)`: Checks whether an item stack is configured as an audio mute tool.

```javascript
IndustrialJS.addMuteTool('#c:tools/wrench');
IndustrialJS.addMuteTool('minecraft:stick');
```

---

## 9. System & State Lifecycle API (`reset()`)

- `IndustrialJS.reset()`: Resets all multipliers, scrap chances, blacklists, mute tools, hazmat sets, drill speeds, laser recipes, storage capacities, and output rates back to vanilla IC2 defaults.
- Useful when writing test scripts or reloading dynamic modpack states.

```javascript
// Reset everything to stock vanilla IC2 settings:
IndustrialJS.reset();
```

---

## 10. Item & Recipe Input/Output Syntax

IndustrialJS features an intelligent `ItemParser` that resolves string identifiers, item tags, stack counts, and native Minecraft/KubeJS objects:

| Syntax Format | Example | Description |
| :--- | :--- | :--- |
| **Simple Item ID** | `'minecraft:iron_ingot'` | Single item (count = 1) |
| **Count Prefix (`x`)** | `'2x minecraft:sand'` | Stack with quantity 2 |
| **Count Prefix (`*`)** | `'64* minecraft:coal'` | Stack with quantity 64 |
| **Item Tag (`#`)** | `'#c:raw_materials/copper'` | Any item matching the item tag |
| **Counted Item Tag** | `'9x #c:ingots/tin'` | Requires 9 of any item matching the tag |
| **Array of Outputs** | `['ic2:purified_iron', '2x ic2:small_iron_dust']` | Multiple outputs (Ore Washer, Centrifuge, Blast Furnace) |
| **Native Objects** | `ItemStack`, `Ingredient`, `ItemLike`, `IRecipeInput` | Full compatibility with native Java / KubeJS objects |

---

## 11. Complete Master API Reference Table

Here is the complete list of all **77 methods** available in `ic2.industrialjs.IndustrialJS`:

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
| **Energy Bridge** | `setEuToFeRatio(ratio)` | `void` | Sets FE conversion ratio per 1 EU | `2.0` |
| **Energy Bridge** | `getEuToFeRatio()` | `double` | Gets FE conversion ratio per 1 EU | `2.0` |
| **Wrench** | `setLosslessWrench(lossless)` | `void` | Enables 100% intact machine retention on wrenching | `false` |
| **Wrench** | `isLosslessWrench()` | `boolean` | Checks if lossless wrenching is enabled | `false` |
| **Recycler** | `setRecyclerScrapChance(chance)` | `void` | Sets 1-in-N scrap drop chance | `8` (12.5%) |
| **Recycler** | `getRecyclerScrapChance()` | `int` | Gets 1-in-N scrap drop chance | `8` |
| **Recycler** | `addRecyclerBlacklist(itemOrTag)` | `void` | Blacklists item or `#tag` from producing scrap | — |
| **Recycler** | `isRecyclerBlacklisted(stack)` | `boolean` | Checks if an ItemStack is blacklisted from recycling | — |
| **Teleporter** | `setTeleporterCrossDimension(enable)` | `void` | Enables cross-dimensional teleportation | `true` |
| **Teleporter** | `isTeleporterCrossDimension()` | `boolean` | Checks if cross-dimensional teleportation is enabled | `true` |
| **Teleporter** | `setTeleporterCrossDimensionFlatCost(cost)`| `void` | Sets flat base EU cost for dimensional jumps | `1,000,000.0` |
| **Teleporter** | `getTeleporterCrossDimensionFlatCost()` | `double` | Gets flat base EU cost for dimensional jumps | `1,000,000.0` |
| **Teleporter** | `setTeleporterCostMultiplier(mult)` | `void` | Sets distance cost multiplier for teleporter | `1.0` |
| **Teleporter** | `setTeleporterDistanceCostMultiplier(mult)`| `void` | Alias for `setTeleporterCostMultiplier` | `1.0` |
| **Teleporter** | `getTeleporterCostMultiplier()` | `double` | Gets distance cost multiplier for teleporter | `1.0` |
| **Nuclear** | `setNuclearReactorMultiplier(mult)` | `void` | Sets reactor EU power output multiplier | `1.0` |
| **Nuclear** | `getNuclearReactorMultiplier()` | `double` | Gets reactor EU power output multiplier | `1.0` |
| **Nuclear** | `setReactorExplosionMultiplier(mult)` | `void` | Sets meltdown explosion blast multiplier (`0.0` = safe) | `1.0` |
| **Nuclear** | `getReactorExplosionMultiplier()` | `double` | Gets meltdown explosion blast multiplier | `1.0` |
| **Nuclear** | `setRadiationDisabled(disabled)` | `void` | Completely turns off nuclear radiation damage | `false` |
| **Nuclear** | `isRadiationDisabled()` | `boolean` | Checks if nuclear radiation damage is disabled | `false` |
| **UU / Matter** | `setUuEnergyMultiplier(mult)` | `void` | Sets EU cost multiplier for UU-Matter | `1.0` |
| **UU / Matter** | `getUuEnergyMultiplier()` | `double` | Gets EU cost multiplier for UU-Matter | `1.0` |
| **UU / Matter** | `setMatterAmplifierMultiplier(mult)` | `void` | Sets scrap amplifier point multiplier | `1.0` |
| **UU / Matter** | `getMatterAmplifierMultiplier()` | `double` | Gets scrap amplifier point multiplier | `1.0` |
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
| **Armor** | `setArmorAbsorptionMultiplier(mult)` | `void` | Sets electric armor damage absorption multiplier | `1.0` |
| **Armor** | `getArmorAbsorptionMultiplier()` | `double` | Gets electric armor damage absorption multiplier | `1.0` |
| **Armor** | `setArmorEnergyCostMultiplier(mult)` | `void` | Sets electric armor energy drain multiplier | `1.0` |
| **Armor** | `getArmorEnergyCostMultiplier()` | `double` | Gets electric armor energy drain multiplier | `1.0` |
| **Quantum Suit** | `setQuantumSuitVoidProtection(enable)` | `void` | Toggles void rescue teleportation safety | `true` |
| **Quantum Suit** | `isQuantumSuitVoidProtection()` | `boolean` | Checks if void rescue protection is enabled | `true` |
| **Quantum Suit** | `setQuantumSprintMultiplier(mult)` | `void` | Sets sprinting speed multiplier for Quantum Leggings| `1.0` |
| **Quantum Suit** | `setQuantumSprintSpeed(speed)` | `void` | Alias for `setQuantumSprintMultiplier` | `1.0` |
| **Quantum Suit** | `getQuantumSprintMultiplier()` | `double` | Gets sprinting speed multiplier for Quantum Leggings| `1.0` |
| **Hazmat** | `addHazmatItem(itemOrTag)` | `void` | Registers custom item or tag as radiation hazmat gear | — |
| **Hazmat** | `isHazmatPiece(stack, slot)` | `boolean` | Checks if an ItemStack is certified hazmat gear | — |
| **Mute Tools** | `addMuteTool(itemOrTag)` | `void` | Registers item or tag to toggle machine audio muting | — |
| **Mute Tools** | `isMuteTool(stack)` | `boolean` | Checks if an ItemStack is a registered mute tool | — |
| **Energy Storage**| `setStorageCapacity(id, cap)` | `void` | Sets storage capacity in EU (`'batbox'`, `'mfsu'`) | — |
| **Energy Storage**| `getStorageCapacity(type, def)` | `int` | Gets storage capacity for block entity type | — |
| **Energy Storage**| `setStorageOutput(id, out)` | `void` | Sets output rate in EU/t (`'batbox'`, `'mfsu'`) | — |
| **Energy Storage**| `getStorageOutput(type, def)` | `int` | Gets output rate for block entity type | — |
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

    // High-tech matter & transmutations
    .molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000.0)
    .molecularTransformer('minecraft:emerald_block', 'minecraft:diamond_block', 25000000.0)
    .scrapbox('minecraft:diamond', 0.05)
    .scrapbox('ic2:iridium_ore', 0.01)
    .amplifier('minecraft:cobblestone', 100)
    .amplifier('minecraft:netherrack', 350);

  // In-world Mining Laser Super-Heat conversions
  IndustrialJS.laserSmelting('minecraft:sand', 'minecraft:glass');
  IndustrialJS.laserSmelting('minecraft:raw_iron_block', 'minecraft:iron_block');

  // ==========================================
  // 2. Machine & Generator Balances
  // ==========================================
  IndustrialJS.setGeneratorMultiplier(1.5);
  IndustrialJS.setSolarMultiplier(2.0); // Sets both day and night to 2.0x
  IndustrialJS.setMachineSpeedMultiplier(1.25);
  IndustrialJS.setMachineConsumptionMultiplier(0.8);
  IndustrialJS.setMachineMultiplier('macerator', 1.5);

  // ==========================================
  // 3. Energy Grid & Storage Capacities
  // ==========================================
  IndustrialJS.setEuToFeRatio(4.0); // 1 EU = 4 FE
  IndustrialJS.setStorageCapacity('batbox', 80000);
  IndustrialJS.setStorageOutput('batbox', 64);
  IndustrialJS.setStorageCapacity('mfsu', 100000000);
  IndustrialJS.setStorageOutput('mfsu', 4096);

  // ==========================================
  // 4. Tools, Nano Saber & Drills
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

  // ==========================================
  // 5. Quantum Suit & Armor Protection
  // ==========================================
  IndustrialJS.setQuantumSuitVoidProtection(true);
  IndustrialJS.setQuantumSprintSpeed(2.0);
  IndustrialJS.setArmorAbsorptionMultiplier(1.2);
  IndustrialJS.setArmorEnergyCostMultiplier(0.9);

  // Custom Hazmat gear
  IndustrialJS.addHazmatItem('create:diving_helmet');
  IndustrialJS.addHazmatItem('create:diving_boots');
  IndustrialJS.addHazmatItem('#c:armors/hazmat');

  // ==========================================
  // 6. Teleporters & Nuclear Safety
  // ==========================================
  IndustrialJS.setTeleporterCrossDimension(true);
  IndustrialJS.setTeleporterCrossDimensionFlatCost(1000000.0);
  IndustrialJS.setTeleporterDistanceCostMultiplier(0.5);

  IndustrialJS.setNuclearReactorMultiplier(1.5);
  IndustrialJS.setReactorExplosionMultiplier(0.0); // No terrain damage on meltdown

  // ==========================================
  // 7. General Utility & Convenience
  // ==========================================
  IndustrialJS.setLosslessWrench(true);
  IndustrialJS.setRecyclerScrapChance(4); // 25% chance (1 in 4)
  IndustrialJS.addRecyclerBlacklist('minecraft:glass_bottle');
  IndustrialJS.addRecyclerBlacklist('#c:stones');
  IndustrialJS.addMuteTool('#c:tools/wrench');
});
```

---

## 13. In-Game Hot Reloading

When you make changes to:
- `config/industrialjs/multipliers.json`
- `config/industrialjs/recipes.json`
- KubeJS `server_scripts/`

Simply run `/reload` in-game (with cheats or console enabled). IndustrialJS reloads all multipliers, sets, drill speeds, and re-injects all recipes immediately without needing to restart Minecraft!
