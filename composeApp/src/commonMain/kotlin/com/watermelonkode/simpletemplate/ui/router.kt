package com.watermelonkode.simpletemplate.ui

import com.outsidesource.oskitkmp.router.IRoute
import com.outsidesource.oskitkmp.router.IWebRoute
import com.outsidesource.oskitkmp.router.Router


sealed class Route(
    override val webRoutePath: String? = null,
    override val webRouteTitle: String? = null,
) : IRoute, IWebRoute {

    data object Home : Route(webRoutePath = "/", webRouteTitle = "Home")
    data class Details(val id: String) : Route(webRoutePath = "/details/$id", webRouteTitle = "Details")

    companion object {
        val deepLinks = Router.buildDeepLinks {
            "/park/:id" routesTo { args, _ -> Details(args[0]) }
        }
    }
}
