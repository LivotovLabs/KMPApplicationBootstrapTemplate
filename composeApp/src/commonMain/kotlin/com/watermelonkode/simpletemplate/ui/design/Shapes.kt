package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The application's corner shapes, one per Composables UI shape token.
 *
 * Corner style is one of the strongest platform signals there is: Material 3 buttons are fully
 * rounded pills with very round dialogs, iOS uses restrained rounded rectangles, and desktop UIs
 * are tighter again. [appShapes] picks the right set.
 *
 * Edit these to match your brand -- but if you want one look everywhere, point [appShapes] at a
 * single set instead of removing the others.
 */
data class AppShapes(
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val button: Shape,
    val dialog: Shape,
    val sheet: Shape,
    val menu: Shape,
    val field: Shape,
)

/** Material 3: pill buttons, generously rounded containers. */
val AppAndroidShapes = AppShapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    button = RoundedCornerShape(percent = 50),
    dialog = RoundedCornerShape(28.dp),
    sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    menu = RoundedCornerShape(12.dp),
    field = RoundedCornerShape(12.dp),
)

/** iOS: rounded rectangles, never pills, sheets barely rounded. */
val AppIOSShapes = AppShapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(14.dp),
    button = RoundedCornerShape(12.dp),
    dialog = RoundedCornerShape(14.dp),
    sheet = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
    menu = RoundedCornerShape(14.dp),
    field = RoundedCornerShape(10.dp),
)

/** Desktop and web: tight radii, closer to native window chrome. */
val AppDesktopShapes = AppShapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(8.dp),
    button = RoundedCornerShape(6.dp),
    dialog = RoundedCornerShape(10.dp),
    sheet = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    menu = RoundedCornerShape(6.dp),
    field = RoundedCornerShape(6.dp),
)

val appShapes: AppShapes = when (appPlatform) {
    AppPlatform.Android -> AppAndroidShapes
    AppPlatform.IOS -> AppIOSShapes
    AppPlatform.Desktop, AppPlatform.Web -> AppDesktopShapes
}
