# Macro commands

## Mouse instructions

### mouseMove

`mouseMove("X", "Y");` places the mouse cursor in a specific (X,Y) location on your screen.

Note: **If you use this command, please consider whether you are solving your problem in the best way. Hard-coding X,Y co-ordinates will not be portable between different systems.**

### click

`click("button");` will `mouseDown` and `mouseUp` in the current cursor location.

`button` is one of:

* `"left"`
* `"middle"`
* `"right"`
* If no value is provided (eg `click();`), then the value of the most recent `setButton("buttonName");` will be used.

Eg:

`click("left");`

### doubleClick

`doubleClick("button");` will `click` twice in the current cursor location with no delay.

`button` is one of:

* `"left"`
* `"middle"`
* `"right"`
* If no value is provided (eg `doubleClick();`), then the value of the most recent `setButton("buttonName");` will be used.

Eg:

`doubleClick("left");`

### mouseDown

`mouseDown("button");` will press the mouse button down, but not release it, in the current cursor location.

`button` is one of:

* `"left"`
* `"middle"`
* `"right"`
* If no value is provided (eg `mouseDown();`), then the value of the most recent `setButton("buttonName");` will be used.

Eg:

`mouseDown("left");`

Note: **There are protections in place to stop the same mouse button from being pressed multiple times without being released. It will work as expected. However, please don't rely on this, because assumptions like that are likely to lead to bugs in your configuration.**

### mouseUp

`mouseUp("button");` will release the mouse button in the current cursor location.

`button` is one of:

* `"left"`
* `"middle"`
* `"right"`
* If no value is provided (eg `mouseUp();`), then the value of the most recent `setButton("buttonName");` will be used.

Eg:

`mouseUp("left");`

Note: **There are protections in place to stop the same mouse button from being released multiple times without being pressed. It will work as expected. However, please don't rely on this, because assumptions like that are likely to lead to bugs in your configuration.**

### setButton

`setButton("buttonName");` sets the default button that will be used for commands like `mouseDown`, `mouseUp`, `click`, `doubleClick` etc.

* `"left"`
* `"middle"`
* `"right"`
* If no value is provided (eg `setButton();`), then `"left"` will be used.

### releaseButtons

`releaseButtons();` makes sure that all buttons are released.

It's important to call this from an event like `general-state-pAbsent-enter` to make sure that no mouse buttons are left pressed down. When this is not done, it can cause weird behaviour, that will not be obvious for the user to debug.

The keyboard equivalent of this is `releaseKeys`.

### rewindScroll

`rewindScroll("additionalDelay");` undoes any scrolling that has taken place in the specified amount of time. That time comes from two places:

* `rewindScrollTime` in the `scroll.yml` configuration file.
* `additionalDelay` (defaults to `"0"`) in the parameter as part of this command.

There are two motivations for doing this:

* The data from the LeapMotion controller tends to fluctuate while doing a gesture. So we want to get the position from when the user began the gesture, not during, or after.
* If we are using something like `delaydDo`, then we should set the `additionalDelay` to the same amount of time that the command was delayed so that we get the scroll location from when the action was triggered.

### rewindCursorPosition

`rewindCursorPosition("additionalDelay");` moves it back to where it was at the specified time. That time comes from two places:

* `rewindCursorTime` in the `click.yml` configuration file.
* `additionalDelay` (defaults to `"0"`) in the parameter as part of this command.

There are two motivations for doing this:

* The data from the LeapMotion controller tends to fluctuate while doing a gesture. So we want to get the position from when the user began the gesture, not during, or after.
* If we are using something like `delaydDo`, then we should set the `additionalDelay` to the same amount of time that the command was delayed so that we get the cursor location from when the action was triggered.

### lockCursor

`lockCursor();` stops the cursor from being moved by handWavey.

This is really useful for things like

* getting expected behaviour while scrolling. Ie you don't want the cursor to be wandering around while you scroll.
* keeping the cursor in one place while the data from the LeapMotion stabilises after introducing the hand.

The counterpart is `unlockCursor();`

### unlockCursor

`unlockCursor();` undoes the lock that was placed by `lockCursor();`

### overrideZone

`overrideZone("zoneName");` replaces the current understanding of which zone the hand is in, with the `zoneName` provided.

Eg

`overrideZone("scroll");`

Scroll is the only implemented option so far, but there are likely to be others in the future.

The purpose is to completely change the behaviour of what handWavey does with the user inputs.

The counterpart is `releaseZone();`

### releaseZone

`releaseZone();` undoes `overrideZone("zoneName");` to restore the behaviour and functionality that would normally be in place for the current gestures.

## Keyboard instructions

### keyDown

`keyDown("keyName");`

