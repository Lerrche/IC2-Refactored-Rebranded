package ic2.api.block;

/**
 * Implement this for add-on block entities that need a logical-server tick from AddonEnergyBlock.
 */
public interface IAddonServerTickable {
  void serverTick();
}
