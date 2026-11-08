package ic2.core.recipe.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import ic2.core.util.StackUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class RecipeInputItemStack extends RecipeInputBase {
  public final ItemStack input;

  public RecipeInputItemStack(ItemStack input) {
    if (StackUtil.isEmpty(input)) {
      this.input = ItemStack.EMPTY;
      return;
    }

    this.input = input.copy();
  }

  @Override
  public boolean matches(ItemStack subject) {
    if (this.input.isEmpty() || StackUtil.isEmpty(subject)) return false;
    if (this.input.getItem() != subject.getItem()) return false;

    CompoundTag requiredNbt = StackUtil.getTag(this.input);
    if (requiredNbt == null || requiredNbt.isEmpty()) return true;

    CompoundTag subjectNbt = StackUtil.getTag(subject);
    if (subjectNbt == null) {
      // Subject has no NBT — check that all required keys map to default values (0 for numbers,
      // etc.)
      for (String key : requiredNbt.getAllKeys()) {
        if (!isDefaultTag(requiredNbt.get(key))) return false;
      }

      return true;
    }

    // Check required ⊆ subject (partial match)
    for (String key : requiredNbt.getAllKeys()) {
      if (!subjectNbt.contains(key)) return false;
      if (!subjectNbt.get(key).equals(requiredNbt.get(key))) return false;
    }

    return true;
  }

  private static boolean isDefaultTag(Tag tag) {
    if (tag instanceof NumericTag num) {
      return num.getAsNumber().doubleValue() == 0.0;
    }

    if (tag instanceof StringTag str) {
      return str.getAsString().isEmpty();
    }

    if (tag instanceof ByteArrayTag byteArray) {
      return byteArray.isEmpty();
    }

    if (tag instanceof IntArrayTag intArray) {
      return intArray.isEmpty();
    }

    if (tag instanceof LongArrayTag longArray) {
      return longArray.isEmpty();
    }

    if (tag instanceof ListTag list) {
      return list.isEmpty();
    }

    if (tag instanceof CompoundTag compound) {
      return compound.isEmpty();
    }

    return false;
  }

  @Override
  public int getAmount() {
    return this.input.isEmpty() ? 0 : this.input.getCount();
  }

  @Override
  protected List<ItemStack> listStacks() {
    return this.input.isEmpty() ? Collections.emptyList() : Arrays.asList(this.input);
  }

  @Override
  public String toString() {
    return this.input.isEmpty()
        ? "RInputItemStack<EMPTY>"
        : "RInputItemStack<" + StackUtil.setImmutableSize(this.input, this.getAmount()) + ">";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || this.getClass() != obj.getClass()) return false;
    RecipeInputItemStack other = (RecipeInputItemStack) obj;
    if (this.input.isEmpty()) return other.input.isEmpty();
    return StackUtil.checkItemEqualityStrict(other.input, this.input)
        && other.getAmount() == this.getAmount();
  }

  @Override
  public JsonElement toJson() {
    JsonObject obj = new JsonObject();
    if (this.input.isEmpty()) {
      return obj;
    }
    obj.addProperty("item", BuiltInRegistries.ITEM.getKey(this.input.getItem()).toString());
    obj.add(
        "data",
        (JsonElement) NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, StackUtil.getTag(this.input)));
    if (this.input.getCount() != 1) {
      obj.addProperty("count", this.input.getCount());
    }

    return obj;
  }
}
