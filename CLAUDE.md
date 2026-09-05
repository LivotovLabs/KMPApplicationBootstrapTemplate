# AI Coding Guidelines: KMP & Compose Multiplatform (VISCE Architecture)

This document serves as the primary context bootstrap and reference guide for the AI coding agent working on Kotlin Multiplatform (KMP) and Jetpack Compose Multiplatform projects. It defines the structural rules, architecture (VISCE), UI patterns, and data layer conventions.

**Core Tech Stack:**
*   **Language:** Kotlin Multiplatform
*   **UI Framework:** Jetpack Compose Multiplatform
*   **Design System:** [Composables UI](https://composables.com/ui/docs) + Lucide icons. **Not Material.** Never import from `androidx.compose.material*`.
*   **DI Framework:** Koin
*   **Async/Reactive:** Kotlin Coroutines & Flow
*   **Networking:** Ktor Client
*   **Architecture & Navigation:** Oskit KMP (`Interactor`, `Coordinator`, `Outcome`, `Route`)
*   **Storage:** Oskit KMP Storage (KVStore)

---

## ⛔ Non-Negotiables

Read this list before writing any code. Each item is here because getting it wrong has already
broken this project at least once.

1.  **Never import `androidx.compose.material*`.** The design system is Composables UI. Material is
    only on the classpath as an OSKit transitive dependency. Use `com.composables.ui.components.*`.
2.  **Never hardcode a colour, text style, shape or spacing value.** Read them through
    `Theme[property][token]`. Add a token instead of a literal. See §3.
3.  **A missing theme token throws at *composition* time, not compile time.** A clean build proves
    nothing about the theme. Run the app after touching `ui/design/`.
4.  **Bind services to their interface, not to themselves:**
    `single { FooServiceImpl() } bind FooService::class`. Binding `bind FooServiceImpl::class` is
    silently useless — every `get<FooService>()` then fails at *runtime*, and only when something
    first injects it. This exact bug shipped undetected in this template.
5.  **Every field in a persisted DTO needs a default value**, or settings written by an older build
    stop deserializing after you add a field.
6.  **Views hold no logic.** No business rules, no `if` on domain state beyond rendering, no
    navigation calls. Delegate to the `ViewInteractor`.
7.  **Services never throw.** Return `Outcome<Value, Error>` with a `sealed class` error type.
8.  **Screens start with `AppScreen`.** Composables UI ships no `Scaffold`.
9.  **Branch on `appPlatform` for platform conventions**, not on `LocalInteractionMode`. See §6.
10. **Do not add `expect`/`actual`** unless you genuinely need a platform-only API. See §6.

---

## 🏛 Architecture Pattern: VISCE

The application implements the **VISCE** architecture (**V**iew, **I**nteractor, **S**ervice, **C**oordinator, **E**ntity). It is a clean, unidirectional data flow (UDF) architecture tailored for reactive UIs.

### 1. View (V)
*   **Role:** The visual representation of application state.
*   **Implementation:** Jetpack Compose `@Composable` functions.
*   **Rules:**
    *   Views are completely passive. They contain **no business logic**.
    *   A View observes state from a **single** `ViewInteractor` (using `interactor.collectAsState()`).
    *   All user interactions and lifecycle events are forwarded to the `ViewInteractor` (e.g., `onClick = { interactor.onSaveClicked() }`).
    *   Use `mutableStateOf()` only for simple, local, ephemeral UI state (e.g., dropdown expanded state).
    *   **Naming:** Suffix screens with `Screen` (e.g., `HomeScreen`). Suffix complex/reusable views with `View` (e.g., `ItemCardView`).

### 2. Interactor (I)
*   **Role:** Manages state, encapsulates logic. Interactors provide a publicly accessible, immutable state stream and update state via atomic `update { }` blocks.
*   **Types:**
    1.  **ViewInteractor (ViewModel):**
        *   Tied to a specific screen/view lifecycle. Instantiated via DI `factory`.
        *   Acts as the glue between Views and App/Domain Interactors.
        *   Exposes immutable state for the View to consume directly.
        *   Public functions should reflect UI actions (e.g., `onSaveClicked()`, `onViewMounted()`), NOT what it does under the hood (e.g., NO `loadData()`).
        *   **Naming:** Suffix with `ViewInteractor` (e.g., `HomeScreenViewInteractor`).
    2.  **Domain/AppInteractor (Use Case/Store):**
        *   Manages application-wide, domain-specific state (e.g., Session, Settings, Items). Instantiated via DI `single`.
        *   Contains the core business rules. Talks to `Services`.
        *   Use maps instead of lists for storing items by ID to improve performance.
        *   **Naming:** Suffix with `Interactor` (e.g., `AccountInteractor`).

### 3. Service (S)
*   **Role:** The boundary to the outside world (REST APIs, Databases, Device Hardware, Sensors).
*   **Rules:**
    *   Always define an Interface (`IService`) in the `domain/service/` package for easy mocking.
    *   Implementations live in the `service/` package.
    *   Services must **never throw exceptions**. All errors must be caught and returned wrapped in an `Outcome<Value, Error>` sealed class.
    *   Translate external data representations (DTOs) into domain `Entities`.

### 4. Coordinator (C)
*   **Role:** The Navigation Layer.
*   **Rules:**
    *   Decides "where to go next", removing routing concerns from Views and Interactors.
    *   `ViewInteractor` calls a semantic method on the Coordinator (e.g., `coordinator.onItemSaved()`), and the Coordinator determines the actual `Route` to push or pop.
    *   Routes are defined as sealed classes/objects implementing `IRoute`.

### 5. Entity (E)
*   **Role:** The Data Model.
*   **Rules:**
    *   Plain, immutable Kotlin `data class` or `sealed class`.
    *   Contains NO application business logic, only data and pure transformation functions.

---

## 📂 Folder Structure

```text
composeApp/src/
  ├── commonMain/kotlin/com/watermelonkode/simpletemplate/
  │   ├── DI.kt                     # commonModule() + initKoin() + expect platformModule()
  │   ├── domain/                   # Pure business rules. No framework or platform types.
  │   │   ├── interactor/           # App/Domain Interactors (AppSettingsInteractor)
  │   │   ├── model/                # Entities, enums, error sealed classes (model/settings/…)
  │   │   └── service/              # Service INTERFACES only (LoggingService, AppSettingsService)
  │   ├── data/                     # Infrastructure. Implements the domain/service interfaces.
  │   │   ├── core/                 # Shared plumbing (KtorClient)
  │   │   └── service/              # Service IMPLEMENTATIONS (…ServiceImpl, incl. expect classes)
  │   └── ui/
  │       ├── app.kt                # App(): AppTheme + RouteSwitch. Register new screens here.
  │       ├── router.kt             # sealed class Route + deep links
  │       ├── coordinator.kt        # AppCoordinator: every navigation decision
  │       ├── interactor.kt         # AppInteractor: app-wide UI state
  │       ├── design/               # The design system — see §3 and §6
  │       │   ├── AppTheme.kt       # buildTheme{} binding values to tokens. Rarely edited.
  │       │   ├── Colors.kt  Shapes.kt  Typography.kt  Spacing.kt   # Brand — edit these
  │       │   ├── Platform.kt  PressEffects.kt  Metrics.kt          # Platform feel — edit these
  │       │   └── components/       # Shared stateless widgets (AppScreen, AppToolbar, AppScrollbar)
  │       └── screen/<feature>/     # <Feature>Screen.kt + <Feature>ScreenViewInteractor.kt
  ├── commonTest/kotlin/…           # Multiplatform tests
  ├── androidMain/…/PlatformDI.kt   # actual platformModule() + actual class PlatformContext
  ├── iosMain/…/PlatformDI.kt
  ├── desktopMain/…/PlatformDI.kt
  └── wasmJsMain/…/PlatformDI.kt
```

Conventions that are easy to get wrong:

*   The four files directly under `ui/` are **lowercase** (`app.kt`, `router.kt`, `coordinator.kt`,
    `interactor.kt`). Everything else is `PascalCase.kt`. Keep it that way.
*   Implementations live in `data/`, interfaces in `domain/service/`. There is no top-level
    `service/` package, and routes are **not** in a `ui/route/` package.
*   A platform-specific service implementation is an `expect class` in
    `commonMain/data/service/FooServiceImpl.kt` with `actual`s named
    `FooServiceImpl.android.kt` / `.ios.kt` / `.desktop.kt` / `.wasm.kt` in the matching source set.
*   There is no `shared/` package yet. Create one for cross-cutting utilities if you need it.

---

## 🧩 Implementation Patterns

### 1. Interactor State Management
State is managed using `com.outsidesource.oskitkmp.interactor.Interactor`.
*   Pass initial state and dependencies (other interactors) to the super constructor.
*   Use the `computed(state: State): State` method to derive UI state automatically when a dependency updates.
*   Use `update { it.copy(...) }` for atomic state modifications.
*   Use `interactorScope.launch { }` for async work.

```kotlin
// State Definition
data class FeatureState(
    val title: String = "",
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: String? = null
)

// ViewInteractor Implementation
class FeatureViewInteractor(
    private val dataInteractor: DataInteractor,
    private val coordinator: AppCoordinator
) : Interactor<FeatureState>(
    initialState = FeatureState(),
    dependencies = listOf(dataInteractor) // React to DataInteractor state changes
) {
    override fun computed(state: FeatureState): FeatureState {
        // Derive state from dependencies automatically
        return state.copy(items = dataInteractor.state.cachedItems)
    }

    fun onRefreshClicked() {
        interactorScope.launch {
            update { it.copy(isLoading = true, error = null) }
            when (val result = dataInteractor.fetchData()) {
                is Outcome.Ok -> update { it.copy(isLoading = false) }
                is Outcome.Error -> update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }
    
    fun onBackClicked() = coordinator.pop()
}
```

### 2. Networking and DTOs (Ktor + Outcome)
*   Network calls are encapsulated in Services.
*   Use Data Transfer Objects (DTOs) for Serialization (`@Serializable`).
*   Map DTOs to Domain Entities inside the Service layer using `.toModel()` extension functions.
*   Always return `Outcome<DomainModel, DomainError>`.

```kotlin
// In domain/model/Errors.kt
sealed class ApiError {
    object NetworkError : ApiError()
    data class ServerError(val code: Int) : ApiError()
}

// In service/api/dto/ItemDto.kt
@Serializable
data class ItemDto(val id: Int, val name: String) {
    fun toModel() = Item(id = id, title = name)
}

// In data/service/ItemServiceImpl.kt
class ItemServiceImpl(private val client: KtorClient) : ItemService {
    override suspend fun fetchItems(): Outcome<List<Item>, ApiError> =
        when (val result = client.get<List<ItemDto>>("https://api.example.com/items")) {
            is Outcome.Ok -> Outcome.Ok(result.value.map { it.toModel() })
            is Outcome.Error -> Outcome.Error(result.error.toApiError())
        }
}

// Translate transport errors into domain errors -- do not leak HttpError past the service.
private fun HttpError.toApiError(): ApiError = when (this) {
    is HttpError.ServerError -> when (code) {
        404 -> ApiError.NotFound
        else -> ApiError.ServerError(code)
    }
    is HttpError.ClientException -> ApiError.NetworkError
}
```

**`KtorClient` (`data/core/KtorClient.kt`)** is the only HTTP entry point. It is preconfigured with
content negotiation, pretty-printed JSON logging through `LoggingService`, and `Outcome` returns, so
services never touch Ktor directly.

*   Extension functions: `get<T>`, `post<T, R>`, `patch<T, R>`, `put<T, R>`, `delete<T>` — each
    returns `Outcome<T, HttpError>` and never throws.
*   `HttpError` is a `sealed class`: `ServerError(code, message, body)` and
    `ClientException(cause)`. Map it to a domain error inside the service.
*   Request helpers for the trailing lambda: `withAuthToken(token)`, `withLanguage(locale)`,
    `withQuery(key, value)`.

```kotlin
client.get<ProfileDto>("$BASE/profile/$id") {
    withAuthToken(token)
    withQuery("expand", "avatar")
}
```

### 3. Jetpack Compose UI & Components

The design system is **Composables UI**, driven by theme tokens rather than a Material theme object.
Read every visual value through the `Theme[property][token]` accessor:

```kotlin
import com.composeunstyled.theme.Theme
import com.composables.ui.theme.colors                            // library tokens
import com.composables.ui.theme.primaryColor
import com.watermelonkode.simpletemplate.ui.design.spacing        // app tokens
import com.watermelonkode.simpletemplate.ui.design.screenPadding
import com.watermelonkode.simpletemplate.ui.design.typography
import com.watermelonkode.simpletemplate.ui.design.h1

Text(text = "Title", style = Theme[typography][h1])
Box(Modifier.background(Theme[colors][primaryColor]).padding(Theme[spacing][screenPadding]))
```

*   **Never** import `androidx.compose.material3.*` (or `material.*`). Material only rides along on the classpath as an OSKit transitive dependency; it is not the app's design system. Use `com.composables.ui.components.*` for `Text`, `Button`, `Icon`, `TextField`, `Switch`, `AlertDialog`, `BottomSheet`, `Tabs`, `NavigationBar`, ...
*   Colour, shape, shadow and alpha tokens come from Composables UI; `typography` and `spacing` are app-defined theme properties in `ui/design/`.
*   Avoid hardcoded colors, text styles or `.dp` spacing. Add a token instead.
*   To restyle the app, edit `ui/design/Colors.kt`, `Shapes.kt`, `Typography.kt` or `Spacing.kt` -- never `AppTheme.kt`, which only binds those values to token names.
*   Composables UI has **no `Scaffold`**. Every screen's root is `AppScreen(toolbar = { AppToolbar(...) }) { ... }` from `ui/design/components/`.
*   Components resize themselves for the current input method (`LocalInteractionMode`): rounder and roomier under a finger, tighter under a pointer. Do not fight this with fixed sizes.
*   A missing token throws at **composition** time, not compile time -- always run the app after adding one.

**Platform feel.** The app is deliberately not identical across platforms. `appPlatform`
(`ui/design/Platform.kt`) is the single switch, and it already drives press effects, haptics,
shapes, type, spacing, tap targets, toolbar metrics and scrollbars.

*   Branch on `appPlatform` for "what do users of this OS expect". Branch on `LocalInteractionMode`
    only for "how big should a tap target be right now" -- an Android tablet with a mouse is still
    `AppPlatform.Android`.
*   Never hardcode a press effect. `appPressIndication()` feeds the theme's indication tokens, and
    every Composables UI control reads them, so press feedback is already native everywhere.
*   Structural sizes go in `Metrics.kt` as plain values, not theme tokens: they describe the
    platform, not the brand. Colours, shapes, type and spacing stay in the theme.
*   Add new platform differences to the existing `when (appPlatform)` blocks rather than introducing
    `expect`/`actual` -- everything needed is available in `commonMain`, and one readable file beats
    four near-identical `actual`s.
*   Pass `Modifier.appPointerCursor()` to clickable components. It is a no-op off web by design.
*   Delegate all actions to the Interactor.
*   **Component Strategy (Shared vs. Local):**
    *   **Shared Components (`ui/design/components/`):** Reusable widgets meant to be used across multiple features (e.g., `AppScreen`, `AppToolbar`). These **MUST be stateless**. Pass data and event callbacks (e.g., `onClick: () -> Unit`) as parameters. Do not wrap a Composables UI component merely to rename its parameters -- those components already read the theme. Add a shared component only when it encodes a real app-wide decision.
    *   **Local Components (`ui/screen/<feature>/`):** Composables specific to a single feature (e.g., a specific list item or header configuration). Use these to break down large screen files. **Rule:** If a local component's composable function exceeds 5 lines of code, it MUST be moved to its own separate file within the feature directory to keep every UI file as simple and small as possible.
*   **Component State Management:**
    *   If a component is simple, pass the state down from the parent screen's `ViewInteractor`.
    *   If a component requires its own business logic, complex state management, or independent lifecycle (e.g., a standalone widget, complex form, or a dedicated BottomSheet), **it must introduce its own `ViewInteractor`**. Do not bloat the parent screen's Interactor with child component logic.

```kotlin
@Composable
fun FeatureScreen(
    interactor: FeatureViewInteractor = rememberInject<FeatureViewInteractor>()
) {
    val state = interactor.collectAsState()

    AppScreen(
        toolbar = {
            AppToolbar(
                title = "Feature",
                showBackButton = true,
                onBackClicked = { interactor.onBackClicked() }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(Theme[spacing][screenPadding]),
            verticalArrangement = Arrangement.spacedBy(Theme[spacing][elementPadding])
        ) {
            if (state.isLoading) {
                IndeterminateProgressIndicator()
            } else {
                Text(
                    text = state.title,
                    style = Theme[typography][h1]
                )
                Button(
                    onClick = { interactor.onRefreshClicked() },
                    style = ButtonStyle.Primary
                ) {
                    Text("Refresh")
                }
            }
        }
    }
}
```

### 4. Dependency Injection (Koin)

Registered in `commonMain/DI.kt` (`commonModule()`) and per-platform `PlatformDI.kt`
(`actual fun platformModule()`). Koin DSL, not annotations, so every binding is visible in one file.

*   `factory` for **ViewInteractors** — a fresh instance per screen or component.
*   `single` for **App/Domain Interactors, Coordinators and Services**.
*   **Always `bind` a service to its interface.** See Non-Negotiable #4: `bind FooServiceImpl::class`
    compiles, does nothing, and fails at runtime the first time anything injects `FooService`.
*   Anything constructed from a platform handle (a `Context`, a file path) belongs in
    `platformModule()`, not `commonModule()`.

```kotlin
// commonMain/DI.kt
fun commonModule() = module {
    // Services — note the interface on the right of `bind`
    single { DiamondEdgeLoggingServiceImpl() } bind LoggingService::class
    single { KVStoreBasedAppSettingsServiceImpl(get()) } bind AppSettingsService::class
    single { KtorClient(get()) }

    // Domain Interactors
    single { AppSettingsInteractor(get(), get()) }

    // UI foundation
    single { AppCoordinator() }
    factory { params -> AppInteractor(params[0], get()) }   // params[0] = deep link

    // View Interactors
    factory { HomeScreenViewInteractor(get(), get()) }
    factory { params -> DetailsScreenViewInteractor(params[0], get()) }  // params[0] = route arg
}
```

```kotlin
// androidMain/PlatformDI.kt — one of these per platform
actual data class PlatformContext(val context: Context)

actual fun platformModule(platformContext: PlatformContext) = module {
    single { platformContext.context }
    single { KmpKvStore(appContext = platformContext.context) }
    single { AppInformationServiceImpl(platformContext.context, get()) } bind AppInformationService::class
}
```

Inject into a composable with `rememberInject`, and pass route arguments with `parametersOf`:

```kotlin
interactor: HomeScreenViewInteractor = rememberInject<HomeScreenViewInteractor>()
interactor: DetailsScreenViewInteractor = rememberInject<DetailsScreenViewInteractor> { parametersOf(id) }
```

### 5. Expected Error Handling
Never throw plain exceptions to the UI. Always handle expected failures with the `Outcome` pattern and `sealed class` error types. Provide extension functions (e.g., `.toUserMessage()`) in the UI layer to translate Domain errors into human-readable Strings using Compose String Resources.

---

## 🧭 Recipe: Add a Screen

Five touchpoints, in this order. Missing #4 is the usual mistake — it fails at runtime, not compile
time.

**1. Route** — `ui/router.kt`. Add to the sealed class; `webRoutePath` powers web URLs and deep links.

```kotlin
data object Settings : Route(webRoutePath = "/settings", webRouteTitle = "Settings")
data class Profile(val userId: String) :
    Route(webRoutePath = "/profile/$userId", webRouteTitle = "Profile")
```

**2. Coordinator method** — `ui/coordinator.kt`. Navigation decisions live here, never in a View or
a ViewInteractor. Name it after the *event*, not the destination.

```kotlin
fun settingsClicked() = push(Route.Settings)
fun profileClicked(userId: String) = push(Route.Profile(userId))
```

**3. ViewInteractor** — `ui/screen/settings/SettingsScreenViewInteractor.kt`. Public functions mirror
UI events (`onSaveClicked()`), never implementation (`loadData()`).

```kotlin
class SettingsScreenViewInteractor(
    private val coordinator: AppCoordinator,
    private val settingsInteractor: AppSettingsInteractor,
) : Interactor<SettingsScreenState>(
    initialState = SettingsScreenState(),
    dependencies = listOf(settingsInteractor),   // recompute when this changes
) {
    override fun computed(state: SettingsScreenState) =
        state.copy(themeMode = settingsInteractor.state.settings.themeMode)

    fun onThemeModeSelected(mode: ThemeMode) = settingsInteractor.setThemeMode(mode)
    fun onBackClicked() = coordinator.pop()
}

data class SettingsScreenState(val themeMode: ThemeMode = ThemeMode.System)
```

**4. Register in DI** — `DI.kt`. Forgetting this throws `NoDefinitionFoundException` at runtime.

```kotlin
factory { SettingsScreenViewInteractor(get(), get()) }
```

**5. Screen + `RouteSwitch`** — the composable, then wire it in `ui/app.kt`.

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
            Text("Theme", style = Theme[typography][h3])
            // ...
        }
    }
}
```

```kotlin
// ui/app.kt
RouteSwitch(coordinator) {
    when (it) {
        Route.Home -> Authorized(state) { HomeScreen() }
        Route.Settings -> Authorized(state) { SettingsScreen() }
        is Route.Details -> Authorized(state) { DetailsScreen(it.id) }
    }
}
```

The `when` is exhaustive over `Route`, so the compiler will tell you if you skip this step.

---

## 🔌 Recipe: Add a Service

**1. Interface** in `domain/service/` — pure Kotlin, `Outcome` returns, no framework types.

```kotlin
interface ProfileService {
    suspend fun fetchProfile(id: String): Outcome<Profile, ProfileError>
}
```

**2. Error type** in `domain/model/` — a `sealed class`, never a raw exception.

```kotlin
sealed class ProfileError {
    data object NotFound : ProfileError()
    data object Network : ProfileError()
    data class Server(val code: Int) : ProfileError()
}
```

**3. Implementation** in `data/service/` — catches everything, maps DTOs to entities.

```kotlin
class ProfileServiceImpl(private val client: KtorClient) : ProfileService {
    override suspend fun fetchProfile(id: String): Outcome<Profile, ProfileError> =
        when (val result = client.get<ProfileDto>("$BASE_URL/profile/$id")) {
            is Outcome.Ok -> Outcome.Ok(result.value.toModel())
            is Outcome.Error -> Outcome.Error(
                when (val e = result.error) {
                    is HttpError.ServerError -> if (e.code == 404) {
                        ProfileError.NotFound
                    } else {
                        ProfileError.Server(e.code)
                    }
                    is HttpError.ClientException -> ProfileError.Network
                },
            )
        }
}
```

**4. Register** in `DI.kt`, bound to the interface:
`single { ProfileServiceImpl(get()) } bind ProfileService::class`

If the implementation needs a platform handle, make it an `expect class` in
`commonMain/data/service/` and register it in each `platformModule()` instead.

---

## 📱 Recipe: Platform-Specific Behaviour

Three tools, in order of preference.

**1. `appPlatform` (`ui/design/Platform.kt`) — default choice.** A `commonMain` value from OSKit's
`Platform.current`. Use it for anything that is a platform *convention*.

```kotlin
val padding = when (appPlatform) {
    AppPlatform.Android -> 16.dp
    AppPlatform.IOS -> 16.dp
    AppPlatform.Desktop, AppPlatform.Web -> 24.dp
}
```

Prefer extending the existing `when (appPlatform)` blocks in `Metrics.kt`, `Shapes.kt`,
`Typography.kt`, `Spacing.kt` and `PressEffects.kt` over adding new ones elsewhere.

**2. `LocalInteractionMode` — only for input-device questions.** It reports finger vs pointer, which
is *not* the same as the OS. An Android tablet with a mouse is `AppPlatform.Android` with
`InteractionMode.Pointer`: it should still show a Material ripple and pill buttons, but may tighten
its tap targets. Use the platform for "what do users of this OS expect" and interaction mode for
"how big should this target be right now".

**3. `expect`/`actual` — only when you need a platform-only API** (`Context`, `UIDevice`,
`window`). Everything Composables UI needs is available in `commonMain`, so reaching for
`expect`/`actual` for styling means four near-identical `actual`s instead of one readable `when`.

Never branch on the platform inside a screen. Put the decision in `ui/design/` and let the screen
read a token.

---

## ✅ Verifying Your Work

A green compile is not enough — theme tokens resolve at composition time, and DI resolves at
runtime. Always get the app on screen.

```bash
./gradlew :composeApp:compileKotlinDesktop      # fastest signal for commonMain
./gradlew :composeApp:allTests                  # multiplatform tests
./gradlew :composeApp:run                       # THE important one: proves tokens + DI resolve
./gradlew :androidApp:installDebug              # touch sizing, ripple, insets
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Task gotchas specific to this project:

