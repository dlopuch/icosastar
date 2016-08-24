package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowFadecandyPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;

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
    lx.setPatterns(new LXPattern[] {
        perlinNoise,
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
        new RainbowFadecandyPattern(lx)
    });
    lx.goPattern(perlinNoise);
  }

  @Override
  public void applyPresets(PerlinNoisePattern perlinNoise) {
  }
}
