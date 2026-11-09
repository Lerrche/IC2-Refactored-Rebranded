# Nuclear Reactors, Rotors, Upgrades & World Mechanics

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
