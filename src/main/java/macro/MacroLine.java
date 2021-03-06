// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Handles running macros that are formatted in a single line. Ie when used in events.

This extends the MacroCore class which provides almost all of its functionality.
*/

package macro;

import mouseAndKeyboardOutput.*;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import bug.ShouldComplete;

public class MacroLine extends MacroCore {
    private ShouldComplete shouldCompleteLine;
    
    public MacroLine(OutputProtection output, HandsState handsState, HandWaveyManager handWaveyManager) {
        super("MacroLine", output, handsState, handWaveyManager);
        
        this.shouldCompleteLine = new ShouldComplete("MacroLine/line");
    }
    
    public void runLine(String line) {
        // TODO This currently will not handle ");" well in parameters. Make sure it doesn't match escaped versions.
        this.shouldCompleteLine.start(line);
        String[] instructions = line.split("\\);");
        
        for (String instruction : instructions) {
            super.doInstruction(instruction + ");");
        }
        this.shouldCompleteLine.finish();
    }
}
