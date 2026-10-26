# Electric armor and jetpacks

## Electric armor

`ic2.api.item.armor.AddonElectricArmor` accepts an armor material holder, equipment slot, item properties, maximum charge, transfer limit, tier, energy per damage, damage absorption ratio, and whether it can provide energy.

The current source validates tier 0–3, non-negative energy per damage, and an absorption ratio from 0 to 1. You still need to supply a valid Minecraft `ArmorMaterial` holder and register the item with your own mod.

## Electric jetpack

`ic2.api.item.armor.AddonElectricJetpack` is an electric chest-slot item implementing `IJetpack`. Its constructor takes an armor material holder, item properties, max charge, transfer limit, tier, energy cost per tick, thrust power, drop percentage, hover multiplier, and world-height divisor.

For custom activation or energy-drain behavior, the source documentation identifies `isJetpackActive(ItemStack)` and `drainEnergy` as methods to override. The current constructor validates its numeric ranges; use the exact source signature for the version you target.

Both classes are reusable bases, not complete content packs. Add your item model/texture, language entry, balancing, and any additional effects yourself.
