import controlP5.*;
ControlP5 cp5;

LEDMapping ledMapping;
VertexPoppers vertexPoppers;
RainbowSpiral rainbowSpiral;
IcosaFFT icosaFft = new IcosaFFT();
FFTSpiral fftSpiral;

int SIDE = 600;

PImage dot;
PImage pine;
PImage fftColors;


float colorOffset = 0;
float colorOffsetRadiansPerBucket = radians(1.5);



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
  //vertexPoppers = new VertexPoppers(this, ledMapping.verticies);
  //rainbowSpiral = new RainbowSpiral(this);
  new VertexFFT(this, icosaFft.beat, 
    Arrays.asList(ledMapping.ring2Vs), // bottom ring
    Arrays.asList(ledMapping.ring1Vs), // middle ring
    Arrays.asList(ledMapping.center)
  );
  fftSpiral = new FFTSpiral(this, dot, icosaFft, fftColors);
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
  
}
