package com.github.dlopuch.icosastar.lx.model;

import heronarts.lx.model.*;
import heronarts.lx.transform.LXVector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IcosastarLXModelBuilder {
  public static final float a = 50;
  public static final float RING_R = a * (float)Math.cos(Math.toRadians(26.57));
  public static final float RING_H = a * (float)Math.sin(Math.toRadians(26.57));

  public static final int RING1_R = 150;
  public static final int RING2_R = (int)((float)RING1_R * 0.9);// 230;

  public static final int NUM_LEDS_PER_SPOKE = 6;

  // number of vertexes in one ring
  public static final int NUM_POINTS = 5;

  private static final float POINT_OFFSET_RAD = (float)(2 * Math.PI / NUM_POINTS);

  public static IcosastarLXModel makeModel() {

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

    return new IcosastarLXModel(innerSpokeLeds, outerSpokeLeds, ring1Leds, unusedLeds);
  }

  private static void addLedSegment(List<LXFixture> toWhere, LXVector start, LXVector end) {
    toWhere.add(new VectorStripModel(start, end, NUM_LEDS_PER_SPOKE));
  }

  private static class Fixture extends LXAbstractFixture {

  }
}
