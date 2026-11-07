package ic2.core.sound;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

public class SoundClient extends Sound {
  private static final int PLAY_GRACE_TICKS = 40;

  private final List<SoundInstance> startedSoundList = new CopyOnWriteArrayList<>();
  public net.minecraft.client.sounds.SoundManager vanillaManager =
      Minecraft.getInstance().getSoundManager();
  private RepeatablePositionedSoundInstance repeatInstance = null;
  private PositionedSoundInstance onceInstance = null;
  private EntityTrackingSoundInstance entityTrackingInstance = null;
  private SoundEvent soundEvent;
  private SoundSource soundCategory;
  private BlockPos pos;
  private LivingEntity entity;
  private float volume;
  private float pitch;
  private Item sourceItem;
  private boolean isStarted = false;
  private boolean isLooping = false;
  private boolean isMachine = false;
  private Runnable onFinishCallback = null;
  private int playGraceTicks = 0;

  public void setMachine(boolean isMachine) {
    this.isMachine = isMachine;
    if (this.repeatInstance != null) {
      this.repeatInstance.setMachine(isMachine);
    }
    if (this.onceInstance != null) {
      this.onceInstance.setMachine(isMachine);
    }
  }

  public boolean isMachine() {
    return this.isMachine;
  }

  protected SoundClient() {}

  public SoundClient(
      SoundEvent soundEvent, SoundSource soundCategory, BlockPos pos, float volume, float pitch) {
    this();
    this.soundEvent = soundEvent;
    this.soundCategory = soundCategory;
    this.pos = pos;
    this.volume = volume;
    this.pitch = pitch;
    this.repeatInstance =
        new RepeatablePositionedSoundInstance(soundEvent, soundCategory, volume, pitch, pos);
    this.onceInstance = new PositionedSoundInstance(soundEvent, soundCategory, volume, pitch, pos);
    if (this.isMachine) {
      this.repeatInstance.setMachine(true);
      this.onceInstance.setMachine(true);
    }
  }

  public SoundClient(
      SoundEvent soundEvent,
      SoundSource soundCategory,
      LivingEntity entity,
      float volume,
      float pitch) {
    this();
    this.soundEvent = soundEvent;
    this.soundCategory = soundCategory;
    this.entity = entity;
    this.volume = volume;
    this.pitch = pitch;
    this.entityTrackingInstance =
        new EntityTrackingSoundInstance(soundEvent, soundCategory, volume, pitch, entity);
  }

  public void setSourceItem(Item item) {
    this.sourceItem = item;
    if (this.entityTrackingInstance != null) {
      this.entityTrackingInstance.setSourceItem(item);
    }
  }

  @Override
  public void play() {
    super.play();
    if (this.isStarted && this.isLooping) {
      return;
    }
    this.isStarted = true;
    this.isLooping = true;
    this.playGraceTicks = PLAY_GRACE_TICKS;
    DeferredSoundOps.run(
        () -> {
          if (this.repeatInstance != null) {
            this.stopAllStarted();
            this.repeatInstance =
                new RepeatablePositionedSoundInstance(
                    this.soundEvent, this.soundCategory, this.volume, this.pitch, this.pos);
            this.repeatInstance.setMachine(this.isMachine);
            if (this.onFinishCallback != null) {
              this.repeatInstance.onFinish(this.onFinishCallback);
            }
            this.vanillaManager.play(this.repeatInstance);
            this.startedSoundList.add(this.repeatInstance);
          }

          if (this.entityTrackingInstance != null) {
            this.stopAllStarted();
            this.entityTrackingInstance =
                new EntityTrackingSoundInstance(
                    this.soundEvent, this.soundCategory, this.volume, this.pitch, this.entity);
            if (this.sourceItem != null) {
              this.entityTrackingInstance.setSourceItem(this.sourceItem);
            }
            if (this.onFinishCallback != null) {
              this.entityTrackingInstance.onFinish(this.onFinishCallback);
            }

            this.vanillaManager.play(this.entityTrackingInstance);
            this.startedSoundList.add(this.entityTrackingInstance);
          }
        });
  }

