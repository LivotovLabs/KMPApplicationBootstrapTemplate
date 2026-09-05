# LAUNCHPAD — Bootstrap a New App From This Template

**For the coding agent.** This is an executable procedure, not background reading. Invoke it with
something like *"bootstrap the app project according to LAUNCHPAD.md"*.

Goal: turn this checkout from *a template* into *one specific app*, so that afterwards nobody has to
think about template leftovers again. That means renaming every identifier, replacing the icon,
stripping the template's own metadata, and proving all targets still build and run.

Work top to bottom. Do not skip §1 (you need the answers) or §5 (a rename that compiles can still be
half-applied).

---

## 0. Preflight

*   Confirm this checkout is still an unbootstrapped template — if `grep -rl watermelonkode --exclude-dir=.git .`
    comes back empty, it has already been bootstrapped. Stop and ask before touching anything.
*   Confirm the working tree is clean (`git status`). Everything below is a bulk rename; the user
    wants to be able to `git diff` it, and to `git checkout .` if they hate it.
*   A fresh clone has no `local.properties` (it is git-ignored), so the Android build cannot find the
    SDK. Create it if missing:
    `echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties` (adjust for the platform), or
    export `ANDROID_HOME`.

---

## 1. Collect the inputs

Ask the user for these six things. Ask for all of them **in one go**, show the defaults, and
validate each answer before moving on. Re-ask on invalid input rather than silently correcting it.

| # | Input | Rules | Example |
|---|---|---|---|
| 1 | **Gradle project name** | `^[A-Za-z][A-Za-z0-9_]*$`. No spaces, dashes or dots — it becomes `rootProject.name` and part of a generated Kotlin package. | `AcmeNotes` |
| 2 | **Display name** | Free text. What users see under the icon, in the window title bar and the browser tab. | `Acme Notes` |
| 3 | **Application ID** | Reverse-DNS, `^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$`, at least two segments. Lowercase only, no dashes. This is the Android `applicationId` **and** the iOS bundle identifier — publishing it is irreversible, so confirm it. | `com.acme.notes` |
| 4 | **Kotlin package / namespace** | Same rules as #3. Becomes the root package of all shared source. Default: same as #3 — offer that. | `com.acme.notes` |
| 5 | **App icon** | Either a path to a source file (SVG, or square PNG ≥ 1024×1024), or a short description for you to generate one. See §4. | `~/art/notes.svg` |
| 6 | **Targets to keep** | Any subset of **iOS, Android, Desktop, Web/WASM**. At least one. Default: all four. Removing one now is much cleaner than removing it after you have written code. See §3a. | `Android, iOS` |

**Reject a segment of #3/#4 that is a Kotlin hard keyword** (`in`, `is`, `as`, `if`, `for`, `fun`,
`val`, `var`, `object`, `class`, `package`, `this`, `null`, `true`, `false`, `when`, `typealias`).
It becomes a package name and will not compile. Ask for a different segment.

Optional, with defaults — mention them but do not block on them:

*   Version name / code — default `1.0.0` / `1`.
*   **Git**: reinitialise history (`rm -rf .git && git init`), point `origin` at a new repo, or leave
    alone? Default: **ask**, never decide this yourself.
*   **License**: the template is Apache-2.0. Keep, replace, or remove?
*   **Demo screens**: keep `Home`/`Details` as working reference code (default), or strip to a single
    empty screen?

---

## 2. Derive and confirm the plan

Compute the table below and **show it to the user for confirmation before editing anything.** This
step is not optional — most of it is a rename you cannot undo without git.

Let `NAME` = #1, `DISPLAY` = #2, `APP_ID` = #3, `PKG` = #4.

| Target | Value |
|---|---|
| `settings.gradle.kts` → `rootProject.name` | `NAME` |
| catalog `app-name` | `NAME` |
| catalog `app-displayName` | `DISPLAY` |
| catalog `app-appId` | `APP_ID` |
| catalog `android-namespace` (composeApp) | `PKG` |
| catalog `app-namespace` (androidApp) | `PKG.android` |
| catalog `app-desktop-entrypoint` | `PKG.MainKt` |
| Kotlin package, all `composeApp` source sets | `com.watermelonkode.simpletemplate` → `PKG` |
| Kotlin package, `androidApp` module | `com.watermelonkode.androidapp` → `PKG.android` |
| `buildkonfig { packageName }` | `PKG` |
| Compose Resources package | derived from `rootProject.name` — see §3, step 5 |

