# Windows Build Setup Guide

This guide helps you set up the build environment for CIT Resewn on Windows.

## Prerequisites

### 1. Install Java 21

Minecraft 1.21+ requires Java 21. Download and install from:
- **Recommended**: [Adoptium Eclipse Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21)
- Choose "Windows x64" installer (`.msi`)
- During installation, enable "Add to PATH" option

### 2. Verify Java Installation

Open PowerShell or Command Prompt and run:

```powershell
java -version
```

You should see something like:
```
openjdk version "21.0.x" 2024-xx-xx
OpenJDK Runtime Environment Temurin-21.0.x+x (build 21.0.x+x)
OpenJDK 64-Bit Server VM Temurin-21.0.x+x (build 21.0.x+x, mixed mode, sharing)
```

### 3. Find Java Installation Path

Run in PowerShell:
```powershell
where.exe java
```

Or check environment variable:
```powershell
$env:JAVA_HOME
```

Common installation paths:
- `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot`
- `C:\Program Files\Java\jdk-21`
- `C:\Users\YourName\.jdks\temurin-21.0.x`

## Configuration

### Method 1: Using gradle.properties (Recommended)

1. Open `gradle.properties` in the project root

2. Uncomment and update the Java home path:

```properties
# Before:
#org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.6.7-hotspot

# After (adjust to your actual path):
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.6.7-hotspot
```

**Important**:
- Use double backslashes `\\` in the path
- Remove the `#` at the beginning of the line
- Make sure the path exists on your system

3. Save the file

### Method 2: Set JAVA_HOME Environment Variable

**Temporary (current session only)**:

In PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
```

In Command Prompt:
```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot
```

**Permanent (recommended)**:

1. Press `Windows + X` and select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "System variables", click "New"
5. Variable name: `JAVA_HOME`
6. Variable value: `C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot`
7. Click "OK" on all dialogs
8. **Restart your terminal/IDE** for changes to take effect

### Method 3: Use Gradle Wrapper with --java-home

If you don't want to set environment variables:

```powershell
./gradlew :1.21.4:build --java-home="C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
```

## Building the Project

### Build for Minecraft 1.21.4

```powershell
./gradlew :1.21.4:build
```

### Build for all versions

```powershell
./gradlew build
```

### Build for specific version

```powershell
# For 1.21
./gradlew :1.21:build

# For 1.20.4
./gradlew :1.20.4:build
```

### Clean build

```powershell
./gradlew clean :1.21.4:build
```

## Common Issues and Solutions

### Issue 1: "Minecraft 1.21 requires Java 21 but Gradle is using XX"

**Cause**: Gradle is using an older Java version

**Solutions**:
1. Set `org.gradle.java.home` in `gradle.properties` (see Method 1 above)
2. Set `JAVA_HOME` environment variable (see Method 2 above)
3. Make sure you restarted your terminal after setting environment variables

### Issue 2: "Could not remap sources"

**Symptom**: Warning during `remapSourcesJar` task with `ArrayIndexOutOfBoundsException`

**Impact**: This is usually just a warning and doesn't affect the build output

**Solution**: If the build completes successfully (says "BUILD SUCCESSFUL"), you can ignore this warning. The JAR file will still work.

### Issue 3: Gradle daemon using wrong Java version

**Solution**: Kill existing Gradle daemons and restart:

```powershell
./gradlew --stop
./gradlew :1.21.4:build
```

### Issue 4: "Access Denied" or "Permission Denied"

**Solution**: Run your terminal as Administrator

### Issue 5: Path not found

**Solution**: Verify the path exists:

```powershell
Test-Path "C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
```

Should return `True`. If it returns `False`, check your installation path.

## Build Output Locations

After a successful build, you'll find the JAR files:

- **Main mod**: `versions/1.21.4/build/libs/citresewn-1.2.2+1.21.4.jar`
- **Sources**: `versions/1.21.4/build/libs/citresewn-1.2.2+1.21.4-sources.jar`

## IDE Setup (Optional)

### IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. File → Project Structure (Ctrl+Alt+Shift+S)
3. Project → SDK → Add SDK → JDK
4. Navigate to your Java 21 installation
5. Click "OK"
6. Reload Gradle project (View → Tool Windows → Gradle → Reload)

### VS Code with Gradle Extension

1. Install "Gradle for Java" extension
2. Open settings (Ctrl+,)
3. Search for "java.configuration.runtimes"
4. Add Java 21 to the list:
```json
"java.configuration.runtimes": [
  {
    "name": "JavaSE-21",
    "path": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.6.7-hotspot",
    "default": true
  }
]
```
5. Reload window (Ctrl+Shift+P → "Reload Window")

## Testing Your Setup

Run this command to verify everything works:

```powershell
./gradlew :1.21.4:compileJava
```

If successful, you should see:
```
BUILD SUCCESSFUL in Xs
```

## Getting Help

If you're still experiencing issues:

1. Check the main [README.md](README.md) for general information
2. Review [MIGRATION_GUIDE_1.21.4.md](MIGRATION_GUIDE_1.21.4.md) for technical details
3. Check [TODO_1.21.4.md](TODO_1.21.4.md) for known issues
4. Create an issue on GitHub with:
   - Your Java version (`java -version`)
   - Your Gradle version (`./gradlew --version`)
   - Full error message
   - Operating system version

## Quick Reference

| Command | Description |
|---------|-------------|
| `java -version` | Check Java version |
| `./gradlew --version` | Check Gradle version |
| `./gradlew :1.21.4:build` | Build for 1.21.4 |
| `./gradlew clean` | Clean build files |
| `./gradlew --stop` | Stop Gradle daemon |
| `./gradlew tasks` | List all available tasks |

## Next Steps

After successful build:
1. Test the mod in Minecraft 1.21.4
2. Check [TODO_1.21.4.md](TODO_1.21.4.md) for remaining work
3. Report any issues you encounter
