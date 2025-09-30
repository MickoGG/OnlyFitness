package com.nemanjamicanovic.only_fitness.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference


data class Message(
    val from: DocumentReference? = null,
    val text: String = "",
    val timestamp: Timestamp? = null,

    var fromUser: User? = null
)
