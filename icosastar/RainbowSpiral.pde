float MAX_RADIUS = 230; // ring2 radius

public class RainbowSpiral {
  float theta = 0;
  float colour = 0;
  
  float rInc = 1;
  float thetaInc = 0.1;
  float colourInc = 1;
  float size = 65;
  
  
  RainbowSpiral(PApplet parent) {
    parent.registerDraw(this);
  }
  
  void draw() {
    float r = 0;
    
    float theta = this.theta;
    float colour = this.colour;
    
    noStroke();
    
    while (r < MAX_RADIUS) {
      colorMode(HSB, 100);
      fill(colour, 100, 50);
      float[] xy = polar2cart(r, theta);
      ellipse(
        xy[0], 
        xy[1],
        this.size,
        this.size
      );
      
      r += this.rInc;
      theta += this.thetaInc;
      colour = (colour + this.colourInc) % 100;
    }
    
    this.theta += this.thetaInc;
    
  }
}
