package com.nemanjamicanovic.only_fitness.database.models

import com.google.firebase.firestore.DocumentReference


data class Subscribe(
    val subscribedToRef: DocumentReference? = null,
    val subscriptionFromRef: DocumentReference? = null
)
