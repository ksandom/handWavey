# How event naming works

The events in handWavey give us a lot of power, and to keep them concise and easy to read, they need to be fairly short. But that also means that they might not be obvious without knowing how they are made up. That is what this document is for.

## Scope

* actionEvents.yml
* audioEvents.yml

## Event examples

* `general-segment-pAnyChange`
    * Hand: Primary
        * Segment has changed.
* `individual-pActive0Open-enter`
    * The following state has just become true.
    * Hand: Primary
        * zone: active
        * segment: 0
        * state: open
* `combined-pNoMove1Closed-sOOB0Absent-exit`
    * The following state was true, but is no longer true.
    * Hand: Primary
        * zone: noMove
        * segment: 1
        * state: closed
    * Hand: Secondary
        * zone: OOB (out of bounds)
        * segment: 0
        * state: absent
* `special-newHandFreeze`
    * A new hand has been introduced and we are waiting for it to become stable.
* `custom-3`
    * A [dynamic gestureLayout](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md) has triggered this user-defined event.

## Event types

### Summary

* general - tracks a single aspect of the state of the hand. ie one of zone, segment, state.
* individual - tracks the complete state of the hand. ie all of zone, segment, state. - Useful for one-handed gestures.
* combined - tracks the complete state of both hands. - Useful for doing powerful gestures with both hands.
* special - Events that don't fit into the generic descriptions of the state of the hand, but are useful anyway. Eg we've just entered into the newHand freeze where we freeze the cursor and events while the new hand stabilises.
* custom - A user-defined event to be used in [dynamic gestureLayouts](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md).

### General

If you want to perform an action when a single thing changes, regardless of the other aspects of the hand state, General is likely the event type that you want.

Eg

* The primary hand closes: Mouse down.
* The primary hand opens: Mouse up.

### Individual

You want to perform an action when an individual hand is in a specific state regardless of what the other hand is doing.

Eg

* The secondary hand is in the active zone, upright (segment 0), and open: CTRL key down.
* The secondary hand is no longer in the active zone, upright (segment 0), and open: CTRL key up.

Note that all aspects of the specified state must be met for the state to be entered. And it is exited as soon as that is no longer true. Ie if any component no longer matches, the whole specified state is not matched.

### Combined

Just like individual, but takes both hands into account. You can use this to completely remap what happens when the primary hand does something, simply by putting the secondary hand into a specific shape. Eg

* Normally (secondary hand is absent):
    * The primary hand closes: Mouse left down.
    * The primary hand opens: Mouse left up.
* Secondary hand upright, open:
    * The primary hand closes: Mouse middle down.
    * The primary hand opens: Mouse middle up.
* Secondary hand tilted naturally, open:
    * The primary hand closes: Mouse right down.
    * The primary hand opens: Mouse right up.
* Secondary hand upright, closed:* `special-newHandFreeze`
    * A new hand has been introduced and we are waiting for it to become stable.

    * The primary hand closes: Double click hold.
    * The primary hand opens: Mouse up.
* Secondary hand tilted naturally* `special-newHandFreeze`
    * A new hand has been introduced and we are waiting for it to become stable.
, closed:
    * The primary hand closes: Tripple click hold.
    * The primary hand opens: Mouse up.

### Special

These are events that happen in certain circumstances that probably don't describe the standard hand state components, but may be useful for the user to be able to assign actions. As of 2022-03-19 these events are:

```bash
$ grep special ~/.config/handWavey/actionEvents.yml 
  special-newHandUnfreezeEvent:
  special-newHandUnfreezeCursor:
  special-newHandFreeze:
```

For descriptions of what these events do, see actionEvents.yml in your configuration directory.

## What states are the hands getting into right now?

Have a look at the terminal output. Eg

```
Debug 1 (HandsState): Current state: pActive0Open, sOOB0Absent
```

In the above example

* Primary hand
    * Zone: Active
    * Segment: 0
    * State: Open
* Secondary hand
    * Zone: OOB (Out of bounds)
    * Segment: 0
    * State: Absent

## OOB vs nonOOB

`OOB` (out of bounds) and `nonOOB` (any zone that is in bounds) are special zones that describe whether the hand is currently being safely tracked.

`nonOOB` is a pseudo zone that you will never see in the output of handWavey, but you can use for assigning actions to events. It's useful when you want to perform an action regardless of what zone you are in. Eg when you want to perform a click regardless of whether your hand is in the active zone, action zone, noMove zone, or none zone.

## What's going on?

Having trouble working out why/why-not something is happening? You may want to play with the [debug levels](../debugging.md). Be gentle, you can flood yourself with information very quickly. But it's also easy to see what is flooding you with information


