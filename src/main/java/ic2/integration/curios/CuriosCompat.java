package ic2.integration.curios;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/** Soft Curios bridge. Reflection keeps Curios an optional runtime dependency. */
public final class CuriosCompat {
  private static final String CURIOS_API = "top.theillusivec4.curios.api.CuriosApi";
  private static final String CURIOS_HANDLER =
      "top.theillusivec4.curios.api.type.capability.ICuriosItemHandler";
  private static final String CURIOS_STACKS_HANDLER =
      "top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler";
  private static final String DYNAMIC_STACK_HANDLER =
      "top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler";

  private CuriosCompat() {}

  /** Visits all equipped Curios stacks when Curios is available. */
  public static void forEachEquippedStack(
      LivingEntity entity, java.util.function.Consumer<ItemStack> consumer) {
    if (entity == null || consumer == null || !ModList.get().isLoaded("curios")) {
      return;
    }

    try {
      Class<?> curiosApi = Class.forName(CURIOS_API);
      Class<?> inventoryApi = Class.forName(CURIOS_HANDLER);
      Object optionalValue =
          curiosApi.getMethod("getCuriosInventory", LivingEntity.class).invoke(null, entity);
      if (!(optionalValue instanceof Optional<?> inventoryOptional)
          || inventoryOptional.isEmpty()) {
        return;
      }

      Object inventory = inventoryOptional.get();
      Object equipped = inventoryApi.getMethod("getEquippedCurios").invoke(inventory);
      Class<?> itemHandler = Class.forName("net.neoforged.neoforge.items.IItemHandler");
      Method getSlots = itemHandler.getMethod("getSlots");
      Method getStackInSlot = itemHandler.getMethod("getStackInSlot", int.class);
      int slotCount = (int) getSlots.invoke(equipped);
      for (int slot = 0; slot < slotCount; slot++) {
        Object stackValue = getStackInSlot.invoke(equipped, slot);
        if (stackValue instanceof ItemStack stack && !stack.isEmpty()) {
          consumer.accept(stack);
        }
      }
    } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
      // Curios is optional. Missing or incompatible Curios APIs must not break IC2 gameplay.
    }
  }

  /**
   * Returns the first stack in Curios' "back" slot that matches the predicate. If Curios is absent
   * or its API is incompatible, this safely returns an empty stack.
   */
  public static ItemStack findBackSlotStack(LivingEntity entity, Predicate<ItemStack> predicate) {
    if (entity == null || predicate == null || !ModList.get().isLoaded("curios")) {
      return ItemStack.EMPTY;
    }

    try {
      Class<?> curiosApi = Class.forName(CURIOS_API);
      Class<?> inventoryApi = Class.forName(CURIOS_HANDLER);
      Object optionalValue =
          curiosApi.getMethod("getCuriosInventory", LivingEntity.class).invoke(null, entity);
      if (!(optionalValue instanceof Optional<?> inventoryOptional)
          || inventoryOptional.isEmpty()) {
        return ItemStack.EMPTY;
      }

      Object inventory = inventoryOptional.get();
      Object curios = inventoryApi.getMethod("getCurios").invoke(inventory);
      if (!(curios instanceof java.util.Map<?, ?> curiosMap)) {
        return ItemStack.EMPTY;
      }

      Object backHandler = curiosMap.get("back");
      if (backHandler == null) {
        return ItemStack.EMPTY;
      }

      Class<?> stacksHandlerApi = Class.forName(CURIOS_STACKS_HANDLER);
      Object stacks = stacksHandlerApi.getMethod("getStacks").invoke(backHandler);
      Class<?> dynamicStacksApi = Class.forName(DYNAMIC_STACK_HANDLER);
      Method getSlots = dynamicStacksApi.getMethod("getSlots");
      Method getStackInSlot = dynamicStacksApi.getMethod("getStackInSlot", int.class);
      int slotCount = (int) getSlots.invoke(stacks);
      for (int slot = 0; slot < slotCount; slot++) {
        Object stackValue = getStackInSlot.invoke(stacks, slot);
        if (stackValue instanceof ItemStack stack && !stack.isEmpty() && predicate.test(stack)) {
          return stack;
        }
      }
    } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
      // Curios is optional. Missing or incompatible Curios APIs must not break IC2 gameplay.
    }

    return ItemStack.EMPTY;
  }
}
