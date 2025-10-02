package com.nemanjamicanovic.only_fitness.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.ui.theme.topAppBarContainerColor
import com.nemanjamicanovic.only_fitness.util.Available
import com.nemanjamicanovic.only_fitness.util.WorksWith
import com.nemanjamicanovic.only_fitness.util.composables.DrawLine
import com.nemanjamicanovic.only_fitness.viewModels.NotificationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.SubscribeViewModel
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerInfo(
    trainerUsername: String,
    userViewModel: UserViewModel,
    subscribeViewModel: SubscribeViewModel,
    notificationViewModel: NotificationViewModel,
    goToChatWithTrainer: () -> Unit,
    goBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var trainer: User? by remember { mutableStateOf(null) }

    userViewModel.getUserByUsername(trainerUsername).addOnSuccessListener {
        trainer = it.toObjects<User>()[0]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.trainer_info_top_bar,
                            trainerUsername
                        )
                    )
                },
                navigationIcon = {
                    IconButton (
                        onClick = { goBack() }
                    ) {
                        Icon (
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = topAppBarContainerColor)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    goToChatWithTrainer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.trainer_info_send_message),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Button(
                onClick = {
                    userViewModel.getUserByUsername(trainerUsername).addOnSuccessListener { result ->
                        subscribeViewModel.subscribeIfNotAlreadySubscribed(result.documents[0].reference) {
                            notificationViewModel.createNotificationForSubscribe(result.documents[0].id, Firebase.auth.currentUser!!.uid)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.trainer_info_subscribe),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            DrawLine(
                color = Color.LightGray,
                yOffset = 0f,
                width = 6f,
                modifier = Modifier.fillMaxWidth().padding(15.dp)
            )

            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                        ) {
                            Text(
                                text = trainer?.fullName ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.trainer_info_subscribers_count,
                                    trainer?.subscribers ?: 0
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                text = when (trainer?.worksWith) {
                                    WorksWith.MALE.name ->
                                        stringResource(id = R.string.trainer_info_genders_male)
                                    WorksWith.FEMALE.name ->
                                        stringResource(id = R.string.trainer_info_genders_female)
                                    WorksWith.MALE_FEMALE.name ->
                                        stringResource(id = R.string.trainer_info_genders_male_female)
                                    else -> ""
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(
                                text = when (trainer?.available) {
                                    Available.ONLINE.name ->
                                        stringResource(id = R.string.trainer_info_availability_online)
                                    Available.ONSITE.name ->
                                        stringResource(id = R.string.trainer_info_availability_onsite)
                                    Available.ONLINE_ONSITE.name ->
                                        stringResource(id = R.string.trainer_info_availability_online_onsite)
                                    else -> ""
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.trainer_info_description),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.trainer_info_about_me),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                            )
                            Text(
                                text = trainer?.aboutMe ?: "",
                                modifier = Modifier.padding(top = 5.dp, start = 10.dp)
                            )

//                            Text(
//                                text = stringResource(id = R.string.trainer_info_certifications_licenses),
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(top = 15.dp, start = 10.dp)
//                            )
//                            Text(
//                                text = "Test",
//                                modifier = Modifier.padding(top = 5.dp, start = 10.dp, bottom = 10.dp)
//                            )
                        }
                    }
                }
            }
        }
    }
}
