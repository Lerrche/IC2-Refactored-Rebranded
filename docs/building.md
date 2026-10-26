# Building IC2 Refactored

## Windows PowerShell

Run from the repository root:

```powershell
.\gradlew.bat spotlessApply
.\gradlew.bat build
```

The project uses the Spotless Gradle plugin with Google Java Format for Java sources and whitespace/newline cleanup for selected project files. `spotlessApply` applies those formatting rules. It does not compile the mod or prove that the code is correct, so always run `build` afterward.

## Linux or macOS

```bash
./gradlew spotlessApply
./gradlew build
```

## Find the JAR

After a successful build, check:

```text
build/libs/
```

Use the regular mod JAR for installation/testing. If multiple artifacts are present, read the filenames and avoid source, Javadoc, or development artifacts unless you specifically need them.

## Run the development client

Windows:

```powershell
.\gradlew.bat runClient
```

Linux/macOS:

```bash
./gradlew runClient
```

## If the build fails

1. Run `spotlessApply` first.
2. Run `build` again and capture the complete output.
3. Fix the first compilation/test error rather than only the final `BUILD FAILED` line.
4. Check the Java version and the Minecraft/NeoForge versions configured in `gradle.properties`.

Formatting can fix a formatting-check failure, but it does not normally resolve missing imports, missing classes, or incompatible APIs.
