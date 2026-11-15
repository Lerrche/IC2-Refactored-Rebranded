package ic2.forge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.authlib.GameProfile;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.item.EnvItemHandler;
import ic2.core.ref.Ic2Blocks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

class TradeOMatTest {

  @Test
  void infiniteModeIsPermanentlyDisabled() {
    TileEntityTradeOMat tradeOMat =
        new TileEntityTradeOMat(BlockPos.ZERO, Ic2Blocks.TRADE_O_MAT.defaultBlockState());
    assertFalse(tradeOMat.infinite, "Trade-O-Mat infinite mode must default to false");

    // Network event 0 (which used to toggle infinite mode) must have no effect
    tradeOMat.onNetworkEvent((Player) null, 0);
    assertFalse(
        tradeOMat.infinite, "Trade-O-Mat infinite mode cannot be toggled on via network event");
  }

  @Test
  void energyOMatEnergyUseAndOwnership() {
    TileEntityEnergyOMat energyOMat =
        new TileEntityEnergyOMat(BlockPos.ZERO, Ic2Blocks.ENERGY_O_MAT.defaultBlockState());
    energyOMat.euBuffer = 1000.0;

    assertTrue(energyOMat.useEnergy(200.0), "useEnergy should succeed when sufficient EU exists");
    assertEquals(800.0, energyOMat.getEnergy(), 0.001, "euBuffer should decrease by used amount");

    assertFalse(energyOMat.useEnergy(900.0), "useEnergy should fail when insufficient EU exists");
    assertEquals(800.0, energyOMat.getEnergy(), 0.001, "euBuffer should be unchanged on failure");

    GameProfile profile = new GameProfile(UUID.randomUUID(), "TestPlayer");
    energyOMat.setOwner(profile);
    assertEquals(profile, energyOMat.getOwner(), "Energy-O-Mat owner should match setOwner");
  }

  @Test
  void personalChestItemHandlerDefensiveCopying() {
    ic2.core.block.personal.TileEntityPersonalChest chest =
        new ic2.core.block.personal.TileEntityPersonalChest(
            BlockPos.ZERO, Ic2Blocks.PERSONAL_CHEST.defaultBlockState());
    GameProfile owner = new GameProfile(UUID.randomUUID(), "Owner");
    chest.setOwner(owner);

    EnvItemHandlerForge handler = new EnvItemHandlerForge();
    EnvItemHandler.AdjacentInventory inv =
        EnvItemHandlerForge.HandlerForge.ofNullable(
            chest.getPrivilegedInventory(owner), net.minecraft.core.Direction.UP);

    ItemStack sourceStack = new ItemStack(net.minecraft.world.item.Items.DIAMOND, 5);

    // Simulate deposit
    int simulated = handler.deposit(inv, sourceStack, true);
    assertEquals(5, simulated, "Should simulate depositing 5 items");
    assertTrue(chest.contentSlot.isEmpty(), "Chest should remain empty after simulation");
    assertEquals(5, sourceStack.getCount(), "Source stack should not be modified by simulation");

    // Actual deposit
    int deposited = handler.deposit(inv, sourceStack, false);
    assertEquals(5, deposited, "Should deposit 5 items");
    assertEquals(5, sourceStack.getCount(), "Source stack must not be modified by deposit");

    // Critical check: Verify chest contains item with count 5, NOT 0 (no reference aliasing bug)
    ItemStack chestStack = chest.contentSlot.get(0);
    assertFalse(chestStack.isEmpty(), "Chest slot 0 must not be empty");
    assertEquals(5, chestStack.getCount(), "Chest slot 0 must contain 5 items");
    assertNotSame(sourceStack, chestStack, "Chest stack must be an independent defensive copy");
  }

  @Test
  void personalChestAccessPermissions() {
    ic2.core.block.personal.TileEntityPersonalChest chest =
        new ic2.core.block.personal.TileEntityPersonalChest(
            BlockPos.ZERO, Ic2Blocks.PERSONAL_CHEST.defaultBlockState());
    GameProfile owner = new GameProfile(UUID.randomUUID(), "Owner");
    GameProfile stranger = new GameProfile(UUID.randomUUID(), "Stranger");
    chest.setOwner(owner);

    assertTrue(chest.permitsAccess(owner), "Owner should have access to personal chest");
    assertFalse(chest.permitsAccess(stranger), "Stranger should not have access to personal chest");
  }
}
