# handWavey Training - Look ma, 2 hands!

At this point you are able to move the mouse around, and do gestures. You're awesome!

Did you know you can do it with two hands? If there are any lessons that you are going to skip, this is the one, because not many [gestureLayouts](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts) require two hands. Although several get more powerful with two hands. For me, in my use-cases, I've found that I don't use it (for now... This might chance soon ;-) ).

Having said that. There are some reasons why the ability to use two hands is really useful:

* The [reducedMobility](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts/reducedMobility) gestureLayouts use two hands so that people with reduced mobility don't have to do gestures that they might find difficult.
* In some gestureLayouts it brings much more power to your finger tips.

This training is in the format of an [example configuration](https://github.com/ksandom/handWavey/blob/main/docs/user/exampleConfigurations.md) that you can apply. It intentionally does not perform any actions like mouse clicks, or key presses. This way you can experiment freely and get comfortable with it in a relaxed setting.

## Exercise - Which is your primary hand?

1. Start by placing your **left** hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Move your hand around.
1. Add your right hand.
1. Move each hand around. Things to notice:
    * Which hand affects the cursor?
    * Notice that both hands can perform gestures.
1. Remove both hands.
1. Place your **right** hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Move your hand around.
1. Add your left hand.
1. Move each hand around. Things to notice:
    * Which hand affects the cursor?

### Explaining which hand is primary

The first hand you introduce is your primary hand. It will stay your primary hand as long as at least one hand is present. Next time you re-introduce any hands, the first one will be primary regardless of which one was primary a moment ago.

This means that multiple people can each use the same computer one after the other, and use their preferred hand.

Both hands can perform gestures. But it is up to the [gestureLayout](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts) to decide what gets done with each hand's gesture.

## Exercise - Don't cross the beams!

1. Start by placing your favourite hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Now add your second favourite hand.
1. Move your primary hand above your secondary hand. Things to notice:
    * What happens to the mouse cursor?
    * What happens when you un-cross your hands?
    * Can you perform gestures?

### Explaining crossing your hands

The sensor sees in a direct line from the sensor to your hand. If one hand is below the other hand, the sensor will not be able to see the upper hand.
