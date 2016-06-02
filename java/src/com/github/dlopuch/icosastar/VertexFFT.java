package com.github.dlopuch.icosastar;

import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class VertexFFT {
  private static float MIN_SIZE_KICK_PX = 30;
  private static float MAX_SIZE_KICK_PX = 150;

  private static float MIN_SIZE_SNARE_PX = 30;
  private static float MAX_SIZE_SNARE_PX = 150;

  private static float MIN_SIZE_HIHAT_PX = 30;
  private static float MAX_SIZE_HIHAT_PX = 150;

  private static float GROW_MULT = 2.0f;
  private static float DECAY_MULT = 0.96f;

  private static float DECAY_HIHAT_MULT = 0.90f;

  PApplet p;
  ColorDot colorDot;

  List<IcosaVertex> kicks;
  List<IcosaVertex> snares;
  List<IcosaVertex> hihats;

  float rKick  = MIN_SIZE_KICK_PX;
  float rSnare = MIN_SIZE_SNARE_PX;
  float rHihat = MIN_SIZE_HIHAT_PX;

  BeatDetect beat;

  VertexFFT(
      PApplet parent,
      ColorDot colorDot,
      BeatDetect beat,
      List<IcosaVertex> kicks,
      List<IcosaVertex> snares,
      List<IcosaVertex> hihats
  ) {
    this.p = parent;
    this.colorDot = colorDot;
    this.kicks = new ArrayList(kicks);
    this.snares = new ArrayList(snares);
    this.hihats = new ArrayList(hihats);

    this.beat = beat;

    parent.registerDraw(this);
  }

  public void draw() {
    // Kick drum
    // --------------
    if (beat.isKick()) {
      this.rKick = MAX_SIZE_KICK_PX; // min(rKick * GROW_MULT, MAX_SIZE_KICK_PX);
    }

    for (IcosaVertex v : this.kicks) {
      colorDot.draw(
          v.x, v.y,
          0, 0, 100,
          this.rKick, 100
      );
    }

    // Snare drum
    // --------------
    if (beat.isSnare()) {
      this.rSnare = MAX_SIZE_SNARE_PX; // min(rKick * GROW_MULT, MAX_SIZE_KICK_PX);
    }

    for (IcosaVertex v : this.snares) {
      colorDot.draw(
          v.x, v.y,
          p.millis() / 10 % 100, 100, 100,
          this.rSnare, 100
      );
    }

    // Hihat drum
    // --------------
    if (beat.isHat()) {
      this.rHihat = p.min(rHihat * GROW_MULT, MAX_SIZE_HIHAT_PX);
    }

    for (IcosaVertex v : this.hihats) {
      colorDot.draw(
          v.x, v.y,
          0, 0, 100,
          this.rHihat, 100
      );
    }

    // Decays
    // ---------
    this.rKick  = p.max(MIN_SIZE_KICK_PX , this.rKick  * DECAY_MULT);
    this.rSnare = p.max(MIN_SIZE_SNARE_PX, this.rSnare * DECAY_MULT);
    this.rHihat = p.max(DECAY_HIHAT_MULT, this.rHihat * DECAY_MULT);
  }
}