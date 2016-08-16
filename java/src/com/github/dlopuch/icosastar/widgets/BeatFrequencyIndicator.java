package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.effects.utils.EventFrequencyCounter;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;

/**
 * Created by dlopuch on 8/12/16.
 */
public class BeatFrequencyIndicator implements Drawable {
  PApplet p;
  BeatDetect beatDetect;

  EventFrequencyCounter kickFreq;
  EventFrequencyCounter snareFreq;
  EventFrequencyCounter hatFreq;

  public BeatFrequencyIndicator(PApplet p, IcosaFFT fft, Long numMsToTrack) {
    this.p = p;
    this.beatDetect = fft.beat;

    kickFreq = new EventFrequencyCounter(numMsToTrack);
    snareFreq = new EventFrequencyCounter(numMsToTrack);
    hatFreq = new EventFrequencyCounter(numMsToTrack);
  }

  public void draw() {
    if (beatDetect.isKick()) {
      kickFreq.tick();
    }

    if (beatDetect.isSnare()) {
      snareFreq.tick();
    }

    if (beatDetect.isHat()) {
      hatFreq.tick();
    }

    p.fill(255, 255, 255);
    p.text("beat detects / sec:"                     , p.width/2 - 120, p.height/2 - 80);
    p.text("Kick: "  + kickFreq.getFrequencyPerSec() , p.width/2 - 120, p.height/2 - 60);
    p.text("Snare: " + snareFreq.getFrequencyPerSec(), p.width/2 - 120, p.height/2 - 40);
    p.text("Hat: "   + hatFreq.getFrequencyPerSec()  , p.width/2 - 120, p.height/2 - 20);
  }
}
