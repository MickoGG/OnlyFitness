package com.nemanjamicanovic.only_fitness.database.dao

import com.google.android.gms.tasks.Task
import com.nemanjamicanovic.only_fitness.database.models.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


interface UserDao {

    fun getUserById(uid: String): Task<DocumentSnapshot>

    fun getUserByUsername(username: String): Task<QuerySnapshot>

    fun getTrainers(): Task<QuerySnapshot>

    fun setUser(uid: String, user: User): Task<Void>

    fun getLiveUpdatesForUser(uid: String, snapshotListener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit)

}
