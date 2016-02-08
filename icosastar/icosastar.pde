import controlP5.*;
ControlP5 cp5;

import http.requests.*;

LEDMapping ledMapping;
VertexPoppers vertexPoppers;
RainbowSpiral rainbowSpiral;
IcosaFFT icosaFft = new IcosaFFT();
FFTSpiral fftSpiral;

FFTSpiral broncosSpiral;
FFTSpiral panthersSpiral;
FFTSpiral penaltySpiral;

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

  //fftSpiral = new FFTSpiral(this, dot, icosaFft, fftColors);
  
  color[] broncos = {#002BAB, #FB6B14, #002BAB, #002BAB, #FB6B14, #FB6B14, #002BAB, #002BAB, #000000 };
  color[] panthers = {#00A3CA, #BFC0BF, #00A3CA, #000000, #BFC0BF, #00A3CA, #000000 };
  color[] penalties = {#FF0000, #FFB300, #FF0000, #FF6600, #000000};
  broncosSpiral = new FFTSpiral(this, dot, icosaFft, broncos);
  panthersSpiral = new FFTSpiral(this, dot, icosaFft, panthers);
  penaltySpiral = new FFTSpiral(this, dot, icosaFft, penalties); 
  
  panthersSpiral.disabled = true;
  broncosSpiral.disabled = true;
  penaltySpiral.disabled = false;
  
  
  
  GetRequest get = new GetRequest("http://localhost:8083/scrape_game");
  get.send();
  
  // ---------
  
  // Keep Last!
  ledMapping.registerDraw(this);
}

void enableBroncos() {
  panthersSpiral.disabled = true;
  broncosSpiral.disabled  = false;
  penaltySpiral.disabled  = true;
}

void enablePanthers() {
  panthersSpiral.disabled = false;
  broncosSpiral.disabled  = true;
  penaltySpiral.disabled  = true;
}

void enablePenalty() {
  panthersSpiral.disabled = true;
  broncosSpiral.disabled  = true;
  penaltySpiral.disabled  = false;
}


void mousePressed() {
  //h = (h + 10) % 100;

  if (!panthersSpiral.disabled) {
    enableBroncos();
    
  } else if (!broncosSpiral.disabled) {
    enablePenalty();
    
  } else {
    enablePanthers();
  }
  
  
  //refreshFromServer();
}

void refreshFromServer() {
  println("-------");
  println(millis(), "refreshing game state");
  
  GetRequest get = new GetRequest("http://localhost:8083/active_game");
  get.send();
  
  JSONObject json = parseJSONObject(get.getContent());
  
  boolean isDenver;
  
  // First, see if last drive is relevant
  boolean isLastDriveRelevant = json.getInt("timeSinceLastDriveMs") < 20000;
  if (isLastDriveRelevant) {
    println("lastDriveRelevant!");
    if ("bad".equals(json.getString("lastDriveReaction"))) {
      println("and its bad!");
      enablePenalty();
      penaltySpiral.spin = 0.010;
      penaltySpiral.goCrazy = true;
      return;
    }
    
    if ("good".equals(json.getString("lastDriveReaction"))) {
      isDenver = "DEN".equals( json.getJSONObject("lastDrive").getString("posteam") );
      println("AND ITS GREAT! PARTY FOR", isDenver ? "DENVER" : "not denver");
      
      FFTSpiral posteam = isDenver ? broncosSpiral : panthersSpiral;
    
      // party mode!
      if (isDenver) { enableBroncos(); } else { enablePanthers(); };
      posteam.goCrazy = true;
      posteam.spin = 0.010;
      return;
    }
    
    // meh, just drop down into normal root-for pos team
  }
  
  isDenver = "DEN".equals( json.getString("posteam") );
  FFTSpiral posteam = isDenver ? broncosSpiral : panthersSpiral;
  if (isDenver) { enableBroncos(); } else { enablePanthers(); };
  posteam.goCrazy = false;
  posteam.spin = 0.001;
}

float imgHeight = SIDE;
float y1 = imgHeight;
float y2 = 0;

float speed = 0.04;

int lastRefresh = millis();

void draw() {
  background(0);
  
  if (millis() - lastRefresh > 1000) {
    refreshFromServer();
    lastRefresh = millis();
  }
  
  icosaFft.forward();
  
  // Mouse pointer
  float hue = (millis() * -speed) % (imgHeight*2);
  colorDot(mouseX, mouseY, hue, 100, 100, 200, 255);// + 200 * sin(t));
  
  
  //float spin = map(mouseY, 0, height, 0.001, 0.010);
  //panthersSpiral.spin = spin;
  //broncosSpiral.spin = spin;
  //penaltySpiral.spin = spin;
  
  
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