  @Override
  public void playOnce() {
    super.playOnce();
    this.isStarted = true;
    this.isLooping = false;
    this.playGraceTicks = PLAY_GRACE_TICKS;
    DeferredSoundOps.run(
        () -> {
          if (this.onceInstance != null) {
            this.stopInstance(this.onceInstance);
            this.onceInstance =
                new PositionedSoundInstance(
                    this.soundEvent, this.soundCategory, this.volume, this.pitch, this.pos);
            this.onceInstance.setMachine(this.isMachine);
            if (this.onFinishCallback != null) {
              this.onceInstance.onFinish(this.onFinishCallback);
            }
            this.vanillaManager.play(this.onceInstance);
            this.startedSoundList.add(this.onceInstance);
          }

          if (this.entityTrackingInstance != null) {
            this.entityTrackingInstance.playOnce();
            if (this.onFinishCallback != null) {
              this.entityTrackingInstance.onFinish(this.onFinishCallback);
            }
            this.startedSoundList.add(this.entityTrackingInstance);
          }
        });
  }

  @Override
  public void stop() {
    super.stop();
    this.isStarted = false;
    this.isLooping = false;
    this.playGraceTicks = 0;
    DeferredSoundOps.run(this::stopAllStarted);
  }

  private void stopAllStarted() {
    for (SoundInstance instance : this.startedSoundList) {
      if (instance != null) {
        this.vanillaManager.stop(instance);
      }
    }
    this.startedSoundList.clear();
    this.stopInstance(this.repeatInstance);
    this.stopInstance(this.onceInstance);
    this.stopInstance(this.entityTrackingInstance);
  }

  private void stopInstance(SoundInstance instance) {
    if (instance != null) {
      this.vanillaManager.stop(instance);
      this.startedSoundList.remove(instance);
    }
  }

  private boolean isPlayingSound(SoundInstance instance) {
    return instance != null && this.vanillaManager.isActive(instance);
  }

  @Override
  public boolean isPlaying() {
    if (!this.isStarted) {
      return false;
    }
    if (this.isLooping) {
      return true;
    }
    if (this.playGraceTicks > 0) {
      return true;
    }
    if (this.onceInstance != null && this.isPlayingSound(this.onceInstance)) {
      return true;
    }
    if (this.entityTrackingInstance != null && this.isPlayingSound(this.entityTrackingInstance)) {
      return true;
    }
    return false;
  }

  private void addOnFinishListener(ListenableSoundInstance instance, Runnable then) {
    if (instance != null) {
      instance.addOnFinishListener(then);
    }
  }

  private void onFinishSound(ListenableSoundInstance instance, Runnable then) {
    if (instance != null) {
      instance.onFinish(then);
    }
  }

  private void checkSoundFinished(SoundInstance instance) {
    if (!(instance instanceof ListenableSoundInstance listenableSoundInstance)) {
      return;
    }

    if (!this.startedSoundList.contains(instance)) {
      return;
    }

    if (this.isLooping) {
      // Looping sounds do not self-finish; they are managed by the parent machine/entity.
      return;
    }

    if (this.playGraceTicks > 0) {
      return;
    }

    if (this.vanillaManager.isActive(instance)) {
      return;
    }

    listenableSoundInstance.finish();
    this.startedSoundList.remove(instance);
  }

  @Override
  public void addOnFinishListener(Runnable then) {
    this.onFinishCallback =
        this.onFinishCallback == null
            ? then
            : () -> {
              this.onFinishCallback.run();
              then.run();
            };
    this.addOnFinishListener(this.onceInstance, then);
    this.addOnFinishListener(this.repeatInstance, then);
    this.addOnFinishListener(this.entityTrackingInstance, then);
  }

  @Override
  public void onFinish(Runnable then) {
    this.onFinishCallback = then;
    this.onFinishSound(this.onceInstance, then);
    this.onFinishSound(this.repeatInstance, then);
    this.onFinishSound(this.entityTrackingInstance, then);
  }

  public void tick() {
    super.tick();
    if (this.playGraceTicks > 0) {
      this.playGraceTicks--;
    }

    if (this.isStarted && !this.isLooping) {
      this.checkSoundFinished(this.onceInstance);
      if (this.playGraceTicks == 0
          && !this.isPlayingSound(this.onceInstance)
          && !this.isPlayingSound(this.entityTrackingInstance)
          && this.startedSoundList.isEmpty()) {
        this.isStarted = false;
      }
    }
  }
}
