package com.nemanjamicanovic.only_fitness.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel
import com.nemanjamicanovic.only_fitness.authentication.AuthResponse
import com.nemanjamicanovic.only_fitness.authentication.AuthenticationManager
import com.nemanjamicanovic.only_fitness.viewModels.AuthenticationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.ChatViewModel
import com.nemanjamicanovic.only_fitness.viewModels.NotificationViewModel
import com.nemanjamicanovic.only_fitness.viewModels.SubscribeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@Composable
fun Login(
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    subscribeViewModel: SubscribeViewModel,
    chatViewModel: ChatViewModel,
    notificationViewModel: NotificationViewModel,
    goToRegister: () -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val authenticationManager = remember { AuthenticationManager(context) }

    val coroutineScope = rememberCoroutineScope()

    val loginState by authenticationViewModel.loginState.collectAsState()

    var loginFailed by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var test by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.login_text),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.login_fill_form),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = loginState.emailOrUsername,
            onValueChange = { authenticationViewModel.setEmailOrUsernameForLogin(it) },
            placeholder = {
                Text(text = stringResource(id = R.string.login_email_or_username_field))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = loginState.password,
            onValueChange = { authenticationViewModel.setPasswordForLogin(it) },
            placeholder = {
                Text(text = stringResource(id = R.string.login_password_field))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authenticationManager.loginWithEmail(
                    "micko1@gmail.com",
                    "test123"
                )
                    .onEach { response ->
                        if (response is AuthResponse.Success) {
                            userViewModel.onLogin()
                            subscribeViewModel.onLogin()
                            chatViewModel.onLogin()
                            notificationViewModel.onLogin()
                            onLogin()
                        }
                        else if (response is AuthResponse.Error) {
                            loginFailed = true
                            errorMessage = response.message
                        }
                    }
                    .launchIn(coroutineScope)

            },
//            onClick = {
//                userViewModel.getUserByUsername(loginState.emailOrUsername)
//                    .addOnSuccessListener {
//                        val users = it.toObjects<User>()
//
//                        authenticationManager.loginWithEmail(
//                            if (users.isEmpty()) loginState.emailOrUsername else users[0].email,
//                            loginState.password
//                        )
//                            .onEach { response ->
//                                if (response is AuthResponse.Success) {
//                                    userViewModel.onLogin(response.uid)
//                                    subscribeViewModel.onLogin()
//                                    chatViewModel.onLogin()
//                                    notificationViewModel.onLogin()
//                                    onLogin()
//                                }
//                                else if (response is AuthResponse.Error) {
//                                    loginFailed = true
//                                    errorMessage = response.message
//                                }
//                            }
//                            .launchIn(coroutineScope)
//                    }
//            },
            enabled = loginState.emailOrUsername.isNotEmpty() && loginState.password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.login_sign_in_button),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 12.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = stringResource(id = R.string.login_or_continue_with))
//        }

//        OutlinedButton(
//            onClick = {
//                authenticationManager.signInWithGoogle()
//                    .onEach { response ->
//                        if (response is AuthResponse.Success) {
//                            test = 1
//                            userViewModel.onLogin()
//                            subscribeViewModel.onLogin()
//                            chatViewModel.onLogin()
//                            onLogin()
//                        }
//                        else if (response is AuthResponse.Error) {
//                            test = 2
//                            loginFailed = true
//                            errorMessage = response.message
//                        }
//                    }
//                    .launchIn(coroutineScope)
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.google_logo),
//                contentDescription = null,
//                modifier = Modifier.size(36.dp)
//            )
//
//            Spacer(modifier = Modifier.width(6.dp))
//
//            Text(
//                text = stringResource(id = R.string.login_sign_in_with_google),
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//        }

        if (loginFailed) {
            Text(
                text = stringResource(id = R.string.login_failed),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 15.dp)
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleSmall,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.login_do_not_have_an_account),
                fontSize = 18.sp,
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    goToRegister()
                }
            )
        }

//        if (test == 0) {
//            Text(text = "Nula")
//        }
//        if (test == 1) {
//            Text(text = "Jedan")
//        }
//        if (test == 2) {
//            Text(text = "Dva")
//        }
    }
}
