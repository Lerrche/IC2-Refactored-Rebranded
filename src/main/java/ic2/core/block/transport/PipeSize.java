package ic2.core.block.transport;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PipeSize implements StringRepresentable {
  TINY("tiny", 1, 0.25F, 1.0F / 6.0F, 4),
  SMALL("small", 4, 0.375F, 1.0F / 3.0F, 6),
  MEDIUM("medium", 16, 0.5F, 1.0F, 8),
  LARGE("large", 64, 0.625F, 2.0F, 10);

  public static final PipeSize[] VALUES = values();

  private final String name;
  public final int maxStackSize;
  public final float thickness;
  public final float multiplier;
  public final int pixelThickness;

  PipeSize(String name, int maxStackSize, float thickness, float multiplier, int pixelThickness) {
    this.name = name;
    this.maxStackSize = maxStackSize;
    this.thickness = thickness;
    this.multiplier = multiplier;
    this.pixelThickness = pixelThickness;
  }

  public float getApothem() {
    return this.thickness / 2.0F;
  }

  @Override
  public @NotNull String getSerializedName() {
    return this.name.toLowerCase(Locale.ROOT);
  }

  public String getName() {
    return this.name;
  }
}
