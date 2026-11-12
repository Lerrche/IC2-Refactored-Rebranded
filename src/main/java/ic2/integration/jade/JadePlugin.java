package ic2.integration.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("ic2")
public class JadePlugin implements IWailaPlugin {

  @Override
  public void register(IWailaCommonRegistration registration) {
    NuclearReactorComponentProvider.registerCommon(registration);
  }

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    NuclearReactorComponentProvider.registerClient(registration);
  }
}
