# Energy Grid & Storage API

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
