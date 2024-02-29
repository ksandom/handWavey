// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides all macro functionality.

Specific implementations like MacroLine, which provides single line macros, should extend this class. Whenever code could be used by multiple implementations, putting it here should be considered.
*/

package macro;

import mouseAndKeyboardOutput.*;
import config.*;
import debug.Debug;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import handWavey.HandWaveyEvent;
import bug.ShouldComplete;
import java.util.Arrays;

public class MacroCore {
    protected Debug debug;
    private OutputProtection output;
    private HandsState handsState;
    private HandWaveyManager handWaveyManager = null;
    private HandWaveyEvent handWaveyEvent;
    private String[] slot = new String[256];
    private int nestingLevel = 0;
    protected static final int maxNesting = 99;
    private Boolean slotsEnabled = true;

    private config.Group macros;
    private config.Group events;

    private ShouldComplete[] shouldCompleteInstruction = new ShouldComplete[this.maxNesting + 1];

    public MacroCore(String context, OutputProtection output, HandsState handsState, HandWaveyManager handWaveyManager, HandWaveyEvent handWaveyEvent) {
        this.debug = Debug.getDebug(context);
        this.output = output;

        this.handsState = handsState;
        this.handWaveyManager = handWaveyManager;
        this.handWaveyEvent = handWaveyEvent;

        this.macros = Config.singleton().getGroup("macros");
        this.events = Config.singleton().getGroup("actionEvents");

        Arrays.fill(this.slot, 0, 256, "");

        for (int level = 0; level < this.maxNesting; level++) {
            this.shouldCompleteInstruction[level] = new ShouldComplete("MacroCore/instruction-" + String.valueOf(level));
        }
        this.debug.out(0, "Successful startup.");
    }

    protected void doInstruction(String instruction) {
        String[] instructionParts = separateInstructionParts(instruction);

        String command = instructionParts[0];
        String[] parameters = separateParameters(instructionParts[1]);

        doInstruction(command, parameters);
    }

    protected boolean doInstruction(String command, String[] parameters) {
        boolean success = true;

        String commandToTry = command.trim();

        if (!doInternalInstruction(commandToTry, parameters)) {
            if (!this.tryMacro(commandToTry)) {
                this.debug.out(0, "Unknown command (not an internal instruction, or a macro): \"" + commandToTry + "\"");
                success = false;
            }
        }

        return success;
    }

    private boolean doInternalInstruction(String command, String[] parameters) {
        String commandSummary = command + "(\"" + String.join("\", \"", parameters) + "\");";
        this.shouldCompleteInstruction[this.nestingLevel].start(commandSummary);

        boolean success = true;

        if (this.debug.shouldOutput(2)) {
            String prefix = nestedDebugPrefix(this.nestingLevel);
            this.debug.out(2, prefix + command + " " + Arrays.toString(parameters));
        }

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
                this.handWaveyManager.rewindScroll(Long.parseLong(parm(parameters, 1, "0")));
                break;
            case "rewindCursorPosition":
                this.handWaveyManager.rewindCursorPosition(Long.parseLong(parm(parameters, 1, "0")));
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


            // Dynamic instructions.
            case "setSlot":
                setSlot(Integer.parseInt(parm(parameters, 0, "")), parm(parameters, 1, ""));
                break;
            case "doSlot":
                doSlot(Integer.parseInt(parm(parameters, 0, "")), parm(parameters, 1, ""));
                break;
            case "setAllSlots":
                setAllSlots(parm(parameters, 0, ""));
                break;
            case "disableSlots":
                this.useSlots(false);
                break;
            case "enableSlots":
                this.useSlots(true);
                break;
            case "do":
                this.debug.out(0, "do: " + parm(parameters, 0, ""));
                this.doSubAction(parm(parameters, 0, ""), "----");
                break;
            case "delayedDo":
                this.handWaveyEvent.addLaterSubEvent(parm(parameters, 0, ""), Long.parseLong(parm(parameters, 1, "")));
                break;
            case "delayedDoSlot":
                delayedDoSlot(Integer.parseInt(parm(parameters, 0, "")), parm(parameters, 1, ""), Long.parseLong(parm(parameters, 2, "")));
                break;
            case "cancelDelayedDo":
                this.handWaveyEvent.cancelLaterSubEvent(parm(parameters, 0, ""));
                break;
            case "cancelAllDelayedDos":
                this.handWaveyEvent.cancelAllLaterSubEvents();
                break;

            // Calibration.
            case "recalibrateSegments":
                this.handWaveyManager.recalibrateSegments();
                break;

            // Gesture control.
            case "lockGestures":
                this.handWaveyManager.lockGestures(parm(parameters, 0, "primary"));
                break;
            case "unlockGestures":
                this.handWaveyManager.unlockGestures(parm(parameters, 0, "primary"));
                break;
            case "lockTaps":
                this.handWaveyManager.lockTaps(parm(parameters, 0, "primary"), Long.parseLong(parm(parameters, 1, "0")));
                break;
            case "unlockTaps":
                this.handWaveyManager.unlockTaps(parm(parameters, 0, "primary"), Long.parseLong(parm(parameters, 1, "0")));
                break;


            // Oh ohhhhhhhh.
            default:
                success = false;
                break;
        }
        this.shouldCompleteInstruction[this.nestingLevel].finish();

