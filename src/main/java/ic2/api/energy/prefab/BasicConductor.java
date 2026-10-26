package ic2.api.energy.prefab;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.info.ILocatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Reusable IC2 energy-network conductor for add-on cables. The owning block entity must call {@link
 * #onLoad()} when attached to a server level and {@link #invalidate()} when removed.
 */
public class BasicConductor extends BasicEnergyTile implements IEnergyConductor {
  private final double conductionLoss;
  private final double insulationAbsorption;
  private final double insulationBreakdown;
  private final double conductorBreakdown;
  private boolean insulated = true;
  private boolean conductorIntact = true;

  public BasicConductor(
      BlockEntity parent,
      double capacity,
      double conductionLoss,
      double insulationAbsorption,
      double insulationBreakdown,
      double conductorBreakdown) {
    super(parent, positive(capacity, "capacity"));
    this.conductionLoss = nonNegative(conductionLoss, "conductionLoss");
    this.insulationAbsorption = nonNegative(insulationAbsorption, "insulationAbsorption");
    this.insulationBreakdown = nonNegative(insulationBreakdown, "insulationBreakdown");
    this.conductorBreakdown = nonNegative(conductorBreakdown, "conductorBreakdown");
  }

  public BasicConductor(
      ILocatable parent,
      double capacity,
      double conductionLoss,
      double insulationAbsorption,
      double insulationBreakdown,
      double conductorBreakdown) {
    super(parent, positive(capacity, "capacity"));
    this.conductionLoss = nonNegative(conductionLoss, "conductionLoss");
    this.insulationAbsorption = nonNegative(insulationAbsorption, "insulationAbsorption");
    this.insulationBreakdown = nonNegative(insulationBreakdown, "insulationBreakdown");
    this.conductorBreakdown = nonNegative(conductorBreakdown, "conductorBreakdown");
  }

  public BasicConductor(
      Level level,
      BlockPos pos,
      double capacity,
      double conductionLoss,
      double insulationAbsorption,
      double insulationBreakdown,
      double conductorBreakdown) {
    super(level, pos, positive(capacity, "capacity"));
    this.conductionLoss = nonNegative(conductionLoss, "conductionLoss");
    this.insulationAbsorption = nonNegative(insulationAbsorption, "insulationAbsorption");
    this.insulationBreakdown = nonNegative(insulationBreakdown, "insulationBreakdown");
    this.conductorBreakdown = nonNegative(conductorBreakdown, "conductorBreakdown");
  }

  @Override
  public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
    return this.conductorIntact;
  }

  @Override
  public boolean emitsEnergyTo(IEnergyAcceptor acceptor, Direction side) {
    return this.conductorIntact;
  }

  @Override
  public double getConductionLoss() {
    return this.conductionLoss;
  }

  @Override
  public double getInsulationEnergyAbsorption() {
    return this.insulated ? this.insulationAbsorption : 0.0;
  }

  @Override
  public double getInsulationBreakdownEnergy() {
    return this.insulated ? this.insulationBreakdown : 0.0;
  }

  @Override
  public double getConductorBreakdownEnergy() {
    return this.conductorBreakdown;
  }

  @Override
  public void removeInsulation() {
    this.insulated = false;
  }

  @Override
  public void removeConductor() {
    this.conductorIntact = false;
  }

  public boolean hasInsulation() {
    return this.insulated;
  }

  public boolean isConductorIntact() {
    return this.conductorIntact;
  }

  @Override
  protected String getNbtTagName() {
    return "IC2AddonBasicConductor";
  }

  private static double positive(double value, String name) {
    if (!Double.isFinite(value) || value <= 0)
      throw new IllegalArgumentException(name + " must be finite and positive");
    return value;
  }

  private static double nonNegative(double value, String name) {
    if (!Double.isFinite(value) || value < 0)
      throw new IllegalArgumentException(name + " must be finite and non-negative");
    return value;
  }
}
