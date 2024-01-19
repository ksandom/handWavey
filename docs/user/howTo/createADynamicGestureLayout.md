# How to create a dynamic gestureLayout

## What is a dynamic gestureLayout?

It's just like a [normal gestureLayout](createAGestureLayout.md), except that you can make some actions change in a predictable way. Eg you can have a smaller number of easy-to-trigger gestures, but then be able to change the actions behind them like you'd change tools in a photo editor.

## How it works

* Instead of running explicit actions (eg `"keyDown("a");"`) when an event is triggered, you run a slot. (Eg `doSlot("0", "custom-0");` where `"0"` is the slot to be run, and `"custom-0"` is the event to trigger if the slot hasn't been set yet.)
* That slot can be redefined using a command like `setSlot("0", "custom-1");` where `"0"` is the slot number, and `"custom-1"` is the event to trigger.

The custom events can be configured without your gestureLayout just like you would for any other event within the gestureLayout.

## Commands

* `doSlot("slotNumber", "eventName")` - Runs the specified slot. Default to `"eventName"` if the slot hasn't been set.
* `setSlot("slotNumber", "eventName")` - Redefines the specified slot to use a specific eventName.
* `disableSlots()` - Disables the doSlot() function. Useful while the user is choosing a new toolset, and doesn't want to be taking action.
* `enableSlots()` - Re-enables the doSlot() function. Useful when the user has finished changing the toolset.
* `setAllSlots("")` - Sets all slots to the specified value. Setting this to `""` is particularly useful to go back to the gestureLayout's default assignments for all slots. Ie to return to the default state.

The [Command documentation](https://github.com/ksandom/handWavey/blob/main/docs/user/reference/macroCommands.md) will likely be useful for you.

## Parameters

* `slotNumber` is an integery from 0 to 255.
* `eventName` is the name of the event to be triggered.

## A worked example

This is a copy and paste of the first few lines of the actionEvents.yml file from the tiltClick/actionModifier gestureLayout.

```yaml
groups: {}
items:
  general-zone-pAction-enter:
    value: disableSlots();
  general-zone-pAction-exit:
    value: enableSlots();
  individual-pAction0Open-enter:
    value: debug("0", "Toolset = Simple (default).");
  individual-pAction0Closed-enter:
    value: setAllSlots("");
  individual-pAction1Open-enter:
    value: debug("0", "Toolset = Copy/paste.");
  individual-pAction1Closed-enter:
    value: setAllSlots("");setSlot("0", "custom-101");setSlot("1", "custom-12");setSlot("3", "custom-11");
```

* The action zone is used to select toolsets.
* Rotating the hand denotes which toolset is desired.
* Closing the hand denotes selecting that toolset.

## Custom events

Custom events are what get triggered by the slots.

* These can all be changed or added to in actionEvents.yml in the user's config.
* Their default values are set in the generateCustomConfig function in [HandWaveyConfig.java](https://github.com/ksandom/handWavey/blob/main/src/main/java/handWavey/HandWaveyConfig.java).

These are the default actions for the custom events:

<!-- BEGIN custom- table. -->
| custom- event | Description |
| --- | --- |
| custom-noOp | Do nothing. Useful to have a blank slot that can sometimes be used for other things. |
| custom-releaseAll | Release all buttons and keys. Useful for getting the keyboard and mouse into a known state. |
| custom-mouseDown-left | Press down the left mouse button. |
| custom-mouseDown-right | Press down the right mouse button. |
| custom-mouseDown-middle | Press down the middle mouse button. |
| custom-releaseZone | Release and zone overrides. This is typically used at the end of overriding the zone for something like scrolling. |
| custom-override-scroll | Override the zone to scroll. This has the effect that any movement of the hand causes scroll movement instead of mouse cursor movement. |
| custom-override-ctrl+scroll | Press the CTRL key down, and override the zone to scroll. Often this is used for zooming. |
| custom-doubleClick-hold | Double click the left button, without lifting the finger at the end of the second click. This is useful for doing things like drag-selecting by word rather than by character. |
| custom-trippleClick-hold | Tripple click the left button, without lifting the finger at the end of the second click. This is useful for doing things like drag-selecting by line rather than by character. |
| custom-doubleClick | Double click the left button. |
| custom-trippleClick | Tripple click the left button. |
| custom-alt+mouseDown-left | Press the ALT key, then hold down the left button. |
| custom-alt+mouseDown-right | Press the ALT key, then hold down the right button. |
| custom-alt+mouseDown-middle | Press the ALT key, then hold down the middle button. |
| custom-ctrl+mouseDown-left | Press the CTRL key, then hold down the left button. |
| custom-ctrl+mouseDown-right | Press the CTRL key, then hold down the right button. |
| custom-ctrl+mouseDown-middle | Press the CTRL key, then hold down the middle button. |
| custom-ctrl+c | CTRL + c. Typically used for copying a selection. |
| custom-ctrl+v | CTRL + v. Typically used for pasting. |
| custom-ctrl+x | CTRL + x. Typically used for cutting a selection. |
| custom-delete | Press and release the delete key. |
| custom-ctrl+z | CTRL + z. Typically used for undo. |
| custom-ctrl+shift+z | CTRL + Shift z. Typically used for re-doing an undone task. |
<!-- END custom- table. -->

This table can be automatically updated with `./generateCustomTable.sh`. It's generated from  generateCustomConfig function in [HandWaveyConfig.java](https://github.com/ksandom/handWavey/blob/main/src/main/java/handWavey/HandWaveyConfig.java#L576) that sets the defaults.
