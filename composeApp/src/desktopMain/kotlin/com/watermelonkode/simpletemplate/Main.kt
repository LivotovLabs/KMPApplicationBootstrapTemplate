package com.watermelonkode.simpletemplate

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    initKoin(platformContext = PlatformContext()).koin

    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP Template",
    ) {
        App()
    }
}
