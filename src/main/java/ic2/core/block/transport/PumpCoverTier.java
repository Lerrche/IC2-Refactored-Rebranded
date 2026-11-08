package ic2.core.block.transport;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PumpCoverTier implements StringRepresentable {
  LV("pump_lv", 32, 640, 0x00FF00),
  MV("pump_mv", 128, 2560, 0xFFFF00),
  HV("pump_hv", 512, 10240, 0xFFA500),
  EV("pump_ev", 2048, 40960, 0xFF0000);

  public static final PumpCoverTier[] VALUES = values();

  private final String name;
  public final int ratePerTick; // mB/t
  public final int ratePerSec; // mB/s
  public final int color;

  PumpCoverTier(String name, int ratePerTick, int ratePerSec, int color) {
    this.name = name;
    this.ratePerTick = ratePerTick;
    this.ratePerSec = ratePerSec;
    this.color = color;
  }

  @Override
  public @NotNull String getSerializedName() {
    return this.name.toLowerCase(Locale.ROOT);
  }

  public String getName() {
    return this.name;
  }
}
