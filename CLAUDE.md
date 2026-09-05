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
  ├── commonMain/kotlin/com/example/app/
  │   ├── domain/               # Pure business rules & abstractions
  │   │   ├── interactor/       # App/Domain Interactors (e.g., AuthInteractor)
  │   │   ├── model/            # Entities, Enums, Error sealed classes
  │   │   └── service/          # Service Interfaces (e.g., ApiService)
  │   ├── service/              # Infrastructure Implementations
  │   │   ├── api/              # Ktor client, endpoints, DTOs
  │   │   ├── datastore/        # KVStore, Preferences
  │   │   └── device/           # Platform-specific service impls
  │   ├── ui/                   # Presentation Layer
  │   │   ├── design/           # AppTheme + Colors/Shapes/Typography/Spacing tokens
  │   │   │                     # Platform.kt/PressEffects.kt/Metrics.kt = platform feel
  │   │   │   └── components/   # Shared stateless widgets (AppScreen, AppToolbar, AppScrollbar)
  │   │   ├── route/            # Coordinators & Routes
  │   │   └── screen/           # Screens & ViewInteractors (grouped by feature)
  │   ├── shared/               # Common Utilities (Formatting, Extensions)
  │   └── DI.kt                 # Koin Dependency Injection modules
  ├── androidMain/kotlin/com/example/app/
  │   └── PlatformDI.kt         # Android specific DI module & actuals
  └── iosMain/kotlin/com/example/app/
      └── PlatformDI.kt         # iOS specific DI module & actuals
```

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

// In service/api/ApiService.kt
class ApiServiceImpl(private val http: HttpService) : ApiService {
    override suspend fun fetchItems(): Outcome<List<Item>, ApiError> {
        val result = http.get<List<ItemDto>>("https://api.example.com/items")
        
        return when (result) {
            is Outcome.Ok -> Outcome.Ok(result.value.map { it.toModel() })
            is Outcome.Error -> Outcome.Error(ApiError.NetworkError)
        }
    }
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
*   Define dependencies in `DI.kt`.
*   Use `factory` for `ViewInteractors` (creates a new instance per screen/component).
*   Use `single` for `AppInteractors`, `Coordinators`, and `Services` (Singleton lifecycle).
*   Use `expect fun platformModule()` to inject iOS/Android specific implementations.

```kotlin
fun commonModule() = module {
    // Services
    single { ApiServiceImpl(get()) } bind ApiService::class
    
    // Domain Interactors
    single { DataInteractor(get()) }
    
    // Navigation
    single { AppCoordinator() }
    
    // View Interactors
    factory { FeatureViewInteractor(get(), get()) }
    factory { params -> DetailViewInteractor(params[0], get(), get()) } // Screen with args
    factory { ComplexWidgetViewInteractor(get()) } // Component with own interactor
}
```

### 5. Expected Error Handling
Never throw plain exceptions to the UI. Always handle expected failures with the `Outcome` pattern and `sealed class` error types. Provide extension functions (e.g., `.toUserMessage()`) in the UI layer to translate Domain errors into human-readable Strings using Compose String Resources.