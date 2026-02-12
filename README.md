# KMP Application Bootstrap

This is a Kotlin Multiplatform (KMP) project template with Compose Multiplatform support for Android, iOS, Desktop (JVM), and Web (WASM) targets.

## Supported Platforms

*   **Android**
*   **iOS**
*   **Desktop (JVM)**: macOS, Linux, Windows
*   **Web (WASM)**: Browser

## Configuration

The project uses `gradle/libs.versions.toml` as the single source of truth for application metadata, versions, and entry points. You can modify the following keys in the `[versions]` section:

*   `app-name`: The display name of the application (used for iOS).
*   `app-appId`: The application ID / Bundle Identifier (e.g., `com.example.app`).
*   `app-versionCode`: The integer version code (used for Android `versionCode` and iOS `CURRENT_PROJECT_VERSION`).
*   `app-versionName`: The semantic version string (used for Android `versionName`, iOS `MARKETING_VERSION`, and Desktop `packageVersion`). **Note:** Must be in `MAJOR.MINOR.BUILD` format (e.g., `1.0.0`) for Windows MSI compatibility.
*   `app-desktop-entrypoint`: The main class for the desktop application.
*   `android-namespace`: The namespace for the library module (`composeApp`).
*   `app-namespace`: The namespace for the Android application module (`androidApp`).

### Android Signing
Android signing is configured in `androidApp/build.gradle.kts`. It automatically loads credentials from `local.properties` (first) or `gradle.properties` (fallback).

The following keys are supported:
*   `android.key.store`: Path to the keystore file (relative to `androidApp` folder).
*   `android.key.store.password`: Password for the keystore.
*   `android.key.alias`: Key alias.
*   `android.key.password`: Password for the key.

**Security Note:** For production, move these values to `local.properties` (which is excluded from version control) or use environment variables in your CI/CD pipeline.

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

## Logging
The project includes a `LoggingService` for unified logging across all platforms. The default implementation uses the DiamondEdge logging library.

### Usage
Inject the `LoggingService` into your interactors or services:

```kotlin
class MyInteractor(
    private val logger: LoggingService
) : Interactor<MyState>(...) {

    fun doSomething() {
        logger.debug("MyTag") { "Doing something..." }
        
        try {
            // ...
        } catch (e: Exception) {
            logger.error("MyTag", e) { "An error occurred" }
        }
    }
}
```

Available methods:
*   `info(tag: String, msg: () -> Any?)`
*   `debug(tag: String, msg: () -> Any?)`
*   `error(tag: String, exception: Any?, msg: () -> Any?)`

*Note: The message is provided as a lambda for lazy evaluation, ensuring no string construction overhead if the log level is disabled.*

## Settings
The `AppSettingsService` provides a unified interface for managing application settings across platforms, backed by a Key-Value store.

### Usage
Inject the `AppSettingsService` to observe or update settings:

```kotlin
class SettingsInteractor(
    private val settingsService: AppSettingsService,
    private val logger: LoggingService
) : Interactor<SettingsState>(...) {

    override suspend fun initialWork() {
        // Observe settings changes
        settingsService.observeSettings().collect { settings ->
            logger.debug("Settings") { "Settings updated: $settings" }
            updateState { copy(muted = settings.muted) }
        }
    }

    fun toggleMute() {
        // Update settings
        scope.launch {
            val currentSettings = state.value.settings
            settingsService.updateSettings(currentSettings.copy(muted = !currentSettings.muted))
        }
    }
}
```

Available methods:
*   `observeSettings(): Flow<AppSettings>`: specific Flow emitting the current settings state.
*   `updateSettings(value: AppSettings)`: Updates the settings with a new value.
*   `clearSettings()`: Resets settings to default.

## App Information
The `AppInformationService` provides a unified way to retrieve application metadata like version name and build number across all platforms.

*   `getAppVersion(): String`: Returns the semantic version (e.g., "1.0.0").
*   `getAppBuildNumber(): Int`: Returns the build number (e.g., 1).

### AppSettingsInteractor
The `AppSettingsInteractor` is a higher-level component that manages both user settings and app metadata. It is useful for displaying version information on settings screens.

**State Includes:**
*   `settings: AppSettings`: User-configurable settings (e.g., `muted`).
*   `appVersion: String`: Current version from `AppInformationService`.
*   `appBuildNumber: Int`: Current build number from `AppInformationService`.

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
The APK will be in `androidApp/build/outputs/apk/release/`.

To build an Android App Bundle (AAB) for Play Store:
```bash
./gradlew :androidApp:bundleRelease
```
The AAB will be in `androidApp/build/outputs/bundle/release/`.

### iOS
To run the iOS application:
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select a simulator or connected device.
3.  Click the "Run" button (or press `Cmd+R`).

To build for the App Store:
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select "Any iOS Device (arm64)" as the target.
3.  Go to **Product -> Archive**.
4.  Once the archive is created, the Organizer window will open. Click **Distribute App** to upload to TestFlight or the App Store.

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

## License

Apache-2.0 (see `LICENSE`).
