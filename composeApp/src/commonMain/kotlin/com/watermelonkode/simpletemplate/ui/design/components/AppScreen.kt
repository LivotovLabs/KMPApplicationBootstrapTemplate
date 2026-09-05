package com.watermelonkode.simpletemplate.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.composables.ui.theme.backgroundColor
import com.composables.ui.theme.colors
import com.composeunstyled.theme.Theme

/**
 * The root container for a screen: paints the themed background and handles safe-area insets.
 *
 * Composables UI deliberately ships no `Scaffold`, so this is the template's stand-in. It takes
 * the place Material's `Scaffold` used to hold, with the same call shape:
 *
 * ```kotlin
 * AppScreen(toolbar = { AppToolbar(title = "Home") }) {
 *     // content, centred in a Box
 * }
 * ```
 *
 * Stateless by design -- pass state and callbacks in from the screen's `ViewInteractor`.
 *
 * @param toolbar Optional toolbar drawn above the content, typically an [AppToolbar]. When present
 *   it handles the top inset itself, so only the bottom and side insets are applied to [content].
 * @param background Screen background. Defaults to the theme's background colour.
 * @param content Screen body, laid out in a [Box] filling the remaining space.
 */
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    toolbar: (@Composable () -> Unit)? = null,
    background: Color = Theme[colors][backgroundColor],
    content: @Composable BoxScope.() -> Unit,
) {
    // The toolbar already pads for the top and side system bars, so padding the content for them
    // again would double the gap.
    val contentInsets = if (toolbar != null) {
        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    } else {
        WindowInsets.safeDrawing
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background),
    ) {
        toolbar?.invoke()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(contentInsets),
            content = content,
        )
    }
}
