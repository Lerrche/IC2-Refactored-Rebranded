# Electric armor and jetpacks

The inspected source contains these add-on-oriented armor classes:

- `ic2.api.item.armor.AddonElectricArmor`
- `ic2.api.item.armor.AddonElectricJetpack`

`AddonElectricArmor` extends IC2R's `ItemArmorElectric`. Its constructor and behavior are tied to that parent class; inspect the exact constructor in your target checkout before creating a subclass. The class exposes configured energy-per-damage, damage absorption, and whether the item can provide energy.

`AddonElectricJetpack` extends `AddonElectricArmor` and implements IC2R's `IJetpack` contract. It exposes jetpack properties such as power, drop percentage, charge level, active state, hover multiplier, and world-height divisor through the interface methods present in the source.

These classes are not a generic vanilla armor registration system. You still need to register the item, provide item models/textures/translations, and test the equipment slot and behavior in-game. Because the constructor is version-specific, copy its signature from the exact source revision you target rather than relying on an example from another IC2R build.
