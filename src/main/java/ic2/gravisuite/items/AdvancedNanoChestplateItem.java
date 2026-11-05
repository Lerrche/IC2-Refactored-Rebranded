package ic2.gravisuite.items;

import ic2.core.item.armor.jetpack.IBoostingJetpack;
import ic2.gravisuite.GravisuiteArmorMaterials;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

/**
 * Advanced NanoChestPlate from Gravitation Suite.
 *
 * <p>Combines NanoSuit body defense with the complete propulsion system of the Advanced Electric
 * Jetpack.
 */
public class AdvancedNanoChestplateItem extends AdvancedJetpackItem implements IBoostingJetpack {
  public static final double MAX_CHARGE = 3_000_000.0;
  public static final double TRANSFER_LIMIT = 5_000.0;
  public static final int TIER = 3;
  public static final int ENERGY_PER_DAMAGE = 5_000;
  public static final double DAMAGE_ABSORPTION_RATIO = 0.8;

  public AdvancedNanoChestplateItem() {
    super(
        GravisuiteArmorMaterials.ADVANCED_NANO_CHESTPLATE,
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
  }

  @Override
  public int getTier(ItemStack stack) {
    return TIER;
  }

  @Override
  public int getEnergyPerDamage() {
    return ENERGY_PER_DAMAGE;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return DAMAGE_ABSORPTION_RATIO;
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(Component.translatable("gravisuite.tooltip.nanochestplate.desc"));
  }
}
