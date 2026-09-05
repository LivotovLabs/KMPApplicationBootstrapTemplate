package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
 */
val typography: ThemeProperty<TextStyle> = ThemeProperty("typography")

val h1: ThemeToken<TextStyle> = ThemeToken("h1")
val h2: ThemeToken<TextStyle> = ThemeToken("h2")
val h3: ThemeToken<TextStyle> = ThemeToken("h3")
val bodyLarge: ThemeToken<TextStyle> = ThemeToken("body_large")
val bodyMedium: ThemeToken<TextStyle> = ThemeToken("body_medium")
val labelLarge: ThemeToken<TextStyle> = ThemeToken("label_large")
val labelMedium: ThemeToken<TextStyle> = ThemeToken("label_medium")

/**
 * Builds the typography scale for the current input method.
 *
 * Touch devices get a slightly larger body size, matching how Composables UI scales its own
 * default text style, so app text and component text stay visually consistent.
 */
@Composable
internal fun appTypography(useTouchSizes: Boolean): Map<ThemeToken<TextStyle>, TextStyle> = mapOf(
    h1 to TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold),
    h2 to TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    h3 to TextStyle(fontSize = 18.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge to appBodyTextStyle(useTouchSizes),
    bodyMedium to TextStyle(
        fontSize = if (useTouchSizes) 15.sp else 13.sp,
        lineHeight = if (useTouchSizes) 20.sp else 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge to TextStyle(
        fontSize = if (useTouchSizes) 15.sp else 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
    ),
    labelMedium to TextStyle(
        fontSize = if (useTouchSizes) 13.sp else 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
)

/** The theme's default text style, inherited by any [com.composables.ui.components.Text] without an explicit style. */
internal fun appBodyTextStyle(useTouchSizes: Boolean) = TextStyle(
    fontSize = if (useTouchSizes) 17.sp else 15.sp,
    lineHeight = if (useTouchSizes) 24.sp else 20.sp,
    fontWeight = FontWeight.Normal,
)