> **Why `androidApp` gets a `.android` suffix.** AGP requires every module's `namespace` to be
> unique; `composeApp` and `androidApp` cannot share one. The user-visible identity is `applicationId`
> (`APP_ID`), which is independent of namespace, so this suffix never leaks to users.

---

## 3. Apply the identity

Do these in order. Steps 3 and 5 depend on 1 and 2 having already happened.

### Step 3a — Prune unwanted targets

Do this **first**, before the rename: fewer source sets means less to rename and a much faster
verification loop. Skip the whole section if the user kept all four.

Never remove every target — if the user's answer leaves none, go back and ask again.

**Android** — this is two things, the `composeApp` Android target *and* the `androidApp` launcher
module:

*   `composeApp/build.gradle.kts`: delete the `androidLibrary { … }` block and the
    `androidMain.dependencies { … }` block; drop `alias(libs.plugins.androidKmpLibrary)` from
    `plugins`.
*   Delete `composeApp/src/androidMain/` and the whole `androidApp/` directory.
*   `settings.gradle.kts`: remove `include(":androidApp")`.
*   Root `build.gradle.kts`: remove the `androidApplication`, `androidLibrary`, `androidKmpLibrary`
    and `kotlinAndroid` plugin aliases.
*   Catalog: `android-compileSdk`, `android-minSdk`, `android-targetSdk`, `android-namespace`,
    `app-namespace`, `gservices`, `androidx-*`, `composeBom`, `splashscreen` and their
    `[libraries]`/`[plugins]` entries all become unused.
*   `local.properties` no longer needs `sdk.dir`.

**iOS:**

*   `composeApp/build.gradle.kts`: delete the `listOf(iosArm64(), iosSimulatorArm64()).forEach { … }`
    block, the `val iosMain by creating { … }` source set and the two
    `getByName("ios…Main").dependsOn(iosMain)` lines. Also delete the `afterEvaluate { … }` block
    that cuts the `generateIcons` edge off `KotlinNativeLink` — with no native targets it is dead
    code — and its `import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink`.
*   Delete `composeApp/src/iosMain/` and the whole `iosApp/` directory.
*   Root `build.gradle.kts`: delete the `syncIosConfig` task **and** the `allprojects { tasks.withType<KotlinCompile> { dependsOn(…) } }`
    block that wires it in, plus the now-unused `KotlinCompile` import. Leave the rest of
    `allprojects` (the stdlib resolution rule) in place.
*   Catalog: `ktor-client-darwin` becomes unused. Keep `kmpAppIconGenerator` only if Android stays.

**Desktop:**

*   `composeApp/build.gradle.kts`: delete `jvm("desktop")`, the `getByName("desktopMain") { … }`
    source set, the entire `compose.desktop { … }` block at the bottom, and the
    `import org.jetbrains.compose.desktop.application.dsl.TargetFormat`.
*   Delete `composeApp/src/desktopMain/`.
*   Catalog: `app-desktop-entrypoint` and `kotlinx-coroutines-swing` become unused.

**Web/WASM:**

*   `composeApp/build.gradle.kts`: delete the `wasmJs { … }` block with its
    `@OptIn(ExperimentalWasmDsl::class)`, the `getByName("wasmJsMain") { … }` source set, and the
    `import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl`.
*   Delete `composeApp/src/wasmJsMain/`.
*   Catalog: `ktor-client-cio` becomes unused.

Delete whole blocks by matching their exact text, not by counting lines or offsets. It is very easy
to leave a stray `}` behind, and the resulting error ("Unexpected symbol" 100 lines away) points
nowhere near the real damage. Cheap sanity check before invoking Gradle:

```bash
python3 -c "
for f in ['composeApp/build.gradle.kts','build.gradle.kts']:
    t = open(f).read(); print(f, 'braces balanced:', t.count('{') == t.count('}'))"
```

Then confirm the project still configures before going further — a stray reference to a deleted
source set fails at configuration time, which is much easier to read now than after the rename:

```bash
./gradlew projects
```

Both directions of this were tested on this template: keeping Android + Desktop, and keeping iOS
alone. Each configured, passed `allTests`, and built its remaining targets.

Two things **not** to prune:

*   `AppPlatform` in `ui/design/Platform.kt`. Its `when (Platform.current)` is exhaustive over
    OSKit's `Platform` enum, not over your build targets, so all four branches must stay even if you
    only ship one. Dead branches cost nothing.
*   `commonTest`, and the `expect`/`actual` pairs for the platforms you kept.

Removing unused catalog entries is optional housekeeping — an unused entry is harmless. Deleting a
target's source set and Gradle blocks is the part that matters.

