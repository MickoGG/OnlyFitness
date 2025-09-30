package com.nemanjamicanovic.only_fitness.database.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference


data class Notification(
    val title: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val seen: Boolean = false,
    val user: DocumentReference? = null
)
