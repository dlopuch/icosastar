
// Converts (r, theta) to [x, y]
float[] polar2cart(float r, float theta) {
  float[] ret = new float[2];
  ret[0] = r * cos(theta);
  ret[1] = r * sin(theta);
  return ret;
}

public class LEDMapping {
  int RING1_R = 150;
  int RING2_R = 230;
  int NUM_POINTS = 5;
  float POINT_OFFSET_RAD = 2 * PI / NUM_POINTS;
  
  int PX_PER_SEGMENT = 6;
  
  float[][] ring1XYs = new float[NUM_POINTS][];
  float[][] ring2XYs = new float[NUM_POINTS][];
  
  OPC opc;

  LEDMapping(PApplet parent) {
    // Initialize the rings
    float r2_theta = 0;
    float r1_theta = POINT_OFFSET_RAD / 2;
    for (int i=0; i<NUM_POINTS; i++) {
      ring1XYs[i] = polar2cart(RING1_R, r1_theta);
      ring2XYs[i] = polar2cart(RING2_R, r2_theta);
      
      r1_theta += POINT_OFFSET_RAD;
      r2_theta += POINT_OFFSET_RAD;
    }
    
    parent.registerDraw(this);
    
    
    // Initialize pixel mappings
    opc = new OPC(parent, "127.0.0.1", 7890);
    
    
    // Fadecandy port 0: Segments lining equator triangles
    // -----------
    int ledI = 64;
    
    ledI = addLEDSegment(ledI, ring2XYs[0], ring1XYs[0]);
    ledI = addLEDSegment(ledI, ring1XYs[0], ring2XYs[1]);
    
    ledI = addLEDSegment(ledI, ring2XYs[1], ring1XYs[1]);
    ledI = addLEDSegment(ledI, ring1XYs[1], ring2XYs[2]);
    
    ledI = addLEDSegment(ledI, ring2XYs[2], ring1XYs[2]);
    ledI = addLEDSegment(ledI, ring1XYs[2], ring2XYs[3]);
    
    ledI = addLEDSegment(ledI, ring2XYs[3], ring1XYs[3]);
    ledI = addLEDSegment(ledI, ring1XYs[3], ring2XYs[4]);
    
    ledI = addLEDSegment(ledI, ring2XYs[4], ring1XYs[4]);
    ledI = addLEDSegment(ledI, ring1XYs[4], ring2XYs[0]);
    
    // Fadecandy port 1: Segments lining top piece
    // -----------
    ledI = 0;
    float[] center = new float[2];
    center[0] = 0.0;
    center[1] = 0.0;
    
    ledI = addLEDSegment(ledI, ring1XYs[2], center);
    ledI = addLEDSegment(ledI, center, ring1XYs[3]);
    ledI = addLEDSegment(ledI, ring1XYs[3], ring1XYs[2]);
    ledI = addLEDSegment(ledI, ring1XYs[2], ring1XYs[1]);
    ledI = addLEDSegment(ledI, ring1XYs[1], center);
  }
  
  private int addLEDSegment(int startLedI, float[] start, float[] end) {
    int numSpacings = PX_PER_SEGMENT + 1;
    
    float deltaX = (end[0] - start[0]) / numSpacings;
    float deltaY = (end[1] - start[1]) / numSpacings;
    
    // SIDE/2 to apply centering transform
    // Note that drawXxx()'s assume transform has been centered
    float x = SIDE/2 + start[0] + deltaX;
    float y = SIDE/2 + start[1] + deltaY;
    
    for (int i=startLedI; i < startLedI + PX_PER_SEGMENT; i++) {
      println("adding led", i, "at", x, y);
      opc.led(i, (int)x, (int)y);
      x += deltaX;
      y += deltaY;
    }
    
    // Return the start index of the next LED or LED segment.
    return startLedI + PX_PER_SEGMENT;
  }
  
  void drawLines() {
    // Draw line segments
    for (int i=0; i<NUM_POINTS; i++) {
      // line from center
      line(0, 0, ring1XYs[i][0], ring1XYs[i][1]);
      
      int nextI = (i + 1) % NUM_POINTS;
      
      // ring 1 circle
      line(
        ring1XYs[i][0], ring1XYs[i][1], ring1XYs[nextI][0], ring1XYs[nextI][1]
      );
      
      // ring 2 circle
      line(
        ring2XYs[i][0], ring2XYs[i][1], ring2XYs[nextI][0], ring2XYs[nextI][1]
      );
      
      // ring1 to ring2, left
      line(
        ring1XYs[nextI][0], ring1XYs[nextI][1], ring2XYs[i][0], ring2XYs[i][1]
      );
      
      // ring1 to ring2, right
      line(
        ring1XYs[i][0], ring1XYs[i][1], ring2XYs[i][0], ring2XYs[i][1]
      );
    }
  }
  
  void drawPoints() {
    
    ellipse(0, 0, 10, 10);
  
    // Then, on top draw the points
    for (int i=0; i<NUM_POINTS; i++) {
      ellipse(ring1XYs[i][0], ring1XYs[i][1], 5, 5);
      ellipse(ring2XYs[i][0], ring2XYs[i][1], 5, 5);
    }
  }
  
  void draw() {
    // Translate the origin point to the center of the screen
    translate(SIDE/2, SIDE/2);
    
    this.drawPoints();
  }
}
