package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Structural sizes that follow each platform's own chrome.
 *
 * These are plain values rather than theme tokens because they describe the *platform*, not the
 * brand -- changing your palette should not change how tall a navigation bar is. Colours, shapes,
 * type and spacing stay in the theme; these do not.
 */
object AppMetrics {

    /**
     * Height of the app's toolbar, excluding the status bar inset above it.
     *
     * Android's Material 3 top app bar is 64dp for the small variant but 56dp remains the
     * long-standing action bar height and reads correctly next to system UI; UIKit navigation bars
     * are 44pt; desktop windows use a compact bar.
     */
    val toolbarHeight: Dp = when (appPlatform) {
        AppPlatform.Android -> 56.dp
        AppPlatform.IOS -> 44.dp
        AppPlatform.Desktop, AppPlatform.Web -> 44.dp
    }

    /**
     * Smallest comfortable tap target.
     *
     * Android's accessibility guidance says 48dp, Apple's HIG says 44pt, and pointer platforms can
     * go much tighter because a mouse is precise.
     */
    val minTouchTarget: Dp = when (appPlatform) {
        AppPlatform.Android -> 48.dp
        AppPlatform.IOS -> 44.dp
        AppPlatform.Desktop, AppPlatform.Web -> 32.dp
    }

    /** Horizontal padding inside the toolbar, before the first control. */
    val toolbarHorizontalPadding: Dp = when (appPlatform) {
        // Material keeps the navigation icon close to the edge; UIKit bar items sit closer still.
        AppPlatform.Android -> 4.dp
        AppPlatform.IOS -> 8.dp
        AppPlatform.Desktop, AppPlatform.Web -> 8.dp
    }

    /** Gap between the leading control and the title, when both are present. */
    val toolbarTitleGap: Dp = when (appPlatform) {
        AppPlatform.Android -> 16.dp
        AppPlatform.IOS, AppPlatform.Desktop, AppPlatform.Web -> 4.dp
    }

    /**
     * iOS centres navigation bar titles; Android and desktop align them to the leading edge.
     */
    val centersToolbarTitle: Boolean = appPlatform == AppPlatform.IOS

    /**
     * Padding applied around screen content on platforms with no system insets to respect.
     *
     * `WindowInsets.safeDrawing` resolves to zero on desktop and web, so without this the content
     * would sit flush against the window edge. Mobile gets nothing extra here because the safe
     * area already provides the breathing room.
     */
    val windowPadding: Dp = if (appPlatform.isPointerFirst) 8.dp else 0.dp
}
