package ic2.core.block.transport;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PipeType implements StringRepresentable {
  BRONZE("bronze", 2400, 174, 81, 17),
  STEEL("steel", 4800, 128, 128, 128);

  public static final PipeType[] VALUES = values();

  private final String name;
  public final int transferRate; // mB/s (divide by 20 for per-tick)
  public final int red;
  public final int green;
  public final int blue;

  PipeType(String name, int transferRate, int red, int green, int blue) {
    this.name = name;
    this.transferRate = transferRate;
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public int getColor() {
    return ((this.red & 0xFF) << 16) | ((this.green & 0xFF) << 8) | (this.blue & 0xFF);
  }

  public int getTransferRatePerTick() {
    return Math.max(1, this.transferRate / 20);
  }

  @Override
  public @NotNull String getSerializedName() {
    return this.name.toLowerCase(Locale.ROOT);
  }

  public String getName() {
    return this.name;
  }

  public String getName(PipeSize size) {
    return this.name + "_pipe_" + size.getName();
  }
}
