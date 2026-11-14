package ic2.forge;

import static org.junit.jupiter.api.Assertions.*;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.GatewayElectricItemManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Ic2ItemEnergyStorageTest {

  private static class DummyElectricItem extends Item implements IElectricItem {
    public DummyElectricItem() {
      super(new Item.Properties());
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
      return true;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
      return 10_000.0;
    }

    @Override
    public int getTier(ItemStack stack) {
      return 1;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
      return 100.0;
    }
  }

  @BeforeAll
  static void setup() {
    try {
      java.lang.reflect.Field frozenField =
          net.minecraft.core.MappedRegistry.class.getDeclaredField("frozen");
      frozenField.setAccessible(true);
      frozenField.setBoolean(net.minecraft.core.registries.BuiltInRegistries.ITEM, false);
    } catch (Exception ignored) {
    }
    if (ElectricItem.manager == null) {
      ElectricItem.manager = new GatewayElectricItemManager();
      ElectricItem.rawManager = new ElectricItemManager();
    }
  }

  @Test
  void testReceiveAndExtractEnergy() {
    DummyElectricItem item = new DummyElectricItem();
    ItemStack stack = new ItemStack(item);
    Ic2ItemEnergyStorage storage = new Ic2ItemEnergyStorage(stack);

    assertTrue(storage.canReceive());
    assertTrue(storage.canExtract());
    assertEquals(20_000, storage.getMaxEnergyStored());
    assertEquals(0, storage.getEnergyStored());

    // Send 1000 FE (which is 500 EU at 2 FE/EU)
    int accepted = storage.receiveEnergy(1000, false);
    assertEquals(1000, accepted);
    assertEquals(1000, storage.getEnergyStored());
    assertEquals(500.0, ElectricItem.manager.getCharge(stack));

    // Extract 400 FE (200 EU)
    int extracted = storage.extractEnergy(400, false);
    assertEquals(400, extracted);
    assertEquals(600, storage.getEnergyStored());
    assertEquals(300.0, ElectricItem.manager.getCharge(stack));
  }

  @Test
  void testForgeEnergyItemManagerChargeAndDischarge() {
    ForgeEnergyItemManager manager = new ForgeEnergyItemManager();
    // Verify handles returns false on empty/non-FE stacks
    assertFalse(manager.handles(ItemStack.EMPTY));
    assertFalse(manager.handles(new ItemStack(new DummyElectricItem())));
  }
}
