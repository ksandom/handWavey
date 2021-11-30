package handWavey;

import config.Config;
import config.*;
import ultraMotion.*;
import com.leapmotion.leap.*;
import handWavey.HandWaveyManager;

public class UltraMotionManager {
    private UltraMotionInput ultraMotionInput = new UltraMotionInput();
    private Controller controller = new Controller();
    private config.Config config;
    private HandWaveyManager handWaveyManager;
    
    private Boolean active = false;
    
    public UltraMotionManager (HandWaveyManager hwm) {
        this.handWaveyManager = hwm;
        this.config = Config.singleton();

        this.controller.addListener(ultraMotionInput);
        this.active = true;
        
        this.ultraMotionInput.setUltraMotionManager(this);
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
        this.controller.removeListener(this.ultraMotionInput);
    }

    public void sleep(int microseconds) {
        try {
            Thread.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
