package com.github.dlopuch.icosastar.effects.perlin_noise;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that moves a list of vectors through Perlin noise space
 */
public class PerlinNoiseExplorer {
  private PApplet p;

  private final float ZOOM_MULTIPLIER = 5;

  private List<PVector> origFeatures;
  private List<PVector> features;
  private List<PVector> zoomedFeatures;
  private float zoomT = 0f; // 1.0 means zoomed in

  /** Vector multiplier to transform features down into noise space -- smaller makes larger perlin features */
  private float noiseXform = 0.01f;

  /** Center of the noise space where LEDs drawn from */
  private final PVector noiseOrigin = new PVector(0, 0);

  private float noiseStepMagnitude = 0.02f;


  // --------------------
  // Configurables:

  /** Direction we step through the perlin noise space */
  public PVector noiseStep;


  public PerlinNoiseExplorer(PApplet p, List<PVector> features) {
    this.p = p;
    this.origFeatures = new ArrayList<>(features);
    this.features = new ArrayList<>(
        features.stream().map(feature -> PVector.mult(feature, noiseXform)).collect(Collectors.toList())
    ); // force ArrayList for quick lookups (what does stream Collectors give?)

    this.zoomedFeatures = new ArrayList<>(
        features.stream().map(feature -> PVector.mult(feature, noiseXform/ZOOM_MULTIPLIER)).collect(Collectors.toList())
    );

    randomizeDirection();
  }

  public PerlinNoiseExplorer randomizeDirection() {
    this.noiseStep = PVector.random2D().mult(this.noiseStepMagnitude);
    //this.noiseStep = new PVector(this.noiseStepMagnitude, 0);
    return this;
  }

  /**
   * Sets the speed of noise explorer through the noise space
   * @param newNoiseStepMagnitude
   * @return
   */
  public PerlinNoiseExplorer setSpeed(float newNoiseStepMagnitude) {
    this.noiseStepMagnitude = newNoiseStepMagnitude;
    this.noiseStep.setMag(newNoiseStepMagnitude);
    return this;
  }

  /**
   * Gets the noise space value for the i'th feature
   * @param i Which feature vector to map into noise
   * @return The perlin noise for that vector
   */
  public float getNoise(int i) {
    PVector noiseVect = PVector.add(noiseOrigin, features.get(i));

    if (zoomT > 0) {
      noiseVect = PVector.lerp(noiseVect, PVector.add(noiseOrigin, zoomedFeatures.get(i)), zoomT);
    }

    return p.noise(noiseVect.x, noiseVect.y);
  }

  /**
   * Move through the noise field according to step vector
   * @return
   */
  public PerlinNoiseExplorer step() {
    noiseOrigin.add(noiseStep);

    // Decay zoom if it exists
    if (zoomT > 0) {
      zoomT -= 0.01;
    }

    return this;
  }

  /**
   * "Zoom in" on the noise field.  The zoom decays back towards normal after each step.
   * @return
   */
  public PerlinNoiseExplorer pullZoom() {
    zoomT += 0.2f;
    PApplet.println("zoom:" + zoomT);
    if (zoomT > 1) {
      zoomT = 1;
      PApplet.println("zoom maxed!");
    }
    return this;
  }
}
