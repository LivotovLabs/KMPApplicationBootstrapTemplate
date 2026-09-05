package com.watermelonkode.simpletemplate.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.SunMoon
import com.composables.ui.components.Button
import com.composables.ui.components.ButtonStyle
import com.composables.ui.components.Icon
import com.composables.ui.components.IconButton
import com.composables.ui.components.Text
import com.composeunstyled.theme.Theme
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.watermelonkode.simpletemplate.domain.model.settings.ThemeMode
import com.watermelonkode.simpletemplate.ui.design.components.AppScreen
import com.watermelonkode.simpletemplate.ui.design.components.AppToolbar
import com.watermelonkode.simpletemplate.ui.design.elementPadding
import com.watermelonkode.simpletemplate.ui.design.h2
import com.watermelonkode.simpletemplate.ui.design.spacing
import com.watermelonkode.simpletemplate.ui.design.typography
import kmpapplicationtemplate.composeapp.generated.resources.Res
import kmpapplicationtemplate.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    interactor: HomeScreenViewInteractor = rememberInject<HomeScreenViewInteractor>()
) {
    val state = interactor.collectAsState()

    AppScreen(
        toolbar = {
            AppToolbar(
                title = "Home",
                trailing = {
                    ThemeModeButton(
                        themeMode = state.themeMode,
                        onClick = { interactor.onThemeModeClicked() }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme[spacing][elementPadding])
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )

            Text(
                text = state.greeting,
                style = Theme[typography][h2]
            )

            Button(
                onClick = { interactor.onDetailsClicked("123") },
                style = ButtonStyle.Primary
            ) {
                Text("Open Details")
            }
        }
    }
}

/**
 * Cycles the app between following the system scheme, light and dark.
 *
 * Local to this screen: it exists to make the persisted `ThemeMode` setting visible in the
 * template, and is small enough to stay in this file per the component guidelines.
 */
@Composable
private fun ThemeModeButton(themeMode: ThemeMode, onClick: () -> Unit) {
    IconButton(onClick = onClick, style = ButtonStyle.Ghost) {
        Icon(
            imageVector = when (themeMode) {
                ThemeMode.System -> Lucide.SunMoon
                ThemeMode.Light -> Lucide.Sun
                ThemeMode.Dark -> Lucide.Moon
            },
            contentDescription = "Switch theme (currently ${themeMode.name})"
        )
    }
}
