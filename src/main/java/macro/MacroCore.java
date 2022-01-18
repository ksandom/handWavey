package macro;

import mouseAndKeyboardOutput.*;
import debug.Debug;
import handWavey.HandsState;
import handWavey.HandWaveyManager;

public class MacroCore {
    protected Debug debug;
    private OutputProtection output;
    private HandsState handsState;
    private HandWaveyManager handWaveyManager = null;
    
    public MacroCore(String context, OutputProtection output, HandsState handsState, HandWaveyManager handWaveyManager) {
        this.debug = Debug.getDebug(context);
        this.output = output;
        
        this.handsState = handsState;
        this.handWaveyManager = handWaveyManager;
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
            
            // Mouse instructions.
            case "moveMouse":
                this.output.setPosition(
                    toInt(parm(parameters, 0, 0)), // X
                    toInt(parm(parameters, 1, 0))); // Y
                break;
            case "click":
                if (!canPerformActions(command)) break;
                this.output.click(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "doubleClick":
                if (!canPerformActions(command)) break;
                this.output.doubleClick(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "mouseDown":
                if (!canPerformActions(command)) break;
                this.output.mouseDown(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "mouseUp":
                if (!canPerformActions(command)) break;
                this.output.mouseUp(
                    getButton(parameters, 0)); // Mouse button. ("left", "middle", "right")
                break;
            case "releaseButtons":
                if (!canPerformActions(command)) break;
                this.output.releaseButtons();
                break;
            case "rewindScroll":
                if (!canPerformActions(command)) break;
                this.handWaveyManager.rewindScroll();
                break;
            case "rewindCursorPosition":
                this.handWaveyManager.rewindCursorPosition();
                break;
            case "lockCursor":
                this.handWaveyManager.setCursorLock();
                break;
            case "unlockCursor":
                this.handWaveyManager.releaseCursorLock();
                break;
            case "overrideZone":
                this.handsState.overrideZone(parm(parameters, 0, "scroll"));
                break;
            case "releaseZone":
                this.handsState.releaseZone();
                break;
            case "setButton":
                this.handsState.setMouseButton(parm(parameters, 0, "left"));
                break;
            
            // Keyboard instructions.
            case "keyDown":
                if (!canPerformActions(command)) break;
                this.output.keyDown(parm(parameters, 0, ""));
                break;
            case "keyUp":
                if (!canPerformActions(command)) break;
                this.output.keyUp(parm(parameters, 0, ""));
                break;
            case "keyPress":
                if (!canPerformActions(command)) break;
                this.output.keyDown(parm(parameters, 0, ""));
                this.output.keyUp(parm(parameters, 0, ""));
                break;
            case "releaseKeys":
                if (!canPerformActions(command)) break;
                this.output.releaseKeys();
                break;
            
            // Oh ohhhhhhhh.
            default:
                this.debug.out(0, "Unknown command: " + command);
                break;
        }
    }
    
    
    private Boolean canPerformActions(String command) {
        Boolean result = true;
        
        if (this.handsState.newHandsClickFreeze() == true) {
            this.debug.out(1, "newHand clickFreeze is in effect. So the " + command + " action was ignored. If this is not the behavior you expect, take a look at the settings for newHand.");
            result = false;
        }
        
        return result;
    }
    
    private String getButton(String[] parameters, int position) {
        // If a button is specified, use that. Otherwise use the one that handsState thinks we should use.
        String button = parm(parameters, position, this.handsState.whichMouseButton());
        this.debug.out(1, "Used button: " + button);
        return button;
    }
    
    private int toInt(String input) {
        return Integer.parseInt(input);
    }
    
    private String parm(String[] parameters, int position, String defaultValue) {
        if (position > parameters.length-1) {
            // Parameter is not provided.
            return defaultValue;

        } else {
            if (parameters[position] == "") {
                return defaultValue;
            } else {
                return parameters[position];
            }
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
