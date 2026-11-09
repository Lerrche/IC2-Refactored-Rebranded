# Master Reference & Example Scripts

## 9. System & State Lifecycle API (`reset()`)

- `IndustrialJS.reset()`: Resets all multipliers, scrap chances, blacklists, mute tools, hazmat sets, drill speeds, laser recipes, storage capacities, output rates, rotors, upgrades, crops, and structure loot back to vanilla IC2 defaults.
- Useful when writing test scripts or reloading dynamic modpack states.

```javascript
IndustrialJS.reset();
```

---


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


---

## 13. In-Game Hot Reloading

When you make changes to:
- `config/industrialjs/multipliers.json`
- `config/industrialjs/recipes.json`
- KubeJS `server_scripts/`

Simply run `/reload` in-game (with cheats or console enabled). IndustrialJS reloads all multipliers, sets, drill speeds, rotors, upgrades, crops, jetpack dynamics, and re-injects all recipes immediately without needing to restart Minecraft!
