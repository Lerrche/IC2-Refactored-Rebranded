package ic2.advsolar;

import com.google.common.base.Suppliers;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class AdvSolarArmorMaterials {
  public static final Holder<ArmorMaterial> ADVANCED_HELMET =
      createMaterial("advanced_solar_panels:advancedsolarhelmet", new int[] {2, 0, 0, 0}, 1.0F);

  public static final Holder<ArmorMaterial> HYBRID_HELMET =
      createMaterial("advanced_solar_panels:hybridsolarhelmet", new int[] {3, 0, 0, 0}, 2.0F);

  public static final Holder<ArmorMaterial> ULTIMATE_HELMET =
      createMaterial("advanced_solar_panels:ultimatesolarhelmet", new int[] {3, 0, 0, 0}, 3.0F);

  private static Holder<ArmorMaterial> createMaterial(
      String name, int[] protectionAmounts, float toughness) {
    Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      int index = type.getSlot().getIndex();
      if (index >= 0 && index < protectionAmounts.length) {
        defense.put(type, protectionAmounts[index]);
      }
    }

    List<ArmorMaterial.Layer> layers =
        List.of(new ArmorMaterial.Layer(ResourceLocation.parse(name)));
    return Holder.direct(
        new ArmorMaterial(
            defense,
            0,
            SoundEvents.ARMOR_EQUIP_IRON,
            Suppliers.memoize(Ingredient::of),
            layers,
            toughness,
            0.0F));
  }
}
