import controlP5.*;
ControlP5 cp5;

LEDMapping ledMapping;
VertexPoppers vertexPoppers;
IcosaFFT icosaFft = new IcosaFFT();

int SIDE = 600;

PImage dot;
PImage pine;
PImage fftColors;

// TODO: FFT vars, refactor into separate
float spin = 0.001;
float radiansPerBucket = radians(1.5);
float decay = 0.97;
float opacity = 25;
float minSize = 0.1;
float sizeScale = 0.3;


// This is a function which knows how to draw a copy of the "dot" image with a color tint.
void colorDot(float x, float y, float hue, float saturation, float brightness, float size, float transparency)
{
  blendMode(ADD);
  colorMode(HSB, 100);
  tint(hue, saturation, brightness);
  
  image(dot, x - size/2, y - size/2, size, size); 
  
  noTint();
  colorMode(RGB, 255);
  blendMode(NORMAL);
}

void setup() {
  size(SIDE, SIDE, P3D); //3D to force GPU blending
  translate(SIDE/2, SIDE/2);
  
  dot = loadImage("dot.png");
  pine = loadImage("pine1.jpg");
  fftColors = loadImage("fftColors.png");
  
  ledMapping = new LEDMapping();
  
  // Enable some implementations:
  // --------
  //new RainbowSpiral(this);
  //vertexPoppers = new VertexPoppers(this, ledMapping.verticies);
  // ---------
  
  // Keep Last!
  ledMapping.registerDraw(this);
}

void mousePressed() {
  //h = (h + 10) % 100;
}

float imgHeight = SIDE;
float y1 = imgHeight;
float y2 = 0;

float speed = 0.04;


void draw() {
  background(0);
  
  icosaFft.forward();
  
  // Mouse pointer
  float hue = (millis() * -speed) % (imgHeight*2);
  colorDot(mouseX, mouseY, hue, 100, 100, 200, 255);// + 200 * sin(t));
  
  
  // Pine tree background
  //rotate(PI/2);
  //image(pine, 0, -y1, SIDE, imgHeight);
  //image(pine, 0, -y2, SIDE, imgHeight);
  
  y1 = (y1 + speed) % (imgHeight * 2);
  y2 = (y2 + speed) % (imgHeight * 2);
  
  
  // Translate the origin point to the center of the screen
  // (for other drawers)
  translate(SIDE/2, SIDE/2);
  
  
  drawFft();
}

void drawFft() {
  float[] fftFilter = icosaFft.getFilter();
  colorMode(RGB);
  
  for (int i = 0; i < fftFilter.length; i += 3) {
    // tall palettes (sample height)   
    /*color rgb = fftColors.get(
      fftColors.width/2,
      int(map(i, 0, fftFilter.length-1, 0, fftColors.height-1))
    );// */
    
    // wide palettes (sample width)
    color rgb = fftColors.get(
      int(map(i, 0, fftFilter.length-1, 0, fftColors.width-1)), 
      fftColors.height/2
    ); 
    
    tint(rgb, fftFilter[i] * opacity);
    blendMode(ADD);
 
    float size = height * (minSize + sizeScale * fftFilter[i]);
    PVector center = new PVector(width * (fftFilter[i] * 0.2), 0);
    center.rotate((millis() * spin)  +  (i * radiansPerBucket));
 
    image(dot, center.x - size/2, center.y - size/2, size, size);
  }
}
