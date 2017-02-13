package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.LedSelectorPattern;
import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.lx.utils.DeferredLxOutputProvider;
import com.github.dlopuch.icosastar.lx.utils.RaspiGpio;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
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

public class RoadBikeModel extends AbstractIcosaLXModel {

  private static final int NUM_LEDS_PER_STRIP = 64; // fadecandy port

  // Coordinate system scale factor to get approximately in sync with other models
  private static final float ALPHA = 10;

  private static final float SEATPOST_LOOP_R = 0.5f * ALPHA;
  private static final float BRAKE_BLOB_R = 1.5f * ALPHA;
  private static final float HEADLIGHT_SPIRAL_R = 0.5f * ALPHA;

  // Bike geometry: Define anchor points / vertices
  // Strip 0:
  private static final LXVector seatpostLoopC    = new LXVector(    0,      1, 0).mult(ALPHA);
  private static final LXVector brakeBlobC       = new LXVector(-0.5f,     -1, 0).mult(ALPHA);
  private static final LXVector seatpostStripV   = new LXVector(    0,     -1, 0).mult(ALPHA);
  private static final LXVector pedals           = new LXVector(    3, -15.5f, 0).mult(ALPHA);
  private static final LXVector strip0End        = new LXVector(   13,    -10, 0).mult(ALPHA);

  // Strip 1:
  private static final LXVector seatpostStripH   = new LXVector(    1, -0.5f, 0).mult(ALPHA);
  private static final LXVector handlebarsTop    = new LXVector(20.5f,     1, 0).mult(ALPHA);
  private static final LXVector headlightSpiralC = handlebarsTop.add(HEADLIGHT_SPIRAL_R, 0, 0);
  private static final LXVector handlebarsBottom = new LXVector(20.5f, -1.5f, 0).mult(ALPHA);
  private static final LXVector strip1End        = new LXVector(12.5f,   -10, 0).mult(ALPHA);


  // Geometry LED counts
  // strip 0
  private static final int SEATPOST_LOOP_NUM_LEDS = 10;
  private static final int BRAKE_BLOB_NUM_LEDS = 12;
  private static final int LEN_SEATPOST_PEDALS = 44 - BRAKE_BLOB_NUM_LEDS - SEATPOST_LOOP_NUM_LEDS;
  private static final int LEN_PEDALS_HANDLEBARS_HALF = NUM_LEDS_PER_STRIP -
      LEN_SEATPOST_PEDALS - BRAKE_BLOB_NUM_LEDS - SEATPOST_LOOP_NUM_LEDS;

  // strip 1
  private static final int LEN_SEATPOST_HANDLEBARS = 30;
  private static final int HEADLIGHT_NUM_LEDS = 46 - LEN_SEATPOST_HANDLEBARS;
  private static final int LEN_HANDLEBARS_PEDALS_HALF = NUM_LEDS_PER_STRIP - HEADLIGHT_NUM_LEDS - LEN_SEATPOST_HANDLEBARS;


  // Class fixtures
  // Fixtures
  public final List<LXFixture> framePieces;

  public final LXFixture seatpostLoop;
  public final LXFixture brakeBlob;
  public final LXFixture headlightSpiral;


