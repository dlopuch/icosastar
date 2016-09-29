package com.github.dlopuch.icosastar.lx.model;

import com.github.dlopuch.icosastar.lx.patterns.LedSelectorPattern;
import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.transform.LXVector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fibonocci Petals LED poster
 */
public class FibonocciPetalsModel extends AbstractIcosaLXModel {
  private static final int NUM_LEDS_PER_STRIP = 64; // fadecandy port

  // Coordinate system scale factor to get approximately in sync with other models
  private static final float ALPHA = 10;

  public static class PetalPoint {
    public final LXVector point;

    private LXFixture cwSide;
    private LXFixture ccwSide;

    private PetalPoint(double thetaRad, int numRotations) {
      // Set magnitude of vector based on numRotations
      point = new LXVector(ALPHA * numRotations, 0, 0);

      // and add rotation
      point.rotate((float)thetaRad);
    }

    public LXFixture getCwSide() {
      return cwSide;
    }

    public LXFixture getCcwSide() {
      return ccwSide;
    }
  }

  // Processing's bezierPoint() function
  private static float bezierPoint(float a, float b, float c, float d, float t) {
    float t1 = 1.0F - t;
    return a * t1 * t1 * t1 + 3.0F * b * t * t1 * t1 + 3.0F * c * t * t * t1 + d * t * t * t;
  }

  private static PetalPoint[] petals = {
      // Fibonocci petals are derived by thetaInc = 2*PI * 1/PHI and continuing to spin that around
      // (ie first petal is 0*thetaInc, then 1*thetaInc, then 2*thetaInc, etc., all mod 2PI)
      // Here we've done that but thrown away everything that's >PI (ie using only "top half" of circle)
      new PetalPoint(0.000000000, 0) , // 0
      new PetalPoint(2.399892449, 0) , // 1
      new PetalPoint(0.916677346, 1) , // 2
      new PetalPoint(1.833354692, 2) , // 3
      new PetalPoint(0.350139589, 3) , // 4
      new PetalPoint(2.750032038, 3) , // 5
      new PetalPoint(1.266816936, 4) , // 6
      new PetalPoint(2.183494282, 5) , // 7
      new PetalPoint(0.700279179, 6) , // 8
      new PetalPoint(3.100171628, 6) , // 9
      new PetalPoint(1.616956525, 7) , // 10
      new PetalPoint(0.133741422, 8) , // 11
      new PetalPoint(2.533633871, 8) , // 12
      new PetalPoint(1.050418768, 9) , // 13
      new PetalPoint(1.967096114, 10), // 14
      new PetalPoint(0.483881012, 11), // 15
      new PetalPoint(2.883773461, 11), // 16
      new PetalPoint(1.400558358, 12), // 17
      new PetalPoint(2.317235704, 13), // 18
      new PetalPoint(0.834020601, 14), // 19
      new PetalPoint(1.750697947, 15), // 20
      new PetalPoint(0.267482845, 16), // 21
      new PetalPoint(2.667375293, 16), // 22
      new PetalPoint(1.184160191, 17), // 23
      new PetalPoint(2.100837537, 18)  // 24
  };

  private static LXVector origin = new LXVector(0,0,0);

