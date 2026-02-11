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

### iOS
To run the iOS application:
1.  Open `iosApp/iosApp.xcodeproj` in Xcode.
2.  Select a simulator or connected device.
3.  Click the "Run" button (or press `Cmd+R`).

### Desktop (JVM)
To run the desktop application:
```bash
./gradlew :composeApp:run
```

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