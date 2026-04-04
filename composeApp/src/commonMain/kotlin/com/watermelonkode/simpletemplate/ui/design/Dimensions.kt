package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppDimensions(
    val screenPadding: Dp,
    val elementPadding: Dp,
    val smallPadding: Dp,
    val cornerRadius: Dp
)

val DefaultAppDimensions = AppDimensions(
    screenPadding = 16.dp,
    elementPadding = 12.dp,
    smallPadding = 8.dp,
    cornerRadius = 8.dp
)

internal val LocalAppDimensions = staticCompositionLocalOf { DefaultAppDimensions }
