package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.util.Ic2Tooltip;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemToolCrafting extends Item implements IBoxable, IItemHudInfo {
  public ItemToolCrafting(Properties settings) {
    super(settings);
  }

  protected static int getRemainingUses(ItemStack stack) {
    return Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
  }

  @Override
  public boolean hasCraftingRemainingItem(ItemStack stack) {
    return stack.isDamageableItem() && stack.getDamageValue() < stack.getMaxDamage() - 1;
  }

  @Override
  public ItemStack getCraftingRemainingItem(ItemStack stack) {
    if (!stack.isDamageableItem() || stack.getDamageValue() >= stack.getMaxDamage() - 1) {
      return ItemStack.EMPTY;
    }

    ItemStack remainder = stack.copy();
    remainder.setDamageValue(remainder.getDamageValue() + 1);
    return remainder;
  }

  public void appendHoverText(
      @NotNull ItemStack stack,
      Item.TooltipContext world,
      List<Component> tooltip,
      @NotNull TooltipFlag advanced) {
    Ic2Tooltip.add(
        tooltip, Component.translatable("ic2.tooltip.tool.uses_left", getRemainingUses(stack)));
  }

  @Override
  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return true;
  }

  @Override
  public List<String> getHudInfo(ItemStack stack, boolean advanced) {
    List<String> info = new LinkedList<>();
    info.add(
        Component.translatable("ic2.tooltip.tool.uses_left", getRemainingUses(stack))
            .getString()
            .formatted(ChatFormatting.GRAY));
    return info;
  }
}
