# How to scroll

This document outlines different methods of scrolling. Refer to the documentation with your [gestureLayout](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts) to find out which method is used with that gestureLayout.

## Methods

### Wheel

#### Like

* a scroll wheel on a mouse.
* the two finger gesture on a touch pad.
* a finger on a touch screen.

#### Mechanics

Moving your hand up and down will move the scrolling content up and down by a relative* amount.

\* There is a little bit of acceleration applied, so it's not exactly 1:1.

### Yank scroll

Yank scroll is an implementation of Wheel scroll, intended for nesting gestures inside of the gesture fr scrolling. It achieves this by disabling the one that you're not using.

#### Basics of using it

Initially, no scrolling will happen. Instead you can perform gestures in the shared space. There are two ways to begin scrolling:

* Wait ~1 second. you will hear 2 dings:
    * First ding: Scrolling is enabled, and gestures are too.
    * Second ding: Only scrolling is enabled.
* Move the hand quickly to begin scrolling immediately.

Once scrolling is activated, it _is_ a wheel scroll.

#### Taps and other gestures while scrolling

* Taps and other gestures are allowed until you start scrolling.
* Once you have started scrolling, you'll need to exit the scrolling gesture, and re-enter it to perform taps and other gestures again.

### Joystick

#### Like

* a joystick.
* a [pointing stick](https://en.wikipedia.org/wiki/Pointing_stick).

#### Mechanics

Ie a neutral position performs no scrolling, but the further your hand is from the neutral position, the faster it will scroll. Moving your hand back to the neutral position will stop scrolling.

In this case, the neutral position is called the dead zone.

* It is the position your hand was in when you performed the scrolling gesture.
* The dead zone will remain in that location for the duration of that scrolling session.
* A new dead zone will be calculated next time you perform the scrolling gesture.

Exiting the dead zone will:

* Make a ding noise to let you know. It will do a slightly different ding when re-entering the dead zone.
* Disable taps and shared gestures.
* Enable the scrolling motion.

#### Taps and other gestures while scrolling

* Taps and other gestures are allowed while your hand is in the neutral area.

## How to use scrolling

Consult your [gestureLayout](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts) documentation to see the exact gesture required to activate scrolling. But a couple of generalisations are:

* [tiltClick](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts/tiltClick) based gestureLayouts typically work by grabbing the thing you want to scroll and then pulling it in the direction you want it to go. Let go when you're done.
* [grabClick](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts/grabClick) based gestureLayouts typically work by turning your hand up-side-down and moving your hand in the direction you want it to go. Turn your hand back up right when you're done.
