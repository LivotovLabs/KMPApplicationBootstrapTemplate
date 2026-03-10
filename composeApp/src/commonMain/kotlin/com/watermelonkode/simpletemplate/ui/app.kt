package com.watermelonkode.simpletemplate.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.outsidesource.oskitcompose.interactor.collectAsState
import com.outsidesource.oskitcompose.lib.rememberInject
import com.outsidesource.oskitcompose.router.RouteSwitch
import com.watermelonkode.simpletemplate.ui.screen.details.DetailsScreen
import com.watermelonkode.simpletemplate.ui.screen.home.HomeScreen
import org.koin.core.parameter.parametersOf


/**
 * The main entry point for the Compose Multiplatform application.
 *
 * This composable initializes the application interactor and coordinator,
 * and manages the top-level navigation using [RouteSwitch].
 *
 * @param deepLink An optional deep link URL to navigate to a specific screen on startup.
 * @param interactor The application-level interactor providing global state.
 * @param coordinator The application coordinator managing navigation routes.
 */
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

/**
 * A helper wrapper to enforce authorization logic before displaying protected content.
 *
 * You can implement global authorization checks here (e.g., checking if a user is logged in).
 * If the user is not authorized, this composable should return a login screen or a placeholder.
 *
 * @param state The current application state.
 * @param content The composable content to display if authorization passes.
 */
@Composable
private fun Authorized(state: AppInteractorState, content: @Composable () -> Unit) {
    when {
        // if !authorized - return login or some placeholder UI here instead of actual content
        else -> content() // else return the actual content
    }
}