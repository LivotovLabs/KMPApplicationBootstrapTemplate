package com.watermelonkode.simpletemplate.domain.model.settings

data class AppSettings(
    val muted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
)

/**
 * Which colour scheme the app should use.
 *
 * Consumed by [com.watermelonkode.simpletemplate.ui.design.AppTheme], which resolves
 * [System] against the platform's current setting.
 */
enum class ThemeMode {
    System,
    Light,
    Dark,
}
