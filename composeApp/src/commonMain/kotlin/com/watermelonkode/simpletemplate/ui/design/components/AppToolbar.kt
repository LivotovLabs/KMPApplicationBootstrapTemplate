package com.watermelonkode.simpletemplate.ui.design.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.ui.components.ButtonStyle
import com.composables.ui.components.Icon
import com.composables.ui.components.IconButton
import com.composables.ui.components.Text
import com.composables.ui.components.Toolbar
import com.composables.ui.components.ToolbarSize
import com.composables.ui.theme.backgroundColor
import com.composables.ui.theme.colors
import com.composeunstyled.theme.Theme

/**
 * The app's top bar: a title, an optional back affordance and an optional trailing slot.
 *
 * A thin, stateless wrapper over Composables UI's `Toolbar` that fixes the title style and the
 * back button so every screen's header looks the same. Reach for `Toolbar`/`CenteredToolbar`
 * directly when a screen needs something this does not cover.
 *
 * @param showBackButton Whether to draw a back arrow ahead of the title.
 * @param onBackClicked Invoked when the back arrow is activated. Forward this to the screen's
 *   `ViewInteractor`, which asks the coordinator to pop.
 * @param size [ToolbarSize.Medium] for a single-line header, [ToolbarSize.Large] for a large title.
 * @param trailing Optional actions drawn at the end of the bar.
 */
@Composable
fun AppToolbar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClicked: () -> Unit = {},
    size: ToolbarSize = ToolbarSize.Medium,
    background: Color = Theme[colors][backgroundColor],
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Toolbar(
        // Composables UI's toolbar container does not stretch itself, so inside AppScreen's Column
        // it would wrap its content -- collapsing the leading and trailing slots on top of each
        // other. Applied before `modifier` so a caller can still override the width.
        modifier = Modifier.fillMaxWidth().then(modifier),
        backgroundColor = background,
        size = size,
        title = {
            Text(
                text = title,
                singleLine = true,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leading = if (showBackButton) {
            {
                IconButton(
                    onClick = onBackClicked,
                    style = ButtonStyle.Ghost,
                ) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                    )
                }
            }
        } else {
            null
        },
        trailing = trailing,
    )
}
