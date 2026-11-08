package ic2.advsolar.items;

import ic2.advsolar.AdvSolarArmorMaterials;
import ic2.api.item.ElectricItem;
import ic2.api.item.armor.AddonElectricArmor;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Advanced, Hybrid, and Ultimate Solar Helmets.
 *
 * <p>High-tier electric helmets that generate clean solar energy under open sky to recharge the
 * helmet itself, worn chestplates, and held electric items.
 */
public class SolarHelmetItem extends AddonElectricArmor {
  private final int dayEu;
  private final int nightEu;

  public SolarHelmetItem(
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      int dayEu,
      int nightEu,
      int energyPerDamage,
      double damageAbsorptionRatio) {
    super(
        material,
        EquipmentSlot.HEAD,
        properties,
        maxCharge,
        transferLimit,
        tier,
        energyPerDamage,
        damageAbsorptionRatio,
        true);
    this.dayEu = dayEu;
    this.nightEu = nightEu;
  }

  public static SolarHelmetItem createAdvanced() {
    return new SolarHelmetItem(
        AdvSolarArmorMaterials.ADVANCED_HELMET,
        new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON),
        1_000_000.0,
        1_000.0,
        2,
        8,
        1,
        200,
        0.4);
  }

  public static SolarHelmetItem createHybrid() {
    return new SolarHelmetItem(
        AdvSolarArmorMaterials.HYBRID_HELMET,
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
        10_000_000.0,
        5_000.0,
        3,
        64,
        8,
        1_000,
        0.7);
  }

  public static SolarHelmetItem createUltimate() {
    return new SolarHelmetItem(
        AdvSolarArmorMaterials.ULTIMATE_HELMET,
        new Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
        10_000_000.0,
        10_000.0,
        4,
        512,
        64,
        5_000,
        0.9);
  }

  @Override
  public void inventoryTick(
      ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    super.inventoryTick(stack, level, entity, slotId, isSelected);
    if (!level.isClientSide && entity instanceof Player player) {
      if (player.getItemBySlot(EquipmentSlot.HEAD) == stack) {
        // Solar generation logic
        boolean canSeeSky =
            level.canSeeSky(player.blockPosition().above())
                && !level.isRaining()
                && !level.isThundering();
        int generation = 0;
        if (canSeeSky) {
          double mult = ic2.industrialjs.IndustrialJS.getSolarHelmetMultiplier(stack);
          generation = (int) Math.round((level.isDay() ? this.dayEu : this.nightEu) * mult);
        }

        if (generation > 0 && ElectricItem.manager != null) {
          // Charge helmet first
          double charged = ElectricItem.manager.charge(stack, generation, this.tier, true, false);
          double leftover = generation - charged;

          // Charge chestplate (e.g. Lappack or Jetpack)
          if (leftover > 0) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (!chest.isEmpty()) {
              double toChest =
                  ElectricItem.manager.charge(chest, leftover, this.tier, false, false);
              leftover -= toChest;
            }
          }

          // Charge held main hand item
          if (leftover > 0) {
            ItemStack held = player.getMainHandItem();
            if (!held.isEmpty()) {
              ElectricItem.manager.charge(held, leftover, this.tier, false, false);
            }
          }
        }
      }
    }
  }

  @Override
  public void appendHoverText(
      ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(
        Component.translatable(
                "advanced_solar_panels.tooltip.solar_helmet.generation", this.dayEu, this.nightEu)
            .withStyle(net.minecraft.ChatFormatting.GRAY));
  }
}
