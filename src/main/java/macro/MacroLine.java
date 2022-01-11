package macro;

import mouseAndKeyboardOutput.*;
import handWavey.HandsState;

public class MacroLine extends MacroCore {
    public MacroLine(Output output, HandsState handsState) {
        super("MacroName", output, handsState);
    }
    
    public void runLine(String line) {
        // TODO This currently will not handle ");" well in parameters. Make sure it doesn't match escaped versions.
        String[] instructions = line.split("\\);");
        
        for (String instruction : instructions) {
            super.doInstruction(instruction + ");");
        }
    }
}
