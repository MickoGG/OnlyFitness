package com.nemanjamicanovic.only_fitness.database.repositories

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.database.Database
import com.nemanjamicanovic.only_fitness.database.dao.ChatDao
import com.nemanjamicanovic.only_fitness.database.models.Chat
import com.nemanjamicanovic.only_fitness.database.models.Message
import com.nemanjamicanovic.only_fitness.database.models.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatRepo @Inject constructor(val db: Database) : ChatDao {

    private val collectionChat: CollectionReference = db.instance.collection(Chat::class.simpleName!!)
    private val collectionUser: CollectionReference = db.instance.collection(User::class.simpleName!!)

    fun getCurrentUserDocumentReference(): DocumentReference {
        return collectionUser.document(Firebase.auth.currentUser!!.uid)
    }

    override fun getChatListForUser(uid: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        collectionChat
            .whereArrayContains(Chat::userIds.name, collectionUser.document(uid))
            .orderBy(Chat::lastTimestamp.name, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                snapshotListener(value, error)
            }
    }

    override fun getMessagesBetweenUsers(chatId: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration {
        return collectionChat
            .document(chatId)
            .collection(Message::class.simpleName!!)
            .orderBy(Message::timestamp.name, Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                snapshotListener(value, error)
            }
    }

    override fun getLiveUpdatesForLastSeenMessage(chatId: String, snapshotListener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration {
        return collectionChat
            .document(chatId)
            .addSnapshotListener { value, error ->
                snapshotListener(value, error)
            }
    }

    override fun sendMessage(text: String, chatId: String, fromId: String, toId: String) {
        val timestamp = Timestamp.now()
        val chatRef = collectionChat.document(chatId)

        chatRef.collection(Message::class.simpleName!!).add(
            mapOf(
                Message::from.name to collectionUser.document(fromId),
                Message::text.name to text,
                Message::timestamp.name to timestamp
            )
        )

        chatRef.get().addOnSuccessListener {
            if (!it.exists()) {
                chatRef.set(
                    mapOf(
                        Chat::userIds.name to listOf(collectionUser.document(fromId), collectionUser.document(toId)),
                        Chat::lastMessage.name to text,
                        Chat::lastTimestamp.name to timestamp,
                        Chat::lastSeenMessage.name to mapOf<String, DocumentReference?>(
                            toId to null,
                            fromId to null
                        )
                    )
                )
            }
            else {
                chatRef.set(
                    mapOf(
                        Chat::lastMessage.name to text,
                        Chat::lastTimestamp.name to timestamp
                    ),
                    SetOptions.merge()
                )
            }
        }
    }

    fun updateLastSeen(
        msg: Message,
        chatId: String,
        currUserId: String,
        otherUserId: String
    ) {
        val chatRef = collectionChat.document(chatId)

        chatRef.collection(Message::class.simpleName!!)
            .whereEqualTo(Message::from.name, msg.from)
            .whereEqualTo(Message::text.name, msg.text)
            .whereEqualTo(Message::timestamp.name, msg.timestamp)
            .limit(1)
            .get()
            .addOnSuccessListener { msgRef ->
                chatRef.get().addOnSuccessListener { chat ->
                    chatRef.set(
                        mapOf(
                            Chat::lastSeenMessage.name to mapOf(
                                currUserId to msgRef.documents[0].reference,
                                otherUserId to (chat[Chat::lastSeenMessage.name] as Map<String, DocumentReference?>)[otherUserId]
                            )
                        ),
                        SetOptions.merge()
                    )
                }
            }
    }

}
