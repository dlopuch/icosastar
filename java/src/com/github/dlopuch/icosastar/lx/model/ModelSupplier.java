package com.github.dlopuch.icosastar.lx.model;

import heronarts.lx.LX;

/**
 * Synchronizer class to supply same model for both headless and GUI apps
 */
public class ModelSupplier {
  public static AbstractIcosaLXModel getModel(boolean hasGui) {
    //return IcosastarLXModelBuilder.makeModel(hasGui);
    //return CloudLXModelBuilder.makeModel(hasGui);
    return BikeModel.makeModel(hasGui);
  }
}
