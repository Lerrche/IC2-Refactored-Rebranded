package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Rubber boots upgraded to harvest a small amount of kinetic energy while walking. */
public class ItemStaticBoots extends ItemArmorHazmat {
  private static final double BLOCKS_PER_EU = 5.0;
  private static final Map<Player, TravelState> TRAVEL = new WeakHashMap<>();

  public ItemStaticBoots(Properties settings) {
    super(EquipmentSlot.FEET, settings);
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

    ItemStack equippedBoots = player.getItemBySlot(EquipmentSlot.FEET);
    if (equippedBoots.getItem() != this) {
      TRAVEL.remove(player);
      return;
    }
    if (equippedBoots != stack) {
      return;
    }

    double x = player.getX();
    double z = player.getZ();
    TravelState state = TRAVEL.computeIfAbsent(player, ignored -> new TravelState(x, z));
    double walked = Math.hypot(x - state.lastX, z - state.lastZ);
    state.lastX = x;
    state.lastZ = z;

    // Ignore teleportation and other discontinuous position changes.
    if (walked > 1.0) {
      state.distance = 0.0;
      return;
    }

    if (!player.onGround()
        || player.isPassenger()
        || player.isSwimming()
        || player.isFallFlying()) {
      state.distance = 0.0;
      return;
    }

    ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
    if (chestplate.isEmpty()) {
      state.distance = 0.0;
      return;
    }

    state.distance += walked;
    while (state.distance >= BLOCKS_PER_EU) {
      double transferred =
          ElectricItem.manager.charge(chestplate, 1.0, Integer.MAX_VALUE, true, false);
      // Energy is not banked when no more energy can be accepted by the chestplate.
      if (transferred < 1.0) {
        state.distance %= BLOCKS_PER_EU;
        break;
      }
      state.distance -= BLOCKS_PER_EU;
    }
  }

  private static final class TravelState {
    private double lastX;
    private double lastZ;
    private double distance;

    private TravelState(double x, double z) {
      this.lastX = x;
      this.lastZ = z;
    }
  }
}
