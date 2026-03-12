# MobsManager - Commands and Permissions

Make sure you have already [installed](installation.md) the plugin before using this command reference.

This page documents the current `/mm` command set and the related permission nodes.

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/mm reload` | `mobsmanager.reload` | Reloads configuration and rebuilds in-memory entity data. |
| `/mm enable <Mob> <SpawnReason> <World>` | `mobsmanager.manageEntity` | Enables one spawn category for an entity. |
| `/mm disable <Mob> <SpawnReason> <World>` | `mobsmanager.manageEntity` | Disables one spawn category for an entity. |
| `/mm info <Mob> <World>` | `mobsmanager.info` | Displays the current spawn configuration for one entity. |
| `/mm status <Mob> <World>` | `mobsmanager.info` | Alias of `/mm info`. |
| `/mm list <World> [Mode] [Page]` | `mobsmanager.info` | Lists entities for a world. Modes: `changed`, `blocked`, `all`. |
| `/mm help` | `mobsmanager.help` | Displays the built-in command help. |

## Permission Nodes

| Permission | Description |
| --- | --- |
| `mobsmanager.manageEntity` | Allows enabling or disabling spawn categories. |
| `mobsmanager.reload` | Allows reloading the plugin. |
| `mobsmanager.help` | Allows viewing command help. |
| `mobsmanager.info` | Allows reading mob spawn information. |

## Notes

- Entity names use Bukkit `EntityType` names.
- Legacy entity names are still recognized by the compatibility layer.
- Use `*` as the world argument when a command supports targeting all worlds.
- `/mm list <World>` shows changed entities by default.
- `/mm list <World> blocked` shows only restricted entities.
- `/mm list <World> all` shows every managed entity for the world.
