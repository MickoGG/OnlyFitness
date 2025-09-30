package com.nemanjamicanovic.only_fitness.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.database.models.Notification
import com.nemanjamicanovic.only_fitness.database.repositories.NotificationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepo: NotificationRepo
) : ViewModel() {

    private var _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private var _numberOfNotSeen = MutableStateFlow(0)
    val numberOfNotSeen = _numberOfNotSeen.asStateFlow()

    private var onLoginDone = false

    fun onLogin() {
        if (onLoginDone) return
        else onLoginDone = true
        getNotifications()
    }

    private fun getNotifications() {
        notificationRepo.getNotifications(Firebase.auth.currentUser!!.uid) { value, error ->
            if (error == null && value != null) {
                _notifications.value = emptyList()
                _numberOfNotSeen.value = 0

                value.toObjects<Notification>().forEach { notification ->
                    if (!notification.seen) _numberOfNotSeen.value =  _numberOfNotSeen.value.plus(1)
                    _notifications.value = _notifications.value.plus(notification)
                }
            }
        }
    }

    fun createNotificationForSubscribe(trainer: String, subscriber: String) {
        notificationRepo.createNotificationForSubscribe(trainer, subscriber)
    }

    fun setEveryAsSeen() {
        notificationRepo.setEveryAsSeen()
    }

}
