package com.nemanjamicanovic.only_fitness.database.repositories

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.Database
import com.nemanjamicanovic.only_fitness.database.dao.NotificationDao
import com.nemanjamicanovic.only_fitness.database.models.Notification
import com.nemanjamicanovic.only_fitness.database.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NotificationRepo @Inject constructor(
    val db: Database,
    @ApplicationContext private val context: Context
) : NotificationDao {

    private val collectionNotification: CollectionReference = db.instance.collection(Notification::class.simpleName!!)
    private val collectionUser: CollectionReference = db.instance.collection(User::class.simpleName!!)

    override fun getNotifications(uid: String, snapshotListener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        collectionNotification
            .whereEqualTo(Notification::user.name, collectionUser.document(uid))
            .orderBy(Notification::timestamp.name, Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                snapshotListener(value, error)
            }
    }

    override fun createNotificationForSubscribe(trainer: String, subscriber: String) {
        collectionUser.document(subscriber).get().addOnSuccessListener { documentSnapshot ->
            collectionNotification
                .add(
                    Notification(
                        title = context.getString(R.string.notification_repo_subscribe_title_subscriber),
                        text = context.getString(R.string.notification_repo_subscribe_text_subscriber, documentSnapshot.toObject<User>()!!.username),
                        timestamp = Timestamp.now(),
                        seen = false,
                        user = collectionUser.document(trainer)
                    )
                )
        }

        collectionUser.document(trainer).get().addOnSuccessListener { documentSnapshot ->
            collectionNotification
                .add(
                    Notification(
                        title = context.getString(R.string.notification_repo_subscribe_title_trainer),
                        text = context.getString(R.string.notification_repo_subscribe_text_trainer, documentSnapshot.toObject<User>()!!.username),
                        timestamp = Timestamp.now(),
                        seen = false,
                        user = collectionUser.document(subscriber)
                    )
                )
        }
    }

    override fun setEveryAsSeen() {
        collectionNotification
            .whereEqualTo(Notification::user.name, collectionUser.document(Firebase.auth.currentUser!!.uid))
            .whereEqualTo(Notification::seen.name, false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { documentSnapshot ->
                    documentSnapshot.reference.set(
                        mapOf(
                            Notification::seen.name to true
                        ),
                        SetOptions.merge()
                    )
                }
            }
    }

}
