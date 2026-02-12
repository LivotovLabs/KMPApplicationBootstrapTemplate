import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
                "-Xbuild-version-string=${libs.versions.app.versionName.get()}", // e.g. "1.0.0"
                "-Xbuild-number=${libs.versions.app.versionCode.get()}", // build number
            )
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // DI
            implementation(libs.koin.core)

            // OSKIT
            api(libs.oskit.kmp)
            implementation(libs.oskit.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val wasmJsMain by getting

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