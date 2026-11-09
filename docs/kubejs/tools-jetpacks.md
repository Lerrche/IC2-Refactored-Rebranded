# Tools, Drills, Combat & Jetpack API

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
