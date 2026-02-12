package com.watermelonkode.simpletemplate

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.watermelonkode.simpletemplate.ui.App
import kotlinx.browser.document
import org.jetbrains.compose.resources.configureWebResources

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin(platformContext = PlatformContext()).koin

    configureWebResources {
        resourcePathMapping { path -> "/$path" }
    }

    ComposeViewport(document.body!!) {
        App(deepLink = document.location?.href)
    }
}
