# KMP Application Bootstrap

This is a comprehensive **Kotlin Multiplatform (KMP)** project template designed to accelerate the development of production-ready applications. It supports **Android**, **iOS**, **Desktop (JVM)**, and **Web (WASM)** out of the box.

The goal is to provide a **minimal-batteries-included** starting point. It solves common architectural challenges—such as navigation, persistent settings, and logging—without enforcing a bloated framework, allowing you to focus on your application's unique features.

## Project Foundation

This project is built on the latest KMP structure compatible with the **Android Gradle Plugin (AGP) 9**.

*   **Origin**: Generated using the official [KMP App Wizard](https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&includeTests=true).
*   **Modernization**: Heavily inspired by the [watermelonKode/kmp-wizard-template](https://github.com/watermelonKode/kmp-wizard-template), incorporating migration strategies for AGP 9 and modern multiplatform best practices.
*   **Libraries**: This project also relies heavily on **[OSKit-KMP](https://github.com/outsidesource/OSKit-KMP)** and **[OSKit-Compose-KMP](https://github.com/outsidesource/OSKit-Compose-KMP)** as its core framework foundation. It also uses **[KmLogging](https://github.com/DiamondEdge1/KmLogging)** for robust multiplatform logging. These libraries provide solid implementations for common architectural patterns and logging needs.

---

## Getting Started

To start a new project using this template:

1.  **Clone** this repository.
2.  **Configure**: Update your App Name, ID, and Namespaces in `gradle/libs.versions.toml`.
3.  **Prune Targets**: Remove any platform targets you don't need from `composeApp/build.gradle.kts`.
4.  **Prune Components**: Remove any pre-installed services or components that aren't relevant to your app.
5.  **Code**: Start building your features in `commonMain`!

---

## Architecture & Core Concepts

This project follows the **VISCE** architecture pattern and utilizes the [OSKit-KMP](https://github.com/outsidesource/OSKit-KMP) library.

*   **Reference**: [VISCE Architecture Documentation](https://ryanmitchener.notion.site/VISCE-va-s-Architecture-d0878313b4154d2999bf3bf36cb072ff)

### Navigation
*   **`App.kt`**: The main UI entry point. It initializes the `AppInteractor` and `AppCoordinator` and uses `RouteSwitch` to render content.
*   **`AppCoordinator`**: Manages navigation logic (push, pop, deep links).
*   **`RouteSwitch`**: A composable that observes the coordinator and switches screens.

**Adding a New Screen:**
1.  **Define Route**: Add a route object to `sealed class Route` in `composeApp/.../ui/router.kt`.
2.  **Create Screen**: Build your Composable (e.g., `SettingsScreen`).
3.  **Register**: Update `RouteSwitch` in `App.kt` to map the route to the screen.
    ```kotlin
    RouteSwitch(coordinator) {
        when (it) {
            Route.Home -> Authorized(state) { HomeScreen() }
            Route.Settings -> Authorized(state) { SettingsScreen() }
        }
    }
    ```
4.  **Navigate**: Call `push(Route.Settings)` from your Coordinator/Interactor.

### Logging
A unified `LoggingService` is available across all platforms.

```kotlin
// Injection
class MyInteractor(private val logger: LoggingService) : Interactor<MyState>(...) {
    fun doSomething() {
        logger.debug("Tag") { "Lazy log message" }
        logger.error("Tag", exception) { "Error occurred" }
    }
}
```

### Dependency Injection
The project uses **Koin** for Dependency Injection, pre-configured for both common and platform-specific code.

We specifically chose the **Koin DSL** over annotation-based configuration to:
*   **Maintain Maximum Flexibility**: Better support for complex dependency setups and conditional registrations.
*   **Centralized Visibility**: All dependencies and their configurations are clearly visible in a single place (`DI.kt` files), making the project easier to understand without hunting for annotations or relying on specialized IDE plugins.

*   **Common DI**: Defined in `composeApp/src/commonMain/kotlin/com/watermelonkode/simpletemplate/DI.kt`.
*   **Platform DI**: Implemented in platform-specific modules (e.g., `androidMain`, `iosMain`, `desktopMain`) to handle platform-specific dependencies.

### Settings & App Information
Use `AppSettingsInteractor` for UI-related state (user settings + app version).

*   **`AppSettingsInteractor`**: Provides `settings` (e.g., theme, mute) and `appVersion`/`buildNumber`.
*   **`AppSettingsService`**: Low-level Key-Value storage for settings.
*   **`AppInformationService`**: Low-level provider for platform-specific version metadata.

---

## Configuration

The `gradle/libs.versions.toml` file is the **single source of truth** for configuration.

### Essential Keys
Modify these in `[versions]`:
*   `app-name`: Application Display Name.
*   `app-appId`: Bundle ID / Application ID.
*   `app-versionName`: Semantic version (e.g., `1.0.0`).
*   `app-versionCode`: Build number (Integer).
*   `android-namespace` / `app-namespace`: Package namespaces.

### Android Signing
Configure signing in `androidApp/build.gradle.kts`. Credentials are loaded from `local.properties` (recommended) or `gradle.properties`.

**Keys**: `android.key.store`, `android.key.store.password`, `android.key.alias`, `android.key.password`.

### iOS Configuration
*   **Auto-Generated**: `iosApp/Configuration/Config.xcconfig` is generated automatically from `libs.versions.toml` by the `syncIosConfig` task. **Do not edit it manually.**

### Application Icons
Icons are managed by the `kmp-app-icon-generator` plugin.

1.  **Source**: Place `icon.svg` or `icon.png` in `composeApp/src/commonMain/composeResources/drawable/`.
2.  **Generate**: Run `./gradlew :composeApp:generateIcons --no-configuration-cache`.
    *   Populates Android `mipmap` and iOS `Assets.xcassets`.
3.  **Desktop**: Manually replace icons in `composeApp/src/desktopMain/resources/icons/` (`icon.icns`, `icon.ico`, `icon.png`).

---

## Building & Running

### Android
*   **Run**: `./gradlew :androidApp:installDebug`
*   **Release APK**: `./gradlew :androidApp:assembleRelease` (Output: `androidApp/build/outputs/apk/release/`)
*   **Play Store Bundle**: `./gradlew :androidApp:bundleRelease`

### iOS
*   **Run**: Open `iosApp/iosApp.xcodeproj` in Xcode and run.
*   **Archive/App Store**: In Xcode, select "Any iOS Device" -> **Product** -> **Archive**.

### Desktop (JVM)
*   **Run**: `./gradlew :composeApp:run`
*   **Package**: `./gradlew :composeApp:package`
    *   Output: `composeApp/build/compose/binaries/main/` (DMG, MSI, or DEB depending on OS).

### Web (WASM)
*   **Run**: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
*   **Build**: `./gradlew :composeApp:wasmJsBrowserDistribution`

---

## Managing Targets

If you do not need all platforms, remove them to speed up builds:

*   **WASM/Desktop**: Remove targets from `composeApp/build.gradle.kts` and delete `src/desktopMain` / `src/wasmJsMain`.
*   **Android**: Delete `androidApp` folder, remove from `settings.gradle.kts`, and remove `androidLibrary` from `composeApp`.
*   **iOS**: Delete `iosApp` folder and remove iOS targets from `composeApp/build.gradle.kts`.

---

## Roadmap

We are continuously improving this template. Current priorities include:
*   **Automated Project Setup**: A custom Gradle task to automate initial configuration (App ID/Name) and recursive package renaming.
*   **Enhanced DI**: Full support for the latest **Koin Kotlin Compiler** plugin for compile-time safety and reduced boilerplate.

## License

Apache-2.0 (see `LICENSE`).