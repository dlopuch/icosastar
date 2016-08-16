package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;

/**
 * Class that defines mapping-specific pattern defaults that patterns can register themselves against.
 */
public abstract class AbstractIcosaLXModel extends LXModel {
  public AbstractIcosaLXModel(LXFixture[] fixtures) {
    super(fixtures);
  }

  public abstract void applyPresets(PerlinNoisePattern perlinNoise);
}