        return success;
    }

    private Boolean tryMacro(String command) {
        if (!this.macros.itemExists(command)) {
            return false;
        }

        config.Item macroItem = this.macros.getItem(command);

        String macro = macroItem.get();
        MacroLine macroLine = MacroLine.singleton();

        if (macroLine == null) {
            this.debug.out(0, "MacroLine doesn't appear to have been initialised yet. Can not run " + command + ", which would do \"" + macro + "\"");

            /* While this is a failure, we've only got here because the macro exists, but we were not in a state to be able to run it. Therefore we should Fail and complain about it, rather than return false, which will lead to a fall-back event being triggered instead, which could mask the problem. */
            return true;
        }

        this.increaseNesting();
        String prefix = nestedDebugPrefix(this.nestingLevel);
        this.debug.out(2, prefix + command + ": " + macro);
        macroLine.runLine(macro);
        this.decreaseNesting();

        return true;
    }

    private String nestedDebugPrefix(int level) {
        return dotsForNestingLevel(level) + String.valueOf(this.nestingLevel) + " ";
    }

    private String dotsForNestingLevel(int level) {
        return new String(new char[level]).replace("\0", ".");
    }

    public void doSubAction(String command, String indent) {
        // Prefer a macro. But if we don't have that, trigger an event instead.
        // Complain if neither exist.

        String commandToTry = command.trim();

        this.increaseNesting();
        String[] parameters = separateParameters("");
        if (!doInternalInstruction(commandToTry, parameters)) {
            this.debug.out(2, "Internal instruction \"" + commandToTry + "\" does not exist.");
            if (!this.tryMacro(commandToTry)) {
                this.debug.out(2, "Macro \"" + commandToTry + "\" does not exist.");
                if (!this.events.itemExists(commandToTry)) {
                    this.debug.out(0, commandToTry + " doesn't appear to be a macro or event.");
                } else {
                    this.debug.out(2, "Event \"" + commandToTry + "\" exists. Triggering that.");
                    this.handWaveyEvent.triggerSubEvent(commandToTry, indent);
                }
            }
        }
        this.decreaseNesting();
    }


    private void setAllSlots(String eventName) {
        this.debug.out(2, "Set all slots to \"" + eventName + "\".");
        Arrays.fill(this.slot, 0, 256, eventName);
    }

    private void setSlot(int slot, String eventName) {
        this.debug.out(2, "Set slot " + String.valueOf(slot) + " to " + eventName);
        this.slot[slot] = eventName;
    }

    private String getSlot(int slot, String eventName) {
        String previousValue = this.slot[slot];
        String eventToRun = (!this.slot[slot].equals(""))?this.slot[slot]:eventName;
        if (slotsEnabled) {
            this.debug.out(2, "Run slot " + String.valueOf(slot) + " == " + eventToRun + ". Previous value: " + previousValue);

            if (eventToRun == "") {
                this.debug.out(2, "Slot " + String.valueOf(slot) + " is currently set to \"\". So not doing anything.");
                return "";
            }

            return eventToRun;
        } else {
            this.debug.out(2, "Would have run slot " + String.valueOf(slot) + " == " + eventToRun + ". Previous value: " + previousValue + ". But slots are currently disabled.");
            return "";
        }
    }

    private void doSlot(int slot, String eventName) {
        String eventToRun = getSlot(slot, eventName);
        this.debug.out(2, "Got event to run: " + eventToRun);
        if (!eventToRun.equals("")) {
            this.doSubAction(eventToRun, "-->");
        }
    }

    private void delayedDoSlot(int slot, String eventName, long delay) {
        String eventToRun = getSlot(slot, eventName);
        if (!eventToRun.equals("")) {
            this.handWaveyEvent.addLaterSubEvent(eventToRun, delay);
        }
    }

    private void useSlots(Boolean state) {
        if (state) {
            this.debug.out(1, "Slots enabled.");
        } else {
            this.debug.out(1, "Slots disabled.");
        }
        this.slotsEnabled = state;
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
            if (parameters[position].equals("")) {
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
        if (!parametersString.equals("")) {
            parameters[0] = parameters[0].substring(1);
            int lastEntry = parameters.length-1;
            parameters[lastEntry] = parameters[lastEntry].substring(0, parameters[lastEntry].length()-1);
        }

        return parameters;
    }

    private Boolean increaseNesting() {
        this.nestingLevel ++;
        if (this.nestingLevel > this.maxNesting) {
            this.nestingLevel = this.maxNesting;
            this.debug.out(0, "CRITICAL: Ran out of nesting levels. The current limit is " + String.valueOf(this.maxNesting) + ". This should be plenty for all known use-cases, and is therefore likely a bug in your gestureLayout.yml. If you think the limit needs to be raised, please create an issue here: https://github.com/ksandom/handWavey/issues");

            return false;
        } else return true;
    }

    private Boolean decreaseNesting() {
        this.nestingLevel --;
        if (this.nestingLevel < 0) {
            this.nestingLevel = 0;
            this.debug.out(0, "CRITICAL: Already at nesting level 0. You probably just saw a \"Ran out of nesting levels.\" error, which contains the information you need. This error is here for completeness.");

            return false;
        } else return true;
    }

    protected int getLevel() {
        return this.nestingLevel;
    }
}
