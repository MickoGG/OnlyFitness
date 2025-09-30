package com.nemanjamicanovic.only_fitness.authentication

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.nemanjamicanovic.only_fitness.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID


sealed class AuthResponse {
    data class Success(val uid: String) : AuthResponse()
    data class Error(val message: String) : AuthResponse()
}


class AuthenticationManager(private val context: Context) {

    private val auth = Firebase.auth

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result.user
                    if (firebaseUser != null) {
                        trySend(AuthResponse.Success(uid = firebaseUser.uid))
                    }
                    else {
                        trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
                    }
                }
                else {
                    trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
                }
            }

        awaitClose()
    }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result.user
                    if (firebaseUser != null) {
                        trySend(AuthResponse.Success(uid = firebaseUser.uid))
                    }
                    else {
                        trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
                    }
                }
                else {
                    trySend(AuthResponse.Error(message = task.exception?.message ?: ""))
                }
            }

        awaitClose()
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, byte ->
            str + "%02x".format(byte)
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)

            val credential = result.credential
            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider
                            .getCredential(googleIdTokenCredential.idToken, null)

                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser = task.result.user
                                    if (firebaseUser != null) {
                                        trySend(AuthResponse.Success(uid = firebaseUser.uid))
                                    }
                                    else {
                                        trySend(
                                            AuthResponse.Error(
                                                message = task.exception?.message ?: ""
                                            )
                                        )
                                    }
                                }
                                else {
                                    trySend(
                                        AuthResponse.Error(
                                            message = task.exception?.message ?: ""
                                        )
                                    )
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        trySend(AuthResponse.Error(message = e.message ?: ""))
                    }
                }
            }

        } catch (e: Exception) {
            trySend(AuthResponse.Error(message = e.message ?: ""))
        }

        awaitClose()
    }

}
