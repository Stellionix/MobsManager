# MobsManager - Configuration

Make sure you have already [installed](installation.md) the plugin before editing its configuration.

MobsManager uses two configuration files:

- `config.yml` for global plugin settings
- `mobsData.yml` for per-world entity spawn rules

After changing `config.yml`, run `/mm reload` or restart the server.

## Global Options

| Key | Type | Default | Description |
| --- | --- | --- | --- |
| `auto-update` | boolean | `true` | Enables the built-in updater at startup. |
| `world-guard-detection` | boolean | `false` | Avoids spawn conflicts when WorldGuard already controls spawn behavior. |

## Data File

`mobsData.yml` stores the effective spawn rules for each entity in each world.

The plugin maintains this file automatically. In most cases, administrators should prefer plugin commands over manual edits.

## Compatibility Notes

Older saved entity names are normalized automatically when the plugin loads. This helps legacy configurations continue to work on newer Spigot APIs without manual cleanup.

## Operational Recommendation

If you are making several changes in a row, prefer editing your global settings first, then use commands to fine-tune world or entity behavior.
