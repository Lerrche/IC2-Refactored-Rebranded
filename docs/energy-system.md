# Energy System & Interoperability

IndustrialCraft 2 uses **Energy Units (EU)**. This page explains how EU works, how cables and voltage tiers behave, and how the built-in Forge Energy (FE) bridge enables cross-mod compatibility.

---

## 1. Energy Units (EU) and Voltage Tiers

In IC2, energy is transmitted in discrete **packets** rather than a continuous stream. The size of these packets corresponds to the **Voltage Tier**:

| Tier | Name | Max Voltage | Default Cable / Tech Level |
|:---:|:---:|:---:|:---|
| `0` | Micro / Ultra Low | 8 EU/t | Tin cable, small sensors, micro-generators |
| `1` | Low Voltage (LV) | 32 EU/t | Copper cable, basic generator, BatBox, tier 1 machines |
| `2` | Medium Voltage (MV) | 128 EU/t | Gold cable, Geothermal generator, CESU, tier 2 machines |
| `3` | High Voltage (HV) | 512 EU/t | HV / Glass cable, MFE, large generators |
| `4` | Extreme Voltage (EV) | 2048 EU/t | Glass cable, MFSU, Molecular Transformer |
| `5` | Super Voltage (SV) | 8192+ EU/t | Quantum generators, endgame reactors |

### Voltage Explosion / Rejection
- Machines have an input voltage tier (e.g. Tier 1).
- If a machine receives a packet exceeding its max input tier (e.g. 128 EU/t into a Tier 1 machine), it will overload unless upgraded with **Transformer Upgrades**.

---

## 2. Cables and Conduction Loss

Energy travelling through cables experiences distance resistance (conduction loss):

| Cable Material | Max Safe Voltage | Loss Rate | Insulation |
|:---|:---:|:---:|:---:|
| **Tin Cable** | 32 EU/t (Tier 1) | ~0.025 EU / block | High efficiency for low voltage |
| **Copper Cable** | 128 EU/t (Tier 2) | ~0.20 EU / block | Standard factory wiring |
| **Gold Cable** | 512 EU/t (Tier 3) | ~0.40 EU / block | Medium-voltage distribution |
| **Iron (HV) Cable** | 2048 EU/t (Tier 4) | ~0.80 EU / block | High-voltage power lines |
| **Glass Fibre Cable** | 8192 EU/t (Tier 5) | ~0.025 EU / block | Ultimate low-loss power grid |

Uninsulated cables will shock nearby players and mobs when energized.

---

## 3. Forge Energy (FE) Interoperability

In Minecraft 1.21.1 on NeoForge, most modern tech mods (Mekanism, Thermal Series, Applied Energistics 2, Ender IO, Flux Networks) use **Forge Energy (FE)**.

IC2 Rebranded features a built-in bidirectional energy bridge in all add-on bases (`AddonEnergyBlockEntity`, `AddonMachineBlockEntity`, `AddonGeneratorBlockEntity`, `AddonEnergyStorageBlockEntity`):

### Conversion Ratio
$$\mathbf{1\text{ EU} = 2\text{ FE}}$$
$$\mathbf{1\text{ FE} = 0.5\text{ EU}}$$

### Exposing FE to Other Mods
To let any FE energy conduit, universal cable, or generator power your block entity, add this one-line registration during `RegisterCapabilitiesEvent`:

```java
import ic2.api.registry.AddonEnergyCapabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

private void registerCapabilities(RegisterCapabilitiesEvent event) {
    // Bridges your custom block entity to Forge Energy automatically
    AddonEnergyCapabilities.registerEnergyStorage(event, MyBlocks.MY_MACHINE_BE.get());
    AddonEnergyCapabilities.registerEnergyStorage(event, MyBlocks.MY_BATTERY_BE.get());
}
```

That's it! Players can now connect any FE pipe or power source directly to your IC2 machine or battery block.
