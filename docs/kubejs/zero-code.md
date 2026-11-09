# Zero-Code Configuration

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
