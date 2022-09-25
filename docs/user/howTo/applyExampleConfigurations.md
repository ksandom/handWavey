# How to apply example configurations

There are lots of [example configurations](https://github.com/ksandom/handWavey/tree/main/examples) that you can try.

In most cases you can drop the files into your [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

## How to install an example configuration

1. Backup your existing configuration.
2. Copy the `.yml` files into your [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).
   On linux (and probably macOS), you can do it with a command like this:
   ```
   cp *yml ~/.config/handWavey
   ```
   On windows, you can do it like this:
   ```
   copy *.yml %USERPROFILE%\handWavey
   ```
3. Restart handWavey.

## How to reset back to the defaults

1. Get the configurations back to the desired state. Two ways that you can do this are:
    * Restore your backup. OR
    * Delete any file you want to reset. It will be re-generated when handWavey is restarted.
2. Restart handWavey.
