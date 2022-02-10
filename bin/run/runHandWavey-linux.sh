#!/bin/bash
# Start handWavey on Linux regardless of where the script is run from.

delayBetweenRuns=1
scriptPath="$(dirname "$(realpath "$0")")"

if ! cd "$scriptPath"; then
    echo "Something went wrong while determining the script path: \"$scriptPath\"." >&2
    exit 1
fi
cd ../.. || exit 1

if [ ! -e ./gradlew ]; then
    echo "Can not see ./gradlew from here. Did you copy the script rather than link to it?" >&2
    exit 1
fi

# This is currently run in a loop because the leap library is occasionally segfaulting. This gets it back up and running. The delay stops it from thrash the CPU if at some point it starts fast-failing on startup. # Details here: https://github.com/ksandom/handWavey/issues/6
while sleep "$delayBetweenRuns"; do
    ./gradlew run
done
