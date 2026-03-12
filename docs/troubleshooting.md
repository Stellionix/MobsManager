# MobsManager - Troubleshooting

This page covers the most common issues you may encounter while installing or operating MobsManager.

## The Plugin Does Not Load

- verify that your server is running a compatible Bukkit-based implementation
- verify that your server uses Java 17+
- check the startup logs for missing dependencies or API incompatibilities

## My Existing Configuration Changed After Update

This is usually expected when upgrading from older versions.

MobsManager now normalizes legacy entity names to current API names. After startup or after `/mm reload`, review `mobsData.yml` if you want to confirm how old names were migrated.

## Spawns Are Still Not Behaving As Expected

- verify that you changed the correct world
- verify that you changed the correct spawn category
- verify that another plugin is not also controlling mob spawns
- if WorldGuard is installed, review the `world-guard-detection` setting

## The Updater Does Not Behave As Expected

MobsManager compares versions by numeric segments.

If you are testing an unreleased development version or a new major version, auto-update may intentionally refuse to download the remote build.

## Still Stuck?

If the issue persists, open an issue on GitHub or ask for help on Discord.
