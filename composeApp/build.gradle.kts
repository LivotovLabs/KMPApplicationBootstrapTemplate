import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kmpAppIconGenerator)
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
        val commonMain by getting
        val commonTest by getting

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

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

        iosMain.dependencies {
            // Ktor Platform Specific
            implementation(libs.ktor.client.darwin)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor Platform Specific
                implementation(libs.ktor.client.okhttp)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                // Ktor Platform Specific
                implementation(libs.ktor.client.cio)
            }
        }

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