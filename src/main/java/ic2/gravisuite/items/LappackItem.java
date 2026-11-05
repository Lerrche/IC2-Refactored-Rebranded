package ic2.gravisuite.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.armor.AddonEnergyPack;
import ic2.gravisuite.GravisuiteArmorMaterials;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/**
 * Advanced and Ultimate Lappacks from Gravitation Suite.
 *
 * <p>Wearable chestplate energy packs that recharge held electric tools and armor in real-time.
 */
public class LappackItem extends AddonEnergyPack {
  public LappackItem(
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier) {
    super(material, properties, capacity, transferLimit, tier);
  }

  public static LappackItem createAdvanced() {
    return new LappackItem(
        GravisuiteArmorMaterials.ADVANCED_LAPPACK,
        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
        3_000_000.0,
        1_000.0,
        2);
  }

  public static LappackItem createUltimate() {
    return new LappackItem(
        GravisuiteArmorMaterials.ULTIMATE_LAPPACK,
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
        30_000_000.0,
        10_000.0,
        3);
  }

  @Override
  public void inventoryTick(
      ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    super.inventoryTick(stack, level, entity, slotId, isSelected);
    if (!level.isClientSide && entity instanceof Player player) {
      if (player.getItemBySlot(EquipmentSlot.CHEST) == stack && ElectricItem.manager != null) {
        double available = ElectricItem.manager.getCharge(stack);
        if (available <= 0) return;

        // Charge main hand item
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand != stack) {
          chargeTarget(stack, mainHand, player);
        }
        // Charge offhand item
        ItemStack offHand = player.getOffhandItem();
        if (!offHand.isEmpty() && offHand != stack) {
          chargeTarget(stack, offHand, player);
        }
      }
    }
  }

  private void chargeTarget(ItemStack pack, ItemStack target, Player player) {
    if (ElectricItem.manager != null) {
      double maxTransfer = this.transferLimit;
      double transferred =
          ElectricItem.manager.charge(target, maxTransfer, this.tier, false, false);
      if (transferred > 0) {
        ElectricItem.manager.discharge(pack, transferred, this.tier, true, false, false);
      }
    }
  }
}
