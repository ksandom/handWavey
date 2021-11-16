package handWavey;

import ultraMotion.*;
import com.leapmotion.leap.*;

public class UltraMotionManager {
    private UltraMotionInput listener = new UltraMotionInput();
    private Controller controller = new Controller();
    
    private Boolean active = false;
    
    public UltraMotionManager () {
        this.controller.addListener(listener);
        this.active = true;
        
        this.listener.setUltraMotionManager(this);
    }
    
    public void keepAlive() {
        this.active = true;
        
        while (this.active) {
            sleep(500);
        }
        
        cleanup();
    }
    
    public void exit() {
        this.active = false;
    }
    
    public void cleanup () {
        this.controller.removeListener(this.listener);
    }

    public void sleep(int microseconds) {
        try {
            Thread.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
