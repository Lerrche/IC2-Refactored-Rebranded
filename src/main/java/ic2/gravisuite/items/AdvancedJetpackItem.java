package ic2.gravisuite.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.armor.AddonElectricJetpack;
import ic2.core.IC2;
import ic2.core.item.armor.jetpack.IBoostingJetpack;
import ic2.core.util.Ic2Tooltip;
import ic2.core.util.KeyboardClient;
import ic2.gravisuite.GravisuiteArmorMaterials;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

/**
 * Advanced Electric Jetpack from Gravitation Suite.
 *
 * <p>High-performance flight engine with boost thrust, hover stabilization, and 3,000,000 EU
 * capacity.
 */
public class AdvancedJetpackItem extends AddonElectricJetpack implements IBoostingJetpack {
  public static final double MAX_CHARGE = 3_000_000.0;
  public static final double TRANSFER_LIMIT = 5_000.0;
  public static final int TIER = 2;
  public static final int BASE_DRAIN_PER_TICK = 12;

  public AdvancedJetpackItem() {
    this(
        GravisuiteArmorMaterials.ADVANCED_JETPACK,
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
  }

  public AdvancedJetpackItem(Holder<ArmorMaterial> material, Item.Properties properties) {
    super(
        material,
        properties,
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        BASE_DRAIN_PER_TICK,
        1.0F,
        0.05F,
        0.2F,
        1.0F);
  }

  @Override
  public float getBaseThrust(ItemStack stack, boolean hoverMode) {
    return hoverMode ? 0.65F : 0.3F;
  }

  @Override
  public float getBoostThrust(Player player, ItemStack stack, boolean hoverMode) {
    return IC2.keyboard.isBoostKeyDown(player) ? 0.55F : 0.0F;
  }

  @Override
  public boolean useBoostPower(ItemStack stack, float amount) {
    if (ElectricItem.manager != null) {
      return ElectricItem.manager.discharge(stack, 30.0, this.tier, true, false, false) > 0.0;
    }
    return false;
  }

  @Override
  public float getHoverBoost(Player player, ItemStack stack, boolean upwards) {
    return IC2.keyboard.isBoostKeyDown(player) ? 1.5F : 1.0F;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    Component boostKey = KeyboardClient.boostKey.getKey().getDisplayName();
    Component forwardKey = Minecraft.getInstance().options.keyUp.getKey().getDisplayName();
    Ic2Tooltip.add(
        tooltip, Component.translatable("item.ic2.jetpack.tooltip.boost", boostKey, forwardKey));
  }
}
