package com.github.dlopuch.icosastar.mappings;

import com.github.dlopuch.icosastar.ColorDot;
import com.github.dlopuch.icosastar.IcosaVertex;
import com.github.dlopuch.icosastar.effects.*;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.vendor.OPC;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * *cats LED cloud
 */
public class CloudMapping extends LedMapping {
  int PX_PER_RADIAL = 64;
  int NUM_RADIALS = 16;

  // bass are so close they overlap
  int NUM_BASS = 1;

  // Theta between radials
  float RADIAL_OFFSET_THETA = 2 * PApplet.PI / NUM_RADIALS;


  private ColorDot dot;
  private IcosaFFT fft;

  IcosaVertex kicks[] = new IcosaVertex[NUM_BASS];
  IcosaVertex snares[] = new IcosaVertex[NUM_RADIALS];
  IcosaVertex hihats[] = new IcosaVertex[NUM_RADIALS];

  List<List<PVector>> radials = new ArrayList<>(NUM_RADIALS);
  List<PVector> allLeds = new ArrayList<>();


  public CloudMapping(PApplet parent, OPC opc, ColorDot dot, IcosaFFT fft) {
    super(parent, opc);

    this.dot = dot;
    this.fft = fft;

    float stripLengthPx = p.width*4/9;

    PVector center = new PVector(0, 0);
    PVector radialCenter = new PVector(p.width/4,0);
    PVector bassCenter = new PVector(0,0);
    PVector hihatCenter = new PVector(p.width*4/9 + 30, 0);

    float theta = 0;
    int ledI = 0;
    for (int i = 0; i < NUM_RADIALS; i++) {

      List<PVector> radialLeds = new ArrayList<>();
      opc.ledStrip(
          ledI,
          PX_PER_RADIAL,
          (float)(p.width/2.0 + radialCenter.x),
          (float)(p.height/2.0 + radialCenter.y),
          stripLengthPx / PX_PER_RADIAL,
          theta,
          false,
          allLeds
      );

      PVector bass = PVector.add(center, bassCenter);
      if (i < NUM_BASS) {
        kicks[i] = new IcosaVertex(new float[] {bass.x, bass.y});
      }

      PVector snare = PVector.add(center, radialCenter);
      snares[i] = new IcosaVertex(new float[] {snare.x, snare.y});

      PVector hihat = PVector.add(center, hihatCenter);
      hihats[i] = new IcosaVertex(new float[] {hihat.x, hihat.y});

      radials.add(Arrays.asList(center, hihats[i].toPVector()));

      ledI += PX_PER_RADIAL;

      theta += RADIAL_OFFSET_THETA;

      radialCenter.rotate(RADIAL_OFFSET_THETA);
      bassCenter.rotate(RADIAL_OFFSET_THETA);
      hihatCenter.rotate(RADIAL_OFFSET_THETA);
    }
  }

  @Override
  public void draw() {
    // no-op
  }

  @Override
  public VertexFFT makeVertexFFT() {
    return new VertexFFT(this.p, dot, fft.beat,
        Arrays.asList(this.kicks),
        Arrays.asList(this.snares),
        Arrays.asList(this.hihats)
    );
  }

  @Override
  public FFTSpiral makeFFTSpiral() {
    return new FFTSpiral(this.p, dot.dot, fft, p.loadImage("data/fftColors.png"));
  }

  @Override
  public BassBlinders makeBassBlinders() {
    return new BassBlinders(this.p, dot, fft, this.kicks);
  }

  @Override
  public HihatSparkles makeHihatSparkles() {
    return new HihatSparkles(this.p, dot, fft,
        Arrays.asList(this.hihats).stream().map(IcosaVertex::toPVector).collect(Collectors.toList())
    );
  }

  @Override
  public RadialStream makeRadialStream() {
    return new RadialStream(p, dot, fft, radials);
  }

  @Override
  public PerlinNoise makePerlinNoiseField() {
    return new PerlinNoise(p, fft,
        allLeds.stream().map(led -> PVector.add(led, new PVector(-p.width/2, -p.height/2))).collect(Collectors.toList())
    );
  }
}
