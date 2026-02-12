package com.watermelonkode.simpletemplate.ui.screen.home

import androidx.compose.foundation.Image
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
import kmpapplicationtemplate.composeapp.generated.resources.Res
import kmpapplicationtemplate.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    interactor: HomeScreenViewInteractor = rememberInject<HomeScreenViewInteractor>()
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
                Image(
                    painter = painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )

                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = { interactor.onDetailsClicked("123") }
                ) {
                    Text("Open Details")
                }
            }
        }
    }
}
