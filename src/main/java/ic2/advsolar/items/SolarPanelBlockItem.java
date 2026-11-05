package ic2.advsolar.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class SolarPanelBlockItem extends BlockItem {
  private final int tier;
  private final int dayEu;
  private final int nightEu;
  private final int maxOutput;
  private final int storage;

  public SolarPanelBlockItem(
      Block block,
      Item.Properties properties,
      int tier,
      int dayEu,
      int nightEu,
      int maxOutput,
      int storage) {
    super(block, properties);
    this.tier = tier;
    this.dayEu = dayEu;
    this.nightEu = nightEu;
    this.maxOutput = maxOutput;
    this.storage = storage;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(
        Component.translatable("ic2.item.tooltip.power_tier", String.valueOf(this.tier))
            .withStyle(ChatFormatting.GRAY));
    tooltip.add(
        Component.literal(String.format("Day: %d EU/t | Night: %d EU/t", this.dayEu, this.nightEu))
            .withStyle(ChatFormatting.GRAY));
    tooltip.add(
        Component.literal(String.format("Max Output: %d EU/t", this.maxOutput))
            .withStyle(ChatFormatting.GRAY));
    tooltip.add(
        Component.literal(String.format("Storage: %,d EU", this.storage))
            .withStyle(ChatFormatting.GRAY));
  }
}
