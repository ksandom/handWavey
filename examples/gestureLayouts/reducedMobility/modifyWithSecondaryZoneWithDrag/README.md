# modifyWithSecondaryZoneWithDrag

The primary hand does the pointing, and can do a basic click.

The secondary hand can modify the actions that the primary hand achieves.

This implementation has drag at the expense of middle click.

## The Gestures

* Clicks
    * Click: Touch action zone with primary.
    * Right click: Secondary in active zone. Touch action zone with primary.
    * Middle click: NA.
* Scroll: Secondary in noMove zone. Move primary up and down.
* Drag: Secondary in action zone. Move the primary hand.

## Considerations

* There is currently no solution for middle click. See "modifyWithSecondaryZone".
* Remember that for a hand to be secondary, it must have been entered into view second.

## Target audience

For people who don't have good use of their hands.
