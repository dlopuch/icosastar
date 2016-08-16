package com.github.dlopuch.icosastar.lx.model;

import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXVector;

/**
 * Model of a strip of points between two vectors
 */
public class VectorStripModel extends LXModel {
  public final LXVector start;
  public final LXVector end;

  public VectorStripModel(LXVector start, LXVector end, int numPoints) {
    super(new PaddedPointsFixture(start, end, numPoints));
    this.start = start;
    this.end = end;
  }

  /**
   * Fixture for a line of LEDs between two vectors.
   * Padded means the LED's won't start AT start, but rather will have a half spacing between start and first one.
   * ie if start is 0, end is 1, and numPoints is 4, will create 4 LEDs at 0.125, 0.375, 0.625, and 0.875
   */
  private static class PaddedPointsFixture extends LXAbstractFixture {
    private PaddedPointsFixture(LXVector start, LXVector end, int numPoints) {
      float lerpInc = 1f/(float)numPoints;
      float lerpI = lerpInc / 2;

      for (int i=0; i<numPoints; i++) {
        LXVector newPoint = start.copy().lerp(end, lerpI);
        addPoint(new LXPoint(newPoint.x, newPoint.y, newPoint.z));
        lerpI += lerpInc;
      }
    }
  }
}
