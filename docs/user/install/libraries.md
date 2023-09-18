# Ultraleap libraries

handWavey requires libraries to talk to the LeapMotion/Ultraleap controller. Last time I checked, the terms and conditions attached to those libraries were too restrictive for me to include them with the project. Therefore we need to get them separately.

## Manual installation

1. [Get the SDK installed](https://github.com/ksandom/installUltraleap).
    * Those instructions tell you where the library files are
      > The files that you will need for java applications are located in:
1. Copy the files in each directory (but not the directories themselves) to lib/ . [Where is my lib/ directory?](#where-is-my-lib-directory)

## Using the tools

If you're on linux, you can install the libraries automatically once you've [installed the LeapSDK](https://github.com/ksandom/installUltraleap). I welcome [pull-requests](https://github.com/ksandom/handWavey/pulls) for Windows and MacOS.

### Steps

1. [Get the SDK installed](https://github.com/ksandom/installUltraleap).
1. Run [getPrerequisites.sh](#getprerequisites.sh).

### Tools

#### getPrerequisites.sh

This will copy everything that is relevant in one easy step.

```bash
./getPrerequisites.sh /path/to/LeapSDK
```

where `/path/to/LeapSDK` is where you extracted the LeapSDK. Eg:

```bash
./getPrerequisites.sh ~/Downloads/LeapDeveloperKit_2.3.1+31549_linux
```

#### clean.sh

This removes library files from lib/. This is probably only useful if you want to test getPrerequisites.sh.

Run it from within the lib/ directory like this:

```bash
./clean.sh
```

## Where is my lib directory

When you've cloned/downloaded the handWavey source code, you should see lib in the handWavey folder. It should contain 3 files

```bash
$ ls -1 lib
clean.sh
getPrerequisites.sh
README.md
```
