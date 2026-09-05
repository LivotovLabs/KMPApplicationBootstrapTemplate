package com.watermelonkode.simpletemplate.ui.design.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.ui.components.VerticalScrollbar
import com.composables.ui.components.rememberVerticalScrollbarState
import com.watermelonkode.simpletemplate.ui.design.appPlatform

/**
 * A vertical scrollbar, on the platforms that expect one.
 *
 * Desktop and web users look for a scrollbar to tell them how long a list is; Android and iOS show
 * their own transient indicator while scrolling and a persistent bar there looks wrong. So this
 * renders nothing on mobile, which means call sites do not need their own platform checks:
 *
 * ```kotlin
 * Box {
 *     Column(Modifier.verticalScroll(scrollState)) { ... }
 *     AppVerticalScrollbar(scrollState, Modifier.align(Alignment.CenterEnd))
 * }
 * ```
 */
@Composable
fun AppVerticalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    if (!appPlatform.isPointerFirst) return
    VerticalScrollbar(state = rememberVerticalScrollbarState(scrollState), modifier = modifier)
}

/** [AppVerticalScrollbar] for a lazy list. */
@Composable
fun AppVerticalScrollbar(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    if (!appPlatform.isPointerFirst) return
    VerticalScrollbar(state = rememberVerticalScrollbarState(lazyListState), modifier = modifier)
}
