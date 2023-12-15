// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Functionality for OutputProtection that protects the outputs/user interfaces from the user/macros doing stupid things like pressing the same key twice without releasing it in-between.
*/

package mouseAndKeyboardOutput;

import debug.Debug;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.*;

public class Protector {
    private HashMap<String, Boolean> things = new HashMap<String, Boolean>();

    public Protector() {
    }

    public Boolean setDown(String name) {
        Boolean result = false;

        if (isUp(name)) {
            this.things.put(name, true);
            result = true;
        }

        return result;
    }

    public Boolean setUp(String name) {
        Boolean result = true;

        if (isDown(name)) {
            this.things.remove(name);
            result = false;
        }

        return result;
    }

    public Boolean isDown(String name) {
        return this.things.containsKey(name);
    }

    public Boolean isUp(String name) {
        return (!this.things.containsKey(name));
    }

    public Set<String> getDownItems() {
        return this.things.keySet();
    }
}
