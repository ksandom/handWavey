// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
For taking the right actions when an event is triggers.
*/

package handWavey;

import debug.Debug;
import mouseAndKeyboardOutput.*;
import audio.*;
import macro.MacroLine;
import config.Config;
import config.Group;
import debug.Debug;
import bug.ShouldComplete;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class HandWaveyEvent {
    public static final Boolean audioDisabled = false;
    public static final Boolean audioEnabled = true;

    private OutputProtection output;
    private Boolean useAudio;
    private String audioPath;

    private Group actionEvents;
    private Group audioEvents;

    private ShouldComplete shouldCompleteEvent;

    private MacroLine macroLine;

    private Debug debug;

    public HandWaveyEvent(OutputProtection output, Boolean useAudio, HandsState handsState, HandWaveyManager handWaveyManager) {
        this.output = output;
        this.useAudio = useAudio;

        this.debug = Debug.getDebug("HandWaveyEvent");

        this.macroLine = new MacroLine(this.output, handsState, handWaveyManager, this);

        this.actionEvents = Config.singleton().getGroup("actionEvents");
        this.audioEvents = Config.singleton().getGroup("audioEvents");

        this.audioPath = Config.singleton().getGroup("audioConfig").getItem("pathToAudio").get() + File.separator;

        this.shouldCompleteEvent = new ShouldComplete("HandWaveyEvent/event");
    }

    public void triggerEvents(List<String> events) {
        if (events == null) return;

        int numberOfEvents = events.size();
        int debugLevel = (numberOfEvents > 0)?1:3;
        this.debug.out(debugLevel, "Received " + String.valueOf(numberOfEvents) + " events.");
        for (String eventName : events) {
            triggerEvent(eventName, "  ");
        }
    }

    public void triggerEvent(String eventName) {
        triggerEvent(eventName, "");
    }

    public void triggerEvent(String eventName, String indent) {
        // Get the info we need for the event.
        this.shouldCompleteEvent.start(eventName);

        this.triggerSubEvent(eventName, indent);

        this.shouldCompleteEvent.finish();
    }

    public void triggerSubEvent(String eventName, String indent) {
        // Get the info we need for the event.
        String macroLine = this.getEventAction(eventName);
        String fileToPlay = "";
        if (this.useAudio) fileToPlay = this.getEventAudio(eventName);

        // Make the decisions.
        Boolean doMacro = (!macroLine.equals(""));
        Boolean doAudio = (this.useAudio && !fileToPlay.equals(""));

        // Only output at debug level 1 if we are going to do something. Otherwise at level 3.
        int debugLevel = (doMacro || doAudio)?1:3;
        this.debug.out(debugLevel, indent + "Event: " + eventName + ".");

        // Do macro.
        if (doMacro) {
            this.debug.out(2, indent + "  macroLine: \"" + macroLine + "\"");
            this.macroLine.runLine(macroLine);
        }

        // Do Audio.
        if (doAudio) {
            this.debug.out(2, indent + "  fileToPlay: \"" + fileToPlay + "\"");
            BackgroundSound.play(fileToPlay);
        }
    }

    public void triggerAudioOnly(String eventName) {
        // Get the info we need for the event.
        String indent = "";
        String fileToPlay = "";
        if (this.useAudio) fileToPlay = this.getEventAudio(eventName);

        // Make the decisions.
        Boolean doAudio = (this.useAudio && !fileToPlay.equals(""));

        // Only output at debug level 1 if we are going to do something. Otherwise at level 3.
        int debugLevel = (doAudio)?1:3;
        this.debug.out(debugLevel, indent + "AudioOnlyEvent: " + eventName + ".");

        // Do Audio.
        if (doAudio) {
            this.debug.out(2, indent + "  fileToPlay: \"" + fileToPlay + "\"");
            BackgroundSound.play(fileToPlay);
        }
    }

    private String getEventAction(String eventName) {
        if (this.actionEvents.getItem(eventName) == null) {
            this.debug.out(0, "No event " + eventName + ". This is probably a bug.");
            return "";
        }

        String action = this.actionEvents.getItem(eventName).get();

        return action;
    }

    private String getEventAudio(String eventName) {
        if (this.audioEvents.getItem(eventName) == null) {
            return "";
        }

        String filePath = this.audioEvents.getItem(eventName).get();
        String fullPath = "";

        if (!filePath.equals("")) {
            fullPath = this.audioPath + filePath;

            if (!new File(fullPath).exists()) {
                this.debug.out(0, "Would have played \"" + fullPath + "\". But the file does not appear to exist.");
                fullPath = "";
            }
        }

        return fullPath;
    }
}
