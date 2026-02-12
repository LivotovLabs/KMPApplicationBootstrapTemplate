package com.watermelonkode.simpletemplate.ui

import androidx.compose.animation.ExperimentalAnimationApi
import com.outsidesource.oskitcompose.router.PushFromRightRouteTransition
import com.outsidesource.oskitcompose.router.PushFromTopRouteTransition
import com.outsidesource.oskitkmp.coordinator.Coordinator
import com.outsidesource.oskitkmp.lib.Platform
import com.outsidesource.oskitkmp.lib.current

@OptIn(ExperimentalAnimationApi::class)
class AppCoordinator(
) : Coordinator(
    initialRoute = Route.Home,
    defaultTransition = if (Platform.current.isMobile) PushFromRightRouteTransition else PushFromTopRouteTransition
) {
    fun handleDeepLink(deepLink: String?) {
        val route = Route.deepLinks.matchRoute(deepLink) ?: return
        push(route, ignoreTransitionLock = true)
    }

    fun canPop() = hasBackStack()
    fun pop() = pop(ignoreTransitionLock = false)

    fun returnToHomeClicked() {
        pop { toRoute(Route.Home) }
    }

    fun detailsClicked(id: String) {
        push(Route.Details(id))
    }

}