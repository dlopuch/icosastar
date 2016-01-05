import java.util.List;

// Converts (r, theta) to [x, y]
float[] polar2cart(float r, float theta) {
  float[] ret = new float[2];
  ret[0] = r * cos(theta);
  ret[1] = r * sin(theta);
  return ret;
}

public class IcosaVertex {
  float x;
  float y;
  float[] xy;
  
  List<IcosaVertex> adjacents = new ArrayList();
  
  IcosaVertex(float[] xy) {
    this.x = xy[0];
    this.y = xy[1];
    this.xy = xy;
  }
  
  public void addAdjacent(IcosaVertex adjacent) {
    this.adjacents.add(adjacent);
    adjacent.adjacents.add(this);
  }
}

public class LEDMapping {
  int RING1_R = 150;
  int RING2_R = 230;
  int NUM_POINTS = 5;
  float POINT_OFFSET_RAD = 2 * PI / NUM_POINTS;
  
  int PX_PER_SEGMENT = 6;
  
  public IcosaVertex[] ring1Vs = new IcosaVertex[NUM_POINTS];
  public IcosaVertex[] ring2Vs = new IcosaVertex[NUM_POINTS];
  public IcosaVertex center;
  
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
    
    this.center = new IcosaVertex(new float[]{ 0.0, 0.0 });
    verticies.add(this.center);
    
    
    // Fadecandy port 1: Segments lining equator triangles
    // -----------
    int ledI = 64;
    
    ledI = addLEDSegment(ledI, ring2Vs[0], ring1Vs[0]);
    ledI = addLEDSegment(ledI, ring1Vs[0], ring2Vs[1]);
    
    ledI = addLEDSegment(ledI, ring2Vs[1], ring1Vs[1]);
    ledI = addLEDSegment(ledI, ring1Vs[1], ring2Vs[2]);
    
    ledI = addLEDSegment(ledI, ring2Vs[2], ring1Vs[2]);
    ledI = addLEDSegment(ledI, ring1Vs[2], ring2Vs[3]);
    
    ledI = addLEDSegment(ledI, ring2Vs[3], ring1Vs[3]);
    ledI = addLEDSegment(ledI, ring1Vs[3], ring2Vs[4]);
    
    ledI = addLEDSegment(ledI, ring2Vs[4], ring1Vs[4]);
    ledI = addLEDSegment(ledI, ring1Vs[4], ring2Vs[0]);
    
    // Fadecandy port 0: Segments lining top piece
    // -----------
    ledI = 0;
    
    ledI = addLEDSegment(ledI, ring1Vs[2], center);
    ledI = addLEDSegment(ledI, center, ring1Vs[3]);
    ledI = addLEDSegment(ledI, ring1Vs[3], ring1Vs[2]);
    ledI = addLEDSegment(ledI, ring1Vs[2], ring1Vs[1]);
    ledI = addLEDSegment(ledI, ring1Vs[1], center);
  }
  
  // Call this after all other classes have registerDraw()'d
  void registerDraw(PApplet parent) {
    parent.registerDraw(this);
    
    // Initialize pixel mappings
    opc = new OPC(parent, "127.0.0.1", 7890);
    
    for (QueuedLed led : this.queuedLeds) {
      opc.led(led.i, led.x, led.y);
    }
  }
  
  private int addLEDSegment(int startLedI, IcosaVertex start, IcosaVertex end) {
    int numSpacings = PX_PER_SEGMENT + 1;
    
    float deltaX = (end.x - start.x) / numSpacings;
    float deltaY = (end.y - start.y) / numSpacings;
    
    // SIDE/2 to apply centering transform
    // Note that drawXxx()'s assume transform has been centered
    float x = SIDE/2 + start.x + deltaX;
    float y = SIDE/2 + start.y + deltaY;
    
    for (int i=startLedI; i < startLedI + PX_PER_SEGMENT; i++) {
      println("adding led", i, "at", x, y);
      queuedLeds.add(new QueuedLed(i, (int)x, (int)y));
      //opc.led(i, (int)x, (int)y);
      x += deltaX;
      y += deltaY;
    }
    
    start.addAdjacent(end); // addAdjacent() is bidirectional
    
    // Return the start index of the next LED or LED segment.
    return startLedI + PX_PER_SEGMENT;
  }
  
  void drawLines() {
    // Draw line segments
    for (int i=0; i<NUM_POINTS; i++) {
      // line from center
      line(0, 0, ring1Vs[i].x, ring1Vs[i].y);
      
      int nextI = (i + 1) % NUM_POINTS;
      
      // ring 1 circle
      line(
        ring1Vs[i].x, ring1Vs[i].y, ring1Vs[nextI].x, ring1Vs[nextI].y
      );
      
      // ring 2 circle
      line(
        ring2Vs[i].x, ring2Vs[i].y, ring2Vs[nextI].x, ring2Vs[nextI].y
      );
      
      // ring1 to ring2, left
      line(
        ring1Vs[nextI].x, ring1Vs[nextI].y, ring2Vs[i].x, ring2Vs[i].y
      );
      
      // ring1 to ring2, right
      line(
        ring1Vs[i].x, ring1Vs[i].y, ring2Vs[i].x, ring2Vs[i].y
      );
    }
  }
  
  void drawPoints() {
    
    ellipse(0, 0, 10, 10);
  
    // Then, on top draw the points
    for (int i=0; i<NUM_POINTS; i++) {
      ellipse(ring1Vs[i].x, ring1Vs[i].y, 5, 5);
      ellipse(ring2Vs[i].x, ring2Vs[i].y, 5, 5);
    }
  }
  
  void draw() {
    this.drawPoints();
  }
}
