package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken

/**
 * Spacing scale, exposed as a theme property so layout metrics are read the same way as colours:
 *
 * ```kotlin
 * Column(verticalArrangement = Arrangement.spacedBy(Theme[spacing][elementPadding]))
 * ```
 *
 * Prefer these tokens over literal `.dp` values in screens -- that is what keeps a rebrand to a
 * single-file change.
 */
val spacing: ThemeProperty<Dp> = ThemeProperty("spacing")

/** Outer padding of a screen's content. */
val screenPadding: ThemeToken<Dp> = ThemeToken("screen_padding")

/** Gap between sibling elements in a group. */
val elementPadding: ThemeToken<Dp> = ThemeToken("element_padding")

/** Tight gap, e.g. between an icon and its label. */
val smallPadding: ThemeToken<Dp> = ThemeToken("small_padding")

internal val appSpacing: Map<ThemeToken<Dp>, Dp> = mapOf(
    screenPadding to 16.dp,
    elementPadding to 12.dp,
    smallPadding to 8.dp,
)
