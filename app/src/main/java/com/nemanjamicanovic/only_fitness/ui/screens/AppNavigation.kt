package com.nemanjamicanovic.only_fitness.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nemanjamicanovic.only_fitness.destinations.DestinationChangePersonalData
import com.nemanjamicanovic.only_fitness.destinations.DestinationChat
import com.nemanjamicanovic.only_fitness.destinations.DestinationChats
import com.nemanjamicanovic.only_fitness.destinations.DestinationEditProfile
import com.nemanjamicanovic.only_fitness.destinations.DestinationHome
import com.nemanjamicanovic.only_fitness.destinations.DestinationNotifications
import com.nemanjamicanovic.only_fitness.destinations.DestinationProfile
import com.nemanjamicanovic.only_fitness.destinations.DestinationTrainerInfo
import com.nemanjamicanovic.only_fitness.destinations.bottomNavigationDestinations
import com.nemanjamicanovic.only_fitness.destinations.destinationsToHideBottomNavigation
import com.nemanjamicanovic.only_fitness.destinations.formalArgs
import com.nemanjamicanovic.only_fitness.util.DataToChange
import com.nemanjamicanovic.only_fitness.viewModels.ChatViewModel
import com.nemanjamicanovic.only_fitness.viewModels.NotificationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.SubscribeViewModel
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@Composable
fun AppNavigation(
    userViewModel: UserViewModel,
    subscribeViewModel: SubscribeViewModel,
    chatViewModel: ChatViewModel,
    notificationViewModel: NotificationViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: DestinationHome.route

    val numberOfNotSeen by notificationViewModel.numberOfNotSeen.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (!destinationsToHideBottomNavigation.contains(currentRoute)) {
                NavigationBar {
                    bottomNavigationDestinations.forEach { navDestination ->
                        NavigationBarItem(
                            icon = {
                                if (navDestination != DestinationNotifications) {
                                    Icon(
                                        imageVector = navDestination.icon,
                                        contentDescription = null,
                                    )
                                }
                                else {
                                    BadgedBox(
                                        badge = {
                                            if (numberOfNotSeen != 0) {
                                                Badge(
                                                    containerColor = Color.Red
                                                ) {
                                                    Text(
                                                        text = numberOfNotSeen.toString(),
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = navDestination.icon,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            label = { Text(text = navDestination.name) },
                            selected = currentRoute == navDestination.route,
                            onClick = {
                                navController.navigate(navDestination.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(DestinationHome.route) {
                                        saveState = true
                                        inclusive = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = DestinationHome.route,
            modifier = Modifier.padding(bottom = it.calculateBottomPadding())
        ) {
            composable(route = DestinationHome.route) {
                Home(
                    userViewModel = userViewModel,
                    goToTrainerInfo = { trainerUsername ->
                        navController.navigate(DestinationTrainerInfo.makeRouteWithActualArgs(trainerUsername)) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(DestinationHome.route) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable(route = DestinationChats.route) {
                Chats(
                    chatViewModel = chatViewModel,
                    goToChat = { user ->
                        chatViewModel.clearMessagesBetweenUsers()
                        navController.navigate(DestinationChat.makeRouteWithActualArgs(user.username)) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(DestinationChats.route) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable(route = DestinationProfile.route) {
                Profile(
                    userViewModel = userViewModel,
                    subscribeViewModel = subscribeViewModel,
                    goToEditProfile = {
                        navController.navigate(DestinationEditProfile.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(DestinationProfile.route) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable(route = DestinationNotifications.route) {
                Notifications(
                    notificationViewModel = notificationViewModel
                )
            }
            composable(
                route = DestinationTrainerInfo.makeRouteWithFormalArgs(formalArgs[DestinationTrainerInfo.name]!!),
                arguments = listOf(navArgument(formalArgs[DestinationTrainerInfo.name]!!) { type = NavType.StringType })
            ) { navBackStackEntry ->
                val trainerUsername = navBackStackEntry.arguments?.getString(formalArgs[DestinationTrainerInfo.name]!!) ?: ""
                TrainerInfo(
                    trainerUsername = trainerUsername,
                    userViewModel = userViewModel,
                    subscribeViewModel = subscribeViewModel,
                    notificationViewModel = notificationViewModel,
                    goToChatWithTrainer = {
                        navController.navigate(DestinationChat.makeRouteWithActualArgs(trainerUsername)) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    goBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable(
                route = DestinationChat.makeRouteWithFormalArgs(formalArgs[DestinationChat.name]!!),
                arguments = listOf(navArgument(formalArgs[DestinationChat.name]!!) { type = NavType.StringType })
            ) { navBackStackEntry ->
                val otherUserUsername = navBackStackEntry.arguments?.getString(formalArgs[DestinationChat.name]!!) ?: ""
                Chat(
                    otherUserUsername = otherUserUsername,
                    userViewModel = userViewModel,
                    chatViewModel = chatViewModel,
                    goBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable(route = DestinationEditProfile.route) {
                EditProfile(
                    userViewModel = userViewModel,
                    goBack = {
                        navController.navigateUp()
                    },
                    goToChangePersonalData = { dataToChange ->
                        navController.navigate(DestinationChangePersonalData.makeRouteWithActualArgs(dataToChange.ordinal.toString())) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(DestinationEditProfile.route) {
                                saveState = true
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable(
                route = DestinationChangePersonalData.makeRouteWithFormalArgs(formalArgs[DestinationChangePersonalData.name]!!),
                arguments = listOf(navArgument(formalArgs[DestinationChangePersonalData.name]!!) { type = NavType.IntType })
            ) { navBackStackEntry ->
                val dataToChange = navBackStackEntry.arguments?.getInt(formalArgs[DestinationChangePersonalData.name]!!) ?: 0
                ChangePersonalData(
                    userViewModel = userViewModel,
                    dataToChange = DataToChange.entries[dataToChange],
                    goBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}
