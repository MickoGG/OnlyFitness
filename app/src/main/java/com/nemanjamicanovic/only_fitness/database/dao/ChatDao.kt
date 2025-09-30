package com.nemanjamicanovic.only_fitness.database.dao

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot


interface ChatDao {

    fun getChatListForUser(uid: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    fun getMessagesBetweenUsers(chatId: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration

    fun getLiveUpdatesForLastSeenMessage(chatId: String, snapshotListener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration

    fun sendMessage(text: String, chatId: String, fromId: String, toId: String)

}
