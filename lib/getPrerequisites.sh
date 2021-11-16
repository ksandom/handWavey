#!/bin/bash
# Get everything needed to run.

command="$(basename "$(pwd)")"

function fail
{
  echo "$1" >&2
  exit 1
}

function showRequiredParameters
{
  echo "Run like this
  $command pathToExtractedLeapMotionSDK
  " >&2
  echo "Eg
  $command \"/tmp/LeapDeveloperKit_2.3.1+31549_linux/LeapSDK\"" >&2
  echo
}

SDKPath="$1"



# Run safety checks.
if [ "$SDKPath" == '' ]; then
  if [ -e ".sdkPath" ]; then
    SDKPath="$(cat .sdkPath)"
  else
    showRequiredParameters
    fail "Need a single parameter as described above."
  fi
fi

if [ "$2" != '' ]; then
  showRequiredParameters
  fail "Unexpected second parameter. Do you have a space in your path, and not quote it?"
fi

if [ ! -e "$SDKPath" ]; then
  fail "\"$SDKPath\" does not appear to exist."
fi

if [ ! -e "$SDKPath/version.txt" ]; then
  fail "Expected to find version.txt inside of \"$SDKPath\". Maybe this isn't the root of the extracted archive?"
fi

# Save it for later so we don't have to specify it every time.
echo "$SDKPath" > .sdkPath

version="$(cat "$SDKPath/version.txt")"


# Copy prerequisites

for file in lib/x64/{libLeap.so,libLeapJava.so} lib/LeapJava.jar; do
  cp -v "$SDKPath/$file" .
done
