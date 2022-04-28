# tiltClick-actionModifier

Applying the actionModifier concept to tiltClick to create a [dynamic gestureLayout](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md). This combines the functionality from several gestureLayouts into one that is easy to manage layout.

Essentially, you use tiltClick normally, and then move your hand through to the action zone to modify what actions get applied by rotating your hand to select a toolset, and then grabbing to select it. Then you can move out of the action zone to perform actions using your chosen tools.

## Using it

* Hold your hand in a relaxed, almost flat position.

## The Gestures

* Slot 0: Tilt your hand off to the side it naturally wants to turn.
* Slot 1: Tilt your hand slightly unnaturally (the top of your hand rotates inwards, towards you).
* Slot 2: Hand in neutral position. Close your hand, then tilt to the side it naturally wants to turn.
* Slot 3: Hand in neutral position. Close your hand. Rotate slightly unnaturally.
* Slot 4: Hand in neutral position. Close your hand. Move hand up/down.

## Selecting a toolset

Move primary hand to action zone. Tilt hand to denote the toolset. Close hand, and open to select.

* Segment 0 (default): Simple.
* Segment 1: Copy/paste.
* Segment 2: _NA (merged into 1)_
* Segment 3: Modified click.
* Segment 4:
* Segment 5: _NA (unreachable)_
* Segment 6: _NA (unreachable)_
* Segment 7: _NA (unreachable)_
* Segment 8: Web.
* Segment 9: Multi-click.

## Toolsets

* Simple.
  * Slot (0/1): Click.
  * Slot (1/6): Right click.
  * Slot (2/7):
  * Slot (3/8):
  * Slot (4/9): Normal scroll.
* Copy/paste
  * Slot (0/5): CTRL + c.
  * Slot (1/6): CTRL + v.
  * Slot (2/7): CTRL + x.
  * Slot (3/8): Delete.
  * Slot (4/9):
* Modified click
  * Slot (0/5): Alt + Click. (Move window in Linux.)
  * Slot (1/6): Alt + Right click. (Resize window in Linux.)
  * Slot (2/7): Ctrl + Click.
  * Slot (3/8): Ctrl + Right click.
  * Slot (4/9): Ctrl + scroll. (zoom)
* Multi-click
  * Slot (0/5): Double click hold.
  * Slot (1/6): Tripple click hold.
  * Slot (2/7): Double click.
  * Slot (3/8): Tripple click.
  * Slot (4/9):
* Web
  * Slot (0/5): Left click.
  * Slot (1/6): Middle click.
  * Slot (2/7):
  * Slot (3/8): Right click.
  * Slot (4/9): Scroll.
* Template
  * Slot (0/5):
  * Slot (1/6):
  * Slot (2/7):
  * Slot (3/8):
  * Slot (4/9):

## Considerations

* Dragging over larger areas is currently unreliable.

## Target audience

It's not particularly complicated, but there's a bit to read here to understand it. It's probably worth getting the hang of one of the other [tiltClick implementations](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts/tiltClick) like [tiltClick-closeDoubleClick](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts/tiltClick/closeDoubleClick).

Most people?

This is meant to

* be easier to click repetitively with less impact on your wrist.
* be a more relax way to hold your hand.

## Terminology

Something ambiguous? Check the [terminology and concepts](https://github.com/ksandom/handWavey/blob/main/docs/terminologyAndConcepts.md) documentation.
