<h1 align="center">
    <img src="mobsmanager-logo.png" alt="MobsManager" width="800" /><br>
</h1>

<p align="center">
    Manage mob spawning on your Minecraft server with precise per-world controls.
</p>

<p align="center">
    <img src="http://cf.way2muchnoise.eu/full_322365_downloads.svg" alt="Downloads"/>
    <a href="https://github.com/stellionix/MobsManager/actions/workflows/ci.yml"><img src="https://github.com/stellionix/MobsManager/actions/workflows/ci.yml/badge.svg" alt="CI"/></a>
    <img src="https://img.shields.io/github/license/stellionix/MobsManager" alt="License"/>
    <img src="https://img.shields.io/github/last-commit/stellionix/MobsManager" alt="Last commit"/>
</p>

## Overview

MobsManager gives server administrators direct control over how mobs spawn, world by world.
Enable or disable natural spawns, custom spawns, spawner-based spawns, egg spawns, breeding, and iron golem spawning with simple commands.

It is designed for Bukkit, Spigot, and Paper servers that need clear, reliable mob management without heavy setup.

## Why MobsManager

- Control mob spawning per world instead of applying one global rule everywhere
- Adjust specific spawn categories without editing complex data files by hand

## Features

- Per-world mob spawn management
- Support for Bukkit, Spigot, and Paper
- Support for modern Spigot APIs
- Compatibility layer for legacy entity names
- Automatic migration of old stored entity names to current API names
- Command-based administration with tab completion

## Commands

- `/mm help` displays the available commands
- `/mm reload` reloads the configuration and rebuilds in-memory data
- `/mm info <Entity> <World>` shows the spawn settings for one entity
- `/mm status <Entity> <World>` alias of `/mm info`
- `/mm list <World>` lists managed entities for a world
- `/mm enable <Entity> <SpawnReason> <World>` enables a spawn category
- `/mm disable <Entity> <SpawnReason> <World>` disables a spawn category

## Spawn Categories

MobsManager currently supports these spawn categories:

- `ALL`
- `CUSTOM`
- `NATURAL`
- `SPAWNER`
- `EGG`
- `BREEDING`
- `IRON_GOLEM`

Newer Minecraft spawn reasons are mapped to these categories so existing configurations remain usable.

## Download

- CurseForge: https://www.curseforge.com/minecraft/bukkit-plugins/mobsmanager
- Bukkit: https://dev.bukkit.org/projects/mobsmanager

## Statistics

<img align="center" src="https://bstats.org/signatures/bukkit/MobsManager.svg" alt="Statistics"/>

More stats are available [here](https://bstats.org/plugin/bukkit/MobsManager/15773).

## Development

Contribution guidelines are available in [CONTRIBUTING.md](CONTRIBUTING.md)
