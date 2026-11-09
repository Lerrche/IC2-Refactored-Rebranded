# Getting Started

This guide explains how to set up your Minecraft 1.21.1 / NeoForge development environment to create an IC2 Rebranded add-on.

---

## 1. Prerequisites

- **Java Development Kit (JDK) 21** or higher.
- A modern IDE: **IntelliJ IDEA** (recommended) or VS Code.
- A standard NeoForge 1.21.1 mod project.

---

## 2. Setting Up `build.gradle`

In your add-on project's `build.gradle`, include the NeoForge mod development plugin and declare your dependency on IC2 Rebranded:

```groovy
plugins {
    id 'java-library'
    id 'net.neoforged.moddev' version '2.0.141'
}

group = 'com.example.myaddon'
version = '1.0.0'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

neoForge {
    version = '21.1.93' // Target NeoForge 1.21.1

    runs {
        client {
            client()
        }
        server {
            server()
        }
    }

    mods {
        myaddon {
            sourceSet sourceSets.main
        }
    }
}

repositories {
    mavenCentral()
    // Local flatDir or your team Maven repository
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    // Depend on IC2 Rebranded JAR (in your libs/ folder or Maven)
    implementation "me.lerche.ic2r.core:ic2r:1.2.1"
}
```

---

## 3. Configuring `neoforge.mods.toml`

Declare IC2 as a required dependency in `src/main/resources/META-INF/neoforge.mods.toml` so NeoForge ensures IC2 loads before your add-on:

```toml
modLoader = "javafml"
loaderVersion = "[4,)"
license = "MIT"

[[mods]]
modId = "myaddon"
version = "1.0.0"
displayName = "My IC2 Addon"
description = "A custom add-on for IC2 Rebranded."

# Require IC2 Rebranded
[[dependencies.myaddon]]
modId = "ic2"
type = "required"
versionRange = "[1.0.3,)"
ordering = "AFTER"
side = "BOTH"

# Require Minecraft
[[dependencies.myaddon]]
modId = "minecraft"
type = "required"
versionRange = "[1.21.1,1.22)"
ordering = "NONE"
side = "BOTH"
```

---

## 4. Next Steps

Once your project builds, proceed to:
1. **[Chapter 1: Registration with `Ic2AddonContent`](guides/registration.md)** to register your first items, blocks, and creative mode tab.
2. **[Complete Example Add-on Mod](example-addon.md)** to see a full working mod template.
