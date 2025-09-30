package com.nemanjamicanovic.only_fitness.destinations

import androidx.compose.ui.graphics.vector.ImageVector


interface Destination {
    val route: String
    val name: String
}

interface DestinationExt : Destination {
    val icon: ImageVector
}

interface Arguments {
    fun makeRouteWithFormalArgs(vararg args: String): String
    fun makeRouteWithActualArgs(vararg args: String): String

    fun makeRouteWithArgs(route: String, actualArgs: Boolean, vararg args: String) : String {
        var finalRoute = route
        if (actualArgs) {
            args.forEach {
                finalRoute += "/$it"
            }
        }
        else {
            args.forEach {
                finalRoute += "/{$it}"
            }
        }
        return finalRoute
    }
}
