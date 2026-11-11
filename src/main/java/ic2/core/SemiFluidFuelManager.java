package ic2.core;

import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.core.fluid.LiquidFuelCompat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class SemiFluidFuelManager implements ISemiFluidFuelManager {
  private final Map<Fluid, ISemiFluidFuelManager.BurnProperty> burnProperties =
      new ConcurrentHashMap<>();
  private final Set<Fluid> nonFuels = Collections.newSetFromMap(new ConcurrentHashMap<>());
  private volatile boolean registryScanned = false;

  @Override
  public void addFluid(Fluid fluid, int amount, double power) {
    if (fluid == null) {
      return;
    }
    this.nonFuels.remove(fluid);
    this.burnProperties.put(fluid, new ISemiFluidFuelManager.BurnProperty(amount, power));
    if (fluid instanceof FlowingFluid flowingFluid) {
      Fluid source = flowingFluid.getSource();
      Fluid flowing = flowingFluid.getFlowing();
      if (source != null) {
        this.nonFuels.remove(source);
        this.burnProperties.put(source, new ISemiFluidFuelManager.BurnProperty(amount, power));
      }
      if (flowing != null) {
        this.nonFuels.remove(flowing);
        this.burnProperties.put(flowing, new ISemiFluidFuelManager.BurnProperty(amount, power));
      }
    }
  }

  @Override
  public ISemiFluidFuelManager.BurnProperty getBurnProperty(Fluid fluid) {
    if (fluid == null) {
      return null;
    }
    ISemiFluidFuelManager.BurnProperty prop = this.burnProperties.get(fluid);
    if (prop != null) {
      return prop;
    }
    if (this.nonFuels.contains(fluid)) {
      return null;
    }
    if (fluid instanceof FlowingFluid flowingFluid) {
      Fluid source = flowingFluid.getSource();
      if (source != fluid && source != null) {
        prop = this.burnProperties.get(source);
        if (prop != null) {
          this.burnProperties.put(fluid, prop);
          return prop;
        }
      }
    }

    prop = LiquidFuelCompat.resolve(fluid);
    if (prop != null) {
      this.burnProperties.put(fluid, prop);
      if (fluid instanceof FlowingFluid flowingFluid) {
        Fluid source = flowingFluid.getSource();
        Fluid flowing = flowingFluid.getFlowing();
        if (source != null) {
          this.burnProperties.put(source, prop);
        }
        if (flowing != null) {
          this.burnProperties.put(flowing, prop);
        }
      }
      return prop;
    } else {
      this.nonFuels.add(fluid);
      return null;
    }
  }

  @Override
  public boolean acceptsFluid(Fluid fluid) {
    return fluid != null && this.getBurnProperty(fluid) != null;
  }

  public void scanAndRegisterAll() {
    for (Fluid fluid : BuiltInRegistries.FLUID) {
      if (fluid != null
          && !this.burnProperties.containsKey(fluid)
          && !this.nonFuels.contains(fluid)) {
        this.getBurnProperty(fluid);
      }
    }
    this.registryScanned = true;
  }

  @Override
  public Set<Fluid> getAcceptedFluids() {
    if (!this.registryScanned) {
      this.scanAndRegisterAll();
    }
    return Collections.unmodifiableSet(this.burnProperties.keySet());
  }

  @Override
  public Map<Fluid, ISemiFluidFuelManager.BurnProperty> getBurnProperties() {
    if (!this.registryScanned) {
      this.scanAndRegisterAll();
    }
    return Collections.unmodifiableMap(this.burnProperties);
  }
}
