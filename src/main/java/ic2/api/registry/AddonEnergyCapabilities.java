package ic2.api.registry;

import ic2.api.block.AddonEnergyBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** Helpers for exposing the FE side of IC2R-compatible add-on energy block entities. */
public final class AddonEnergyCapabilities {
  private AddonEnergyCapabilities() {}

  /**
   * Call from the add-on's {@code RegisterCapabilitiesEvent} for every energy block entity type.
   */
  public static <T extends BlockEntity> void registerEnergyStorage(
      RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
    event.registerBlockEntity(
        Capabilities.EnergyStorage.BLOCK,
        type,
        (blockEntity, side) ->
            blockEntity instanceof AddonEnergyBlockEntity energy
                ? energy.getEnergyStorage(side)
                : null);
  }
}
