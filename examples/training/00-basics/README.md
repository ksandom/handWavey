# handWavey Training - The basics

This configuration is for training you to:

* Confidently use the zones.
* Confidently move the mouse, and get it to where you want quickly, and accurately.
* Confidently recognise when you are out of bounds, and what to do about it.
* Confidently work within the limitations of the leapmotion device.

There are step-by-step instructions to give you a feel for each exercise. And explanations to you can understand why, and get the best out of it.

This training is in the format of an [example configuration](https://github.com/ksandom/handWavey/blob/main/docs/user/exampleConfigurations.md) that you can apply. It intentionally does not perform any actions like mouse clicks, or key presses. This way you can experiment freely and get comfortable with it in a relaxed setting.

Make sure you have your sound on so you can hear the feedback.

## Exercise - Basic movement

1. Start by placing your hand about 30cm above the sensor.
1. Try moving your hand around and see what happens. Things to notice:
    * When you move your hand up and down, the mouse cursor will go up and down.
    * When you move your hand side to side, the mouse cursor will go side to side.
    * When you move your hand closer and further away you get different behaviour, and different piano noises along with it. These represent the zones.
1. Play around with this for a while and get comfortable being able to:
    * move the mouse cursor to where you want it to go.
    * move into the zone you want to.
    * move the mouse cursor while staying in the zone that you intend to be in.

### Explaining the zones

There are 3 main zones that you need to know:

* **Active** - When your hand is directly above the sensor where it is most accurate. This is where you move the mouse cursor around.
* **None** - When you hand is closer to you than the active zone. This gives you more possibilities that we'll talk about later. But for now, just know that you can still perform clicks and other actions here, but the mouse cursor does not move.
* **Action** - Not used as much as I originally thought it would be. But can be used to trigger a difficult action just by entering your hand into the zone. Eg a tripple click to select a large amount of text. You don't need to think about this for now.

## Exercise - Multi-step movements

1. Choose a goal for where you want to move the mouse.
1. Start by placing your hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Move your hand slowly to get the mouse cursor part way there, but don't go the whole way.
1. Move your hand closer towards you into the "None" zone.
1. Now, while keeping your hand in the "None" zone, move your hand back to the centre area, or perhaps even a little beyond.
1. Now move your hand forwards to return it into the "Active" zone.
1. Move your hand horizontally/vertically to continue moving the mouse cursor to your goal.

### Explaining the multi-step movement

While the "Active" zone moves the mouse, the "None" zone does not. This is like lifting your finger off a touch pad on a laptop to then do another swipe to continue the intended movement. There are 3 big advantages of this:

* It removes the need to be accurate first time and makes it easy for you to keep your hand in a comfortable position.
* It keeps the device tracking your hand so that you can operate it much more quickly. There is a small delay every time the device has to find your hand again. So if it doesn't need to find your hand again, we don't have to wait for that delay again.
* It makes it easy to maintain a gesture so that you can do things like perform a drag.


## Exercise - Where are the boundaries?

1. Start by placing your hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Move your hand side to side slowly. Things to notice:
    * How far can you go in each direction until mouse cursor stops responding?
    * How far do you have to bring it back before it starts responding again?
    * Does the mouse cursor jump around or behave erratically in any specific locations?
1. Bring your hand back to the centre about 30 CM above the sensor.
1. Move you hand slowly down. Things to notice:
    * When does the mouse cursor stop moving down?
    * How far do you have to raise it back up again before the mouse cursor starts responding again.
1. Bring your hand back to the centre about 30 CM above the sensor.
1. Move your hand slowly up. Things to notice:
    * How far can you raise your hand before the cursor stops responding?
    * How far do you have to bring it back before it starts responding again?
    * Does the mouse cursor jump around or behave erratically in any specific locations?

### Explaining the boundaries

In every dimension, the sensor has areas that are very accurate, and areas that are much less accurate. If we try to do precise things with imprecise data, the experience gets frustrating very quickly. So handWavey intentionally cuts down the usable area to give us the best chance of success.

The leapmotion sensor has two cameras facing out from the device. These can see out in a cone shape. IE Only a small area is visible very close to the cameras, and a much larger area is visible further away. Being in the centre of that cone will give the best results.

When the hand is too close to the cameras; they may not be able to see all of the hand, it may be out of focus, or it may be over exposed. All of which prevent the leapmotion software from accurately measuring the hand.

Conversely, when the hand is too far away from the device, there is not much resolution (detail) available to represent the hand, and it may be out of focus. So it is hard for the leapmotion software to accurately measure the hand there as well.

Therefore handWavey cuts down the usable area in the following ways:

* Narrowing the cone. - How far you can move your hand on the X, and Z axis (horizontally).
* Minimum height. - How close you can move your hand.
* Maximum height. - How far away you can move your hand.

If any of these are exceeded, handWavey will behave as if the hand isn't there at all.

These can all be configured in physicalBoundaries.yml. Take care expanding this as it can dramatically worsen your experience. Also note that the narrowing of the cone is limited in the shape of a rectangle rather than circle. This was simply for practicality for how we are actually using the data.


## Summary

Phew! That's enough for this lesson. It was actually by far the biggest lesson. But should give you a really good foundation for the next steps. I suggest trying these exercises as many times as it takes to be comfortable with them. You can always come back to this lesson at any time.
