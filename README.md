# KMP Application Bootstrap

This is a comprehensive **Kotlin Multiplatform (KMP)** project template designed to accelerate the development of production-ready applications. It supports **Android**, **iOS**, **Desktop (JVM)**, and **Web (WASM)** out of the box.

The goal is to provide a **minimal-batteries-included** starting point. It solves common architectural challenges—such as navigation, persistent settings, and logging—without enforcing a bloated framework, allowing you to focus on your application's unique features.

## Project Foundation

This project is built on the latest KMP structure compatible with the **Android Gradle Plugin (AGP) 9**.

*   **Origin**: Generated using the official [KMP App Wizard](https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&includeTests=true).
*   **Modernization**: Heavily inspired by the [watermelonKode/kmp-wizard-template](https://github.com/watermelonKode/kmp-wizard-template), incorporating migration strategies for AGP 9 and modern multiplatform best practices.
*   **Libraries**: This project also relies heavily on **[OSKit-KMP](https://github.com/outsidesource/OSKit-KMP)** and **[OSKit-Compose-KMP](https://github.com/outsidesource/OSKit-Compose-KMP)** as its core framework foundation. It also uses **[KmLogging](https://github.com/DiamondEdge1/KmLogging)** for robust multiplatform logging. These libraries provide solid implementations for common architectural patterns and logging needs.
*   **Design System**: The UI is built on **[Composables UI](https://composables.com/ui/docs)** (MIT-licensed) and **[Lucide Icons](https://composables.com/icons)**, not Material. See [Design System](#design-system) below.

## Version Information

*   **Kotlin:** 2.4.10
*   **Gradle:** 9.7.1
*   **AGP Plugin:** 9.4.0
*   **Android compileSdk / targetSdk:** 37
*   **Google Services** 4.5.0
*   **Compose Multiplatform:** 1.12.0
*   **Composables UI:** 0.2.0 (with Compose Unstyled 2.7.0)
*   **Lucide Icons:** 2.2.1
*   **Koin:** 4.2.2
*   **Ktor:** 3.5.2
*   **OSKIT:** 5.2.0-rc1
*   **OSKIT Compose:** 4.2.0-rc2

> **Note on the OSKit release candidates.** OSKit 5.2.0-rc1 / Compose 4.2.0-rc2 are built against
> Kotlin 2.4.0, Compose 1.11.1, Koin 4.2.2 and Ktor 3.5.1 -- the stack this template targets. The
> last stable release (4.1.1) was built against Compose 1.10.1 and pulls Compose **Material 2** onto
> the classpath. Pin `oskitKmp = "5.1.0"` / `oskitCompose = "4.1.1"` in `libs.versions.toml` if you
> need stable-only dependencies.

---

## Release Notes

### Version 2.0
*   **Migrated to Composables UI**: The UI layer now uses **[Composables UI](https://composables.com/ui/docs) 0.2.0** (MIT-licensed, free) instead of Material 3. This replaces the old `composables-ui` branch, which used the retired paid *Composables Core* library.
*   **Single project variant**: The `main` / `composables-ui` split is gone. There is one template, and it is Composables UI. Nothing to check out, nothing to keep in sync.
*   **New design system** under `composeApp/.../ui/design/`: an app-owned `AppTheme` plus editable `Colors`, `Shapes`, `Typography` and `Spacing` token files, and `AppScreen` / `AppToolbar` shared components. See [Design System](#design-system).
*   **Light/dark theme setting**: `ThemeMode` (System / Light / Dark) is now a persisted user setting wired through `AppSettingsInteractor` into the theme, with a working toggle on the Home screen.
*   **Dependencies and toolchain updated**: Kotlin 2.4.10, Gradle 9.7.1, AGP 9.4.0, Compose Multiplatform 1.12.0, Koin 4.2.2, Ktor 3.5.2, BuildKonfig 0.22.0, OSKit 5.2.0-rc1.
*   **Fixed `AppInformationService` dependency injection**: the platform modules bound `AppInformationServiceImpl` to itself rather than to the `AppInformationService` interface, so anything injecting `AppSettingsInteractor` crashed at startup. It was latent because nothing injected it before.
*   **Fixed the configuration cache for iOS builds**: `kmp-app-icon-generator` wires `generateIcons` onto every Kotlin/Native link task, and that task is not configuration-cache compatible -- which failed every iOS build and `:composeApp:allTests`. The dependency edge is now cut in `composeApp/build.gradle.kts`; run icon generation on demand instead.
*   **Removed dead code**: the unused Material 3 theme in `androidApp` (`ui/theme/`) and its `colors.xml` palette.
*   **Modernized the Gradle DSL**: replaced the `by getting` source-set accessors that Gradle 10 removes.
*   **Platform-native feel**: press effects (Material ripple / iOS highlight / desktop hover), haptics, shapes, type scale, spacing, tap targets, toolbar metrics and scrollbars now follow the host platform. See [Platform feel](#platform-feel).
*   **Fixed the iOS Xcode build**: `ComposeApp` is a static framework, so the app target has to link `sqlite3` itself for OSKit's KV storage. Without it Xcode failed with "symbol(s) not found for architecture arm64" — meaning the README's "open in Xcode and run" never worked. `OTHER_LDFLAGS` is now part of the generated `Config.xcconfig`.
*   **AI guidelines moved to `CLAUDE.md`**; `GEMINI.md` is now just a pointer to it.

### Version 1.1
*   **Updated Dependencies**: Bumped Kotlin, Compose Multiplatform, Koin, and Ktor to their latest robust versions (see Version Information for details).
*   **Fixed Android App Icon Resolution**: Removed conflicting default XML adaptive icons (`ic_launcher`, `ic_launcher_round`) from the `androidApp` module's resources. This ensures Android properly resolves the custom icons generated by the KMP App Icon Generator plugin inside the `composeApp` module.

---

## Getting Started

To start a new project using this template:

1.  **Clone** this repository.
2.  **Configure**: Update your App Name, ID, and Namespaces in `gradle/libs.versions.toml`.
3.  **Prune Targets**: Remove any platform targets you don't need from `composeApp/build.gradle.kts`.
4.  **Prune Components**: Remove any pre-installed services or components that aren't relevant to your app.
5.  **Rebrand**: Edit `ui/design/Colors.kt` (and `Shapes.kt` / `Typography.kt` / `Spacing.kt`). Leave
    `AppTheme.kt` alone — it only binds those values to token names. See [Design System](#design-system).
6.  **Code**: Start building your features in `commonMain`.

Then read these three, in order:

*   **[Project Layout](#project-layout)** — where each kind of file goes.
*   **[Architecture & Core Concepts](#architecture--core-concepts)** — VISCE, plus copy-paste recipes
    for adding a screen and adding a service.
*   **[`CLAUDE.md`](CLAUDE.md)** — the authoritative coding conventions: the ten non-negotiable
    rules, `Interactor` state patterns, platform-specific code, and a table of common failure modes.
    Written for AI coding agents, but it is the reference humans should follow too. Agents pick it up
    automatically; `GEMINI.md` just points at it.

Before you go far, skim **[Verifying Changes](#verifying-changes)**: theme tokens resolve at
composition time and Koin resolves at runtime, so a green compile does not mean working code.

---

## Project Layout

Where things go. `data/` implements the interfaces declared in `domain/service/`; the UI never talks
to `data/` directly.

```text
composeApp/src/commonMain/kotlin/com/watermelonkode/simpletemplate/
  ├── DI.kt                     # commonModule() + initKoin() + expect platformModule()
  ├── domain/                   # Pure business rules. No framework or platform types.
  │   ├── interactor/           # App-wide interactors (AppSettingsInteractor)
  │   ├── model/                # Entities, enums, error sealed classes
  │   └── service/              # Service INTERFACES only
  ├── data/                     # Infrastructure
  │   ├── core/                 # Shared plumbing (KtorClient)
  │   └── service/              # Service IMPLEMENTATIONS
  └── ui/
      ├── app.kt                # App(): AppTheme + RouteSwitch — register screens here
      ├── router.kt             # sealed class Route + deep links
      ├── coordinator.kt        # AppCoordinator — every navigation decision
      ├── interactor.kt         # AppInteractor — app-wide UI state
      ├── design/               # The design system (see below)
      │   ├── AppTheme.kt                                      # binds values to tokens
      │   ├── Colors.kt Shapes.kt Typography.kt Spacing.kt      # your brand
      │   ├── Platform.kt PressEffects.kt Metrics.kt            # platform feel
      │   └── components/       # AppScreen, AppToolbar, AppScrollbar
      └── screen/<feature>/     # <Feature>Screen.kt + <Feature>ScreenViewInteractor.kt
```

Platform source sets (`androidMain`, `iosMain`, `desktopMain`, `wasmJsMain`) each hold a
`PlatformDI.kt` with `actual fun platformModule()` and `actual class PlatformContext`, plus any
`actual` service implementations.

Two naming conventions worth keeping: the four files directly under `ui/` are lowercase
(`app.kt`, `router.kt`, `coordinator.kt`, `interactor.kt`), and platform `actual`s are suffixed
`.android.kt` / `.ios.kt` / `.desktop.kt` / `.wasm.kt`.

---

## Architecture & Core Concepts

This project follows the **VISCE** architecture pattern and utilizes the [OSKit-KMP](https://github.com/outsidesource/OSKit-KMP) library.

*   **Reference**: [VISCE Architecture Documentation](https://ryanmitchener.notion.site/VISCE-va-s-Architecture-d0878313b4154d2999bf3bf36cb072ff)

### Navigation
*   **`App.kt`**: The main UI entry point. It initializes the `AppInteractor` and `AppCoordinator` and uses `RouteSwitch` to render content.
*   **`AppCoordinator`**: Manages navigation logic (push, pop, deep links).
*   **`RouteSwitch`**: A composable that observes the coordinator and switches screens.

**Adding a New Screen** — five touchpoints, in this order:

1.  **Route** — add to `sealed class Route` in `ui/router.kt`. `webRoutePath` powers web URLs and
    deep links.
    ```kotlin
    data object Settings : Route(webRoutePath = "/settings", webRouteTitle = "Settings")
    ```
2.  **Coordinator method** — in `ui/coordinator.kt`. Navigation decisions live *only* here, never in
    a screen or a ViewInteractor. Name it after the event, not the destination:
    ```kotlin
    fun settingsClicked() = push(Route.Settings)
    ```
3.  **ViewInteractor** — `ui/screen/settings/SettingsScreenViewInteractor.kt`. Holds the screen's
    state and logic; its public functions mirror UI events (`onSaveClicked()`), not implementation
    details (`loadData()`).
4.  **Register it in DI** — add `factory { SettingsScreenViewInteractor(get(), get()) }` to
    `commonModule()` in `DI.kt`. **This step fails at runtime, not compile time**, so it is the one
    people forget.
5.  **Screen + `RouteSwitch`** — write the composable starting from `AppScreen`, then map the route
    in `ui/app.kt`:
    ```kotlin
    RouteSwitch(coordinator) {
        when (it) {
            Route.Home -> Authorized(state) { HomeScreen() }
            Route.Settings -> Authorized(state) { SettingsScreen() }
            is Route.Details -> Authorized(state) { DetailsScreen(it.id) }
        }
    }
    ```
    The `when` is exhaustive over `Route`, so the compiler catches a missing entry here.

A minimal screen looks like this — note that everything visual comes from a token, and the toolbar
and insets come from `AppScreen`/`AppToolbar`:

```kotlin
@Composable
fun SettingsScreen(
    interactor: SettingsScreenViewInteractor = rememberInject<SettingsScreenViewInteractor>()
) {
    val state = interactor.collectAsState()

    AppScreen(
        toolbar = {
            AppToolbar(
                title = "Settings",
                showBackButton = true,
                onBackClicked = { interactor.onBackClicked() },
            )
        }
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(Theme[spacing][screenPadding]),
            verticalArrangement = Arrangement.spacedBy(Theme[spacing][elementPadding]),
        ) {
            Text("Appearance", style = Theme[typography][h3])
            Button(
                onClick = { interactor.onThemeModeSelected(ThemeMode.Dark) },
                style = ButtonStyle.Primary,
            ) {
                Text("Use dark theme")
            }
        }
    }
}
```

**Adding a Service** — the boundary to the outside world (HTTP, database, sensors):

1.  **Interface** in `domain/service/`, returning `Outcome<Value, Error>`. Services must never throw.
2.  **Error type** in `domain/model/` as a `sealed class`.
3.  **Implementation** in `data/service/`, catching everything and mapping DTOs to domain entities.
    Use the preconfigured `KtorClient` from `data/core/` for HTTP — its `get`/`post`/`patch`/`put`/
    `delete` extensions already return `Outcome<T, HttpError>` and never throw. Translate `HttpError`
    into your own domain error inside the service so transport details do not leak upward.
4.  **Register** in `DI.kt` bound to the *interface*:
    `single { ProfileServiceImpl(get()) } bind ProfileService::class`.

> **Watch out:** binding to the implementation (`bind ProfileServiceImpl::class`) compiles, does
> nothing, and every `get<ProfileService>()` then fails at runtime — and only once something first
> injects it. This exact bug sat undetected in this template until v2.0.

If the implementation needs a platform handle (an Android `Context`, a file path), make it an
`expect class` in `commonMain/data/service/` and register it in each `platformModule()` instead.

`CLAUDE.md` holds the full conventions, including copy-paste versions of these recipes, the
`Interactor` state patterns and a table of common failure modes. It is written for AI coding agents
but is the authoritative reference for humans too.

### Design System

The UI is built on **[Composables UI](https://composables.com/ui/docs)** -- accessible, unstyled
components with a token-based theme -- plus **Lucide** icons. There is no Material theme.

Everything visual is read through one accessor, `Theme[property][token]`:

```kotlin
import com.composeunstyled.theme.Theme

// Colour, shape, shadow and alpha tokens come from the library:
import com.composables.ui.theme.colors
import com.composables.ui.theme.primaryColor

// Typography and spacing are this app's own theme properties:
import com.watermelonkode.simpletemplate.ui.design.typography
import com.watermelonkode.simpletemplate.ui.design.h1

Text(text = "Title", style = Theme[typography][h1])
Box(Modifier.background(Theme[colors][primaryColor]))
```

#### Rebranding the app

Everything lives in `composeApp/src/commonMain/kotlin/.../ui/design/`:

| File | What to change |
|---|---|
| `Colors.kt` | **Start here.** `AppLightPalette` / `AppDarkPalette` -- one property per colour token. |
| `Shapes.kt` | Corner radii, with separate touch and pointer variants. |
| `Typography.kt` | The type scale (`h1`..`h3`, `bodyLarge`, `labelLarge`, ...). |
| `Spacing.kt` | `screenPadding`, `elementPadding`, `smallPadding`. |
| `AppTheme.kt` | Leave alone -- it only binds the values above to token names. |

`AppTheme` is a full replacement for the library's own `ComposablesTheme`, not a wrapper around it.
Compose Unstyled's `buildTheme {}` replaces the ambient theme wholesale, and the library exposes no
API for overriding one token, so owning the definition is the supported way to control the palette.
Because `AppTheme` populates the library's own token identities, every stock component
(`Button`, `TextField`, `AlertDialog`, ...) picks up your values automatically.

> If a future Composables UI release adds a token that `AppTheme.kt` does not define,
> `Theme[property][token]` throws at **composition** time with a message naming the missing token.
> Always run the app after upgrading the library -- a clean compile does not prove the theme is
> complete.

#### Screens and components

Composables UI ships no `Scaffold`, so screens are built from the template's own shared components
in `ui/design/components/`:

```kotlin
AppScreen(
    toolbar = { AppToolbar(title = "Details", showBackButton = true, onBackClicked = { ... }) }
) {
    // content, in a Box that already handles safe-area insets
}
```

Components adapt to the current input method via `LocalInteractionMode`: bigger and rounder under a
finger, tighter under a mouse pointer. Prefer letting them size themselves.

#### Platform feel

The design system deliberately does **not** look the same everywhere. One switch,
`appPlatform` in `ui/design/Platform.kt` (resolved from OSKit's `Platform.current`), drives every
platform difference:

| | Android | iOS | Desktop / Web |
|---|---|---|---|
| Press effect | Material ripple | Instant tint, slow fade, no hover | Tint on hover, stronger on press |
| Haptics | Yes | Yes | n/a |
| Buttons | Pill | 12dp rounded rect | 6dp rounded rect |
| Dialogs | 28dp | 14dp | 10dp |
| Toolbar | 56dp, leading title, 22sp | 44pt, **centred** title, 17sp semibold | 44dp, leading title, 15sp |
| Back icon | Arrow | Chevron | Arrow |
| Body text | 16sp | 17pt | 14–15sp |
| Screen padding | 16dp | 16dp | 24dp |
| Min tap target | 48dp | 44pt | 32dp |
| Scrollbars | System, transient | System, transient | Persistent |
| Cursor | n/a | n/a | Arrow (desktop), hand (web) |

Where it lives:

* **`PressEffects.kt`** — `appPressIndication()` returns the platform's press feedback. This is the
  highest-leverage file: Composables UI components resolve their press effect from the theme's
  indication tokens, so every stock `Button`, `IconButton`, `Tabs`, `NavigationBarItem` and menu row
  becomes native at once, with no component wrapping. On Android it really is the Material ripple —
  `rememberRippleIndication` wraps `androidx.compose.material.ripple`.
* **`Metrics.kt`** — structural sizes (toolbar height, minimum tap target, window padding). These
  are plain values, not theme tokens, because they describe the *platform*, not your brand.
* **`Platform.kt`** — the `AppPlatform` switch, plus `AppFeel` for taste-level toggles. Set
  `AppFeel.HAPTIC_FEEDBACK_ON_PRESS = false` to stop the tap tick.

`appPlatform` is not the same thing as `LocalInteractionMode`, which Composables UI uses to describe
the current **input device**. An Android tablet driven by a mouse is still Android and still gets a
ripple and pill buttons, while its controls may tighten up for a pointer. Use the platform for "what
do users of this OS expect" and interaction mode for "how big should a tap target be right now".

Two things the library keeps from us: `Button` hardcodes its heights (48dp touch / 36dp pointer) and
applies a 0.98 scale-down on press on every platform. The press *effect* is fully native, which is
the part users notice, but those two details are uniform. `AppToolbar` is built from primitives
precisely because `Toolbar` hardcoded 64dp and a 20sp title the same way.

#### Theme mode

`ThemeMode` (`System` / `Light` / `Dark`) is a persisted setting. Read it from
`AppSettingsInteractor.state.settings.themeMode` and change it with
`AppSettingsInteractor.setThemeMode(...)`; `App()` feeds it into `AppTheme`, which crossfades the
colours on change. The Home screen has a working toggle you can copy or delete.

> **On Material:** the app's design system is Composables UI only, and no app code imports
> `androidx.compose.material*`. Compose Material does still appear on the *classpath*, because
> `oskit-compose` declares it as a transitive dependency. That is as far as removal goes without
> dropping OSKit.

---

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

*   **`AppSettingsInteractor`**: Provides `settings` (`themeMode`, `muted`) and `appVersion`/`buildNumber`, plus `setThemeMode(...)` / `setMuted(...)` to change them.
    *   Settings are persisted as `AppSettingsDto`. **Every field in that DTO must have a default**, so blobs written by an older build still deserialize after you add a field.
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

### Google Services (Optional)
*   **Android**: Drop the google-services.json into `androidApp` folder, then uncomment the google services plugin in `androidApp/build.gradle.kts`
*   **iOS**: Drop the google-services.plist into `iosApp/iosApp` folder, import it into XCode project, then add Firebase ios SDK as per Firebase manual

### iOS Configuration
*   **Auto-Generated**: `iosApp/Configuration/Config.xcconfig` is generated automatically from `libs.versions.toml` by the `syncIosConfig` task. **Do not edit it manually.**

### Application Icons
For Android and iOS targets, icons are managed by the `kmp-app-icon-generator` plugin. Other targets
require manual steps:

1.  **Android and iOS targets**:
    *   Place `icon.svg` or 1024x1024 `icon.png` in `composeApp/src/commonMain/composeResources/drawable/`.
    *   Run `./gradlew :composeApp:generateIcons --no-configuration-cache`
    *   The task will generate platform icon resources in: Android - `mipmap` and iOS - `Assets.xcassets`.
    *   This is a deliberate **on-demand** step. The `kmp-app-icon-generator` plugin normally hooks
      `generateIcons` onto every Kotlin/Native link task, but the task is not configuration-cache
      compatible, so that broke every iOS build. `composeApp/build.gradle.kts` cuts that dependency
      edge -- which is also why the task needs `--no-configuration-cache` when you do run it.
    
2.   **Desktop target**: 
     *  Manually replace icons in `composeApp/src/desktopMain/resources/icons/` (`icon.icns`, `icon.ico`, `icon.png`).

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
*   **Package**: `./gradlew :composeApp:packageDistributionForCurrentOS`
    *   Output: `composeApp/build/compose/binaries/main/` (DMG, MSI, or DEB depending on OS).
    *   The Compose plugin's umbrella `:composeApp:package` task is not configuration-cache
      compatible; use the task above, or add `--no-configuration-cache`.

### Web (WASM)
*   **Run**: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
*   **Build**: `./gradlew :composeApp:wasmJsBrowserDistribution`

---

## Verifying Changes

Two whole classes of failure in this stack do **not** show up at compile time, so a green build is
not enough:

*   **Theme tokens resolve at composition time.** Reading a token that `AppTheme.kt` does not define
    throws on first render with a message naming the token.
*   **Koin resolves at runtime.** A missing `factory { }` or a service bound to its own class instead
    of its interface only fails when something first injects it.

So always get the app on screen:

```bash
./gradlew :composeApp:compileKotlinDesktop     # fastest signal for commonMain
./gradlew :composeApp:allTests                 # multiplatform tests
./gradlew :composeApp:run                      # proves tokens and DI actually resolve
./gradlew :androidApp:installDebug             # touch sizing, ripple, system insets
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Because the design system is platform-dependent, a change to `ui/design/` should be checked on more
than one target — desktop and Android together cover the pointer and touch paths.

---

## Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `There is no <property> property in the AppTheme theme` | A token is read but not defined | Add it to the matching `properties[...]` map in `AppTheme.kt` |
| `NoDefinitionFoundException` for a service | Bound to the implementation, not the interface | `single { FooServiceImpl() } bind FooService::class` |
| `NoDefinitionFoundException` for a ViewInteractor | Not registered in `commonModule()` | Add a `factory { }` |
| Settings reset after adding a field | The persisted DTO field has no default | Give every `AppSettingsDto` field a default |
| Screen looks unstyled | A Material component was imported | Import from `com.composables.ui.components.*` |
| Xcode: `symbol(s) not found for architecture arm64` | `sqlite3` not linked into the app target | `./gradlew syncIosConfig` — `OTHER_LDFLAGS` is generated into `Config.xcconfig` |
| Config-cache failure on `:composeApp:package` | The Compose plugin's umbrella task | Use `packageDistributionForCurrentOS` |
| Config-cache failure mentioning `generateIcons` | The icon plugin holds a `Project` reference | Run icon generation with `--no-configuration-cache` |

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