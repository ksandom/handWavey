# handWavey Training - Segments

While the leapmotion device is able to distinguish very fine detail about how you are moving your hands, a common mistake I've see developers make when trying make interfaces like this is to rely on those fine movements, and the users' ability to reproduce them "correctly". Every single one that I tried was either frustrating or limited in what it could do. I wanted this to be both powerful, and easy.

Enter segments.

This training is in the format of an [example configuration](https://github.com/ksandom/handWavey/blob/main/docs/user/exampleConfigurations.md) that you can apply. It intentionally does not perform any actions like mouse clicks, or key presses. This way you can experiment freely and get comfortable with it in a relaxed setting.

## Playing with segments

1. Start by placing your hand about 30cm above the sensor. The mouse cursor should be moving with your hand.
1. Slowly rotate your wrist in one direction. Things to notice:
    * You will hear a piano noise every time you hit another segment.
    * Notice that the first segment that you hit is larger than the next ones.
    * Notice that the cursor doesn't jump around any more when you change segments.

### Explaining segments

Segments define how far you rotate your wrist. The concept is that you divide up the 350 degrees of rotation into segments. In reality, several of those segments are not easy to reach, so we simply don't assign actions to those. Similarly, some segments put your wrist in a position that is hard for the device to measure. Therefore usage of those are minimised as well.

The larger 1st segment is like that because I've mearged it with at least one other. I've done this because we use this segment a lot, particularly for dragging. So it's nice to have a large area to hit to make it easier to trigger actions more reliably.

In this example configuration, I've split the circle into 8 segments, and merged segments 1 and 2 together. If you take a look at gestureConfig.yml, you'll see that there are many more possibilities.

The reason why the cursor doesn't jump around any more is that I've now added the following to actionEvents.yml

```yaml
  general-segment-pAnyChange:
    value: lockCursor();rewindCursorPosition();
```

This is standard. It stops the cursor moving for a few milliseconds while the leapmotion data settles.
