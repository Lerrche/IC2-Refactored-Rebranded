# Example: an electric melee weapon

`ic2.api.item.AddonElectricWeapon` is a base class for a charge-powered melee weapon. It handles an EU-use attempt on a successful hit on the logical server, but **does not cancel vanilla melee damage when the weapon is empty**. Override `hurtEnemy` if your design needs a different inactive behavior.

```java
package example.addon.item;

import ic2.api.item.AddonElectricWeapon;
import net.minecraft.world.item.Item;

public final class PulseBlade extends AddonElectricWeapon {
    public PulseBlade() {
        super(new Item.Properties().stacksTo(1), 80_000, 256, 2, 50);
    }
}
```

Register it with `CONTENT.registerItem("pulse_blade", PulseBlade::new)`. Configure attack damage and attack speed using the Minecraft 1.21.1 item-attribute pattern used by your project; the IC2R weapon base constructor itself accepts `Item.Properties`, charge capacity, transfer limit, tier, and EU cost per hit.

The values are example balance settings only.