### Step 1 — Version catalog

Edit `gradle/libs.versions.toml` `[versions]` and set each key to its value from the §2 table:
`app-name`, `app-displayName`, `app-appId`, `android-namespace`, `app-namespace`,
`app-desktop-entrypoint`, and `app-versionName` / `app-versionCode` if the user changed them.

Set these **explicitly**. Do not let the rename in step 3 touch this file — `app-appId` must end up
as `APP_ID`, not `PKG.android`, and a blind search-and-replace would get that wrong.

### Step 2 — Project name

`settings.gradle.kts`: `rootProject.name = "NAME"`.

### Step 3 — Rename the Kotlin packages

Move the directories, then rewrite the package and import statements. Two independent packages, no
shared prefix beyond `com.watermelonkode`, so order does not matter — but never replace
`com.watermelonkode` on its own.

```bash
OLD_SHARED="com.watermelonkode.simpletemplate"
OLD_ANDROID="com.watermelonkode.androidapp"
NEW_SHARED="<PKG>"
NEW_ANDROID="<PKG>.android"

# --- move directories (composeApp: every source set) ---
for src in composeApp/src/*/kotlin; do
  old="$src/${OLD_SHARED//.//}"
  [ -d "$old" ] || continue
  new="$src/${NEW_SHARED//.//}"
  mkdir -p "$(dirname "$new")"
  git mv "$old" "$new"
done

# --- move directories (androidApp: main, test, androidTest) ---
for src in androidApp/src/*/java; do
  old="$src/${OLD_ANDROID//.//}"
  [ -d "$old" ] || continue
  new="$src/${NEW_ANDROID//.//}"
  mkdir -p "$(dirname "$new")"
  git mv "$old" "$new"
done

# --- rewrite references in code, resources and docs ---
# .toml is deliberately absent: the catalog was set by hand in step 1.
find . -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.md" \) \
  -not -path "./build/*" -not -path "*/build/*" -not -path "./.git/*" -print0 |
  xargs -0 sed -i '' -e "s/$OLD_ANDROID/$NEW_ANDROID/g" -e "s/$OLD_SHARED/$NEW_SHARED/g"

# --- prune the now-empty old directories ---
find composeApp/src androidApp/src -type d -empty -delete
```

On GNU `sed` (Linux) drop the `''` after `-i`.

`*.md` is included on purpose: `README.md` and `CLAUDE.md` both cite the old package in their folder
trees and import examples, and stale docs are worse than none.

### Step 4 — Android manifest, theme and labels

*   `androidApp/src/main/AndroidManifest.xml` — `android:name` for the Application class and
    `.MainActivity` are already handled by step 3. Rename the theme reference
    `@style/Theme.SimpleTemplate` → `@style/Theme.App` (two occurrences: `<application>` and
    `<activity>`).
*   `androidApp/src/main/res/values/themes.xml` — rename the style to `Theme.App`. Keep the
    `android:Theme.Material.Light.NoActionBar` parent: that is a *platform* theme the splash screen
    and window background need, not a Compose Material dependency.
*   Set `DISPLAY` as `app_name` in **both** string files:
    *   `androidApp/src/main/res/values/strings.xml`
    *   `composeApp/src/androidMain/res/values/strings.xml`

The desktop window title, the browser tab title and the iOS display name all come from
`app-displayName` via BuildKonfig and the generated xcconfig, so they need no manual edit — except
`composeApp/src/wasmJsMain/resources/index.html`, whose `<title>` is static HTML. Set it to `DISPLAY`.

### Step 5 — Fix the generated Compose Resources import

`rootProject.name` determines the package of the generated `Res` class, so changing it in step 2
breaks the import in `HomeScreen.kt`. Derive the new package rather than guessing it:

```bash
./gradlew :composeApp:generateComposeResClass -q
find composeApp/build -name "Res.kt" -path "*generated*" | head -1 | xargs grep -m1 '^package'
```

Then rewrite the imports to whatever that printed (for `rootProject.name = AcmeNotes` it is
`acmenotes.composeapp.generated.resources`):

```bash
NEWPKG="<the package printed above>"
find composeApp/src -name "*.kt" -print0 | xargs -0 sed -i '' \
  "s/kmpapplicationtemplate\.composeapp\.generated\.resources/$NEWPKG/g"
```

In the stock template the only affected file is `HomeScreen.kt`. Verify with
`grep -rn "generated.resources" $(find composeApp/src -name '*.kt')`.

### Step 6 — iOS

