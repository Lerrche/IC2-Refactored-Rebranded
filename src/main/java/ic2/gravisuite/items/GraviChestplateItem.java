package ic2.gravisuite.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.armor.AddonElectricArmor;
import ic2.gravisuite.GravisuiteArmorMaterials;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * GraviChestPlate from Gravitation Suite.
 *
 * <p>Combines QuantumSuit body armor protection, Ultimate Lappack capacity, and gravitational
 * engine flight.
 */
public class GraviChestplateItem extends AddonElectricArmor {
  public static final double MAX_CHARGE = 30_000_000.0;
  public static final double TRANSFER_LIMIT = 30_000.0;
  public static final int TIER = 3;
  public static final int ENERGY_PER_DAMAGE = 20_000;
  public static final double DAMAGE_ABSORPTION_RATIO = 1.0;
  public static final double FLIGHT_DRAIN_PER_TICK = 5.0;
  public static final double BOOST_DRAIN_PER_TICK = 50.0;

  public GraviChestplateItem() {
    super(
        GravisuiteArmorMaterials.GRAVI_CHESTPLATE,
        EquipmentSlot.CHEST,
        new Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        ENERGY_PER_DAMAGE,
        DAMAGE_ABSORPTION_RATIO,
        true);
  }

  @Override
  public void inventoryTick(
      ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    super.inventoryTick(stack, level, entity, slotId, isSelected);
    if (!level.isClientSide && entity instanceof Player player) {
      boolean isEquipped = player.getItemBySlot(EquipmentSlot.CHEST) == stack;
      boolean hasCharge = hasEnergy(stack, FLIGHT_DRAIN_PER_TICK);
      boolean abilitiesChanged = false;
      float targetFlyingSpeed = 0.05F;

      if (isEquipped && hasCharge) {
        if (!player.getAbilities().mayfly) {
          player.getAbilities().mayfly = true;
          abilitiesChanged = true;
        }

        if (player.getAbilities().flying) {
          useEnergy(stack, FLIGHT_DRAIN_PER_TICK, player);

          // Boost flight speed cleanly when sprinting (Ctrl / double-tap W)
          if (player.isSprinting() && hasEnergy(stack, BOOST_DRAIN_PER_TICK)) {
            useEnergy(stack, BOOST_DRAIN_PER_TICK, player);
            targetFlyingSpeed = 0.16F;
          } else {
            targetFlyingSpeed = 0.08F;
          }
        }
      } else if (!player.isCreative() && !player.isSpectator()) {
        if (player.getAbilities().mayfly) {
          player.getAbilities().mayfly = false;
          player.getAbilities().flying = false;
          abilitiesChanged = true;
        }
      }

      if (Math.abs(player.getAbilities().getFlyingSpeed() - targetFlyingSpeed) > 0.001F) {
        player.getAbilities().setFlyingSpeed(targetFlyingSpeed);
        abilitiesChanged = true;
      }

      if (abilitiesChanged) {
        player.onUpdateAbilities();
      }

      // Lappack function: charge held items
      if (isEquipped && ElectricItem.manager != null) {
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held != stack) {
          double transferred =
              ElectricItem.manager.charge(held, this.transferLimit, this.tier, false, false);
          if (transferred > 0) {
            ElectricItem.manager.discharge(stack, transferred, this.tier, true, false, false);
          }
        }
      }
    }
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(Component.translatable("gravisuite.tooltip.gravichestplate.desc"));
  }
}
