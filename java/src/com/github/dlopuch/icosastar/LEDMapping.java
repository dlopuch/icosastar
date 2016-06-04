package com.github.dlopuch.icosastar;

import com.github.dlopuch.icosastar.vendor.OPC;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class LEDMapping {
  int RING1_R = 150;
  int RING2_R = 230;
  int NUM_POINTS = 5;
  float POINT_OFFSET_RAD = (float)(2 * Math.PI / NUM_POINTS);

  int PX_PER_SEGMENT = 6;

  private PApplet p;

  public IcosaVertex[] ring1Vs = new IcosaVertex[NUM_POINTS];
  public IcosaVertex[] ring2Vs = new IcosaVertex[NUM_POINTS];
  public IcosaVertex center;

  public List<PVector> innerSpokeLeds;
  public List<PVector> outerSpokeLeds;
  public List<PVector> ring1Leds;

  public List<IcosaVertex> verticies = new ArrayList<IcosaVertex>();

  OPC opc;

  private class QueuedLed {
    int i;
    int x;
    int y;

    QueuedLed(int i, int x, int y) {
      this.i = i;
      this.x = x;
      this.y = y;
    }
  }
  List<QueuedLed> queuedLeds = new ArrayList();

  LEDMapping() {

    // Initialize the rings
    float r2_theta = 0;
    float r1_theta = POINT_OFFSET_RAD / 2;
    for (int i=0; i<NUM_POINTS; i++) {
      ring1Vs[i] = new IcosaVertex( polar2cart(RING1_R, r1_theta) );
      ring2Vs[i] = new IcosaVertex( polar2cart(RING2_R, r2_theta) );

      verticies.add(ring1Vs[i]);
      verticies.add(ring2Vs[i]);

      r1_theta += POINT_OFFSET_RAD;
      r2_theta += POINT_OFFSET_RAD;
    }

    this.center = new IcosaVertex(new float[]{ 0.0f, 0.0f });
    verticies.add(this.center);

    innerSpokeLeds = new ArrayList<>();
    outerSpokeLeds = new ArrayList<>();
    ring1Leds = new ArrayList<>();


    // Fadecandy port 1: Segments lining equator triangles
    // -----------
    int ledI = 64;

    ledI = addLEDSegment(ledI, ring2Vs[0], ring1Vs[0], outerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[0], ring2Vs[1], outerSpokeLeds);

    ledI = addLEDSegment(ledI, ring2Vs[1], ring1Vs[1], outerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[1], ring2Vs[2], outerSpokeLeds);

    ledI = addLEDSegment(ledI, ring2Vs[2], ring1Vs[2], outerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[2], ring2Vs[3], outerSpokeLeds);

    ledI = addLEDSegment(ledI, ring2Vs[3], ring1Vs[3], outerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[3], ring2Vs[4], outerSpokeLeds);

    ledI = addLEDSegment(ledI, ring2Vs[4], ring1Vs[4], outerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[4], ring2Vs[0], outerSpokeLeds);

    // Fadecandy port 0: Segments lining top piece
    // -----------
    ledI = 0;

    ledI = addLEDSegment(ledI, ring1Vs[2], center, innerSpokeLeds);
    ledI = addLEDSegment(ledI, center, ring1Vs[3], innerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[3], ring1Vs[2], ring1Leds);
    ledI = addLEDSegment(ledI, ring1Vs[2], ring1Vs[1], ring1Leds);
    ledI = addLEDSegment(ledI, ring1Vs[1], center, innerSpokeLeds);
    ledI = addLEDSegment(ledI, center, ring1Vs[0], innerSpokeLeds);
    // zero is furthest away from me

    ledI = addLEDSegment(ledI, ring1Vs[0], ring1Vs[4], ring1Leds);
    ledI = addLEDSegment(ledI, ring1Vs[4], center, innerSpokeLeds);
    ledI = addLEDSegment(ledI, ring1Vs[3], ring1Vs[4], ring1Leds);
    ledI = addLEDSegment(ledI, ring1Vs[0], ring1Vs[1], ring1Leds);
    //04
    //43
    //32
    //21
    //10

    //2
    //3
    //1
    //0
    //4
    //3

  }

  // Converts (r, theta) to [x, y]
  static private float[] polar2cart(float r, float theta) {
    float[] ret = new float[2];
    ret[0] = r * (float)cos(theta);
    ret[1] = r * (float)sin(theta);
    return ret;
  }


  // Call this after all other classes have registerDraw()'d
  public void registerDraw(PApplet parent) {
    this.p = parent;

    parent.registerDraw(this);

    // Initialize pixel mappings
    opc = new OPC(parent, "127.0.0.1", 7890);

    for (QueuedLed led : this.queuedLeds) {
      opc.led(led.i, led.x, led.y);
    }
  }

  private int addLEDSegment(int startLedI, IcosaVertex start, IcosaVertex end, Collection<PVector> ledCollection) {
    int numSpacings = PX_PER_SEGMENT + 1;

    float deltaX = (end.x - start.x) / numSpacings;
    float deltaY = (end.y - start.y) / numSpacings;

    // SIDE/2 to apply centering transform
    // Note that drawXxx()'s assume transform has been centered
    float x = Config.SIDE/2 + start.x + deltaX;
    float y = Config.SIDE/2 + start.y + deltaY;

    for (int i=startLedI; i < startLedI + PX_PER_SEGMENT; i++) {
      System.out.println("adding led " + i + " at " + x + " " + y);
      queuedLeds.add(new QueuedLed(i, (int)x, (int)y));
      ledCollection.add(new PVector(x - Config.SIDE/2, y - Config.SIDE/2));
      //opc.led(i, (int)x, (int)y);
      x += deltaX;
      y += deltaY;
    }

    start.addAdjacent(end); // addAdjacent() is bidirectional

    // Return the start index of the next LED or LED segment.
    return startLedI + PX_PER_SEGMENT;
  }

  private void drawLines() {
    // Draw line segments
    for (int i=0; i<NUM_POINTS; i++) {
      // line from center
      p.line(0, 0, ring1Vs[i].x, ring1Vs[i].y);

      int nextI = (i + 1) % NUM_POINTS;

      // ring 1 circle
      p.line(
          ring1Vs[i].x, ring1Vs[i].y, ring1Vs[nextI].x, ring1Vs[nextI].y
      );

      // ring 2 circle
      p.line(
          ring2Vs[i].x, ring2Vs[i].y, ring2Vs[nextI].x, ring2Vs[nextI].y
      );

      // ring1 to ring2, left
      p.line(
          ring1Vs[nextI].x, ring1Vs[nextI].y, ring2Vs[i].x, ring2Vs[i].y
      );

      // ring1 to ring2, right
      p.line(
          ring1Vs[i].x, ring1Vs[i].y, ring2Vs[i].x, ring2Vs[i].y
      );
    }
  }

  private void drawPoints() {

    p.ellipse(0, 0, 10, 10);

    // Then, on top draw the points
    for (int i=0; i<NUM_POINTS; i++) {
      p.ellipse(ring1Vs[i].x, ring1Vs[i].y, 5, 5);
      p.ellipse(ring2Vs[i].x, ring2Vs[i].y, 5, 5);
    }
  }

  public void draw() {
    this.drawPoints();
  }
}