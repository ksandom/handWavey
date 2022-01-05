package macro;

import mouseAndKeyboardOutput.*;

public class MacroLine extends MacroCore {
    public MacroLine(Output output) {
        super("MacroName", output);
    }
    
    public void runLine(String line) {
        // TODO This currently will not handle ");" well in parameters. Make sure it doesn't match escaped versions.
        String[] instructions = line.split("\\);");
        
        for (String instruction : instructions) {
            super.doInstruction(instruction + ");");
        }
    }
}
