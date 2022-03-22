// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides all macro functionality.

Specific implementations like MacroLine, which provides single line macros, should extend this class. Whenever code could be used by multiple implementations, putting it here should be considered.
*/

package macro;

import mouseAndKeyboardOutput.*;
import debug.Debug;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import bug.ShouldComplete;

public class MacroCore {
    protected Debug debug;
    private OutputProtection output;
    private HandsState handsState;
    private HandWaveyManager handWaveyManager = null;
    
    private ShouldComplete shouldCompleteInstruction;
    
    public MacroCore(String context, OutputProtection output, HandsState handsState, HandWaveyManager handWaveyManager) {
        this.debug = Debug.getDebug(context);
        this.output = output;
        
        this.handsState = handsState;
        this.handWaveyManager = handWaveyManager;
        
        this.shouldCompleteInstruction = new ShouldComplete("MacroCore/instruction");
    }
    
    protected void doInstruction(String instruction) {
        String[] instructionParts = separateInstructionParts(instruction);
        
        String command = instructionParts[0];
        String[] parameters = separateParameters(instructionParts[1]);
        
        doInstruction(command, parameters);
    }
    
    protected void doInstruction(String command, String[] parameters) {
        String commandSummary = command + "(\"" + String.join("\", \"", parameters) + "\");";
        this.shouldCompleteInstruction.start(commandSummary);
        
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
            case "releaseButtons":
                this.output.releaseButtons();
                break;
            case "rewindScroll":
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
                this.output.keyDown(parm(parameters, 0, ""));
                break;
            case "keyUp":
                this.output.keyUp(parm(parameters, 0, ""));
                break;
            case "keyPress":
                this.output.keyDown(parm(parameters, 0, ""));
                this.output.keyUp(parm(parameters, 0, ""));
                break;
            case "releaseKeys":
                this.output.releaseKeys();
                break;
            
            // Oh ohhhhhhhh.
            default:
                this.debug.out(0, "Unknown command: " + command);
                break;
        }
        this.shouldCompleteInstruction.finish();
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
