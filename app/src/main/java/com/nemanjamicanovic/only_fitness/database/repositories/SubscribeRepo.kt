package com.nemanjamicanovic.only_fitness.database.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateQuerySnapshot
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.database.Database
import com.nemanjamicanovic.only_fitness.database.dao.SubscribeDao
import com.nemanjamicanovic.only_fitness.database.models.Chat
import com.nemanjamicanovic.only_fitness.database.models.Subscribe
import com.nemanjamicanovic.only_fitness.database.models.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SubscribeRepo @Inject constructor(val db: Database) : SubscribeDao {

    private val collectionSubscribe: CollectionReference = db.instance.collection(Subscribe::class.simpleName!!)
    private val collectionUser: CollectionReference = db.instance.collection(User::class.simpleName!!)

    override fun getSubscribedForUser(uid: String): Task<QuerySnapshot> {
        return collectionSubscribe
            .whereEqualTo(Subscribe::subscribedToRef.name, collectionUser.document(uid))
            .get()
    }

    override fun getSubscriptionsForUser(uid: String): Task<QuerySnapshot> {
        return collectionSubscribe
            .whereEqualTo(Subscribe::subscriptionFromRef.name, collectionUser.document(uid))
            .get()
    }

    override fun checkIfAlreadySubscribed(subscribedTo: DocumentReference): Task<AggregateQuerySnapshot> {
        return collectionSubscribe
            .whereEqualTo(Subscribe::subscribedToRef.name, subscribedTo)
            .whereEqualTo(Subscribe::subscriptionFromRef.name, collectionUser.document(Firebase.auth.currentUser!!.uid))
            .count()
            .get(AggregateSource.SERVER)
    }

    override fun subscribe(subscribedTo: DocumentReference): Task<DocumentReference> {
        return collectionSubscribe
            .add(
                Subscribe(
                    subscribedToRef = subscribedTo,
                    subscriptionFromRef = collectionUser.document(Firebase.auth.currentUser!!.uid)
                )
            )
            .addOnSuccessListener {
                subscribedTo.get().addOnSuccessListener { documentSnapshot ->
                    subscribedTo.set(
                        mapOf(
                            User::subscribers.name to documentSnapshot[User::subscribers.name] as Long + 1
                        ),
                        SetOptions.merge()
                    )
                }
            }
    }

}
