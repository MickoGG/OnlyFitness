package com.nemanjamicanovic.only_fitness.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nemanjamicanovic.only_fitness.destinations.DestinationLogin
import com.nemanjamicanovic.only_fitness.destinations.DestinationMain
import com.nemanjamicanovic.only_fitness.destinations.DestinationRegister
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel
import com.nemanjamicanovic.only_fitness.viewModels.AuthenticationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.ChatViewModel
import com.nemanjamicanovic.only_fitness.viewModels.NotificationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.SubscribeViewModel


@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val authenticationViewModel: AuthenticationViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val subscribeViewModel: SubscribeViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = DestinationLogin.route,
    ) {
        composable(route = DestinationLogin.route) {
            Login(
                authenticationViewModel = authenticationViewModel,
                userViewModel = userViewModel,
                subscribeViewModel = subscribeViewModel,
                chatViewModel = chatViewModel,
                notificationViewModel = notificationViewModel,
                goToRegister = {
                    navController.navigate(DestinationRegister.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(DestinationLogin.route) {
                            saveState = true
                            inclusive = false
                        }
                    }
                },
                onLogin = {
                    navController.navigate(DestinationMain.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(DestinationLogin.route) {
                            saveState = true
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = DestinationRegister.route) {
            Register(
                authenticationViewModel = authenticationViewModel,
                userViewModel = userViewModel,
                goBack = {
                    navController.navigateUp()
                },
                onRegister = {
                    navController.navigate(DestinationMain.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(DestinationLogin.route) {
                            saveState = true
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = DestinationMain.route) {
            AppNavigation(
                userViewModel = userViewModel,
                subscribeViewModel = subscribeViewModel,
                chatViewModel = chatViewModel,
                notificationViewModel = notificationViewModel
            )
        }
    }
}
