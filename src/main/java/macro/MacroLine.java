package macro;

import mouseAndKeyboardOutput.*;
import handWavey.HandsState;
import handWavey.HandWaveyManager;

public class MacroLine extends MacroCore {
    public MacroLine(Output output, HandsState handsState, HandWaveyManager handWaveyManager) {
        super("MacroName", output, handsState, handWaveyManager);
    }
    
    public void runLine(String line) {
        // TODO This currently will not handle ");" well in parameters. Make sure it doesn't match escaped versions.
        String[] instructions = line.split("\\);");
        
        for (String instruction : instructions) {
            super.doInstruction(instruction + ");");
        }
    }
}
