package ic2.gravisuite;

import ic2.api.registry.Ic2AddonContent;
import ic2.gravisuite.items.AdvancedChainsawItem;
import ic2.gravisuite.items.AdvancedDrillItem;
import ic2.gravisuite.items.AdvancedJetpackItem;
import ic2.gravisuite.items.AdvancedNanoChestplateItem;
import ic2.gravisuite.items.GraviChestplateItem;
import ic2.gravisuite.items.GraviToolItem;
import ic2.gravisuite.items.LappackItem;
import ic2.gravisuite.items.VajraItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.registries.DeferredItem;

@Mod(GraviSuiteMod.MOD_ID)
public class GraviSuiteMod {
  public static final String MOD_ID = "gravisuite";
  public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

  // Tools
  public static final DeferredItem<VajraItem> VAJRA = CONTENT.registerItem("vajra", VajraItem::new);
  public static final DeferredItem<AdvancedDrillItem> ADVANCED_DRILL =
      CONTENT.registerItem("advanced_drill", AdvancedDrillItem::new);
  public static final DeferredItem<AdvancedChainsawItem> ADVANCED_CHAINSAW =
      CONTENT.registerItem("advanced_chainsaw", AdvancedChainsawItem::new);
  public static final DeferredItem<GraviToolItem> GRAVITOOL =
      CONTENT.registerItem("gravitool", GraviToolItem::new);

  // Armors & Jetpacks
  public static final DeferredItem<GraviChestplateItem> GRAVI_CHESTPLATE =
      CONTENT.registerItem("gravi_chestplate", GraviChestplateItem::new);
  public static final DeferredItem<AdvancedNanoChestplateItem> ADVANCED_NANO_CHESTPLATE =
      CONTENT.registerItem("advanced_nano_chestplate", AdvancedNanoChestplateItem::new);
  public static final DeferredItem<AdvancedJetpackItem> ADVANCED_JETPACK =
      CONTENT.registerItem("advanced_jetpack", AdvancedJetpackItem::new);
  public static final DeferredItem<LappackItem> ADVANCED_LAPPACK =
      CONTENT.registerItem("advanced_lappack", LappackItem::createAdvanced);
  public static final DeferredItem<LappackItem> ULTIMATE_LAPPACK =
      CONTENT.registerItem("ultimate_lappack", LappackItem::createUltimate);

  // Crafting Components
  public static final DeferredItem<Item> SUPERCONDUCTOR =
      CONTENT.registerItem(
          "superconductor", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> SUPERCONDUCTOR_COVER =
      CONTENT.registerItem(
          "superconductor_cover", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
  public static final DeferredItem<Item> COOLING_CORE =
      CONTENT.registerItem(
          "cooling_core", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
  public static final DeferredItem<Item> GRAVITATION_ENGINE =
      CONTENT.registerItem(
          "gravitation_engine", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<Item> MAGNETRON =
      CONTENT.registerItem("magnetron", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
  public static final DeferredItem<Item> VAJRA_CORE =
      CONTENT.registerItem("vajra_core", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
  public static final DeferredItem<Item> ENGINE_BOOSTER =
      CONTENT.registerItem(
          "engine_booster", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

  // Creative Tab
  static {
    CONTENT.registerCreativeModeTab(
        "main",
        () -> new ItemStack(GRAVI_CHESTPLATE.get()),
        (params, output) -> {
          output.accept(GRAVI_CHESTPLATE.get());
          output.accept(ADVANCED_NANO_CHESTPLATE.get());
          output.accept(ADVANCED_JETPACK.get());
          output.accept(ADVANCED_LAPPACK.get());
          output.accept(ULTIMATE_LAPPACK.get());

          output.accept(VAJRA.get());
          output.accept(ADVANCED_DRILL.get());
          output.accept(ADVANCED_CHAINSAW.get());
          output.accept(GRAVITOOL.get());

          output.accept(SUPERCONDUCTOR.get());
          output.accept(SUPERCONDUCTOR_COVER.get());
          output.accept(COOLING_CORE.get());
          output.accept(GRAVITATION_ENGINE.get());
          output.accept(MAGNETRON.get());
          output.accept(VAJRA_CORE.get());
          output.accept(ENGINE_BOOSTER.get());
        });
  }

  public GraviSuiteMod(IEventBus modEventBus) {
    CONTENT.register(modEventBus);
    NeoForge.EVENT_BUS.addListener(GraviSuiteMod::onLivingEquipmentChange);
    if (FMLEnvironment.dist == Dist.CLIENT) {
      modEventBus.addListener(GraviSuiteMod::onClientSetup);
    }
  }

  private static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
    if (event.getSlot() == EquipmentSlot.CHEST && event.getEntity() instanceof Player player) {
      if (event.getFrom().getItem() instanceof GraviChestplateItem
          && !(event.getTo().getItem() instanceof GraviChestplateItem)) {
        if (!player.isCreative() && !player.isSpectator()) {
          player.getAbilities().mayfly = false;
          player.getAbilities().flying = false;
        }
        player.getAbilities().setFlyingSpeed(0.05F);
        player.onUpdateAbilities();
      }
    }
  }

  private static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(
        () -> {
          ItemProperties.register(
              GRAVITOOL.get(),
              ResourceLocation.parse("gravisuite:mode"),
              (stack, level, entity, seed) -> {
                GraviToolItem.Mode mode = GraviToolItem.getMode(stack);
                return switch (mode) {
                  case HOE -> 0.0F;
                  case TREETAP -> 0.25F;
                  case WRENCH -> 0.5F;
                  case SCREWDRIVER -> 0.75F;
                };
              });
        });
  }
}
