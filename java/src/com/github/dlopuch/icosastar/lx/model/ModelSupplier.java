package com.github.dlopuch.icosastar.lx.model;

import com.github.dlopuch.icosastar.lx.utils.DeferredLxOutputProvider;
import heronarts.lx.LX;
import heronarts.lx.output.LXOutput;

/**
 * Synchronizer class to supply same model for both headless and GUI apps
 */
public class ModelSupplier {

  public static AbstractIcosaLXModel getModel(boolean hasGui, DeferredLxOutputProvider outputProvider) {
    return IcosastarLXModel.makeModel(hasGui, outputProvider);
    //return CloudLXModelBuilder.makeModel(hasGui);
    //return BikeModel.makeModel(hasGui, outputProvider);

    //return FibonocciPetalsModel.makeModel(hasGui);
  }
}
