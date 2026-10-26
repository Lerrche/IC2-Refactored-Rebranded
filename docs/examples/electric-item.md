# Example: a chargeable electric item

`ic2.api.item.AddonElectricItem` is a configurable base class for electric items. The constructor parameters in the current source are maximum charge, transfer limit, tier, and whether the item can provide energy.

```java
package example.addon.item;

import ic2.api.item.AddonElectricItem;
import net.minecraft.world.item.Item;

public final class DenseCell extends AddonElectricItem {
    public DenseCell() {
        super(new Item.Properties().stacksTo(1), 500_000, 512, 3, true);
    }
}
```

Register it under your namespace:

```java
public static final DeferredItem<DenseCell> DENSE_CELL = CONTENT.registerItem(
    "dense_cell", DenseCell::new);
```

The numbers above are illustrative balance choices, not a recommendation for every pack. In the current implementation, tiers must be from 0 through 3, and maximum charge and transfer limit must be finite positive values.

Use IC2R's `ElectricItem.manager` to interact with charge. Do not manually edit IC2R's internal charge data or NBT format.
