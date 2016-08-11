package com.github.dlopuch.icosastar.signal;

import ddf.minim.AudioBuffer;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;

/**
 * Mock BeatDetect wrapper that doesn't do any actual processing.
 */
public class MockBeatDetect extends BeatDetect {
  PApplet p;

  public MockBeatDetect(PApplet parent) {
    this.p = parent;
  }

  @Override
  public void detect(AudioBuffer buffer) {
    // replace with no-op
  }

  public boolean isKick() {
    return this.p.millis() % 1000 < 100;
  }

  public boolean isSnare() {
    return this.p.millis() % 500 < 50;
  }

  public boolean isHat() {
    return this.p.millis() % 100 < 10;
  }
}
