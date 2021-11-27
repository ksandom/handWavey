package handWavey;

import ultraMotion.*;
import com.leapmotion.leap.*;
import config.*;
import handWavey.HandWaveyManager;

public class UltraMotionManager {
    private UltraMotionInput ultraMotionInput = new UltraMotionInput();
    private Controller controller = new Controller();
    private config.Config config;
    private HandWaveyManager handWaveyManager;
    
    private Boolean active = false;
    
    public UltraMotionManager (HandWaveyManager hwm, config.Config config) {
        this.handWaveyManager = hwm;
        this.config = config;

        this.controller.addListener(ultraMotionInput);
        this.active = true;
        
        this.ultraMotionInput.setUltraMotionManager(this);

        int maxHands=Integer.parseInt(this.config.getGroup("ultraMotion").getItem("maxHands").get());
        this.ultraMotionInput.setMaxHands(maxHands);
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
