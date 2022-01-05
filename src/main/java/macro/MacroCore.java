package macro;

import mouseAndKeyboardOutput.*;
import debug.Debug;

public class MacroCore {
    protected Debug debug;
    private Output output;
    
    public MacroCore(String context, Output output) {
        this.debug = new Debug(1, context);
        this.output = output;
    }
    
    protected void doInstruction(String instruction) {
        String[] instructionParts = separateInstructionParts(instruction);
        
        String command = instructionParts[0];
        String[] parameters = separateParameters(instructionParts[1]);
        
        doInstruction(command, parameters);
    }
    
    protected void doInstruction(String command, String[] parameters) {
        switch (command) {
            case "debug":
                this.debug.out(Integer.parseInt(parameters[0]), parameters[1]);
                break;
            case "moveMouse":
                this.output.setPosition(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]));
                break;
        }
    }
    
    
    private String[] separateInstructionParts(String instruction) {
        // Remove the ending.
        // TODO This will currently not handle occurences of ");" in parameters well. Make it ignore escaped occurences.
        String[] withoutEnding = instruction.split("\\);");
        
        String[] parts = withoutEnding[0].split("\\(", 2);
        
        return parts;
    }
    
    private String[] separateParameters(String parametersString) {
        // TODO This will currently not handle occurences of "", "" in parameters well. Make it ignore escaped occurences.
        String[] parameters = parametersString.split("\", \"");
        
        // Remove remaining fluff from the parameters that didn't get picked up from the initial split.
        parameters[0] = parameters[0].substring(1);
        int lastEntry = parameters.length-1;
        parameters[lastEntry] = parameters[lastEntry].substring(0, parameters[lastEntry].length()-1);
        
        return parameters;
    }
}
