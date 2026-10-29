package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.core.ref.Ic2ArmorMaterials;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Portable sky-exposed power source from classic IndustrialCraft 2. It charges the electric item
 * worn in the chest slot at 1 EU per tick.
 */
public class ItemSolarHelmet extends ItemArmorUtility {
  public ItemSolarHelmet(Properties settings) {
    super(Ic2ArmorMaterials.SOLAR_HELMET.holder(), settings, EquipmentSlot.HEAD);
  }

  @Override
  public void inventoryTick(
      @NotNull ItemStack stack,
      @NotNull Level world,
      @NotNull Entity entity,
      int slot,
      boolean selected) {
    super.inventoryTick(stack, world, entity, slot, selected);
    if (world.isClientSide || !(entity instanceof Player player)) {
      return;
    }

    if (player.getItemBySlot(EquipmentSlot.HEAD) != stack
        || !world.canSeeSky(player.blockPosition())) {
      return;
    }

    ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
    if (!chestplate.isEmpty()) {
      // The gateway manager also supports external electric-item integrations.
      ElectricItem.manager.charge(chestplate, 1.0, Integer.MAX_VALUE, true, false);
    }
  }
}
