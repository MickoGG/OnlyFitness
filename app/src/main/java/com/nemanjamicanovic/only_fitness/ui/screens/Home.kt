package com.nemanjamicanovic.only_fitness.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.util.Available
import com.nemanjamicanovic.only_fitness.util.WorksWith
import com.nemanjamicanovic.only_fitness.util.composables.Spinner
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    userViewModel: UserViewModel,
    goToTrainerInfo: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val trainers = userViewModel.trainers.collectAsStateWithLifecycle()

    var showFilters by rememberSaveable { mutableStateOf(false) }

    var searchNameUsernameText: String by rememberSaveable { mutableStateOf("") }
    var searchLocationText: String by rememberSaveable { mutableStateOf("") }

    val worksWithValues = listOf(stringResource(id = R.string.home_disable_text))
        .plus(stringArrayResource(id = R.array.worksWithValues))
    var worksWithText: String by rememberSaveable { mutableStateOf(worksWithValues[0]) }

    val availableValues = listOf(stringResource(id = R.string.home_disable_text))
        .plus(stringArrayResource(id = R.array.availableValues))
    var availableText: String by rememberSaveable { mutableStateOf(availableValues[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.home_available_trainers),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = null)
                    }
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
            if (showFilters) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(color = Color(212, 212, 212))
                        .border(width = 2.dp, color = Color.Black)
                ) {
                    TextField(
                        value = searchNameUsernameText,
                        onValueChange = { searchNameUsernameText = it },
                        placeholder = {
                            Text(text = stringResource(id = R.string.home_search_name_username_placeholder))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors().copy(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                    )

                    TextField(
                        value = searchLocationText,
                        onValueChange = { searchLocationText = it },
                        placeholder = {
                            Text(text = stringResource(id = R.string.home_search_location_placeholder))
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        shape = CircleShape,
                        colors = TextFieldDefaults.colors().copy(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp, start = 20.dp, end = 20.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_works_with_text),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spinner(
                            value = if (worksWithText == worksWithValues[0]) worksWithText
                            else worksWithValues[enumValueOf<WorksWith>(worksWithText).ordinal + 1],
                            onSelect = { option ->
                                worksWithText = if (option == worksWithValues[0]) option
                                else WorksWith.entries[worksWithValues.indexOfFirst { it == option } - 1].name
                            },
                            options = worksWithValues
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_available_text),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spinner(
                            value = if (availableText == availableValues[0]) availableText
                            else availableValues[enumValueOf<Available>(availableText).ordinal + 1],
                            onSelect = { option ->
                                availableText = if (option == availableValues[0]) option
                                else Available.entries[availableValues.indexOfFirst { it == option } - 1].name
                            },
                            options = availableValues
                        )
                    }

                    TextButton(
                        onClick = {
                            searchNameUsernameText = ""
                            searchLocationText = ""
                            worksWithText = worksWithValues[0]
                            availableText = availableValues[0]
                        },
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_reset_filter_text),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            ShowTrainers(
                trainers.value.filter { trainer ->
                    (searchNameUsernameText.isEmpty() ||
                        trainer.username.contains(searchNameUsernameText, ignoreCase = true) ||
                        trainer.fullName.contains(searchNameUsernameText, ignoreCase = true)) &&
                    (searchLocationText.isEmpty() || trainer.location.contains(searchLocationText, ignoreCase = true)) &&
                    (worksWithText == worksWithValues[0] || trainer.worksWith == worksWithText) &&
                    (availableText == availableValues[0] || trainer.available == availableText)
                },
                userViewModel,
                goToTrainerInfo
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTrainers(trainers: List<User>, userViewModel: UserViewModel, goToTrainerInfo: (id: String) -> Unit) {
    val homeRefreshUiState by userViewModel.homeRefreshUiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = homeRefreshUiState.isRefreshing,
        onRefresh = {
            userViewModel.getTrainers()
        }
    ) {
        LazyColumn {
            items(trainers) { trainer ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .align(Alignment.CenterVertically)
                        ) {
                            if (trainer.profilePicture.isNotEmpty()) {
                                val bytes = Base64.decode(trainer.profilePicture, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_picture),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(3f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = trainer.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Text(text = trainer.username)
                        }

                        OutlinedButton(
                            onClick = {
                                goToTrainerInfo(trainer.username)
                            },
                            modifier = Modifier
                                .weight(2f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = "See more",
                            )
                        }
                    }
                }
            }
        }
    }
}
