# Gesture layouts

This is the place to share (via a pull request) and find other gesture layouts that might suit you better.

## Users trying layouts found here

1. Copy the `.yml` files into your configuration directory.
   On linux, you can do it with a command like this:
   ```
   cp *yml ~/.config/handWavey
   ```
1. Restart handWavey.

## People creating new layouts

### Considerations

#### Tools to assist you.

* You may want to set `saveBackConfig` in `main.yml` to `false` while writing your layout.

#### What to include in your gestureLayout

* You should include all of the files that are relevant to your layout.
    * At least:
        * `actionEvents.yml`
        * `audioEvents.yml`
    * You may want to include:
        * `gestureConfig.yml`
    * Should not include:
        * `main.yml`
* You should run `../../../utils/prepareEventConfig.sh` to clean out non-essential values that could cause confusion during development.
* The default values in the template are the ones that get created by default if they don't exist. If you don't want them, you need to set them to `""` so that they don't get recreated.

#### Ergonomics and reliability

* Consider both, what is comfortable, and likely to minimise the chances of long term injury. Those aren't necessarily the same thing.
    * Is the gesture easy to do without straining?
        * Just because it is for you, doesn't mean that it will be for everybody.
    * Is it easy to be precise without straining or tensing up?
    * Are the relevant joins/ligaments/other prone to injury from [repetitive strain](https://en.wikipedia.org/wiki/Repetitive_strain_injury)?
* What is easy for the device to actually recognise from its expected location?
    * When pitch gets above about 45 degrees; open/closed data starts becoming unreliable.
    * When roll gets close to 90 degrees; Y data, open/closed data starts becoming unreliable.
    * When the hand is upside down, open/closed data usually works, but is less reliable.
