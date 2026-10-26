package ic2.api.energy;

import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.ILocatable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Safe registration helpers for custom IC2 energy-network nodes. */
public final class AddonEnergyNetwork {
  private static final Set<IEnergyTile> REGISTERED =
      Collections.newSetFromMap(new IdentityHashMap<>());

  private AddonEnergyNetwork() {}

  /** Registers a block-entity-backed energy node on the logical server. */
  public static synchronized void register(BlockEntity entity) {
    if (!(entity instanceof IEnergyTile tile))
      throw new IllegalArgumentException("Block entity must implement IEnergyTile");
    if (entity.getLevel() == null
        || entity.getLevel().isClientSide
        || EnergyNet.instance == null
        || !REGISTERED.add(tile)) return;
    EnergyNet.instance.addBlockEntityTile((BlockEntity & IEnergyTile) entity);
  }

  /** Registers a non-block-entity node implementing IC2's ILocatable contract. */
  public static synchronized void register(ILocatable locatable) {
    if (!(locatable instanceof IEnergyTile tile))
      throw new IllegalArgumentException("Locatable must implement IEnergyTile");
    if (locatable.getWorldObj() == null
        || locatable.getWorldObj().isClientSide
        || EnergyNet.instance == null
        || !REGISTERED.add(tile)) return;
    EnergyNet.instance.addLocatableTile((ILocatable & IEnergyTile) locatable);
  }

  /** Unregisters a previously registered node. Safe to call more than once. */
  public static synchronized void unregister(IEnergyTile tile) {
    if (tile != null && REGISTERED.remove(tile) && EnergyNet.instance != null)
      EnergyNet.instance.removeTile(tile);
  }
}
