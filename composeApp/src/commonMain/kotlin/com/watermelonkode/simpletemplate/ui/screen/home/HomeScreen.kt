package com.watermelonkode.simpletemplate.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.watermelonkode.simpletemplate.ui.design.AppTheme
import com.watermelonkode.simpletemplate.ui.design.components.AppBaseButton
import kmpapplicationtemplate.composeapp.generated.resources.Res
import kmpapplicationtemplate.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    interactor: HomeScreenViewInteractor = rememberInject<HomeScreenViewInteractor>()
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
                Image(
                    painter = painterResource(Res.drawable.compose_multiplatform),
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.TriangleAlert,
                        contentDescription = null,
                        tint = AppTheme.colors.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.greeting,
                        style = AppTheme.typography.h2,
                        color = AppTheme.colors.onBackground
                    )
                }

                AppBaseButton(
                    label = "Open Details",
                    onClick = { interactor.onDetailsClicked("123") }
                )
            }
        }
    }
}
