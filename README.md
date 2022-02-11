# handWavey

A more intuitive/usable desktop control using leapmotion/ultramotion.

If you use it, I'd love to [hear from you](https://github.com/ksandom/handWavey/issues/4).

## Requirements

### Hardware 

* [LeapMotion controller](https://www.ultraleap.com/product/leap-motion-controller/#pricingandlicensing).

### Softare

* [LeapMotion/UltraLeap SDK](https://github.com/ksandom/installUltraleap).
* Java. One of:
    * [OpenJDK](https://openjdk.java.net/install/).
    * Or [Oracle's Java](https://www.oracle.com/java/technologies/downloads/).

## Run

### Linux

1. Satisfy the [requirements](#Requirements).
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `./gradlew test`
1. Run it:
   `./gradlew run`

### Windows

1. Satisfy the [requirements](#Requirements).
1. `copy build.gradle.windows build.gradle`
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `gradlew.bat test`
1. Run it:
   `gradlew.bat run`

### MacOS

This is theoretically possible, but has not been tested.

1. Satisfy the [requirements](#Requirements).
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `./gradlew test`
1. Run it:
   `./gradlew run`

## Install

### Linux

### Windows

TODO - [Ticket](https://github.com/ksandom/handWavey/issues/2).

### MacOS

This is theoretically possible, but has not been tested.

TODO - [Ticket](https://github.com/ksandom/handWavey/issues/1).
