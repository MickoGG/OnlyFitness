package com.nemanjamicanovic.only_fitness.viewModels

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


@Parcelize
data class UserLoginState(
    val emailOrUsername: String = "",
    val password: String = ""
) : Parcelable

@Parcelize
data class UserRegisterState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = ""
) : Parcelable


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val LOGIN_STATE_KEY = "only_fitness_login_data"
        const val REGISTER_STATE_KEY = "only_fitness_register_data"
    }

    private val _loginState = savedStateHandle.getStateFlow(LOGIN_STATE_KEY, UserLoginState())
    private val _registerState = savedStateHandle.getStateFlow(REGISTER_STATE_KEY, UserRegisterState())

    val loginState = _loginState
    val registerState = _registerState

    // Utils //

    fun setEmailOrUsernameForLogin(newEmailOrUsername: String) {
        savedStateHandle[LOGIN_STATE_KEY] = _loginState.value.copy(
            emailOrUsername = newEmailOrUsername
        )
    }

    fun setPasswordForLogin(newPassword: String) {
        savedStateHandle[LOGIN_STATE_KEY] = _loginState.value.copy(
            password = newPassword
        )
    }

    fun setFullNameForRegister(newFullName: String) {
        savedStateHandle[REGISTER_STATE_KEY] = _registerState.value.copy(
            fullName = newFullName
        )
    }

    fun setUsernameForRegister(newUsername: String) {
        savedStateHandle[REGISTER_STATE_KEY] = _registerState.value.copy(
            username = newUsername
        )
    }

    fun setEmailForRegister(newEmail: String) {
        savedStateHandle[REGISTER_STATE_KEY] = _registerState.value.copy(
            email = newEmail
        )
    }

    fun setPasswordForRegister(newPassword: String) {
        savedStateHandle[REGISTER_STATE_KEY] = _registerState.value.copy(
            password = newPassword
        )
    }

}
