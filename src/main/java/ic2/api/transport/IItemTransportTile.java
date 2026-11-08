package ic2.api.transport;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IItemTransportTile extends IPipe {
  int getMaxStackSizeAllowed();

  int getTransferRate();

  ItemStack getContents();

  void setContents(ItemStack stack);

  int putItems(ItemStack stack, Direction from, boolean simulate);
}
