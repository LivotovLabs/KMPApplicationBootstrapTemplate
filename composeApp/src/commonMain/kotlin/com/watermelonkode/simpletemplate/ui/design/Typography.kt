package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken

/**
 * Typography scale.
 *
 * Composables UI ships no typography tokens of its own -- it only sets a single default body style
 * -- so the app defines its own theme property. Read the styles the same way as any built-in token:
 *
 * ```kotlin
 * Text(text = "Title", style = Theme[typography][h1])
 * ```
 *
 * No `fontFamily` is set anywhere on purpose. Leaving it unspecified makes Compose fall back to the
 * platform's own UI font -- San Francisco on iOS and macOS, Roboto on Android, Segoe on Windows,
 * whatever the browser resolves on web -- which is most of what makes text feel native. Set one
 * only if your brand genuinely requires it.
 */
val typography: ThemeProperty<TextStyle> = ThemeProperty("typography")

val h1: ThemeToken<TextStyle> = ThemeToken("h1")
val h2: ThemeToken<TextStyle> = ThemeToken("h2")
val h3: ThemeToken<TextStyle> = ThemeToken("h3")
val bodyLarge: ThemeToken<TextStyle> = ThemeToken("body_large")
val bodyMedium: ThemeToken<TextStyle> = ThemeToken("body_medium")
val labelLarge: ThemeToken<TextStyle> = ThemeToken("label_large")
val labelMedium: ThemeToken<TextStyle> = ThemeToken("label_medium")

/** Style of the title in [com.watermelonkode.simpletemplate.ui.design.components.AppToolbar]. */
val toolbarTitle: ThemeToken<TextStyle> = ThemeToken("toolbar_title")

/**
 * Base body size per platform: iOS 17pt is the system body size, Android 16sp matches Material's
 * bodyLarge, and pointer platforms read comfortably a couple of points smaller.
 */
private val bodySize: TextUnit = when (appPlatform) {
    AppPlatform.Android -> 16.sp
    AppPlatform.IOS -> 17.sp
    AppPlatform.Desktop -> 14.sp
    AppPlatform.Web -> 15.sp
}

private val bodyLineHeight: TextUnit = when (appPlatform) {
    AppPlatform.Android -> 24.sp
    AppPlatform.IOS -> 22.sp
    AppPlatform.Desktop -> 20.sp
    AppPlatform.Web -> 22.sp
}

/**
 * Toolbar titles diverge sharply: Material 3 uses a large 22sp title, UIKit a compact 17pt
 * semibold one, and desktop bars are smaller again.
 */
private val appToolbarTitleStyle: TextStyle = when (appPlatform) {
    AppPlatform.Android -> TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Normal)
    AppPlatform.IOS -> TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
    AppPlatform.Desktop, AppPlatform.Web ->
        TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
}

/** The theme's default text style, inherited by any [com.composables.ui.components.Text] without an explicit style. */
internal val appBodyTextStyle = TextStyle(
    fontSize = bodySize,
    lineHeight = bodyLineHeight,
    fontWeight = FontWeight.Normal,
)

internal val appTypography: Map<ThemeToken<TextStyle>, TextStyle> = mapOf(
    h1 to TextStyle(fontSize = bodySize * 2, lineHeight = bodySize * 2.5f, fontWeight = FontWeight.Bold),
    h2 to TextStyle(fontSize = bodySize * 1.5f, lineHeight = bodySize * 2, fontWeight = FontWeight.Bold),
    h3 to TextStyle(fontSize = bodySize * 1.15f, lineHeight = bodySize * 1.6f, fontWeight = FontWeight.SemiBold),
    bodyLarge to appBodyTextStyle,
    bodyMedium to TextStyle(
        fontSize = bodySize * 0.875f,
        lineHeight = bodyLineHeight * 0.85f,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge to TextStyle(
        fontSize = bodySize * 0.875f,
        lineHeight = bodyLineHeight * 0.85f,
        fontWeight = FontWeight.Medium,
    ),
    labelMedium to TextStyle(
        fontSize = bodySize * 0.75f,
        lineHeight = bodyLineHeight * 0.75f,
        fontWeight = FontWeight.Medium,
    ),
    toolbarTitle to appToolbarTitleStyle,
)
