package com.github.dlopuch.icosastar.lx.model;

import com.github.dlopuch.icosastar.lx.patterns.*;
import com.github.dlopuch.icosastar.lx.utils.DeferredLxOutputProvider;
import com.github.dlopuch.icosastar.lx.utils.RaspiGpio;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.transform.LXVector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IcosastarLXModel extends AbstractIcosaLXModel {
  public final List<LXFixture> innerSpokeLeds;
  public final List<LXFixture> outerSpokeLeds;
  public final List<LXFixture> ring1Leds;

  public static final float a = 50;
  public static final float RING_R = a * (float)Math.cos(Math.toRadians(26.57));
  public static final float RING_H = a * (float)Math.sin(Math.toRadians(26.57));

  public static final int RING1_R = 150;
  public static final int RING2_R = (int)((float)RING1_R * 0.9);// 230;

  public static final int NUM_LEDS_PER_SPOKE = 6;

  // number of vertexes in one ring
  public static final int NUM_POINTS = 5;

  private static final float POINT_OFFSET_RAD = (float)(2 * Math.PI / NUM_POINTS);

  public static IcosastarLXModel makeModel(boolean hasGui, DeferredLxOutputProvider outputProvider) {
    // If not gui, then Bike is running on raspi.  Get GPIO inputs.
    if (!hasGui) {
      RaspiGpio.init(outputProvider);
    }

    LXVector[] ring1Vs = new LXVector[NUM_POINTS];
    LXVector[] ring2Vs = new LXVector[NUM_POINTS];
    LXVector center = new LXVector(0, 0, 0);

    List<LXFixture> innerSpokeLeds  = new ArrayList<>();
    List<LXFixture> outerSpokeLeds = new ArrayList<>();
    List<LXFixture> ring1Leds = new ArrayList<>();

    LXVector r1 = new LXVector(RING_R, 0, a - RING_H).rotate(POINT_OFFSET_RAD/2);
    LXVector r2 = new LXVector(RING_R, 0, a + RING_H);

    for (int i=0; i<NUM_POINTS; i++) {
      ring1Vs[i] = r1.copy();
      ring2Vs[i] = r2.copy();

      r1.rotate(POINT_OFFSET_RAD);
      r2.rotate(POINT_OFFSET_RAD);
    }

    // Port 0: Segments lining top piece
    // --------------------

    addLedSegment(innerSpokeLeds, ring1Vs[2], center);
    addLedSegment(innerSpokeLeds, center, ring1Vs[3]);
    addLedSegment(ring1Leds, ring1Vs[3], ring1Vs[2]);
    addLedSegment(ring1Leds, ring1Vs[2], ring1Vs[1]);
    addLedSegment(innerSpokeLeds, ring1Vs[1], center);
    addLedSegment(innerSpokeLeds, center, ring1Vs[0]);
    // zero is furthest away from me

    addLedSegment(ring1Leds, ring1Vs[0], ring1Vs[4]);
    addLedSegment(innerSpokeLeds, ring1Vs[4], center);
    addLedSegment(ring1Leds, ring1Vs[3], ring1Vs[4]);
    addLedSegment(ring1Leds, ring1Vs[0], ring1Vs[1]);


    // That's 60 LED's.  Create 4 dummy ones to fill up the fadecandy port of 64 in the lx buffer.
    Fixture unusedLeds = new Fixture();
    unusedLeds.addPoint(new LXPoint(0, 0));
    unusedLeds.addPoint(new LXPoint(0, 0));
    unusedLeds.addPoint(new LXPoint(0, 0));
    unusedLeds.addPoint(new LXPoint(0, 0));


    // Fadecandy port 1: Segments lining equator triangles
    addLedSegment(outerSpokeLeds, ring2Vs[0], ring1Vs[0]);
    addLedSegment(outerSpokeLeds, ring1Vs[0], ring2Vs[1]);

    addLedSegment(outerSpokeLeds, ring2Vs[1], ring1Vs[1]);
    addLedSegment(outerSpokeLeds, ring1Vs[1], ring2Vs[2]);

    addLedSegment(outerSpokeLeds, ring2Vs[2], ring1Vs[2]);
    addLedSegment(outerSpokeLeds, ring1Vs[2], ring2Vs[3]);

    addLedSegment(outerSpokeLeds, ring2Vs[3], ring1Vs[3]);
    addLedSegment(outerSpokeLeds, ring1Vs[3], ring2Vs[4]);

    addLedSegment(outerSpokeLeds, ring2Vs[4], ring1Vs[4]);
    addLedSegment(outerSpokeLeds, ring1Vs[4], ring2Vs[0]);

    return new IcosastarLXModel(innerSpokeLeds, outerSpokeLeds, ring1Leds, unusedLeds, hasGui);
  }

  private static void addLedSegment(List<LXFixture> toWhere, LXVector start, LXVector end) {
    toWhere.add(new VectorStripModel(start, end, NUM_LEDS_PER_SPOKE));
  }

  private static class Fixture extends LXAbstractFixture {

  }

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
    System.out.println("Starting up with icosastar model");

    this.innerSpokeLeds = innerSpokeLeds;
    this.outerSpokeLeds = outerSpokeLeds;
    this.ring1Leds = ring1Leds;
  }

  @Override
  public void addPatternsAndGo(LX lx, PApplet p, IcosaFFT icosaFft) {
    LXPattern perlinNoise = new PerlinNoisePattern(lx, p, icosaFft);
    LXPattern whiteSparkleWipe = new WhiteSparkleWipe(lx, p);
    lx.setPatterns(new LXPattern[] {
        perlinNoise,
        whiteSparkleWipe,
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
    });
    lx.goPattern(perlinNoise);
    //lx.goPattern(whiteSparkleWipe);
  }

  public void applyPresets(PerlinNoisePattern perlinNoise) {
    RaspiPerlinNoiseDefaults.applyPresetsIfRaspiGpio(perlinNoise);
//
//    perlinNoise.hueXForm.setValue(0.02);
//    perlinNoise.hueSpeed.setValue(0.015);
  }
}