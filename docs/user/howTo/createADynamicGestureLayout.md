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

## What can you run in slots?

Any [macroCommand](https://github.com/ksandom/handWavey/blob/main/docs/user/reference/macroCommands.md).

While you would use the full form, eg `doSomething();` inside an event, or another macro; you use the short form within slots, eg `setSlot("0", "doSomething");`
