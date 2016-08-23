package com.github.dlopuch.icosastar.lx.model;

import heronarts.lx.model.LXFixture;
import heronarts.lx.transform.LXVector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CloudLXModelBuilder {

  public static final int NUM_FADECANDIES = 4;

  /** Length of radials, in lx display units */
  private static final float ALPHA = 100;

  public static final int RADIALS_PER_FADECANDY = 8;
  private static final int PX_PER_RADIAL = 64;

  private static final int NUM_RADIALS = NUM_FADECANDIES * RADIALS_PER_FADECANDY; // 16 for half-cloud, 32 for full-cloud


  // Theta between radials
  private static final float RADIAL_OFFSET_THETA = 2f * (float)Math.PI / (float)NUM_RADIALS;

  public static CloudLXModel makeModel(boolean hasGui) {
    List<List<LXFixture>> fadecandyFixtures = new ArrayList<>(NUM_FADECANDIES);

    LXVector center = new LXVector(0, 0, 0);
    LXVector radial = new LXVector(ALPHA, 0, 0);

    for (int fc=0; fc < NUM_FADECANDIES; fc++) {
      List<LXFixture> radials = new ArrayList<>(RADIALS_PER_FADECANDY);
      fadecandyFixtures.add(radials);

      for (int radialI=0; radialI < RADIALS_PER_FADECANDY; radialI++) {
        radials.add(new VectorStripModel(center, radial, PX_PER_RADIAL));
        radial.rotate(RADIAL_OFFSET_THETA);
      }
    }

    List<LXFixture> allFixtures = new LinkedList<>();
    fadecandyFixtures.forEach(allFixtures::addAll);

    return new CloudLXModel(allFixtures, fadecandyFixtures, hasGui);
  }
}
