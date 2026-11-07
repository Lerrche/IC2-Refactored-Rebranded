package ic2.core.init;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

public class IC2ClientConfig {
  public static final ModConfigSpec SPEC;

  public static final Audio audio;
  public static final Misc misc;

  private static double currentMachineVolume = -1.0;
  private static final File PREFS_FILE =
      FMLPaths.CONFIGDIR.get().resolve("ic2-audio.properties").toFile();

  static {
    ModConfigSpec.Builder b = new ModConfigSpec.Builder();

    audio = new Audio(b);
    misc = new Misc(b);

    SPEC = b.build();
  }

  public static double getMachineVolumeDouble() {
    if (currentMachineVolume >= 0.0) {
      return currentMachineVolume;
    }
    if (PREFS_FILE.exists()) {
      try (FileReader reader = new FileReader(PREFS_FILE)) {
        Properties props = new Properties();
        props.load(reader);
        String val = props.getProperty("machinery_volume");
        if (val != null) {
          currentMachineVolume = Math.max(0.0, Math.min(1.0, Double.parseDouble(val)));
          return currentMachineVolume;
        }
      } catch (Exception ignored) {
      }
    }
    try {
      if (audio != null && audio.volume != null) {
        Double val = audio.volume.get();
        currentMachineVolume = (val != null) ? Math.max(0.0, Math.min(1.0, val)) : 1.0;
        return currentMachineVolume;
      }
    } catch (Exception ignored) {
    }
    currentMachineVolume = 1.0;
    return 1.0;
  }

  public static float getMachineVolume() {
    try {
      if (audio != null && audio.enabled != null && !audio.enabled.get()) {
        return 0.0f;
      }
    } catch (Exception ignored) {
    }
    double vol = getMachineVolumeDouble();
    if (vol <= 0.0) {
      return 0.00001f;
    }
    return (float) vol;
  }

  public static void setMachineVolume(double val) {
    currentMachineVolume = Math.max(0.0, Math.min(1.0, val));
    try {
      Properties props = new Properties();
      props.setProperty("machinery_volume", Double.toString(currentMachineVolume));
      try (FileWriter writer = new FileWriter(PREFS_FILE)) {
        props.store(writer, "IC2 Audio Settings");
      }
    } catch (Exception ignored) {
    }
    try {
      if (audio != null && audio.volume != null) {
        audio.volume.set(currentMachineVolume);
        audio.volume.save();
      }
    } catch (Exception ignored) {
    }
    try {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.getSoundManager() != null && mc.options != null) {
        float blocksVol = mc.options.getSoundSourceVolume(SoundSource.BLOCKS);
        mc.getSoundManager().updateSourceVolume(SoundSource.BLOCKS, blocksVol);
      }
    } catch (Exception ignored) {
    }
  }

  public static class Audio {
    public final ModConfigSpec.BooleanValue enabled;
    public final ModConfigSpec.DoubleValue volume;
    public final ModConfigSpec.IntValue fadeDistance;
    public final ModConfigSpec.IntValue maxSourceCount;

    Audio(ModConfigSpec.Builder b) {
      b.push("audio");
      b.comment("Enable IC2's custom sound system.");
      enabled = b.define("enabled", true);
      b.comment("Volume of IC2's sounds, range from 0 (silent) ... 1 (100%).");
      volume = b.defineInRange("volume", 1.0, 0.0, 1.0);
      b.comment("The number of blocks the sounds attenuate over.");
      fadeDistance = b.defineInRange("fadeDistance", 16, 0, Integer.MAX_VALUE);
      b.comment(
          "Maximum number of active audio sources, only change it if you know what you're doing.");
      maxSourceCount = b.defineInRange("maxSourceCount", 32, 0, Integer.MAX_VALUE);
      b.pop();
    }
  }

  public static class Misc {
    public final ModConfigSpec.BooleanValue hideSecretRecipes;
    public final ModConfigSpec.BooleanValue quantumSpeedOnSprint;

    Misc(ModConfigSpec.Builder b) {
      b.push("misc");
      b.comment("Enable hiding of secret recipes in CraftGuide/NEI.");
      hideSecretRecipes = b.define("hideSecretRecipes", true);
      b.comment(
          "Enable activation of the quantum leggings' speed boost when sprinting instead of holding the boost key.");
      quantumSpeedOnSprint = b.define("quantumSpeedOnSprint", true);
      b.pop();
    }
  }
}
