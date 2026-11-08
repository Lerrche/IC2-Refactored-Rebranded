package ic2.core.block.transport;

import ic2.core.util.Ic2Tooltip;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemTransportPipe extends BlockItem {
  public final PipeType pipeType;
  public final PipeSize pipeSize;

  public ItemTransportPipe(TransportPipeBlock block, Properties properties) {
    super(block, properties);
    this.pipeType = block.type;
    this.pipeSize = block.size;
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack stack,
      Item.TooltipContext context,
      @NotNull List<Component> tooltip,
      @NotNull TooltipFlag flag) {
    int capacity = (int) (this.pipeType.transferRate * this.pipeSize.multiplier);
    int ratePerTick = this.pipeType.getTransferRatePerTick();

    Ic2Tooltip.add(
        tooltip,
        Component.translatable("tooltip.ic2.pipe.transfer_rate", ratePerTick));
    Ic2Tooltip.add(
        tooltip,
        Component.translatable("tooltip.ic2.pipe.capacity", capacity));
    Ic2Tooltip.add(
        tooltip,
        Component.translatable("tooltip.ic2.pipe.max_stack", this.pipeSize.maxStackSize));
    Ic2Tooltip.add(
        tooltip,
        Component.translatable("tooltip.ic2.pipe.wrench").withStyle(ChatFormatting.GOLD));
  }
}
