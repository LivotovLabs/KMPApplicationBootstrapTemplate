package com.watermelonkode.simpletemplate.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.outsidesource.oskitcompose.router.RouteSwitch
import com.watermelonkode.simpletemplate.ui.screen.details.DetailsScreen
import com.watermelonkode.simpletemplate.ui.screen.home.HomeScreen
import org.koin.core.parameter.parametersOf


@Composable
@Preview
fun App(
    deepLink: String? = null,
    interactor: AppInteractor = rememberInject<AppInteractor> { parametersOf(deepLink) },
    coordinator: AppCoordinator = rememberInject<AppCoordinator>()
) {
    val state = interactor.collectAsState()

    RouteSwitch(coordinator) {
        when (it) {
            Route.Home -> Authorized(state) { HomeScreen() }
            is Route.Details -> Authorized(state) { DetailsScreen(it.id) }
        }
    }
}

@Composable
private fun Authorized(state: AppInteractorState, content: @Composable () -> Unit) {
    when {
        // if !authorized return login or login placeholder screen here
        else -> content() // else return the actual content
    }
}