*   Use `:composeApp:packageDistributionForCurrentOS`, **not** `:composeApp:package` — the umbrella
    task is not configuration-cache compatible.
*   `:composeApp:generateIcons` needs `--no-configuration-cache` and is deliberately on-demand; its
    dependency edge onto every Kotlin/Native link task is cut in `composeApp/build.gradle.kts`.
*   iOS builds from Xcode need `OTHER_LDFLAGS = -lsqlite3`, generated into
    `iosApp/Configuration/Config.xcconfig` by the `syncIosConfig` task. Never edit that file by hand.

---

## 🐛 Common Failure Modes

| Symptom | Cause | Fix |
|---|---|---|
| `There is no <property> property in the AppTheme theme` at runtime | A token is read but not defined | Add it to the matching `properties[...]` map in `AppTheme.kt` |
| `Tried to access the value of the token called <x>` | Same, for a single token | Same |
| `NoDefinitionFoundException` for a service | Bound to the impl instead of the interface | `bind FooService::class` |
| `NoDefinitionFoundException` for a ViewInteractor | Not registered | Add a `factory { }` to `commonModule()` |
| Settings silently reset after adding a field | DTO field has no default | Give every persisted DTO field a default |
| Leading and trailing toolbar slots overlap | A container that does not stretch | Ensure `fillMaxWidth()`; `AppToolbar` already does |
| Compiles, then looks unstyled | Imported a Material component | Import from `com.composables.ui.components.*` |
| Ripple on iOS, or no hover on desktop | Press effect hardcoded instead of themed | Let `appPressIndication()` drive the indication tokens |