package com.watermelonkode.simpletemplate.ui.screen.details

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composables.ui.components.Button
import com.composables.ui.components.ButtonStyle
import com.composables.ui.components.Text
import com.composeunstyled.theme.Theme
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.watermelonkode.simpletemplate.ui.design.components.AppScreen
import com.watermelonkode.simpletemplate.ui.design.components.AppToolbar
import com.watermelonkode.simpletemplate.ui.design.elementPadding
import com.watermelonkode.simpletemplate.ui.design.h2
import com.watermelonkode.simpletemplate.ui.design.spacing
import com.watermelonkode.simpletemplate.ui.design.typography
import org.koin.core.parameter.parametersOf

@Composable
fun DetailsScreen(
    id: String,
    interactor: DetailsScreenViewInteractor = rememberInject<DetailsScreenViewInteractor> { parametersOf(id) }
) {
    val state = interactor.collectAsState()

    AppScreen(
        toolbar = {
            AppToolbar(
                title = "Details",
                showBackButton = true,
                onBackClicked = { interactor.onBackClicked() }
            )
        }
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme[spacing][elementPadding])
        ) {
            Text(
                text = "Details ${state.id}",
                style = Theme[typography][h2]
            )

            Button(
                onClick = { interactor.homeClicked() },
                style = ButtonStyle.Secondary
            ) {
                Text("Close")
            }
        }
    }
}
