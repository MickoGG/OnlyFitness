package com.nemanjamicanovic.only_fitness.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.nemanjamicanovic.only_fitness.database.models.Subscribe
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.database.repositories.SubscribeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class ProfileRefreshUiState(
    val isRefreshing: Boolean = false
)

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val subscribeRepo: SubscribeRepo
) : ViewModel() {

    private var _subscribed = MutableStateFlow<List<User>>(emptyList())
    val subscribed = _subscribed.asStateFlow()

    private var _subscriptions = MutableStateFlow<List<User>>(emptyList())
    val subscriptions = _subscriptions.asStateFlow()

    private val _profileRefreshUiState = MutableStateFlow(ProfileRefreshUiState())
    val profileRefreshUiState = _profileRefreshUiState.asStateFlow()

    private var onLoginDone = false

    fun onLogin() {
        if (onLoginDone) return
        else onLoginDone = true
        getSubscribedForUser()
        getSubscriptionsForUser()
    }

    fun getSubscribedForUser() {
        _profileRefreshUiState.update { it.copy(isRefreshing = true) }
        subscribeRepo.getSubscribedForUser(Firebase.auth.currentUser!!.uid).addOnSuccessListener {
            _subscribed.value = listOf()
            it.toObjects<Subscribe>().forEach { subscribe ->
                subscribe.subscriptionFromRef!!.get().addOnSuccessListener { documentSnapshot ->
                    _subscribed.value = _subscribed.value.plus(documentSnapshot.toObject<User>()!!)
                }
            }
            _profileRefreshUiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun getSubscriptionsForUser() {
        _profileRefreshUiState.update { it.copy(isRefreshing = true) }
        subscribeRepo.getSubscriptionsForUser(Firebase.auth.currentUser!!.uid).addOnSuccessListener {
            _subscriptions.value = listOf()
            it.toObjects<Subscribe>().forEach { subscribe ->
                subscribe.subscribedToRef!!.get().addOnSuccessListener { documentSnapshot ->
                    _subscriptions.value = _subscriptions.value.plus(documentSnapshot.toObject<User>()!!)
                }
            }
            _profileRefreshUiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun subscribeIfNotAlreadySubscribed(subscribedTo: DocumentReference, callback: () -> Unit) {
        subscribeRepo.checkIfAlreadySubscribed(subscribedTo).addOnSuccessListener { snapshot ->
            if (snapshot.count == 0L) {
                subscribe(subscribedTo)
                callback()
            }
        }
    }

    private fun subscribe(subscribedTo: DocumentReference) = viewModelScope.launch {
        subscribeRepo.subscribe(subscribedTo).await()
        getSubscriptionsForUser()
    }

}
