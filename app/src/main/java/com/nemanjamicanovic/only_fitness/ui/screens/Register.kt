package com.nemanjamicanovic.only_fitness.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.database.models.User
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel
import com.nemanjamicanovic.only_fitness.authentication.AuthResponse
import com.nemanjamicanovic.only_fitness.authentication.AuthenticationManager
import com.nemanjamicanovic.only_fitness.ui.theme.topAppBarContainerColor
import com.nemanjamicanovic.only_fitness.viewModels.AuthenticationViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    goBack: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val authenticationManager = remember { AuthenticationManager(context) }

    val coroutineScope = rememberCoroutineScope()

    val registerState by authenticationViewModel.registerState.collectAsState()

    var registrationFailed by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.register_back))
                },
                navigationIcon = {
                    IconButton (
                        onClick = { goBack() }
                    ) {
                        Icon (
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = topAppBarContainerColor)
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp + it.calculateLeftPadding(LayoutDirection.Ltr),
                    end = 20.dp + it.calculateRightPadding(LayoutDirection.Ltr),
                    bottom = 20.dp + it.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.register_text),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.register_fill_form),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = registerState.fullName,
                onValueChange = { authenticationViewModel.setFullNameForRegister(it) },
                placeholder = {
                    Text(text = stringResource(id = R.string.register_full_name_field))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = registerState.username,
                onValueChange = { authenticationViewModel.setUsernameForRegister(it) },
                placeholder = {
                    Text(text = stringResource(id = R.string.register_username_field))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Tag,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = registerState.email,
                onValueChange = { authenticationViewModel.setEmailForRegister(it) },
                placeholder = {
                    Text(text = stringResource(id = R.string.register_email_field))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = registerState.password,
                onValueChange = { authenticationViewModel.setPasswordForRegister(it) },
                placeholder = {
                    Text(text = stringResource(id = R.string.register_password_field))
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
                    authenticationManager.createAccountWithEmail(
                        registerState.email.lowercase(),
                        registerState.password
                    )
                        .onEach { response ->
                            if (response is AuthResponse.Success) {
                                val user = User(
                                    fullName = registerState.fullName,
                                    username = registerState.username,
                                    email = registerState.email.lowercase()
                                )
                                userViewModel.onRegister(user)
                                onRegister()
                            }
                            else if (response is AuthResponse.Error) {
                                registrationFailed = true
                                errorMessage = response.message
                            }
                        }
                        .launchIn(coroutineScope)
                },
                enabled = registerState.fullName.isNotEmpty() && registerState.username.isNotEmpty() &&
                        registerState.email.isNotEmpty() && registerState.password.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.register_button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (registrationFailed) {
                Text(
                    text = stringResource(id = R.string.register_failed),
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
        }
    }
}
