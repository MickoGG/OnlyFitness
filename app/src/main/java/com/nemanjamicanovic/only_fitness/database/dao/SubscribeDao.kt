package com.nemanjamicanovic.only_fitness.database.dao

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot


interface SubscribeDao {

    fun getSubscribedForUser(uid: String): Task<QuerySnapshot>

    fun getSubscriptionsForUser(uid: String): Task<QuerySnapshot>

    fun checkIfAlreadySubscribed(subscribedTo: DocumentReference): Task<AggregateQuerySnapshot>

    fun subscribe(subscribedTo: DocumentReference): Task<DocumentReference>

}
