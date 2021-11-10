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
import java.util.concurrent.TimeUnit;

class MouseX {
  public void shootingStar() {
    System.out.println("Shooting star.");
  
    for (int x=400; x<6000; x+=10) {
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
  
    for (int y=400; y<4000; y+=10) {
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
    moveMouse(p);
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
    
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Couldn't move to the point, it may be off screen.
    return;
  }
  
  public void basicMoveMouse(Point p) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    try {
      Robot r = new Robot();
      r.mouseMove(p.x, p.y);
    } catch (AWTException e) {
      e.printStackTrace();
    }
    
    // Couldn't move to the point, it may be off screen.
    return;
  }
  
  public void info() {
    System.out.println("Info.");
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    Point centerPoint =  ge.getCenterPoint();
    System.out.println("Center: " + centerPoint.toString());
    
    Rectangle rectangle = ge.getMaximumWindowBounds();
    System.out.println("Rectangle: " + rectangle.toString());
    
    System.out.println("");
    
    GraphicsDevice[] gs = ge.getScreenDevices();
    int length = gs.length;
    String len = String.valueOf(length);
    System.out.println("Number of devices: " + len);
    
    for (int i = 0; i < gs.length; i ++) {
      /*if (theArray[i] != null)
        counter ++;*/
      
      String id = gs[i].getIDstring();
      System.out.println("ID: " + id);
      
      DisplayMode dm = gs[i].getDisplayMode();
      System.out.println("  Mode via DisplayMode: " + String.valueOf(dm.getWidth()) + " " + String.valueOf(dm.getHeight()));
      
//       Window wid = gs[i].getFullScreenWindow();
//       Point location = wid.getLocation();
//       System.out.println("  Location: " + location.toString());
      
      
      System.out.println("");
    }
  }
  
  public void center() {
    System.out.println("Center.");
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    Point centerPoint =  ge.getCenterPoint();
    System.out.println("Center: " + centerPoint.toString());
    
    moveMouse(centerPoint);
  }
  
  public void touchEachScreen() {
    System.out.println("Touch each screen.");
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    // Search the devices for the one that draws the specified point.
    for (GraphicsDevice device: gs) {
    
      String id = device.getIDstring();
      System.out.println("ID: " + id);
      
      try {
        Robot r = new Robot(device);
        r.mouseMove(200, 200);
      } catch (AWTException e) {
        e.printStackTrace();
      }
      
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    // Couldn't move to the point, it may be off screen.
    return;
  }
  
  public void moveMouse(Point p) {
    // Lifted from https://stackoverflow.com/questions/2941324/how-do-i-set-the-position-of-the-mouse-in-java
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    // Search the devices for the one that draws the specified point.
    for (GraphicsDevice device: gs) { 
      GraphicsConfiguration[] configurations = device.getConfigurations();
      for (GraphicsConfiguration config: configurations) {
        Rectangle bounds = config.getBounds();
        if(bounds.contains(p)) {
          // Set point to screen coordinates.
          Point b = bounds.getLocation(); 
          Point s = new Point(p.x - b.x, p.y - b.y);

          try {
            Robot r = new Robot(device);
            r.mouseMove(s.x, s.y);
          } catch (AWTException e) {
            e.printStackTrace();
          }

          return;
        }
      }
    }
    // Couldn't move to the point, it may be off screen.
    return;
  }
}

class MouseMover {
  public static void main(String[] args) {
    MouseX mouseX = new MouseX();
    
    mouseX.info();
//     mouseX.upperLeftish();
    mouseX.shootingStar();
    mouseX.fallingStar();
//     mouseX.center();
//     mouseX.touchEachScreen();
//     mouseX.basicSet();
  }
}
