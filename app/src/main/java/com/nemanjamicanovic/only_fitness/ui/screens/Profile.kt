package com.nemanjamicanovic.only_fitness.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.destinations.DestinationSubscribed
import com.nemanjamicanovic.only_fitness.destinations.DestinationSubscriptions
import com.nemanjamicanovic.only_fitness.util.composables.DrawLine
import com.nemanjamicanovic.only_fitness.viewModels.SubscribeViewModel
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    userViewModel: UserViewModel,
    subscribeViewModel: SubscribeViewModel,
    goToEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val user by userViewModel.user.collectAsStateWithLifecycle()

    var subscribedActive by rememberSaveable { mutableStateOf(true) }

    val subscribed by subscribeViewModel.subscribed.collectAsStateWithLifecycle()
    val subscriptions by subscribeViewModel.subscriptions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = user?.username ?: "")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null
                    )
                }
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxWidth()
        ) {
            if (userViewModel.profilePicture != null) {
                Image(
                    bitmap = userViewModel.profilePicture!!,
                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
            }
            else {
                Image(
                    painter = painterResource(id = R.drawable.profile_picture),
                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Text(
                text = user?.fullName ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally)
            )

            OutlinedButton(
                onClick = {
                    goToEditProfile()
                },
                modifier = Modifier
                    .padding(top = 15.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.profile_edit_profile),
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }

            Row(
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            navController.navigate(DestinationSubscribed.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(DestinationSubscriptions.route) {
                                    saveState = true
                                    inclusive = true
                                }
                            }

                            subscribedActive = true
                        },
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_subscribed)
                        )
                    }

                    DrawAppropriateLine(subscribedActive)
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            navController.navigate(DestinationSubscriptions.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(DestinationSubscribed.route) {
                                    saveState = true
                                    inclusive = true
                                }
                            }

                            subscribedActive = false
                        },
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_subscriptions)
                        )
                    }

                    DrawAppropriateLine(!subscribedActive)
                }
            }

            NavHost(
                navController = navController,
                startDestination = DestinationSubscribed.route,
            ) {
                composable(route = DestinationSubscribed.route) {
                    Subscribe(DestinationSubscribed.route, subscribeViewModel, subscribed)
                }
                composable(route = DestinationSubscriptions.route) {
                    Subscribe(DestinationSubscriptions.route, subscribeViewModel, subscriptions)
                }
            }
        }
    }
}


@Composable
private fun DrawAppropriateLine(selected: Boolean) {
    if (selected) {
        DrawLine(
            color = Color.Black,
            yOffset = -7f,
            width = 8f
        )
    }
    else {
        DrawLine(
            color = Color.LightGray,
            yOffset = -5f,
            width = 4f
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Subscribe(route: String, subscribeViewModel: SubscribeViewModel, users: List<User>) {
    val profileRefreshUiState by subscribeViewModel.profileRefreshUiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = profileRefreshUiState.isRefreshing,
        onRefresh = {
            if (route == DestinationSubscribed.route) subscribeViewModel.getSubscribedForUser()
            else subscribeViewModel.getSubscriptionsForUser()
        }
    ) {
        LazyColumn {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            if (user.profilePicture.isNotEmpty()) {
                                val bytes = Base64.decode(user.profilePicture, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_picture),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Text(text = user.username)
                        }
                    }
                }
            }
        }
    }
}
