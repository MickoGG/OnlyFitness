package com.nemanjamicanovic.only_fitness.database.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.nemanjamicanovic.only_fitness.database.dao.UserDao
import com.nemanjamicanovic.only_fitness.database.models.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.nemanjamicanovic.only_fitness.database.Database
import com.nemanjamicanovic.only_fitness.util.UserRole
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRepo @Inject constructor(val db: Database) : UserDao {

    private val collectionUser: CollectionReference = db.instance.collection(User::class.simpleName!!)

    override fun getUserById(uid: String): Task<DocumentSnapshot> {
        return collectionUser
            .document(uid)
            .get()
    }

    override fun getUserByUsername(username: String): Task<QuerySnapshot> {
        return collectionUser
            .whereEqualTo(User::username.name, username)
            .limit(1)
            .get()
    }

    override fun getTrainers(): Task<QuerySnapshot> {
        return collectionUser
            .whereEqualTo(User::role.name, UserRole.TRAINER.toString())
            .get()
    }

    override fun setUser(uid: String, user: User): Task<Void> {
        return collectionUser
            .document(uid)
            .set(user)
    }

    override fun getLiveUpdatesForUser(uid: String, snapshotListener: (DocumentSnapshot?, FirebaseFirestoreException?) -> Unit) {
        collectionUser
            .document(uid)
            .addSnapshotListener { value, error ->
                snapshotListener(value, error)
            }
    }

}
