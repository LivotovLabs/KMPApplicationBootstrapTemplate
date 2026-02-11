# KMP Application Bootstrap

This is a Kotlin Multiplatform (KMP) project template with Compose Multiplatform support for Android, iOS, Desktop (JVM), Web (WASM), and Native Console targets.

## Supported Platforms

*   **Android**
*   **iOS**
*   **Desktop (JVM)**: macOS, Linux, Windows
*   **Web (WASM)**: Browser
*   **Console (Native)**: macOS (Arm64/x64), Linux (x64), Windows (MinGW x64)

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