package com.nemanjamicanovic.only_fitness.database.dao

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


interface NotificationDao {

    fun getNotifications(uid: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    fun createNotificationForSubscribe(trainer: String, subscriber: String)

    fun setEveryAsSeen()

}
