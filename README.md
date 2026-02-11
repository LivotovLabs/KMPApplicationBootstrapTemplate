# KMP Application Bootstrap

This is a Kotlin Multiplatform (KMP) project template with Compose Multiplatform support for Android, iOS, Desktop (JVM), Web (WASM), and Native Console targets.

## Supported Platforms

*   **Android**
*   **iOS**
*   **Desktop (JVM)**: macOS, Linux, Windows
*   **Web (WASM)**: Browser
*   **Console (Native)**: macOS (Arm64/x64), Linux (x64), Windows (MinGW x64)

## Configuration

The project uses `gradle/libs.versions.toml` as the single source of truth for application metadata, versions, and entry points. You can modify the following keys in the `[versions]` section:

*   `app-name`: The display name of the application (used for iOS).
*   `app-appId`: The application ID / Bundle Identifier (e.g., `com.example.app`).
*   `app-versionCode`: The integer version code (used for Android `versionCode` and iOS `CURRENT_PROJECT_VERSION`).
*   `app-versionName`: The semantic version string (used for Android `versionName`, iOS `MARKETING_VERSION`, and Desktop `packageVersion`). **Note:** Must be in `MAJOR.MINOR.BUILD` format (e.g., `1.0.0`) for Windows MSI compatibility.
*   `app-console-entrypoint`: The entry point for the native console application.
*   `app-desktop-entrypoint`: The main class for the desktop application.
*   `android-namespace`: The namespace for the Android module.

### Launcher Icons
Launcher icons for Android and iOS are managed using the `kmp-app-icon-generator` plugin.

1.  **Source Image**: Place your source icon (SVG or high-resolution PNG) at `composeApp/src/commonMain/composeResources/drawable/icon.svg` (or `icon.png`).
2.  **Generate Assets**: Run the following command to generate the platform-specific assets:
    ```bash
    ./gradlew :composeApp:generateIcons --no-configuration-cache
    ```
    *Note: The `--no-configuration-cache` flag is currently required for this task due to a plugin limitation.*

This task automatically populates:
*   **Android**: `androidApp/src/main/res/mipmap-*`
*   **iOS**: `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset`

For **Desktop**, platform-specific icons are stored at `composeApp/src/desktopMain/resources/icons/`. You should replace the following files with your own valid icons:
*   **macOS**: `icon.icns`
*   **Windows**: `icon.ico`
*   **Linux**: `icon.png`

These are configured in `composeApp/build.gradle.kts` under `compose.desktop.application.nativeDistributions`:
```kotlin
nativeDistributions {
    // ...
    val iconsDir = project.file("src/desktopMain/resources/icons")
    macOS {
        iconFile.set(iconsDir.resolve("icon.icns"))
    }
    windows {
        iconFile.set(iconsDir.resolve("icon.ico"))
    }
    linux {
        iconFile.set(iconsDir.resolve("icon.png"))
    }
}
```

#### Desktop Icon Requirements
*   **macOS (`icon.icns`)**:
    *   **Format**: Apple Icon Image (`.icns`).
    *   **Content**: Should include multiple sizes: 16x16, 32x32, 128x128, 256x256, 512x512, and 1024x1024 (for Retina).
    *   **Transparency**: Supported (Alpha channel).
*   **Windows (`icon.ico`)**:
    *   **Format**: Windows Icon (`.ico`).
    *   **Content**: Should include multiple sizes: 16x16, 24x24, 32x32, 48x48, 64x64, and 256x256.
    *   **Transparency**: Supported.
*   **Linux (`icon.png`)**:
    *   **Format**: PNG (`.png`).
    *   **Size**: Recommended 512x512 or higher.
    *   **Transparency**: Supported.

### iOS Configuration
The iOS project configuration file at `iosApp/Configuration/Config.xcconfig` is **auto-generated** from the values in `gradle/libs.versions.toml`.
*   A Gradle task `syncIosConfig` runs automatically before any Kotlin compilation to ensure the iOS config is up-to-date.
*   **Do not modify `iosApp/Configuration/Config.xcconfig` manually**, as your changes will be overwritten.

## Running the Application

### Android
To run the Android application on a connected device or emulator:
```bash
./gradlew :androidApp:installDebug
```
Or open the project in Android Studio and run the `androidApp` configuration.

To build a release APK:
```bash
./gradlew :androidApp:assembleRelease
```
The APK will be located in `androidApp/build/outputs/apk/release/`.

To build an App Bundle (AAB) for Google Play:
```bash
./gradlew :androidApp:bundleRelease
```
The AAB will be located in `androidApp/build/outputs/bundle/release/`.

### iOS
To run the iOS application:
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select a simulator or connected device.
3.  Click the "Run" button (or press `Cmd+R`).

To build for the App Store:
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select a physical device or "Any iOS Device (arm64)".
3.  Go to **Product** > **Archive**.
4.  Once the archive is created, the Organizer window will open, allowing you to validate and distribute the app to TestFlight or the App Store.

### Desktop (JVM)
To run the desktop application:
```bash
./gradlew :composeApp:run
```

To create a release distribution for your current platform:
```bash
./gradlew :composeApp:package
```
The resulting package (DMG, MSI, or DEB) will be located in `composeApp/build/compose/binaries/main/`.

*   **macOS**: Generates `.dmg`
*   **Windows**: Generates `.msi`
*   **Linux**: Generates `.deb`

*Note: To build a distribution for a specific platform, you must run the task on that platform.*

### Web (WASM)
To run the WebAssembly application in your default browser (with hot reload):
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
To build a production distribution:
```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```

### Console (Native)
To run the console application, use the task specific to your operating system:

**macOS (Apple Silicon / Arm64):**
```bash
./gradlew :composeApp:runDebugExecutableMacosArm64
```

**macOS (Intel / x64):**
```bash
./gradlew :composeApp:runDebugExecutableMacosX64
```

**Linux (x64):**
```bash
./gradlew :composeApp:runDebugExecutableLinuxX64
```

**Windows (MinGW x64):**
```bash
./gradlew :composeApp:runDebugExecutableMingwX64
```

## License

Apache-2.0 (see `LICENSE`).