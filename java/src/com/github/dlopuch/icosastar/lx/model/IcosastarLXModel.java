package com.github.dlopuch.icosastar.lx.model;

import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IcosastarLXModel extends LXModel {
  public final List<LXFixture> innerSpokeLeds;
  public final List<LXFixture> outerSpokeLeds;
  public final List<LXFixture> ring1Leds;

  protected IcosastarLXModel(
      List<LXFixture> innerSpokeLeds, List<LXFixture> outerSpokeLeds, List<LXFixture> ring1Leds,
      LXFixture unusedLeds // LEDs in LX buffer we need to fill out fadecandy ports
  ) {
    super(
        Stream.concat(
            Stream.concat(innerSpokeLeds.stream(), outerSpokeLeds.stream()),
            Stream.concat(ring1Leds.stream(), Arrays.asList(unusedLeds).stream())
        ).toArray(LXFixture[]::new)
    );
    this.innerSpokeLeds = innerSpokeLeds;
    this.outerSpokeLeds = outerSpokeLeds;
    this.ring1Leds = ring1Leds;

  }
}