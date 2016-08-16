package com.github.dlopuch.icosastar.effects;

import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.effects.perlin_noise.PerlinNoiseExplorer;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps a list of PVectors to a perlin noise field
 */
public class PerlinNoise implements Drawable {
  private PApplet p;
  private BeatDetect beat;

  private List<PVector> leds;

  private PerlinNoiseExplorer hueNoise;
  private float hueOffset = 0;
  private float hueInc = 0.0005f;
  private float hueSpeed = 0.02f;

  private PerlinNoiseExplorer brightnessBoostNoise;
  private float brightnessBoostT = 0;
  private float brightnessBoostDecay = 0.97f;

  public PerlinNoise(PApplet p, IcosaFFT fft, List<PVector> leds) {
    this.p = p;
    this.beat = fft.beat;

    // bring LED's down into noise space
    this.leds = leds;
    this.hueNoise = new PerlinNoiseExplorer(p, leds).setSpeed(hueSpeed);

    this.brightnessBoostNoise = new PerlinNoiseExplorer(p, leds).setSpeed(hueSpeed * 2);
  }


  private void drawLedSquare(PVector led, float h, float s, float v) {
    p.fill(h, s, v);
    p.rect(led.x - 1, led.y - 1, 3, 3);
  }

  public void draw() {
    p.pushStyle();

    p.noStroke();
    p.colorMode(PConstants.HSB, 100);

    boolean isBrightnessBoost = beat.isKick();
    if (isBrightnessBoost) {
      brightnessBoostT = 1.0f;
    } else if (brightnessBoostT > 0.05) {
      brightnessBoostT *= brightnessBoostDecay;
    }

    for (int i=0; i<leds.size(); i++) {
      PVector led = leds.get(i);
      drawLedSquare(
          led,
          Math.abs(hueOffset + hueNoise.getNoise(i)) % 1 * 100,
          100,
          30 + (brightnessBoostT > 0.05 ? brightnessBoostT * 60 * brightnessBoostNoise.getNoise(i) : 0)
      );
    }

    if (beat.isSnare() && beat.isKick()) {
      hueOffset += p.random(-0.5f, 0.5f);
    }

    // EXPERIMENT: Randomize direction on some sort of beat
    // Result: Eh, not terrible, but can be disorienting a bit
//    if (beat.isSnare()) {
//      hueNoise.randomizeDirection();
//    }


    // EXPERIMENT: Pulse speed on kick
    // Result: meh.  Looks more like it stutters
//    if (beat.isKick()) {
//      hueSpeed = 0.025f;
//      hueNoise.setSpeed(hueSpeed);
//    } else if (hueSpeed > 0.01) {
//      hueSpeed -= 0.001f;
//      hueNoise.setSpeed(hueSpeed);
//    }

    hueNoise.step();
    hueOffset += hueInc;

    p.popStyle();
  }
}
