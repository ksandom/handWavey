// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Tracks when a item/group/all of configuration has changed from the default or stored state.
*/

package config;

import config.Config;

public class Dirt { // Manages whether configuration has changed since a known state.
    private Boolean dirty = false;
    private Config config = null;
    private Boolean startup = true;

    public Dirt(Boolean startup) {
        this.startup = startup;
    }

    protected void makeDirty() {
        if (this.startup == false) {
            this.dirty = true;

            if (this.config != null) {
                this.config.makeDirty();
            }
        }
    }

    public void makeClean() {
        this.dirty = false;
    }

    public Boolean isDirty() {
        return this.dirty;
    }

    public Boolean isStartingUp() {
        return this.startup;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="EI_EXPOSE_REP2",
        justification="This is just used for unit testing.")

    public void setConfigManager(Config config) {
        this.config = config;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="EI_EXPOSE_REP",
        justification="This is just used for unit testing.")

    public Config getConfigManager() {
        return this.config;
    }

    public void finishedStartup() {
        this.startup = false;
    }
}
