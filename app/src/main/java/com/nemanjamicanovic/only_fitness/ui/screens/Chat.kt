package com.nemanjamicanovic.only_fitness.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.Message
import com.nemanjamicanovic.only_fitness.ui.theme.topAppBarContainerColor
import com.nemanjamicanovic.only_fitness.viewModels.ChatViewModel
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    otherUserUsername: String,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel,
    goBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by chatViewModel.chatWithCurrentUser.collectAsStateWithLifecycle()
    val currentLastSeen by chatViewModel.currentLastSeen.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()

    var otherUserId by rememberSaveable { mutableStateOf("") }
    var otherUserProfilePicture by rememberSaveable { mutableStateOf("") }
    var otherUserBitmap: Bitmap? = null

    if (otherUserProfilePicture.isNotEmpty()) {
        val bytes = Base64.decode(otherUserProfilePicture, Base64.DEFAULT)
        otherUserBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    var currUserId by rememberSaveable { mutableStateOf("") }
    var currUserUsername by rememberSaveable { mutableStateOf("") }

    var text by rememberSaveable { mutableStateOf("") }
    var showDateForMessage: Message? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(otherUserUsername, messages.size) {
        if (currUserId.isEmpty()) {
            val currUser = userViewModel.getCurrentUser().await()
            currUserId = currUser.id
            currUserUsername = currUser["username"] as String

            val otherUser = userViewModel.getUserByUsername(otherUserUsername).await()
            otherUserId = otherUser.documents[0].id
            otherUserProfilePicture = otherUser.documents[0].get("profilePicture") as String

            chatViewModel.getLiveUpdatesForLastSeenMessage(currUserUsername, otherUserUsername, otherUserId)
            chatViewModel.getMessagesBetweenUsers(currUserUsername, otherUserUsername)
        }
        else {
            if (messages.isNotEmpty()) {
                lazyListState.scrollToItem(messages.lastIndex)

                messages.lastOrNull { it.fromUser!!.username == otherUserUsername }?.let {
                    chatViewModel.updateLastSeen(
                        it,
                        currUserUsername,
                        otherUserUsername,
                        currUserId,
                        otherUserId
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (otherUserProfilePicture.isNotEmpty()) {
                            Image(
                                bitmap = otherUserBitmap!!.asImageBitmap(),
                                contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                            )
                        }
                        else {
                            Image(
                                painter = painterResource(id = R.drawable.profile_picture),
                                contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Text(
                            text = otherUserUsername,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
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
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { message ->
                    Row(
                        horizontalArrangement = if ((message.fromUser?.username
                                ?: "") == otherUserUsername
                        )
                            Arrangement.Start
                        else
                            Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if ((message.fromUser?.username ?: "") == otherUserUsername) {
                            if (otherUserProfilePicture.isNotEmpty()) {
                                Image(
                                    bitmap = otherUserBitmap!!.asImageBitmap(),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .size(35.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_picture),
                                    contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .size(35.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }

                        Box {
                            Card(
                                colors = CardDefaults.cardColors().copy(containerColor = Color(150, 170, 240)),
                                modifier = Modifier
                                    .padding(vertical = 3.dp, horizontal = 5.dp)
                                    .clickable { showDateForMessage = message }
                            ) {
                                Text(
                                    text = message.text,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 5.dp, horizontal = 6.dp)
                                )
                            }

                            if (message == showDateForMessage) {
                                Popup(
                                    alignment = Alignment.TopCenter,
                                    onDismissRequest = { showDateForMessage = null }
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
                                                .format(message.timestamp!!.toDate()),
                                            color = Color.White,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (message.text == currentLastSeen.text && message.timestamp == currentLastSeen.timestamp) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .padding(vertical = 0.dp, horizontal = 5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "seen",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        stringResource(id = R.string.chat_send_message_placeholder)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            if (text.isNotEmpty()) {
                                chatViewModel.sendMessage(text, currUserUsername, otherUserUsername, currUserId, otherUserId)
                                text = ""
                            }
                        }
                    )
                },
                singleLine = true,
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .scale(1f, 0.8f)
            )
        }
    }
}