Which keys can be used is defined in [Pressables.java](https://github.com/ksandom/handWavey/blob/main/src/main/java/mouseAndKeyboardOutput/Pressables.java). Eg

```
definePressable("c", KeyEvent.VK_C);
```

The above line tells us that `c` is a keyName that we can trigger. To get a capital "c" (C), we'd need to also use `shift`. So it would look something like this:

`keyDown("shift");keyDown("c");keyUp("c");keyUp("shift");`

If you wanted to trigger CTRL+C to copy something to the clipboard, you could do it like this:

`keyDown("ctrl");keyDown("c");keyUp("c");keyUp("ctrl");`

Important notes:

* Make sure to release any keys with `keyDown`. Either explicitly with `keyUp`, or with `releaseKeys` when the hand is removed.
* The counterpart is `keyUp("keyName");`

### keyUp

`keyUp("keyName");` releases the specific key that has been pressed down with `keyDown("keyName")`.

Eg:

`keyUp("c");`

### keyPress

`keyPress("keyName");` does a complete `keyDown` and `keyUp` of the `"keyName"` that you specify.

See `keyDown` for details on what can be pressed.

Let's use `keyPress` to simplify `keyDown`'s example:

`keyDown("ctrl");keyDown("c");keyUp("c");keyUp("ctrl");`

to

`keyDown("ctrl");keyPress("c");keyUp("ctrl");`

### releaseKeys

`releaseKeys();` releases all keys that have not been `keyUp`'d after their `keyDown`.

It's important to call this from an event like `general-state-pAbsent-enter` to make sure that no keyboard keys are left pressed down. When this is not done, it can cause weird behaviour, that will not be obvious for the user to debug.

The mouse equivalent of this is `releaseButtons`.

## Dynamic instructions

### setSlot

`setSlot("slotNumber", "eventName");` is used for [creating dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md), and is better explained in that documentation. But the TL;DR is that it specifies which event should be used on a given slot when `doSlot();` is called.

* `slotNumber`: Integer 0-255.
* `eventName`: Specify the eventName to be triggered by `doSlot();`. This is intended for the `custom-` events that you can create yourself.

### doSlot

`doSlot("slotNumber", "eventName");` is used for [creating dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md), and is better explained in that documentation. But the TL;DR is that it runs the eventName allocated to the specified slot. If no eventName has been set with `setSlot();` yet, it will use the eventName provided as the default.

* `slotNumber`: Integer 0-255.
* `eventName`: Specify the eventName to be triggered. This is intended for the `custom-` events that you can create yourself.

### setAllSlots

`setAllSlots("eventName");` is used for [creating dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md), and is better explained in that documentation. But the TL;DR is that it sets all slots to the specified eventName. Eg `setAllSlots("");` can be used to reset all slots back to defaults by making them empty.

* `eventName`: Specify the eventName to be triggered. This is intended for the `custom-` events that you can create yourself.

### disableSlots

`disableSlots();` is used for [creating dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md), and is better explained in that documentation. But the TL;DR is that it disables the `doSlot();` command. This is useful for disabling actions while the slots are being reconfigured (ie you're changing toolsets in a dynamic gestureLayout).

The counterpart is `enableSlots();`

### enableSlots

`enableSlots();` is used for [creating dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md), and is better explained in that documentation. But the TL;DR is that it re-enables the `doSlot();` command after having been disabled by the `disableSlots();` command. This is useful the slots have been reconfigured (ie you changed toolsets in a dynamic gestureLayout).

The counterpart is `disableSlots();`

### do

`do("eventName");` triggers the specified event name. It's intended for running `custom-` events that you define in your gesture layout, so that you can abstract out repeating functionality.

Example

```yaml
  custom-click-left:
    value: setButton("left");lockCursor();rewindCursorPosition();click();unlockCursor();
  tap-p0Open:
    value: do("custom-click-left");
```

Here we create an event called `custom-click-left` that has the abstracted functionality. Then when the `tap-p0Open` event gets triggered, it triggers the `custom-click-left`.

### delayedDo

`delaydDo("eventName", "millisecondsDelay");` triggers the specified event name after `millisecondsDelay` has elapsed. It's intended for running `custom-` events that you define in your gesture layout, so that you can abstract out repeating functionality.

Example

```yaml
  custom-tap-left:
    value: setButton("left");lockCursor();rewindCursorPosition("150");click();unlockCursor();
  tap-p0Open:
    value: delayedDo("custom-tap-left", "150");
```

Just like with the `do();` example, we create an event called `custom-click-left` that has the abstracted functionality. Then when the `tap-p0Open` event gets triggered, it triggers the `custom-click-left`.

However, we do so with a 150 millisecond delay. Ie it doesn't get triggered immediately. In a future iteration, pending events will be checked, and when the time has elapsed, it will be triggered.

Notice that we've specified a 150 millisecond delay, but also specified an `additionalDelay` of 150 milliseconds in `rewindCursorPosition("150");`. This is because rewindCursorPosition is expecting to have been triggered immediately, so we are telling it how much it has been delayed by so that it can rewind to the correct time. See `rewindCursorPosition` for full information.

Have a look at `cancelDelayedDo();` for why we'd want to do this.

### cancelDelayedDo

`cancelDelayedDo("eventName");` cancels a requested eventName that has been requested by `delayedDo();`. This is useful for either canceling, or continuing with an event that was uncertain to be correct.

Example

```yaml
  custom-tap-left:
    value: setButton("left");lockCursor();rewindCursorPosition("150");click();unlockCursor();
  custom-scroll-start:
    value: rewindCursorPosition();overrideZone("scroll");lockTaps("primary");
  tap-p0Open:
    value: delayedDo("custom-tap-left", "150");
  individual-pNonOOB0Closed-enter:
    value: cancelDelayedDo("custom-tap-left");do("custom-scroll-start");
```

Here, we've added two more events to the `delayedDo();` example:

* `custom-scroll-start` - Get handWavey into the scrolling zone.
* `individual-pNonOOB0Closed-enter` - The hand is flat and closed to grab the page for scrolling.

The logic here is that when you begin closing the hand, the Z axis from the LeapMotion controller becomes unreliable, often triggering a tap event. So we use `delayedDo();` to make use of that event, but delay it enough that we have some certainty about whether it was correct. If the hand has closed enough to trigger the `individual-pNonOOB0Closed-enter` event, we can cancel the `custom-tap-left` event that might have been waiting to be triggered.

This has the effect of allowing two very different actions to be reliably triggered from similar data changes.

## Calibration instructions

### recalibrateSegments

`recalibrateSegments();` recalibrates the roll of every present hand so that segment 0 is centered around the current hand position.

This is typically called from an event like `special-newHandUnfreezeCursor`.

This instantly makes handWavey automatically calibrated to the individual person at the moment in time when the hand was unfrozen after being inserted.

See [autoCalibration](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/autoCalibration.md).

## Gesture control

### lockTaps

`lockTaps("hand", "unlockAfter");` stops taps from being triggered.

* hand - Which hand to lock taps on:
    * `"primary"` - default
    * `"secondary"`
* unlockAfter - milliseconds to automatically unlock after:
    * `"0"` - Don't automatically unlock. - default
    * _A number greater than 0._ - Unlock after that many milliseconds.

This is useful for stopping taps from happening when you know there shouldn't be taps. Eg during scrolling.

Example 1

```
lockTaps("primary");
```

Will lock taps on the primary hand until further notice.

Example 2

```
lockTaps("secondary", "300");
```

Will lock taps on the secondary hand for 300 milliseconds.


Example 3

```
lockTaps();
```

Will lock the primary hand.

The counterpart is `unlockTaps`.

### unlockTaps

`unlockTaps(hand, unlockAfter);` unlocks the taps after they have been locked by `lockTaps();`.

* hand - Which hand to lock taps on:
    * `"primary"` - default
    * `"secondary"`
* unlockAfter - milliseconds to automatically unlock after:
    * `"0"` - Immediately unlock. - default
    * _A number greater than 0._ - Unlock after that many milliseconds.

You call this when you have finished a period where no taps should be called. Eg you exit out of the scrolling state.

Example 1

```
unlockTaps("primary");
```

Will unlock taps on the primary hand immediately.

Example 2

```
unlockTaps("secondary", "300");
```

Will unlock taps on the secondary hand after 300 milliseconds from now.

Example 3

```
unlockTaps();
```

Will unlock the primary hand.

The counterpart is `lockTaps();`.

### lockGestures

`lockGestures("hand");` locks roll and open/closed state changes, effectively locking most gestures that can be performed.

* hand - Which hand to lock taps on:
    * `"primary"` - default
    * `"secondary"`

This is useful for keeping the roll and open/close based states stable during times when that data from the LeapMotion controller would normally be unstable, such as when the hand is moving quickly.

Example 1

```
lockGestures("primary");
```

Will lock gestures on the primary hand.

Example 2

```
lockGestures("secondary");
```

Will lock gestures on the secondary hand.

Example 3

```
lockGestures();
```

Will lock gestures on the primary hand.

See [speedLock](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/speedLock.md).

The counterpart is `unlockGestures();`

### unlockGestures

`unlockGestures("hand");` unlocks the gestures that have previously been locked by `lockGestures();`. See `lockGestures();` for more information.

* hand - Which hand to lock taps on:
    * `"primary"` - default
    * `"secondary"`

Example 1

```
unlockGestures("primary");
```

Will unlock gestures on the primary hand.

Example 2

```
unlockGestures("secondary");
```

Will unlock gestures on the secondary hand.

Example 3

```
unlockGestures();
```

Will unlock gestures on the primary hand.

The counterpart is `lockGestures();`