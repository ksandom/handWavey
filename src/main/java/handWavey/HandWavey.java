// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Gets HandWavey started.

To understand it, start in HandWaveyManager.
*/

package handWavey;

import handWavey.*;
import config.*;

public class HandWavey {
    public static void main(String[] args) {
        HandWaveyManager hwm = new HandWaveyManager(true); // This must be loaded first so that the config gets initialised before other thigns try to read it.
        UltraMotionManager umm = new UltraMotionManager(hwm);

        umm.keepAliveUntilBreak();
    }
}
