package ic2.integration.create;

/** Bridge interface mixed into Create's KineticBlockEntity to allow IC2 to drive rotation. */
public interface IKineticBlockEntityBridge {
  void ic2$updateExternalRotation(float speed, float capacityPerRpm);

  float ic2$getExternalSpeed();
}
