package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import heronarts.lx.model.LXFixture;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudLXModel extends AbstractIcosaLXModel {

  public List<LXFixture> allRadials;
  public List<List<LXFixture>> radialsByFadecandy;

  public CloudLXModel(List<LXFixture> allRadials, List<List<LXFixture>> radialsByFadecandy) {
    super(allRadials.toArray(new LXFixture[allRadials.size()]));

    // Do it again, for super() ordering rules
    this.allRadials = allRadials;

    this.radialsByFadecandy = radialsByFadecandy;
  }

  @Override
  public void applyPresets(PerlinNoisePattern perlinNoise) {
    // TODO
  }
}
