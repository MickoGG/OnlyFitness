package com.nemanjamicanovic.only_fitness.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.viewModels.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chats(
    chatViewModel: ChatViewModel,
    goToChat: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    val chats by chatViewModel.chats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.chat_text),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
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
            LazyColumn {
                items(chats) { chat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 3.dp, bottom = 3.dp, start = 5.dp, end = 5.dp)
                            .clickable {
                                goToChat(chat.otherUser!!)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(2f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                if (chat.otherUser!!.profilePicture.isNotEmpty()) {
                                    val bytes = Base64.decode(chat.otherUser!!.profilePicture, Base64.DEFAULT)
                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = stringResource(id = R.string.profile_picture_content_description),
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(CircleShape)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                                else {
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
                                    .weight(3f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = chat.otherUser!!.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                Text(text = chat.otherUser!!.username)

                                if (chat.lastMessage.length > 18) {
                                    Text(text = chat.lastMessage.take(18) + "...")
                                }
                                else {
                                    Text(text = chat.lastMessage)
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .scale(1.1f)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}
