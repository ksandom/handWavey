# Sensitivity

This topic covers how quickly the mouse cursor moves in relation to how quickly you move your hand.

## What to fiddle with

* touchPad.yml
  * `inputMultiplier`
  * `outputMultiplier`
  * `acceleration`

## Examples

See the sensitivity directory in the examples directory.

## What's the difference between the inputMultiplier and the outputMultiplier

* The `inputMultiplier` is applied **before** doing acceleration calculations.
* The `outputMultiplier` is applied **after** doing acceleration calculations.

These are both in touchPad.yml.

The acceleration has a threshold that the input must meet before the acceleration can be applied. If the input is below that threshold, then the data is passed straight through without acceleration. This is what gives you the gentle control with slow movements, but gives you the ability to flick the cursor around with larger movements.

By setting the `inputMultiplier` to a lower number, it takes a larger movement before the acceleration kicks in. While setting the `outputMultiplier` to a lower number will simply reduce how far the cursor moves, but won't affect when the acceleration kicks in based on your hand movement.

As a general rule, start with the `inputMultiplier` to get the acceleration kicking in when you want it to, and move then to the `outputMultiplier` if you further need to tune the cursor movement.
