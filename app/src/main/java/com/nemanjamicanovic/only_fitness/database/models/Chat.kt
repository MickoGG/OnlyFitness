package com.nemanjamicanovic.only_fitness.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference


data class Chat(
    val userIds: List<DocumentReference> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Timestamp = Timestamp.now(),
    val lastSeenMessage: HashMap<String, DocumentReference?> = HashMap(),

    var otherUser: User? = null
)
