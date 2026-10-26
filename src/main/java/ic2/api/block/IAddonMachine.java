package ic2.api.block;

/** Public hooks for addon machines; methods are called on the logical server. */
public interface IAddonMachine {
  /** Return true while this machine has valid inputs and output space. */
  boolean canProcess();

  /** Energy cost consumed each processing tick. */
  int getEnergyPerTick();

  /** Processing duration in ticks, at least one. */
  int getProcessingTime();

  /** Perform one completed operation; update inventory/fluids in this method. */
  void processOperation();

  /** Current progress in ticks. */
  int getAddonProgress();
}