```bash
./gradlew syncIosConfig          # regenerates iosApp/Configuration/Config.xcconfig
rm -rf iosApp/iosApp.xcodeproj/xcuserdata   # other developers' schemes, do not inherit these

# Stale built-product reference left over from the template's own PRODUCT_NAME. Cosmetic -- Xcode
# takes the real name from the xcconfig -- but it shows up in the Products group and in the
# acceptance grep in §5.
sed -i '' "s/Simple Template\.app/<NAME>.app/g" iosApp/iosApp.xcodeproj/project.pbxproj
```

Never hand-edit `Config.xcconfig`; it is generated from the catalog. Apart from that one product
reference, leave `project.pbxproj` alone: the Xcode *target* stays named `iosApp` and the framework
stays `ComposeApp`, neither is user-visible, and renaming them properly means restructuring the
project file for no gain.

---

## 4. App icon

The source of truth is a single file in `composeApp/src/commonMain/composeResources/drawable/`,
named `icon.svg` (preferred) **or** `icon.png`.

> Note that `generateIcons` rasterizes an SVG source into `icon.png` in that same folder, so an
> `icon.png` appearing after a build is *output*, not a second input — it is git-ignored for exactly
> that reason. If you supply a PNG as your source instead, remove that rule from `.gitignore` so it
> is tracked.

**If the user supplied a file:** copy it in under the right name (SVG preferred; PNG must be square
and ≥ 1024×1024).

**If the user asked you to generate one:** write an SVG. Keep it simple — it has to read at 40px.
A rounded-square background plus one bold mark or monogram works; avoid words, fine detail and thin
strokes. The template's own `icon.svg` is a minimal example to start from:

```svg
<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 512 512">
  <rect width="512" height="512" rx="100" ry="100" fill="#4CAF50"/>
  <circle cx="256" cy="256" r="150" fill="white"/>
  <path d="M256 150 L350 350 L162 350 Z" fill="#4CAF50"/>
</svg>
```

Show the user what you generated and offer to iterate before continuing.

**Generate the platform icons** (Android `mipmap-*` and iOS `Assets.xcassets`):

```bash
./gradlew :composeApp:generateIcons --no-configuration-cache
```

The `--no-configuration-cache` flag is required: the icon plugin is not cache-compatible, which is
also why its hook onto every Kotlin/Native link task is deliberately cut in
`composeApp/build.gradle.kts`.

**Desktop icons are not covered by that task.** Replace them by hand in
`composeApp/src/desktopMain/resources/icons/` — `icon.png`, `icon.icns` (macOS), `icon.ico`
(Windows). From a 1024×1024 PNG on macOS:

```bash
mkdir -p /tmp/icon.iconset
for s in 16 32 128 256 512; do
  sips -z $s $s source-1024.png --out /tmp/icon.iconset/icon_${s}x${s}.png
  sips -z $((s*2)) $((s*2)) source-1024.png --out /tmp/icon.iconset/icon_${s}x${s}@2x.png
done
iconutil -c icns /tmp/icon.iconset -o composeApp/src/desktopMain/resources/icons/icon.icns
cp source-1024.png composeApp/src/desktopMain/resources/icons/icon.png
```

For `.ico`, use ImageMagick if available:
`magick source-1024.png -define icon:auto-resize=256,128,64,48,32,16 composeApp/src/desktopMain/resources/icons/icon.ico`.
If it is not installed, say so plainly and leave the placeholder rather than shipping a broken file.

---

## 5. Verify

A rename that compiles can still be half-applied, and two whole classes of failure in this stack do
not surface at compile time. Run all of it.

**Acceptance check — no template identifiers may survive:**

```bash
grep -rIn --exclude-dir=build --exclude-dir=.git --exclude-dir=.gradle --exclude=LAUNCHPAD.md \
  -e watermelonkode -e simpletemplate -e SimpleTemplate \
  -e KMPApplicationTemplate -e kmpapplicationtemplate -e "Simple Template" .
```

This must print **nothing**. Any hit is an unfinished rename — fix it before continuing.
`LAUNCHPAD.md` is excluded because it necessarily quotes the old identifiers; §6 deletes it anyway.

**Builds:**

Run only the lines for the targets the user kept (§3a).

```bash
./gradlew clean
./gradlew :composeApp:allTests
./gradlew :androidApp:assembleDebug                            # Android
./gradlew :composeApp:wasmJsBrowserDistribution                # Web/WASM
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64      # iOS
./gradlew :composeApp:packageDistributionForCurrentOS          # Desktop
```

**Run it — this is the step that matters.** Theme tokens resolve at composition time and Koin
resolves at runtime, so a green build proves neither.

