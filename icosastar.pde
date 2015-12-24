import controlP5.*;
ControlP5 cp5;

LEDMapping ledMapping;

int SIDE = 600;

PImage dot;
PImage pine;

// This is a function which knows how to draw a copy of the "dot" image with a color tint.
void colorDot(float x, float y, float hue, float saturation, float brightness, float size)
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
  size(SIDE, SIDE);
  translate(SIDE/2, SIDE/2);
  
  dot = loadImage("dot.png");
  pine = loadImage("pine1.jpg");
  
  ledMapping = new LEDMapping(this); 
}

void mousePressed() {
  //h = (h + 10) % 100;
}

float imgHeight = SIDE;
float y1 = imgHeight;
float y2 = 0;

float speed = 0.4;


void draw() {
  background(0);
  
  float y = (millis() * -speed) % (imgHeight*2);
  
  colorMode(HSB, 1.0, 1.0, 1.0);
  
  
  
  // Translate the origin point to the center of the screen
  //translate(SIDE/2, SIDE/2);
  
  //colorDot(0, 0, h, 100, 100, 200);// + 200 * sin(t));
  rotate(PI/2);
  image(pine, 0, -y1, SIDE, imgHeight);
  image(pine, 0, -y2, SIDE, imgHeight);
  
  y1 = (y1 + speed) % (imgHeight * 2);
  y2 = (y2 + speed) % (imgHeight * 2);
 
  
}
