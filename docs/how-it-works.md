# MobsManager - How It Works

This page explains the core behavior of the plugin.

## Default Behavior

MobsManager blocks the spawn of entities that you have disabled through its data file and commands.
Rules are applied per world, so each world can keep its own spawn policy.

!!! note
    Existing entities are not removed automatically just because future spawning has been disabled.

## Spawn Categories

MobsManager groups Minecraft spawn reasons into a small set of categories:

- `ALL`: blocks every kind of spawn
- `NATURAL`: blocks natural or world-driven spawning
- `CUSTOM`: blocks command, plugin, transformation, or other non-natural spawning
- `SPAWNER`: blocks spawner-driven spawning, including modern spawner variants
- `EGG`: blocks spawn egg and related egg-based spawning
- `BREEDING`: blocks animal breeding results
- `IRON_GOLEM`: blocks spawns triggered by building an iron golem

## Newer Minecraft Spawn Reasons

Minecraft and Spigot keep introducing new spawn reasons. MobsManager maps newer reasons to the existing categories so that older configurations remain usable without being rewritten from scratch.

Examples:

- `TRIAL_SPAWNER` is treated as `SPAWNER`
- `DISPENSE_EGG` is treated as `EGG`
- `COMMAND` is treated as `CUSTOM`
- `REINFORCEMENTS` is treated as `NATURAL`

## World-by-World Management

Each entity is tracked per world. This means you can allow a mob in one world while blocking the same mob in another.