  private static void addPetal(List<LXFixture> allFixtures, PetalPoint petal, int numLedsThere, int numLedsBack) {
    addPetal(allFixtures, petal, numLedsThere, numLedsBack, true);
  }
  private static void addPetal(List<LXFixture> allFixtures, PetalPoint petal, int numLedsThere, int numLedsBack,
                               boolean stripGoesCw) {
    LXVector tip = petal.point;
    LXVector originControl = origin.copy().lerp(tip, 0.4f);
    LXVector tipControlThere = tip.copy()
        .rotate((stripGoesCw ? 1 : -1) * (float)Math.PI * 1.2f/2f)
        .setMag(tip.mag() * 0.3f);
    LXVector tipControlBack = tip.copy()
        .rotate((stripGoesCw ? -1 : 1) * (float)Math.PI * 1.2f/2f)
        .setMag(tip.mag() * 0.3f);

    List<LXPoint> therePts = new ArrayList<>(numLedsThere);
    for (int i=0; i<numLedsThere; i++) {
      therePts.add(new LXPoint(
          bezierPoint(origin.x, originControl.x, tip.x + tipControlThere.x, tip.x, (float)i / ((float)numLedsThere)),
          bezierPoint(origin.y, originControl.y, tip.y + tipControlThere.y, tip.y, (float)i / ((float)numLedsThere))
      ));
    }

    List<LXPoint> backPts = new ArrayList<>(numLedsThere);
    for (int i=0; i<numLedsThere; i++) {
      therePts.add(new LXPoint(
          bezierPoint(tip.x, tip.x + tipControlBack.x, originControl.x, origin.x, (float)i / ((float)numLedsThere)),
          bezierPoint(tip.y, tip.y + tipControlBack.y, originControl.y, origin.y, (float)i / ((float)numLedsThere))
      ));
    }

    LXFixture there = () -> therePts;
    LXFixture back = () -> backPts;

    petal.cwSide  = stripGoesCw ? there : back;
    petal.ccwSide = stripGoesCw ? back  : there;

    allFixtures.add(there);
    allFixtures.add(back);
  }

  private static void addTail(List<LXFixture> allFixtures, List<LXFixture> tails, int numLeds) {
    LXFixture tail = new VectorStripModel(
        origin,
        new LXVector(0, (float)numLeds * -ALPHA/3f, 0),
        numLeds
    );

    allFixtures.add(tail);
    tails.add(tail);
  }


  public static FibonocciPetalsModel makeModel(boolean hasGui) {
    List<LXFixture> allFixtures = new ArrayList<>();

    List<LXFixture> tails = new ArrayList<>();

    // Now we add petals in order of LED wiring

    // FC port 0 (petals 0-5)
    addPetal(allFixtures, petals[0], 4, 4);
    addPetal(allFixtures, petals[2], 5, 4);
    addPetal(allFixtures, petals[3], 6, 6);
    addPetal(allFixtures, petals[1], 4, 3);
    addPetal(allFixtures, petals[5], 7, 7);
    addPetal(allFixtures, petals[4], 8, 6);

    // FC port 1
    addPetal(allFixtures, petals[6], 9, 7);
    addPetal(allFixtures, petals[7], 9, 9);
    addPetal(allFixtures, petals[9], 10, 12);
    addTail(allFixtures, tails, 8);

    // FC port 2
    addPetal(allFixtures, petals[12], 13, 13);
    addPetal(allFixtures, petals[10], 11, 13);
    addTail(allFixtures, tails, 15);

    // FC port 3
    addPetal(allFixtures, petals[11], 13, 13);
    addPetal(allFixtures, petals[8], 10, 12 );
    addTail(allFixtures, tails, 16);

    // FC port 4
    addPetal(allFixtures, petals[13], 14, 14);
    addPetal(allFixtures, petals[17], 18, 18); // TODO: last four on strip broken

    // FC port 5
    addPetal(allFixtures, petals[15], 17, 14);
    addPetal(allFixtures, petals[19], 17, 16);

    // FC port 6
    addPetal(allFixtures, petals[14], 16, 14);
    addPetal(allFixtures, petals[18], 17, 17);

    // FC port 7
    addPetal(allFixtures, petals[16], 16, 16);
    addTail(allFixtures, tails, 32);

    return new FibonocciPetalsModel(allFixtures, hasGui);
  }

  @Override
  public void addPatternsAndGo(LX lx, PApplet p, IcosaFFT icosaFft) {
    LXPattern perlinNoise = new PerlinNoisePattern(lx, p, icosaFft);

    List<LXPattern> patterns = new ArrayList<>(Arrays.asList(
        perlinNoise,
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx)
    ));

    if (hasGui) {
      patterns.add(new LedSelectorPattern(lx));
    }

    lx.setPatterns(patterns.toArray(new LXPattern[patterns.size()]));
    lx.goPattern(perlinNoise);
  }

  private FibonocciPetalsModel(List<LXFixture> allFixtures, boolean hasGui) {
    super(allFixtures.toArray(new LXFixture[allFixtures.size()]), hasGui);

    System.out.println("Starting up with fibonocci petals model");
  }

  @Override
  public void applyPresets(PerlinNoisePattern perlinNoise) {
    // no-op
  }
}
