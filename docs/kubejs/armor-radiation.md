# Armor, Hazmat & Radiation API

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
