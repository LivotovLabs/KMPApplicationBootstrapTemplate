package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The application's corner shapes, one per Composables UI shape token.
 *
 * Two variants exist because Composables UI components resize themselves for the current input
 * method: rounder and larger under a finger, tighter and sharper under a mouse pointer. [AppTheme]
 * picks between them from `LocalInteractionMode`.
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

val AppTouchShapes = AppShapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    button = RoundedCornerShape(100.dp),
    dialog = RoundedCornerShape(16.dp),
    sheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    menu = RoundedCornerShape(16.dp),
    field = RoundedCornerShape(16.dp),
)

val AppPointerShapes = AppTouchShapes.copy(
    button = RoundedCornerShape(10.dp),
    field = RoundedCornerShape(12.dp),
)
