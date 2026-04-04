package com.watermelonkode.simpletemplate.ui.screen.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.watermelonkode.simpletemplate.ui.design.AppTheme
import com.watermelonkode.simpletemplate.ui.design.components.AppBaseButton
import org.koin.core.parameter.parametersOf

@Composable
fun DetailsScreen(
    id: String,
    interactor: DetailsScreenViewInteractor = rememberInject<DetailsScreenViewInteractor> { parametersOf(id) }
) {
    val state = interactor.collectAsState()

    Scaffold(
        containerColor = AppTheme.colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.elementPadding)
            ) {
                Text(
                    text = "Details ${state.id}",
                    style = AppTheme.typography.h2,
                    color = AppTheme.colors.onBackground
                )

                AppBaseButton(
                    label = "Close",
                    onClick = { interactor.homeClicked() }
                )
            }
        }
    }
}
