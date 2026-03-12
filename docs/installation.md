# MobsManager - Installation

Make sure you have already [downloaded](download.md) the plugin before following these steps.

## Installation Steps

1. Download the `MobsManager-X.X.X.jar` file.
2. Place it in your server's `plugins` directory.
3. Start or restart the server.

If everything loads correctly, the console should show a message similar to:

```text
[MobsManager] Enabling MobsManager vX.X.X
```

## After Startup

On first start, the plugin generates its configuration files and initializes per-world entity data.

If you are updating from an older version, MobsManager may migrate older entity names automatically so your stored configuration stays compatible with newer APIs.

## If Startup Fails

- verify that your server software is compatible
- verify that the server is running Java 17+
- check the startup log for dependency or API errors
- ask for help on Discord if needed
