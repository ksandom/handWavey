# modifyWithSecondaryZoneWithDragAndMiddleClick

The primary hand does the pointing, and can do a basic click.

The secondary hand can modify the actions that the primary hand achieves.

This implementation has drag and middle click, but with an extra left click just before, and after the middle click. This is due to using the secondary hand in the action zone to mean two different things.

## The Gestures

* Clicks
    * Click: Touch action zone with primary.
    * Right click: Secondary in active zone. Touch action zone with primary.
    * Middle click: Secondary in action zone. Then primary in action zone. Remove primary first, then secondary entirely.
* Scroll: Secondary in noMove zone. Move primary up and down.
* Drag: Secondary in action zone. Move the primary hand while in the action zone.

## Considerations

* There is  no solution for middle click. See "modifyWithSecondaryZone".
* Remember that for a hand to be secondary, it must have been entered into view second.

## Target audience

For people who don't have good use of their hands.
