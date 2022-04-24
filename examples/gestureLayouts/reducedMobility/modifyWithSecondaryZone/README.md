# modifyWithSecondaryZone

The primary hand does the pointing, and can do a basic click.

The secondary hand can modify the actions that the primary hand achieves.

This implementation has middle click at the expense of drag.

## The Gestures

* Clicks
    * Click: Touch action zone with primary.
    * Right click: Secondary in active zone. Touch action zone with primary.
    * Middle click: Secondary in noMove zone. Touch action zone with primary.
* Scroll: Secondary in action zone. Move primary up and down.
* Drag: NA.

## Considerations

* There is currently no solution for dragging. See "modifyWithSecondaryZoneWithDrag".
* Remember that for a hand to be secondary, it must have been entered into view second.

## Target audience

For people who don't have good use of their hands.

## Terminology

Something ambiguous? Check the [terminology and concepts](https://github.com/ksandom/handWavey/blob/main/docs/terminologyAndConcepts.md) documentation.
