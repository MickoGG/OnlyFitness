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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
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
                Column {
//                    SearchBar(
//                        inputField = {
//                            SearchBarDefaults.InputField(
//                                query = searchText,
//                                onQueryChange = {
//                                    searchText = it
//                                },
//                                onSearch = {
//                                    isActive = false
//                                },
//                                expanded = false,
//                                onExpandedChange = {
//
//                                },
//                                placeholder = {
//                                    Text(text = stringResource(id = R.string.home_search_placeholder))
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Default.Search,
//                                        contentDescription = null
//                                    )
//                                }
//                            )
//                        },
//                        expanded = false,
//                        onExpandedChange = {
//
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
//                    ) {
//                        if (searchText.isNotEmpty()) {
//                            val searchTextLowercase = searchText.lowercase()
//                            val filteredTrainers = trainers.value.filter { trainer ->
//                                trainer.username.contains(searchTextLowercase) ||
//                                        trainer.fullName.lowercase().contains(searchTextLowercase)
//                            }
//                            ShowTrainers(filteredTrainers, goToTrainerInfo)
//                        }
//                    }

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
                            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
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
                            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                    )
                }
            }

            //if (!isActive) {
                ShowTrainers(trainers.value, goToTrainerInfo)
            //}
        }
    }
}


@Composable
fun ShowTrainers(trainers: List<User>, goToTrainerInfo: (id: String) -> Unit) {
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
                        }
                        else {
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

                    Button(
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
