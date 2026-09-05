import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.kmpAppIconGenerator) apply false
    alias(libs.plugins.buildkonfig) apply false
}

val syncIosConfig by tasks.registering {
    group = "build setup"
    description = "Syncs iOS configuration from libs.versions.toml"
    
    val versionsTomlFile = file("gradle/libs.versions.toml")
    val configFile = file("iosApp/Configuration/Config.xcconfig")
    
    inputs.file(versionsTomlFile)
    outputs.file(configFile)
    
    doLast {
        val versionsTomlContent = versionsTomlFile.readText()
        
        fun getValue(key: String): String {
            val regex = Regex("""^\s*${Regex.escape(key)}\s*=\s*"(.*)"\s*$""", RegexOption.MULTILINE)
            return regex.find(versionsTomlContent)?.groupValues?.get(1) 
                ?: throw GradleException("Key '$key' not found in libs.versions.toml")
        }

        val appName = getValue("app-name")
        val appId = getValue("app-appId")
        val versionCode = getValue("app-versionCode")
        val versionName = getValue("app-versionName")

        val configContent = """
            // This file is auto-generated from gradle/libs.versions.toml
            // Do not modify directly.
            
            PRODUCT_NAME=$appName
            PRODUCT_BUNDLE_IDENTIFIER=$appId

            CURRENT_PROJECT_VERSION=$versionCode
            MARKETING_VERSION=$versionName
        """.trimIndent()

        configFile.writeText(configContent)
        logger.lifecycle("Synced iosApp/Configuration/Config.xcconfig with libs.versions.toml")
    }
}

// Resolved eagerly into a plain String: the resolution rule below runs at execution time and
// must not capture the catalog accessor, or the configuration cache cannot serialize it.
val kotlinVersion = libs.versions.kotlin.get()

allprojects {
    tasks.withType<KotlinCompile> {
        dependsOn(rootProject.tasks.named("syncIosConfig"))
    }
    // Transitive dependencies still pin older stdlibs; keep everything on the catalog's Kotlin
    // version so the compiler and the runtime library can never drift apart.
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib")) {
                useVersion(kotlinVersion)
            }
        }
    }
}