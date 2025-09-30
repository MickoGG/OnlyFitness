package com.nemanjamicanovic.only_fitness.destinations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications


object DestinationHome : DestinationExt {
    override val icon = Icons.Default.Home
    override val route = "destination_home"
    override val name = "Home"
}

object DestinationChats : DestinationExt {
    override val icon = Icons.AutoMirrored.Default.Chat
    override val route = "destination_chats"
    override val name = "Chats"
}

object DestinationProfile : DestinationExt {
    override val icon = Icons.Default.AccountCircle
    override val route = "destination_profile"
    override val name = "Profile"
}

object DestinationNotifications : DestinationExt {
    override val icon = Icons.Default.Notifications
    override val route = "destination_notifications"
    override val name = "Notifications"
}

object DestinationTrainerInfo : Destination, Arguments {
    override val route = "destination_trainer_info"
    override val name = "TrainerInfo"

    override fun makeRouteWithFormalArgs(vararg args: String): String = makeRouteWithArgs(
        route, false, *args)
    override fun makeRouteWithActualArgs(vararg args: String): String = makeRouteWithArgs(
        route, true, *args)
}

object DestinationChat : Destination, Arguments {
    override val route = "destination_chat"
    override val name = "Chat"

    override fun makeRouteWithFormalArgs(vararg args: String): String = makeRouteWithArgs(
        route, false, *args)
    override fun makeRouteWithActualArgs(vararg args: String): String = makeRouteWithArgs(
        route, true, *args)
}

object DestinationEditProfile : Destination {
    override val route = "destination_edit_profile"
    override val name = "EditProfile"
}

object DestinationChangePersonalData : Destination, Arguments {
    override val route = "destination_change_personal_data"
    override val name = "ChangePersonalData"

    override fun makeRouteWithFormalArgs(vararg args: String): String = makeRouteWithArgs(
        route, false, *args)
    override fun makeRouteWithActualArgs(vararg args: String): String = makeRouteWithArgs(
        route, true, *args)
}


val formalArgs = mapOf(
    DestinationTrainerInfo.name to "trainerUsername",
    DestinationChat.name to "username",
    DestinationChangePersonalData.name to "dataToChange"
)

val bottomNavigationDestinations = listOf(
    DestinationHome,
    DestinationChats,
    DestinationProfile,
    DestinationNotifications
)

val destinationsToHideBottomNavigation = listOf(
    DestinationTrainerInfo.makeRouteWithFormalArgs(formalArgs[DestinationTrainerInfo.name]!!),
    DestinationChat.makeRouteWithFormalArgs(formalArgs[DestinationChat.name]!!),
    DestinationEditProfile.route,
    DestinationChangePersonalData.makeRouteWithFormalArgs(formalArgs[DestinationChangePersonalData.name]!!)
)