  public static RoadBikeModel makeModel(boolean hasGui, DeferredLxOutputProvider outputProvider) {
    // If not gui, then Bike is running on raspi.  Get GPIO inputs.
    if (!hasGui) {
      RaspiGpio.init(outputProvider);
    }

    List<LXFixture> allFixtures = new ArrayList<>();
    List<LXFixture> framePieces = new ArrayList<>();


    // Topmost seatpost loop: circle in Y-axis
    List<LXPoint> seatpostLoopPoints = new ArrayList<>(SEATPOST_LOOP_NUM_LEDS);
    float thetaInc = (float)Math.PI * 2 / SEATPOST_LOOP_NUM_LEDS;
    LXVector seatpostLoopRadial = new LXVector(SEATPOST_LOOP_R, 0, SEATPOST_LOOP_R); // will rotate in y axis
    for (int i=0; i<SEATPOST_LOOP_NUM_LEDS; i++) {
      LXVector led = seatpostLoopC.copy().add(seatpostLoopRadial);
      seatpostLoopPoints.add(new LXPoint(led.x, led.y, led.z));
      seatpostLoopRadial.rotate(thetaInc, 0, 1, 0);
    }
    LXFixture seatpostLoop = () -> seatpostLoopPoints; // getPoints() method implementation lambda.
    allFixtures.add(seatpostLoop);


    // Brake blob.  Model as another circle in the y-axis
    List<LXPoint> brakeBlobPoints = new ArrayList<>(BRAKE_BLOB_NUM_LEDS);
    thetaInc = (float)Math.PI * 2 / BRAKE_BLOB_NUM_LEDS;
    LXVector brakeBlobRadial = new LXVector(BRAKE_BLOB_R, 0, 0); // will rotate in y axis
    for (int i=0; i<BRAKE_BLOB_NUM_LEDS; i++) {
      LXVector led = brakeBlobC.copy().add(brakeBlobRadial);
      brakeBlobPoints.add(new LXPoint(led.x, led.y, led.z));
      brakeBlobRadial.rotate(thetaInc, 0, 1, 0);
    }
    LXFixture brakeBlob = () -> brakeBlobPoints; // new fixtures just define a getPoints() method.  Lambda that fucker.
    allFixtures.add(brakeBlob);


    LXFixture fixture = new VectorStripModel(seatpostStripV, pedals, LEN_SEATPOST_PEDALS);
    allFixtures.add(fixture);
    framePieces.add(fixture);

    fixture = new VectorStripModel(pedals, strip0End, LEN_PEDALS_HANDLEBARS_HALF);
    allFixtures.add(fixture);
    List<LXPoint> pedalsToHandlebarsHalf = fixture.getPoints().stream().collect(Collectors.toList());
    // Don't add to framePieces because we'll join this into one piece below


    fixture = new VectorStripModel(seatpostStripH, handlebarsTop, LEN_SEATPOST_HANDLEBARS);
    allFixtures.add(fixture);
    framePieces.add(fixture);


    // "Headlight" spiral: make two loops of LEDs spiraling from handlebarsTop to handlebarsBottom
    List<LXPoint> headlightSpiralPoints = new ArrayList<>(HEADLIGHT_NUM_LEDS);
    thetaInc = (float)Math.PI*4 / HEADLIGHT_NUM_LEDS; // Spiral is 2 loops, so 4*PI
    LXVector headlightSpiralRadial = new LXVector(-HEADLIGHT_SPIRAL_R, 0, 0); // will rotate in y axis
    float deltaY = (handlebarsBottom.y - handlebarsTop.y) / HEADLIGHT_NUM_LEDS;
    float y = headlightSpiralC.y;
    for (int i=0; i<HEADLIGHT_NUM_LEDS; i++) {
      LXVector led = headlightSpiralC.copy().add(headlightSpiralRadial);
      headlightSpiralPoints.add(new LXPoint(led.x, y, led.z));
      y += deltaY;
      headlightSpiralRadial.rotate(thetaInc, 0, 1, 0);
    }
    LXFixture headlightSpiral = () -> headlightSpiralPoints;
    allFixtures.add(headlightSpiral);


    fixture = new VectorStripModel(handlebarsBottom, strip1End, LEN_HANDLEBARS_PEDALS_HALF);
    allFixtures.add(fixture);
    List<LXPoint> handlebarsToPedalHalf = fixture.getPoints().stream().collect(Collectors.toList());
    // Don't add to framePieces because we'll join this into one piece below


    // Join the two pedals<-->handlebars halves into one fixture
    List<LXPoint> handlesbarsToPedalsSecondHalf = new ArrayList<>(pedalsToHandlebarsHalf);
    Collections.reverse(handlesbarsToPedalsSecondHalf);
    List<LXPoint> handlebarsToPedals = Stream.concat(
        handlebarsToPedalHalf.stream(),
        handlesbarsToPedalsSecondHalf.stream()
    ).collect(Collectors.toList());
    framePieces.add(() -> handlebarsToPedals); // lambda-ify new LXFixture()

    return new RoadBikeModel(allFixtures, framePieces, seatpostLoop, brakeBlob, headlightSpiral, hasGui);
  }

  private RoadBikeModel(List<LXFixture> allFixtures, List<LXFixture> framePieces,
                        LXFixture seatpostLoop, LXFixture brakeBlob, LXFixture headlightSpiral, boolean hasGui) {
    super(allFixtures.toArray(new LXFixture[allFixtures.size()]), hasGui);

    System.out.println("Starting up with bike model");

    this.framePieces = framePieces;
    this.seatpostLoop = seatpostLoop;
    this.brakeBlob = brakeBlob;
    this.headlightSpiral = headlightSpiral;
  }

  @Override
  public RoadBikeModel initLx(LX lx) {
    lx.engine.framesPerSecond.setValue(40); // higher on raspi and FFT seems to miss things.
    return this;
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

  @Override
  public void applyPresets(PerlinNoisePattern perlinNoise) {
    RaspiPerlinNoiseDefaults.applyPresetsIfRaspiGpio(perlinNoise);
  }

  @Override
  public float getMaxBrightness() {
    return 80; // battery pack cuts out above this
  }
}