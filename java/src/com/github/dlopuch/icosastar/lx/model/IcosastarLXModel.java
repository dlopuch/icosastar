package com.github.dlopuch.icosastar.lx.model;

import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowFadecandyPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IcosastarLXModel extends AbstractIcosaLXModel {
  public final List<LXFixture> innerSpokeLeds;
  public final List<LXFixture> outerSpokeLeds;
  public final List<LXFixture> ring1Leds;

  protected IcosastarLXModel(
      List<LXFixture> innerSpokeLeds, List<LXFixture> outerSpokeLeds, List<LXFixture> ring1Leds,
      LXFixture unusedLeds, // LEDs in LX buffer we need to fill out fadecandy ports
      boolean hasGui
  ) {
    super(
        Stream.concat(
            Stream.concat(innerSpokeLeds.stream(), outerSpokeLeds.stream()),
            Stream.concat(ring1Leds.stream(), Arrays.asList(unusedLeds).stream())
        ).toArray(LXFixture[]::new),
        hasGui
    );
    this.innerSpokeLeds = innerSpokeLeds;
    this.outerSpokeLeds = outerSpokeLeds;
    this.ring1Leds = ring1Leds;
  }

  @Override
  public void addPatternsAndGo(LX lx, PApplet p, IcosaFFT icosaFft) {
    LXPattern perlinNoise = new PerlinNoisePattern(lx, p, icosaFft);
    lx.setPatterns(new LXPattern[] {
        perlinNoise,
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
    });
    lx.goPattern(perlinNoise);
  }

  public void applyPresets(PerlinNoisePattern perlinNoise) {
    perlinNoise.hueXForm.setValue(0.02);
    perlinNoise.hueSpeed.setValue(0.015);
  }
}