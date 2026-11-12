package ic2.integration.jade;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.config.IPluginConfig;

/** Jade provider displaying Nuclear Reactor core heat, max heat, and cycle rate. */
public enum NuclearReactorComponentProvider
    implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
  INSTANCE;

  public static final ResourceLocation UID =
      ResourceLocation.fromNamespaceAndPath("ic2", "nuclear_reactor");

  public static void registerCommon(IWailaCommonRegistration registration) {
    registration.registerBlockDataProvider(INSTANCE, Block.class);
  }

  public static void registerClient(IWailaClientRegistration registration) {
    registration.registerBlockComponent(INSTANCE, Block.class);
  }

  @Override
  public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
    IReactor reactor = null;
    if (accessor.getBlockEntity() instanceof IReactor direct) {
      reactor = direct;
    } else if (accessor.getBlockEntity() instanceof IReactorChamber chamber) {
      reactor = chamber.getReactorInstance();
    }

    if (reactor != null) {
      tag.putInt("heat", reactor.getHeat());
      tag.putInt("max_heat", reactor.getMaxHeat());
      tag.putFloat("output", reactor.getReactorEnergyOutput());
      tag.putInt("cycle_ticks", reactor.getTickRate());
    }
  }

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    if (data.contains("heat") && data.contains("max_heat")) {
      int heat = data.getInt("heat");
      int maxHeat = data.getInt("max_heat");
      float output = data.getFloat("output");
      int cycleTicks = data.contains("cycle_ticks") ? data.getInt("cycle_ticks") : 20;

      double pct = maxHeat > 0 ? ((double) heat / maxHeat) * 100.0 : 0.0;
      ChatFormatting heatColor;
      if (pct < 25.0) {
        heatColor = ChatFormatting.GREEN;
      } else if (pct < 50.0) {
        heatColor = ChatFormatting.YELLOW;
      } else if (pct < 75.0) {
        heatColor = ChatFormatting.GOLD;
      } else {
        heatColor = ChatFormatting.RED;
      }

      Component heatComponent =
          Component.translatable(
              "ic2.jade.reactor.heat",
              Component.literal(String.format(Locale.ROOT, "%,d", heat)).withStyle(heatColor),
              Component.literal(String.format(Locale.ROOT, "%,d", maxHeat))
                  .withStyle(ChatFormatting.GRAY),
              Component.literal(String.format(Locale.ROOT, "%.1f%%", pct)).withStyle(heatColor));
      tooltip.add(heatComponent);

      double cycleSeconds = cycleTicks / 20.0;
      Component cycleComponent =
          Component.translatable(
              "ic2.jade.reactor.cycle",
              Component.literal(String.format(Locale.ROOT, "%.1f EU/t", output))
                  .withStyle(ChatFormatting.AQUA),
              Component.literal(String.format(Locale.ROOT, "%.1fs (%dt)", cycleSeconds, cycleTicks))
                  .withStyle(ChatFormatting.GRAY));
      tooltip.add(cycleComponent);
    }
  }

  @Override
  public ResourceLocation getUid() {
    return UID;
  }
}
