# Install on Linux

TODO: This can be made a lot better. Eg this should be pretty easy to automate using [the `~/.config/autostart` method](https://stackoverflow.com/questions/8247706/start-script-when-gnome-starts-up). There is [a ticket for improving the installation on Linux](https://github.com/ksandom/handWavey/issues/5).

## Steps

1. Satisfy the requirements listed in [the README.md](https://github.com/ksandom/handWavey#requirements).
1. Satisfy the steps to be able to run it listed in [the README.md](https://github.com/ksandom/handWavey#linux).
1. Browse to where you ran `./gradlew run`.
1. Run `echo "$(pwd)/bin/run/runHandWavey-linux.sh"`
    You'll use the output of this in the next step.
1. Follow the steps in the relevant section from the [Specific environments](#specific-environments) below.

## Specific environments

### KDE

1. Follow these [instructions for starting a command on startup](https://www.simplified.guide/kde/automatically-run-program-on-startup) to start the command you generated just a moment ago.
    * Skip "Add application" (Steps 5, and 6 as of 2022-02-10.)

You can stop it from starting up on login by removing the entry that you created in these instructions.

### Gnome

1. Follow [instructions for starting a command on startup](https://stackoverflow.com/questions/8247706/start-script-when-gnome-starts-up) to start the command you generated just a moment ago.

You can stop it from starting up on login by removing the file that you created in these instructions.

### Systemd --user

[This appears to not be viable](https://wiki.archlinux.org/title/systemd/User#How_it_works) for this project because handWavey needs to be tied to the session:

> Be aware that the systemd --user instance is a per-user process, and not per-session. The rationale is that most resources handled by user services, like sockets or state files will be per-user (live on the user's home directory) and not per session. This means that all user services run outside of a session. As a consequence, programs that need to be run inside a session will probably break in user services.
