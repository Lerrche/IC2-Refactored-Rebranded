package ic2.api.transport;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IFluidPipe extends IPipe {
  int getTransferRate();

  int getCurrentInnerCapacity();

  int getMaxInnerCapacity();

  IFluidHandler getFluidTank();
}
