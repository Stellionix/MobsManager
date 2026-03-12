# Contributing

## Scope

MobsManager is a Bukkit/Spigot/Paper plugin for controlling mob spawn behavior per world. Contributions should stay focused on plugin behavior, compatibility, reliability, and maintainability.

## Setup

Requirements:

- Java 17
- Git

Build locally:

```bash
./gradlew build
```

Run tests:

```bash
./gradlew test
```

## Workflow

1. Create a branch from your target base branch.
2. Make focused changes with clear commit messages.
3. Add or update tests when behavior changes.
4. Run `./gradlew build` before opening a pull request.
5. Open a pull request with a short explanation of what changed and why.

## Expectations

- Keep compatibility with the supported Java and Spigot API version declared in the repo.
- Prefer small, reviewable pull requests over broad refactors.
- Preserve existing config behavior unless the change explicitly introduces a migration path.
- If you change commands, config, or compatibility behavior, update the documentation too.

## Testing

At minimum, verify:

- `./gradlew build` passes
- unit tests pass
- command or config behavior affected by your change is covered by tests when practical

GitHub Actions also runs the build and test workflow on every branch and pull request.

## Reporting Changes

A pull request description should mention:

- the problem being solved
- the chosen approach
- any config, command, or compatibility impact
- any follow-up work that remains
