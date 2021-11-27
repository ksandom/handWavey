package handWavey;

import handWavey.*;
import config.*;

public class HandWavey {
    public static void main(String[] args) {
        HandWaveyManager hwm = new HandWaveyManager();
        UltraMotionManager umm = new UltraMotionManager(hwm, hwm.getConfig());

        umm.keepAlive();
    }
}
