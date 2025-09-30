package com.nemanjamicanovic.only_fitness.viewModels

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.database.models.Chat
import com.nemanjamicanovic.only_fitness.database.models.Message
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.database.repositories.ChatRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
) : ViewModel() {

    private var _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats = _chats.asStateFlow()

    private var _chatWithCurrentUser = MutableStateFlow<List<Message>>(emptyList())
    val chatWithCurrentUser = _chatWithCurrentUser.asStateFlow()
    private var chatWithCurrentUserListener: ListenerRegistration? = null

    private var _currentLastSeen = MutableStateFlow<Message>(Message())
    val currentLastSeen = _currentLastSeen.asStateFlow()
    private var currentLastSeenListener: ListenerRegistration? = null

    private var onLoginDone = false

    companion object {
        private fun generateChatId(username1: String, username2: String): String {
            return if (username1 < username2) {
                "$username1#$username2"
            }
            else {
                "$username2#$username1"
            }
        }
    }

    fun onLogin() {
        if (onLoginDone) return
        else onLoginDone = true
        getChatListForUser()
    }

    private fun getChatListForUser() {
        chatRepo.getChatListForUser(Firebase.auth.currentUser!!.uid) { value, error ->
            if (error == null && value != null) {
                _chats.value = emptyList()

                value.toObjects<Chat>().forEach { chat ->
                    chat.userIds.first { documentReference ->
                        documentReference != chatRepo.getCurrentUserDocumentReference()
                    }
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            chat.otherUser = documentSnapshot.toObject<User>()
                            _chats.value = _chats.value.plus(chat)
                        }
                }
            }
        }
    }

    fun clearMessagesBetweenUsers() {
        _chatWithCurrentUser.value = emptyList()
    }

    fun getMessagesBetweenUsers(username1: String, username2: String) {
        chatWithCurrentUserListener?.remove()
        chatWithCurrentUserListener = chatRepo.getMessagesBetweenUsers(generateChatId(username1,username2)) { value, error ->
            if (error == null && value != null) {
                _chatWithCurrentUser.value = emptyList()

                val messagesList = value.toObjects<Message>()
                val taskList = mutableListOf<Task<DocumentSnapshot>>()

                messagesList.forEach { message ->
                    taskList.add(message.from!!.get())
                }

                Tasks.whenAllSuccess<DocumentSnapshot>(taskList).addOnSuccessListener { result ->
                    result.forEachIndexed { index, documentSnapshot ->
                        messagesList[index].fromUser = documentSnapshot.toObject<User>()
                        _chatWithCurrentUser.value = _chatWithCurrentUser.value.plus(messagesList[index])
                    }
                }
            }
        }
    }

    fun getLiveUpdatesForLastSeenMessage(currUserUsername: String, otherUserUsername: String, otherUserId: String) {
        currentLastSeenListener?.remove()
        currentLastSeenListener = chatRepo.getLiveUpdatesForLastSeenMessage(generateChatId(currUserUsername, otherUserUsername)) { value, error ->
            if (error == null && value != null && value.exists()) {
                val chat: Chat = value.toObject<Chat>()!!

                chat.lastSeenMessage[otherUserId]?.get()?.addOnSuccessListener { message ->
                    _currentLastSeen.value = message.toObject<Message>()!!
                }
            }
        }
    }

    fun sendMessage(text: String, fromUsername: String, toUsername: String, fromId: String, toId: String) {
        chatRepo.sendMessage(text, generateChatId(fromUsername, toUsername), fromId, toId)
    }

    fun updateLastSeen(msg: Message, currUserUsername: String, otherUserUsername: String, currUserId: String, otherUserId: String) {
        chatRepo.updateLastSeen(
            msg,
            generateChatId(currUserUsername, otherUserUsername),
            currUserId,
            otherUserId
        )
    }

}
