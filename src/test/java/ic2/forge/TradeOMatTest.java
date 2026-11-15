package ic2.forge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.authlib.GameProfile;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.ref.Ic2Blocks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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
}
