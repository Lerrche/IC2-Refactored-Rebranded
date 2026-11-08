package ic2.core.sound;

import ic2.core.IC2;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class PositionedSoundInstance extends SimpleSoundInstance
    implements ListenableSoundInstance {
  private Runnable onFinish = null;
  protected boolean isMachine = false;

  public void setMachine(boolean isMachine) {
    this.isMachine = isMachine;
  }

  public boolean isMachine() {
    return this.isMachine;
  }

  @Override
  public float getVolume() {
    float v = super.getVolume();
    if (this.isMachine) {
      if (ic2.industrialjs.IndustrialJS.isAudioDisabled()) {
        return 0.0F;
      }
      v *=
          ic2.core.init.IC2ClientConfig.getMachineVolume()
              * (float) ic2.industrialjs.IndustrialJS.getMachineVolumeMultiplier();
    }
    return v;
  }

  public PositionedSoundInstance(
      SoundEvent sound, SoundSource category, float volume, float pitch, BlockPos pos) {
    super(sound, category, volume, pitch, IC2.random, pos);
  }

  @Override
  public void addOnFinishListener(Runnable then) {
    this.onFinish =
        this.onFinish == null
            ? then
            : () -> {
              this.onFinish.run();
              then.run();
            };
  }

  @Override
  public void onFinish(Runnable then) {
    this.onFinish = then;
  }

  @Override
  public void clearOnFinishListener() {
    this.onFinish = null;
  }

  @Override
  public void finish() {
    if (this.onFinish != null) {
      this.onFinish.run();
    }
  }
}
