import java.io.IOException;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.DisplayMode;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;

class MouseX {
  public void shootingStar() {
    System.out.println("Shooting star.");
  
    for (int x=400; x<6000; x+=20) {
      Point p = new Point(x, 200);
      basicMoveMouse(p);
      
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void fallingStar() {
    System.out.println("Falling star.");
  
    for (int y=400; y<4000; y+=20) {
      Point p = new Point(200, y);
      basicMoveMouse(p);
      
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void upperLeftish() {
    System.out.println("Top left-ish.");
    
    Point p = new Point(300, 300);
    basicMoveMouse(p);
  }
  
  public void basicSet() {
    System.out.println("Basic set.");
    
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    try {
      Robot r = new Robot();
      r.mouseMove(6000, 200);
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }
  
  public void basicMoveMouse(Point p) {
    // This is the entirety of what is needed to move the mouse.
    try {
      Robot r = new Robot();
      r.mouseMove(p.x, p.y);
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }
  
  public Dimension getDesktopResolution() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();
    
    int x = 0;
    int y = 0;
    int w = 0;
    int h = 0;
    
    for (GraphicsDevice device: gs) {
      GraphicsConfiguration[] configuration = device.getConfigurations();
      Rectangle screenBounds = configuration[0].getBounds();
      
      if (screenBounds.x > x) {
        x = screenBounds.x;
        w = x + screenBounds.width;
      }
      
      if (screenBounds.y > y) {
        y = screenBounds.y;
        h = y + screenBounds.height;
      }
    }
    
    return new Dimension(w, h);
  }
  
  public void info() {
    System.out.println("Info.");
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    Dimension desktopResolution = getDesktopResolution();
    System.out.println("Desktop resolution: " + desktopResolution.width + " " + desktopResolution.height);
    
    Point centerPoint =  ge.getCenterPoint();
    System.out.println("Center: " + centerPoint.toString());
    
    Rectangle rectangle = ge.getMaximumWindowBounds();
    System.out.println("Rectangle: " + rectangle.toString());
    
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    System.out.println("Desktop size: " + String.valueOf(size.getWidth()) + " " + String.valueOf(size.getHeight()));
    
    System.out.println("");
    
    GraphicsDevice[] gs = ge.getScreenDevices();
    int length = gs.length;
    String len = String.valueOf(length);
    System.out.println("Number of devices: " + len);
    
    for (int i = 0; i < gs.length; i ++) {
      String id = gs[i].getIDstring();
      System.out.println("ID: " + id);
      
      DisplayMode dm = gs[i].getDisplayMode();
      System.out.println("  Mode via DisplayMode: " + String.valueOf(dm.getWidth()) + " " + String.valueOf(dm.getHeight()));
      
      GraphicsConfiguration[] gc = gs[i].getConfigurations();
      //for (int j=0; j < gc.length; j++) {
        Rectangle gcBounds = gc[0].getBounds();
        System.out.println("  Rectangle(0): " + gcBounds.toString());
      
      
      System.out.println("");
    }
  }
  
  public void center() {
    System.out.println("Center.");
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    Point centerPoint =  ge.getCenterPoint();
    System.out.println("Center: " + centerPoint.toString());
    
    basicMoveMouse(centerPoint);
  }
  
//   public void moveMouse(Point p) {
//     // Lifted from https://stackoverflow.com/questions/2941324/how-do-i-set-the-position-of-the-mouse-in-java
//     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//     GraphicsDevice[] gs = ge.getScreenDevices();
// 
//     // Search the devices for the one that draws the specified point.
//     for (GraphicsDevice device: gs) { 
//       GraphicsConfiguration[] configurations = device.getConfigurations();
//       for (GraphicsConfiguration config: configurations) {
//         Rectangle bounds = config.getBounds();
//         if(bounds.contains(p)) {
//           // Set point to screen coordinates.
//           Point b = bounds.getLocation(); 
//           Point s = new Point(p.x - b.x, p.y - b.y);
// 
//           try {
//             Robot r = new Robot(device);
//             r.mouseMove(s.x, s.y);
//           } catch (AWTException e) {
//             e.printStackTrace();
//           }
// 
//           return;
//         }
//       }
//     }
//     // Couldn't move to the point, it may be off screen.
//     return;
//   }
}

class MouseMover {
  public static void main(String[] args) {
    MouseX mouseX = new MouseX();
    
    mouseX.info();
//     mouseX.upperLeftish();
//     mouseX.shootingStar();
//     mouseX.fallingStar();
//     mouseX.center();
//     mouseX.basicSet();
  }
}
