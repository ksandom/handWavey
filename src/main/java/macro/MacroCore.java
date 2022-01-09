package macro;

import mouseAndKeyboardOutput.*;
import debug.Debug;
import handWavey.HandsState;

public class MacroCore {
    protected Debug debug;
    private Output output;
    private HandsState handsState;
    
    public MacroCore(String context, Output output) {
        this.debug = new Debug(1, context);
        this.output = output;
        
        this.handsState = HandsState.singleton();
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
                this.debug.out(
                    toInt(parm(parameters, 0, "0")), // Debug level.
                    parm(parameters, 1, "Missing debug text in macro.")); // Text to output.
                break;
            case "moveMouse":
                this.output.setPosition(
                    toInt(parm(parameters, 0, 0)), // X
                    toInt(parm(parameters, 1, 0))); // Y
                break;
            case "click":
                this.output.click(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "doubleClick":
                this.output.doubleClick(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "mouseDown":
                this.output.mouseDown(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "mouseUp":
                this.output.mouseUp(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
        }
    }
    
    /*
        * #Override defaults in config before load finishes.
        * Set zone action to doubleclick.
        * Add events to HandsState.
        * Add keyboard commands
        * Add other commands
        * Rewind
            move to handsState
                movingMeans
                history


        this.output.getMouseButtonID("left")
        this.output.scroll(1);
                this.output.keyDown(1);
                keyUp
                assertEquals(99, this.output.getKeyID("thing"));
                
                setButton
                setKey
                overrideZone
                releaseZone
                rewind
                freeze
    */
    
    
    private int getButton(String[] parameters, int position) {
        // If a button is specified, use that. Otherwise use the one that handsState thinks we should use.
        String button = parm(parameters, position, this.handsState.whichMouseButton());
        return this.output.getMouseButtonID(button);
    }
    
    private int toInt(String input) {
        return Integer.parseInt(input);
    }
    
    private String parm(String[] parameters, int position, String defaultValue) {
        if (position > parameters.length-1) {
            // Parameter is not provided.
            return defaultValue;
        } else {
            return parameters[position];
        }
    }
    
    private String parm(String[] parameters, int position, int defaultValue) {
        return parm(parameters, position, String.valueOf(defaultValue));
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
        if (parametersString != "") {
            parameters[0] = parameters[0].substring(1);
            int lastEntry = parameters.length-1;
            parameters[lastEntry] = parameters[lastEntry].substring(0, parameters[lastEntry].length()-1);
        }
        
        return parameters;
    }
}
