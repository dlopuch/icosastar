package com.github.dlopuch.icosastar.effects;

import com.github.dlopuch.icosastar.ColorDot;
import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dlopuch on 8/11/16.
 */
public class RadialStream implements Drawable {
  private final int PERIOD_MS = 2000;

  private final PApplet p;
  private final List<List<PVector>> radials;
  private final ColorDot dot;
  private final BeatDetect beat;

  private float lastDistance = -1;

  private List<Particle> particles = new LinkedList();

  public RadialStream(PApplet p, ColorDot dot, IcosaFFT fft, List<List<PVector>> radials) {
    this.p = p;
    this.dot = dot;
    this.radials = radials;
    this.beat = fft.beat;
  }

  public void draw() {
//    if (p.millis() % 500 < 10) {
//      particles.add(new Particle());
//    }

    if (beat.isKick()) {
      particles.add(new Particle());
      particles.stream().forEach(particle -> particle.value = 1);
    }

    particles.forEach(Particle::draw);

    particles = particles.stream().filter(p -> p.isAlive).collect(Collectors.toList());
  }


  // ------------------------

  private class Particle {
    float distancePerMilli = 0.001f;
    float valueDecayPerMilli = 0.002f;

    int birth;
    int lastDraw;
    List<PVector> radial;

    float distance;
    float velocity;
    boolean isAlive;

    float value;

    protected Particle() {
      this.reset();
    }

    public void reset() {
      isAlive = true;
      birth = lastDraw = p.millis();
      radial = radials.get( (int)(Math.random() * radials.size()) );
      distance = 0;

      // HSV value, 0-1
      value = 1;

      velocity = 0;
    }

    public void draw() {
      distance = (p.millis() - birth) * distancePerMilli;

      int numMillisElapsed = p.millis() - lastDraw;
      value -= numMillisElapsed * valueDecayPerMilli;


      if (distance >= 1) {
        isAlive = false;
        return;
      }

      float tween = distance * (radial.size() - 1);

      int startI = (int)tween;

      PVector start = radial.get(startI);
      PVector end = radial.get(startI + 1);
      float amount = tween % 1;

      PVector where = new PVector(
          p.lerp(start.x, end.x, amount),
          p.lerp(start.y, end.y, amount)
      );

      dot.draw(where.x, where.y, 0, 0, 100*value, 80, 0);

      lastDraw = p.millis();
    }
  }
}
