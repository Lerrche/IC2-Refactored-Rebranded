package ic2.advsolar.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class QuantumGeneratorBlockItem extends BlockItem {
  public QuantumGeneratorBlockItem(Block block, Item.Properties properties) {
    super(block, properties);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(
        Component.translatable("ic2.item.tooltip.power_tier", "1 - 5 (Configurable)")
            .withStyle(ChatFormatting.GRAY));
    tooltip.add(
        Component.literal("Output: 0 - 1,000,000+ EU/t (Configurable)")
            .withStyle(ChatFormatting.GRAY));
  }
}
