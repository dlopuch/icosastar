
public class FFTSpiral {
  
  PImage dot;
  IcosaFFT icosaFft;
  PImage fftColors;
  color[] palette;
  
  boolean disabled = false;
  
  // Default parameters, change per instance if need be
  // ------------
  
  // True: forget the fft levels, just make a light-everything spiral
  boolean goCrazy = false;
  
  // Higher to make a frequency bucket scale further out
  float freqPowerScaler = 0.2;
  
  // Higher to make each frequency bucket bigger
  float dotSizeScaler = 0.2; //0.3;
  
  // Higher to add more twists to spiral (~0.65 is one full rotation)
  float radiansPerBucket = radians(1.5);
  
  // Higher to spin faster
  float spin = 0.001;
  
  float minSize = 0.1;
  
  float opacity = 25;
  
  
  FFTSpiral(PApplet parent, PImage dot, IcosaFFT fft, PImage paletteImage) {
    parent.registerDraw(this);
    this.dot = dot;
    this.icosaFft = fft;
    this.fftColors = paletteImage;
  }
  
  FFTSpiral(PApplet parent, PImage dot, IcosaFFT fft, color[] palette) {
    parent.registerDraw(this);
    this.dot = dot;
    this.icosaFft = fft;
    this.palette = palette;
  }
  
  void draw() {
    if (this.disabled) {
      return;
    }
    
    float[] fftFilter = this.icosaFft.getFilter();
    colorMode(RGB);
    
    // goCrazy: forget the fft levels, just make a light-everything spiral
    if (this.goCrazy) {
      int len = fftFilter.length;
      fftFilter = new float[ len ];
      for (int i=0; i<len; i++) {
        fftFilter[i] = ((float)i/(float)(len - 1)) * 1.8;
      }
      
    }
    
    for (int i = 0; i < fftFilter.length; i += 3) {
      color rgb;
      if (this.fftColors != null) {
        // tall palettes (sample height)   
        /*rgb = this.fftColors.get(
          fftColors.width/2,
          int(map(i, 0, fftFilter.length-1, 0, this.fftColors.height-1))
        );// */
        
        // wide palettes (sample width)
        rgb = this.fftColors.get(
          int(map(i, 0, fftFilter.length-1, 0, this.fftColors.width-1)), 
          fftColors.height/2
        ); 
      } else {
        // pick a color from palette
        rgb = this.palette[
          int(map(i, 0, fftFilter.length-1,  0, this.palette.length-1))
        ];
      }
      
      tint(rgb, fftFilter[i] * this.opacity);
      blendMode(ADD);
   
      // Size of the bucket's dot overlay
      float size = height * (this.minSize + this.dotSizeScaler * fftFilter[i]);
      
      // Make a vector for the bucket
      PVector center = new PVector(width * (fftFilter[i] * freqPowerScaler), 0);
      center.rotate((millis() * this.spin)  +  (i * this.radiansPerBucket));
   
      image(this.dot, center.x - size/2, center.y - size/2, size, size);
    }
  }
}
