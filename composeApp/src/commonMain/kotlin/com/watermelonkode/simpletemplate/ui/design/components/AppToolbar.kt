package com.watermelonkode.simpletemplate.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.ui.components.Icon
import com.composables.ui.components.Text
import com.composables.ui.theme.backgroundColor
import com.composables.ui.theme.colors
import com.composables.ui.theme.indications
import com.composables.ui.theme.defaultIndication
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.Theme
import com.watermelonkode.simpletemplate.ui.design.AppMetrics
import com.watermelonkode.simpletemplate.ui.design.AppPlatform
import com.watermelonkode.simpletemplate.ui.design.appPlatform
import com.watermelonkode.simpletemplate.ui.design.appPointerCursor
import com.watermelonkode.simpletemplate.ui.design.toolbarTitle
import com.watermelonkode.simpletemplate.ui.design.typography

/**
 * The app's top bar: a title, an optional back affordance and an optional trailing slot.
 *
 * Built from primitives rather than wrapping Composables UI's `Toolbar`, because that component
 * hardcodes a 64dp height and a 20sp title with no way to change either, and navigation bars are
 * the most platform-specific chrome an app has. What differs, all driven by [AppMetrics]:
 *
 * ```text
 * iOS  44pt, centred      Android  56dp, leading     Desktop/Web  44dp, leading
 * ┌──────────────────┐    ┌──────────────────┐       ┌──────────────────┐
 * │ ‹      Title     │    │ ←  Title         │       │ ←  Title         │
 * └──────────────────┘    └──────────────────┘       └──────────────────┘
 *   17sp semibold           22sp regular              15sp medium
 *   chevron back            arrow back                arrow back
 * ```
 *
 * Stateless -- pass state and callbacks in from the screen's `ViewInteractor`.
 *
 * @param showBackButton Whether to draw a back affordance ahead of the title.
 * @param onBackClicked Invoked when the back affordance is activated. Forward this to the screen's
 *   `ViewInteractor`, which asks the coordinator to pop.
 * @param trailing Optional actions drawn at the end of the bar.
 */
@Composable
fun AppToolbar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClicked: () -> Unit = {},
    background: Color = Theme[colors][backgroundColor],
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            // The bar owns the top inset so it can sit under the status bar and still centre its
            // content in the visible area below it.
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
            )
            .height(AppMetrics.toolbarHeight)
            .padding(horizontal = AppMetrics.toolbarHorizontalPadding),
    ) {
        if (showBackButton) {
            AppToolbarBackButton(
                onClick = onBackClicked,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }

        Text(
            text = title,
            style = Theme[typography][toolbarTitle],
            singleLine = true,
            overflow = TextOverflow.Ellipsis,
            modifier = if (AppMetrics.centersToolbarTitle) {
                Modifier.align(Alignment.Center)
            } else {
                Modifier
                    .align(Alignment.CenterStart)
                    // Clear the back affordance when there is one, so the title never sits on it.
                    .padding(
                        start = if (showBackButton) {
                            AppMetrics.minTouchTarget + AppMetrics.toolbarTitleGap
                        } else {
                            AppMetrics.toolbarTitleGap
                        },
                    )
            },
        )

        if (trailing != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(AppMetrics.toolbarTitleGap),
                verticalAlignment = Alignment.CenterVertically,
                content = trailing,
            )
        }
    }
}

/**
 * Back affordance sized to the platform's minimum tap target.
 *
 * Uses `UnstyledButton` rather than the library's `IconButton` so the target is exactly
 * [AppMetrics.minTouchTarget] -- `IconButton` is fixed at 48dp/36dp, which overflows a 44pt iOS
 * navigation bar. The press effect still comes from the theme, so it stays native.
 */
@Composable
private fun AppToolbarBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    UnstyledButton(
        onClick = onClick,
        modifier = modifier
            .appPointerCursor()
            .size(AppMetrics.minTouchTarget)
            // Clipped so the ripple or highlight stays inside a circle, as platform icon buttons do.
            .clip(CircleShape),
        indication = Theme[indications][defaultIndication],
    ) {
        Icon(
            // Apple uses a chevron for "back"; Material uses a full arrow.
            imageVector = if (appPlatform == AppPlatform.IOS) Lucide.ChevronLeft else Lucide.ArrowLeft,
            contentDescription = "Back",
        )
    }
}
