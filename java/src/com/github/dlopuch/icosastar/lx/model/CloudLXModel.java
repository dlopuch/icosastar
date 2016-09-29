package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.*;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudLXModel extends AbstractIcosaLXModel {

  public final List<LXFixture> allRadials;
  public final List<List<LXFixture>> radialsByFadecandy;

  public CloudLXModel(List<LXFixture> allRadials, List<List<LXFixture>> radialsByFadecandy, boolean hasGui) {
    super(allRadials.toArray(new LXFixture[allRadials.size()]), hasGui);

    System.out.println("Starting up with the dance cloud model");

    this.allRadials = allRadials;

    this.radialsByFadecandy = radialsByFadecandy;
  }

  @Override
  public void addPatternsAndGo(LX lx, PApplet p, IcosaFFT icosaFft) {
    LXPattern perlinNoise = new PerlinNoisePattern(lx, p, icosaFft);

    List<LXPattern> patterns = new ArrayList<>(Arrays.asList(
        perlinNoise,
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
        new RainbowFadecandyPattern(lx)
    ));

    if (hasGui) {
      patterns.add(new LedSelectorPattern(lx));
    }

    lx.setPatterns(patterns.toArray(new LXPattern[patterns.size()]));
    lx.goPattern(perlinNoise);
  }

  @Override
  public void applyPresets(PerlinNoisePattern perlinNoise) {
  }
}
