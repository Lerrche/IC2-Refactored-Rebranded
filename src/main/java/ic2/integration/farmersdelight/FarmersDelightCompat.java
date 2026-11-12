package ic2.integration.farmersdelight;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.Ic2SoundEvents;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Compatibility handler for Farmer's Delight Cutting Boards.
 *
 * <p>Allows IC2 electric tools (e.g. Electric Chainsaw and GraviSuite Advanced Chainsaw) to act as
 * knives and shears on the cutting board with appropriate EU consumption and sound feedback, while
 * blocking cuts when the tool is out of power.
 */
public final class FarmersDelightCompat {

  public static final String MOD_ID = "farmersdelight";

  private FarmersDelightCompat() {}

  public static boolean isCuttingBoard(ResourceLocation blockId) {
    if (blockId == null) {
      return false;
    }
    String ns = blockId.getNamespace();
    String path = blockId.getPath().toLowerCase(Locale.ROOT);
    return (MOD_ID.equals(ns) && path.contains("cutting_board")) || path.endsWith("cutting_board");
  }

  public static ItemStack getBoardStoredItem(Level level, BlockPos pos) {
    try {
      var itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, Direction.UP);
      if (itemHandler != null && itemHandler.getSlots() > 0) {
        return itemHandler.getStackInSlot(0);
      }
    } catch (Throwable ignored) {
    }

    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof Container container && container.getContainerSize() > 0) {
      return container.getItem(0);
    }

    return ItemStack.EMPTY;
  }

  public static class EventHandler {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
      if (event.getLevel().isClientSide()) {
        return;
      }

      BlockPos pos = event.getPos();
      Level level = event.getLevel();
      BlockState state = level.getBlockState(pos);
      ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
      if (!isCuttingBoard(blockId)) {
        return;
      }

      ItemStack held = event.getItemStack();
      if (held.isEmpty() || !(held.getItem() instanceof IElectricItem)) {
        return;
      }

      ItemStack stored = getBoardStoredItem(level, pos);
      if (stored.isEmpty()) {
        // Player is placing items on an empty board; do not intercept
        return;
      }

      double cost = 100.0;
      if (held.getItem() instanceof ItemElectricTool electricTool) {
        cost = electricTool.getOperationEnergyCost(held);
      }

      Player player = event.getEntity();

      // Check if tool has sufficient power to perform the cut
      if (!ElectricItem.manager.canUse(held, cost)) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        if (player != null) {
          player.displayClientMessage(
              Component.translatable("ic2.item.tooltip.empty").withStyle(ChatFormatting.RED), true);
          level.playSound(
              null, pos, Ic2SoundEvents.ITEM_ELECTRIC_SHUTDOWN, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return;
      }

      // Tool has power: capture snapshot to detect when cut succeeds
      ItemStack snapshot = stored.copy();
      int countBefore = stored.getCount();
      double finalCost = cost;

      if (level.getServer() != null) {
        level
            .getServer()
            .execute(
                () -> {
                  if (player == null || !player.isAlive()) {
                    return;
                  }

                  ItemStack currentHeld = player.getItemInHand(event.getHand());
                  if (currentHeld.getItem() != held.getItem()) {
                    return;
                  }

                  ItemStack after = getBoardStoredItem(level, pos);
                  boolean cutProcessed =
                      after.isEmpty()
                          || after.getItem() != snapshot.getItem()
                          || after.getCount() < countBefore;

                  if (cutProcessed) {
                    ElectricItem.manager.use(currentHeld, finalCost, player);
                    level.playSound(
                        null,
                        pos,
                        Ic2SoundEvents.ITEM_CHAINSAW_USE1,
                        SoundSource.PLAYERS,
                        0.8F,
                        1.0F);

                    if (ElectricItem.manager.getCharge(currentHeld) <= 0.0) {
                      IC2.network.get(true).initiateItemEvent(player, currentHeld, 0, true);
                    }
                  }
                });
      }
    }
  }
}
