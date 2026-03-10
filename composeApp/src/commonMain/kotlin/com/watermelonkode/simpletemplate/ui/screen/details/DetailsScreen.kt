package com.watermelonkode.simpletemplate.ui.screen.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import org.koin.core.parameter.parametersOf

@Composable
fun DetailsScreen(
    id: String,
    interactor: DetailsScreenViewInteractor = rememberInject<DetailsScreenViewInteractor> { parametersOf(id) }
) {
    val state = interactor.collectAsState()

    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Details ${state.id}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = { interactor.homeClicked() }
                ) {
                    Text("Close")
                }
            }
        }
    }
}
