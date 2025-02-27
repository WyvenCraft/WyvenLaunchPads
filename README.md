# WyvenLaunchPads

A Minecraft plugin that creates custom launchpads using slime blocks to launch players to specified locations.

## Features

- Create custom launchpads with specified target locations
- Link multiple slime blocks to a single launchpad
- Safe landing system with no fall damage
- Simple command interface
- Persistent data storage

## Commands

- `/launchpad create <name>` - Creates a new launchpad at your location
- `/launchpad target <name>` - Sets the target destination for a launchpad
- `/launchpad link <name>` - Enters linking mode to connect slime blocks to a launchpad
- `/launchpad delete <name>` - Removes a launchpad and its linked blocks

## Usage

1. Create a launchpad using `/launchpad create <name>`
2. Set its destination using `/launchpad target <name>`
3. Use `/launchpad link <name>` and right-click slime blocks to connect them
4. Players will be launched to the target location when stepping on linked slime blocks

## Requirements

- Bukkit/Spigot server
- Minecraft version: 1.13+
- Java 17 or higher