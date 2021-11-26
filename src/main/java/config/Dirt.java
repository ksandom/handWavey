package config;

import config.Config;

class Dirt { // Manages whether configuration has changed since a known state.
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
    
    protected void makeClean() {
        this.dirty = false;
    }
    
    public Boolean isDirty() {
        return this.dirty;
    }

    public Boolean isStartingUp() {
        return this.startup;
    }
    
    public void setConfigManager(Config config) {
        this.config = config;
    }

    public Config getConfigManager() {
        return this.config;
    }
    
    public void finishedStartup() {
        this.startup = false;
    }
}
