package com.nemanjamicanovic.only_fitness.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Database @Inject constructor() {
    val instance: FirebaseFirestore = Firebase.firestore
}
