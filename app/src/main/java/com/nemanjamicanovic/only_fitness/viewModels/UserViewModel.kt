package com.nemanjamicanovic.only_fitness.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.database.repositories.UserRepo
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    var profilePicture: ImageBitmap? = null
        private set

    private var _trainers = MutableStateFlow<List<User>>(emptyList())
    val trainers = _trainers.asStateFlow()

    private var onLoginOrRegisterDone = false

    fun onLogin() {
        if (onLoginOrRegisterDone) return
        else onLoginOrRegisterDone = true

        userRepo.getLiveUpdatesForUser(Firebase.auth.currentUser!!.uid) { value, error ->
            if (error == null && value != null && value.exists()) {
                _user.value = value.toObject()

                if (_user.value!!.profilePicture.isNotEmpty()) {
                    val bytes = Base64.decode(_user.value!!.profilePicture, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    profilePicture = bitmap.asImageBitmap()
                }
                else {
                    profilePicture = null
                }
            }
        }

        getTrainers()
    }

    fun onRegister(user: User) {
        if (onLoginOrRegisterDone) return

        userRepo.setUser(Firebase.auth.currentUser!!.uid, user).addOnSuccessListener {
            onLogin()
        }
    }

    private fun getTrainers(): Task<QuerySnapshot> {
        return userRepo.getTrainers().addOnSuccessListener { result ->
            _trainers.value = result.toObjects<User>().filter { user -> user.email != Firebase.auth.currentUser!!.email }
        }
    }

    fun getCurrentUser(): Task<DocumentSnapshot> {
        return userRepo.getUserById(Firebase.auth.currentUser!!.uid)
    }

    fun getUserByUsername(username: String): Task<QuerySnapshot> {
        return userRepo.getUserByUsername(username)
    }

    fun getCurrentLocation(context: Context, callback: (location: String) -> Unit) = viewModelScope.launch {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return@launch
        }

        val locationTask = LocationServices
            .getFusedLocationProviderClient(context)
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            )

        val location = locationTask.await()

        val geocoder = Geocoder(context, Locale.US)
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val city = addresses?.firstOrNull()?.locality ?: "Unknown city"
        val country = addresses?.firstOrNull()?.countryName ?: "Unknown country"

        callback("$city, $country")
    }

    fun changeFullName(fullName: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(fullName = fullName))
    }

    fun changeUsername(username: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(username = username))
    }

    fun changeLocation(location: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(location = location))
    }

    fun changeRole(role: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(role = role))
    }

    fun changeWorksWith(worksWith: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(worksWith = worksWith))
    }

    fun changeAvailable(available: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(available = available))
    }

    fun changeAboutMe(aboutMe: String): Task<Void> {
        return userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(aboutMe = aboutMe))
    }

    // Implementation using Firebase Storage
//    fun changeProfilePicture(uri: Uri) = viewModelScope.launch {
//        val storageRef = Firebase.storage.reference
//        val imageRef = storageRef.child("profile_pictures/$currUserUID.jpg")
//
//        imageRef.putFile(uri).await()
//        val downloadUrl = imageRef.downloadUrl.await().toString()
//
//        userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(profilePicture = downloadUrl))
//    }

    fun changeProfilePicture(context: Context, uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            val base64 = bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) } ?: ""

            userRepo.setUser(Firebase.auth.currentUser!!.uid, _user.value!!.copy(profilePicture = base64))
        }
        catch (_: Exception) {}
    }

}
