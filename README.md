# IC2 Refactored (Rebranded)

An unofficial continuation of the IndustrialCraft 2 port for Minecraft 1.21.1 on NeoForge.

I started working on this because I wanted to improve the original port with more mod compatibility, some quality-of-life changes, and a better way for other modders to make addons without having to rewrite the mod's code.

This project is based on the original port by [neo-industrial-mc/IC2R](https://github.com/neo-industrial-mc/IC2R). I did **not** make the original port and I do not claim ownership of the original IC2 code or assets. Most of my work is focused on changes and additions on top of it.

## What I'm working on

- Compatibility between IC2 energy systems and other tech mods.
- An addon API for creating custom machines, generators, cables and other electric blocks.
- Support for custom electric tools, mining tools, weapons and armor effects.
- Quality-of-life changes and fixes.
- Making it easier to extend IC2 without having to modify its internal code every time.

The main idea is to let addon developers make their own content and have it work with IC2's systems, instead of needing to rewrite everything from scratch.

## Current status

This is still a work in progress. Some features may need more testing, and the API can change while I'm working on it.

Don't expect everything to work perfectly yet. If you find a bug, feel free to open an issue with the error log and steps to reproduce it.

## Requirements

- Minecraft 1.21.1
- NeoForge
- Java 21

## Building from source

Clone or download the repository, then run:

```powershell
.\gradlew.bat build
```

To launch the development client:

```powershell
.\gradlew.bat runClient
```

## Credits

- Original port: [neo-industrial-mc/IC2R](https://github.com/neo-industrial-mc/IC2R)
- IndustrialCraft 2 and its original developers.

This project is unofficial and is not affiliated with or endorsed by the original IndustrialCraft 2 developers.

## License and ownership

This project contains code derived from the original IC2R port. I do not own the original IC2 code or assets, and this repository does not claim otherwise.

Please check the included `LICENSE` file and the original repository for licensing information. The original project's licensing and the rights to the underlying IC2 code and assets are separate matters.

IndustrialCraft 2 is a trademark of its respective owners, if applicable. This project is a community effort and is not an official release.