```bash
./gradlew :composeApp:run          # Desktop — window title must show DISPLAY
./gradlew :androidApp:installDebug # Android — launcher label and icon must be the new ones
```

If the user dropped both Desktop and Android, run the app on whichever target remains (a browser via
`:composeApp:wasmJsBrowserDevelopmentRun`, or the iOS simulator below). Do not skip this — it is the
only step that exercises theme tokens and DI.

For iOS, build and launch on a simulator and confirm the display name and icon:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug \
  -destination "id=$(xcrun simctl list devices available -j | \
    python3 -c "import json,sys;print(next(d['udid'] for v in json.load(sys.stdin)['devices'].values() for d in v if 'iPhone' in d['name']))")" \
  -derivedDataPath /tmp/iosbuild build
```

---

## 6. Strip the template's own identity

These files belong to the template, not to the new app.

**Delete:**

*   `LAUNCHPAD.md` — this file. Last thing you do.
*   `.github/FUNDING.yml` — funds the *template's* author.
*   `composeApp/src/commonMain/composeResources/.DS_Store`
*   `drawable/icon.svg` **only if** the user supplied a PNG source instead — otherwise keep it, it
    is the icon source. Never delete both.

**Rewrite `README.md`:** replace the title, the intro, **Project Foundation**, **Version
Information** and the whole **Release Notes** section — that is the template's history, not the
app's. Keep and keep current: **Project Layout**, **Architecture & Core Concepts**, **Design
System**, **Configuration**, **Building & Running**, **Verifying Changes**, **Troubleshooting**.

**Keep as-is:** `CLAUDE.md` and `GEMINI.md`. The conventions still apply and agents load them
automatically. Step 3 already updated the package names inside them.

**Signing:** `gradle.properties` ships a placeholder keystore and dummy passwords. Tell the user to
generate a real keystore and move the credentials to `local.properties` (git-ignored) before any
release build. Do not generate a keystore or invent passwords yourself.

**License:** act on the user's answer from §1.

**Git:** act on the user's answer from §1. If they chose to reinitialise, do it only after §5 passes,
so the previous state stays recoverable until then.

---

## 7. Report back

Tell the user, concisely:

*   The final values applied (the §2 table).
*   Which verification steps passed, and which you could not run — for example iOS needs macOS and
    Xcode, and `.ico` generation needs ImageMagick. Say so explicitly rather than implying coverage
    you do not have.
*   Anything still on them: real signing keystore, desktop `.ico` if it was skipped, license choice,
    git remote.

Then confirm the project is ready for feature work, and point at `CLAUDE.md` as the conventions to
follow from here.

---

## Reference: everything that carries the template's identity

Useful if a rename looks incomplete and you need to audit by hand.

| Where | What |
|---|---|
| `gradle/libs.versions.toml` | `app-name`, `app-displayName`, `app-appId`, `android-namespace`, `app-namespace`, `app-desktop-entrypoint` |
| `settings.gradle.kts` | `rootProject.name` → also drives the Compose Resources package |
| `composeApp/build.gradle.kts` | `buildkonfig { packageName }` |
| `composeApp/src/*/kotlin/…` | package + import statements, all five source sets |
| `androidApp/src/*/java/…` | package + import statements, three source sets |
| `androidApp/src/main/AndroidManifest.xml` | Application class, `.MainActivity`, `@style/Theme.*` |
| `androidApp/src/main/res/values/themes.xml` | style name |
| `androidApp/.../strings.xml`, `composeApp/src/androidMain/.../strings.xml` | `app_name` |
| `composeApp/src/wasmJsMain/resources/index.html` | `<title>` |
| `composeApp/src/commonMain/…/HomeScreen.kt` | generated-resources import |
| `iosApp/Configuration/Config.xcconfig` | generated — run `syncIosConfig`, never edit |
| `composeApp/src/commonMain/composeResources/drawable/` | `icon.svg` / `icon.png` |
| `composeApp/src/androidMain/res/mipmap-*`, `iosApp/…/Assets.xcassets` | generated by `generateIcons` |
| `composeApp/src/desktopMain/resources/icons/` | manual: `icon.png`, `icon.icns`, `icon.ico` |
| `composeApp/build.gradle.kts`, root `build.gradle.kts`, `settings.gradle.kts` | per-target blocks removed in §3a |
| `README.md`, `CLAUDE.md` | package names in trees and examples |
| `.github/FUNDING.yml`, `LICENSE`, `LAUNCHPAD.md` | template metadata |
