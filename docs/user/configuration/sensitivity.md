# Sensitivity

This topic covers how quickly the mouse cursor moves in relation to how quickly you move your hand. It's now really easy to get the behaviour that you want.

## What to fiddle with

* touchPad.yml:
  * `accelerationThreshold`
  * `accelerationExponent`
  * `unAcceleratedBaseMultiplier`
  * `acceleratedBaseMultiplier`
  * `maxSpeed`
* dataCleaning.yml:
  * `maxChange`

While all of those _could_ be relevant to you. You should start with `accelerationThreshold` and `accelerationExponent`.

## Examples

See the [sensitivity directory](https://github.com/ksandom/handWavey/tree/main/examples/sensitivity) in the [examples directory](https://github.com/ksandom/handWavey/tree/main/examples).

## What the settings do

### accelerationThreshold

The speed at which acceleration should kick in. Before this point, the movement is 1:1.

Use this to control when acceleration will kick in.

* Too high: It will be hard to trigger acceleration with a flick of the hand.
* Too low: It will be very twitchy, and hard to do precise movements.

### accelerationExponent

The speed above the `accelerationThreshold` then has an exponent applied to it to make the acceleration grow exponentially. This is that exponent.

Use this to control how the cursor shoots around when you flick your hand.

* Too high: The cursor will go way too far.
* Too low: The cursor won't go far enough without really throwing your hand around.

This value is very sensitive. In my experimentation, I've liked a value around 1.1 to 1.2.

#### A worked example

* The `accelerationThreshold` is set to 8.
* The `accelerationExponent` is set to 2.
* The speed at this moment is 11.

```
remainder = speed - accelerationThreshold
remainder = 11 - 8
remainder = 3
```

So the remainder that we'll be operating on is 3.

```
acceleratedDistance = remainder ^ accelerationExponent
acceleratedDistance = 3 ^ 2
acceleratedDistance = 9
```

For completeness, let's figure out the unacceleratedDistance.

```
unacceleratedDistance = min(speed, accelerationThreshold)
unacceleratedDistance = min(11, 8)
unacceleratedDistance = 8
```

And the totalDistance.

```
totalDistance = unacceleratedDistance + acceleratedDistance
totalDistance = 8 + 9
totalDistance = 17
```

_Note that this is an oversimplification, but is sufficient for understanding how to tune it._

### unAcceleratedBaseMultiplier

The baseMultiplier is how much the result is amplified to be used as a distance to move the mouse cursor.

The `unAcceleratedBaseMultiplier` is the baseMultiplier that is used for all calculations before the `accelerationThreshold`. Use this when the unaccelerated movement is too fast or slow.

* Too high: Cursor will move rapidly and be hard to be precise.
* Too low: Cursor will move slowly. Easy to be precise, but frustrating to get anywhere.

Note that the `unAcceleratedBaseMultiplier` also applies during acceleration to the amount of the speed before the `accelerationThreshold` while the `acceleratedBaseMultiplier` only applies to the remainder. This gives smooth, consistent, motion and acceleration regardless of how the two settings are relatively configured. However, when tuning, you should start with similar values to help define an intuitive behaviour. Once you know you need something different, then change them as needed.

#### A worked example

* The `accelerationThreshold` is set to **8**.
* The `unAcceleratedBaseMultiplier` is set to **2**.
* The `acceleratedBaseMultiplier` is set to **4**.
  * _Note that normally this should be set to the same value as `unAcceleratedBaseMultiplier`, but I'm using a different value to make the calculations easier to follow._
* The speed at this moment is **11**.

Let's get the components:

```
unAcceleratedSpeed = min(speed, accelerationThreshold)
unAcceleratedSpeed = min(11, 8)
unAcceleratedSpeed = 8

if (speed > accelerationThreshold)
  acceleratedSpeed = speed - unAcceleratedSpeed
  acceleratedSpeed = 11 - 8
  acceleratedSpeed = 3
else
  // We don't get here in this example.
  acceleratedSpeed = 0
```

Let's apply the baseMultipliers. I'm going to ignore the `accelerationExponent` for this example, but you can read about that above.

```
unAcceleratedValue = unAcceleratedSpeed * unAcceleratedBaseMultiplier
unAcceleratedValue = 8 * 2
unAcceleratedValue = 16

acceleratedValue = acceleratedSpeed * acceleratedBaseMultiplier
acceleratedValue = 3 * 4
acceleratedValue = 12

finalValue = unAcceleratedValue + acceleratedValue
finalValue = 16 + 12
finalValue = 28
```

The final linear distance that we will move the mouse cursor by is 28.

### acceleratedBaseMultiplier

See `unAcceleratedBaseMultiplier` above for full context of how this works.

In theory, use this when the accelerated movement is too fast or slow.

In practise, start by setting this to something similar to the `unAcceleratedBaseMultiplier`, and then adjusting the `accelerationExponent`. If the accelerated component is still too fast/slow, then mess with this setting.

* Too high: The cursor will shoot around, and be hard to control.
* Too low: Flicking your hand will not get the cursor to go as far as you want.

### maxSpeed

A speed limit on how fast the cursor can move. Performed after acceleration calculations have been done.

* Too high: Acceleration can get out of hand, causing the cursor to overshoot.
* Too low: No matter how much effort you put in, the cursor just won't go fast enouh.

### maxChange

A speed limit for your hand. Ie any movement above this speed will be capped before acceleration calculations are done.

* Too high: Error from the LeapMotion controller can get through, causing erratic movement.
* Too low: You can't get enough velocity through to the acceleration calculations, and you may not trigger acceleration at all.

**Generally, you shouldn't need to change this. But if you are getting problems, then this would be worth a look.**

## How to tune them

Each time you make an adjustment, save the YAML file, and restart handWavey.

1. Start by setting `unAcceleratedBaseMultiplier` and `acceleratedBaseMultiplier` to something like 2.
2. Adjust `accelerationThreshold` until acceleration kicks in at the right time for you.
3. Adjust `accelerationExponent` until you get the amount of acceleration that you want.
4. If you want to proportionally adjust the speed of the cursor, then now is the time to adjust `unAcceleratedBaseMultiplier` and `acceleratedBaseMultiplier`.
