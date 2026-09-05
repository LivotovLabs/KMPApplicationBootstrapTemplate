import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kmpAppIconGenerator)
    alias(libs.plugins.buildkonfig)
}

buildkonfig {
    packageName = "com.watermelonkode.simpletemplate"
    objectName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "APP_VERSION", libs.versions.app.versionName.get())
        buildConfigField(FieldSpec.Type.INT, "APP_BUILD_NUMBER", libs.versions.app.versionCode.get())
    }
}

kotlin {
    androidLibrary {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        namespace = libs.versions.android.namespace.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources.enable = true
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")

            export(libs.oskit.kmp)
            export(libs.kotlinx.coroutines.core)

            binaryOption("bundleId", libs.versions.app.appId.get())
            binaryOption("bundleVersion", libs.versions.app.versionCode.get())

            freeCompilerArgs += listOf(
                "-Xbuild-version-string=${libs.versions.app.versionName.get()}",
                "-Xbuild-number=${libs.versions.app.versionCode.get()}",
            )
        }
    }

    sourceSets {
        val commonMain = getByName("commonMain")
        val commonTest = getByName("commonTest")

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Design system: Composables UI + Compose Unstyled theming + Lucide icons
            implementation(libs.composables.ui)
            implementation(libs.composeunstyled)
            implementation(libs.composeunstyled.theming)
            implementation(libs.composables.interaction)
            implementation(libs.composables.ripple)
            implementation(libs.composables.icons)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)

            // Logger
            implementation(libs.logger)

            // DI
            implementation(libs.koin.core)

            // OSKIT
            api(libs.oskit.kmp)
            implementation(libs.oskit.compose)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization.json)
            implementation(libs.ktor.client.logging)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)

            // Ktor Platform Specific
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        
        getByName("iosArm64Main").dependsOn(iosMain)
        getByName("iosSimulatorArm64Main").dependsOn(iosMain)

        getByName("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor Platform Specific
                implementation(libs.ktor.client.okhttp)
            }
        }

        getByName("wasmJsMain") {
            dependencies {
                // Ktor Platform Specific
                implementation(libs.ktor.client.cio)
            }
        }

    }
}

// kmp-app-icon-generator hooks `generateIcons` onto every Kotlin/Native link task, and that task
// holds a Project reference at execution time -- which fails the configuration cache for any iOS
// build or test. Icon generation is a one-off setup step that rewrites files in the source tree
// (see README > Application Icons), so it has no business running on every link. Cut the edge and
// keep the task on demand:
//
//     ./gradlew :composeApp:generateIcons --no-configuration-cache
//
// Deferred to afterEvaluate so the plugin has already added the dependency by the time we drop it.
afterEvaluate {
    tasks.withType<KotlinNativeLink>().configureEach {
        setDependsOn(dependsOn.filterNot { (it as? TaskProvider<*>)?.name == "generateIcons" })
    }
}

compose.desktop {
    application {
        mainClass = libs.versions.app.desktop.entrypoint.get()
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = libs.versions.app.appId.get()
            packageVersion = libs.versions.app.versionName.get()

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
    }
}