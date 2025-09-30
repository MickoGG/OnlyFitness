package com.nemanjamicanovic.only_fitness.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DoneOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.ui.theme.topAppBarContainerColor
import com.nemanjamicanovic.only_fitness.util.Available
import com.nemanjamicanovic.only_fitness.util.DataToChange
import com.nemanjamicanovic.only_fitness.util.composables.SwitchButton
import com.nemanjamicanovic.only_fitness.util.UserRole
import com.nemanjamicanovic.only_fitness.util.WorksWith
import com.nemanjamicanovic.only_fitness.util.composables.Spinner
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePersonalData(
    userViewModel: UserViewModel,
    dataToChange: DataToChange,
    goBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by userViewModel.user.collectAsStateWithLifecycle()

    val worksWithValues = stringArrayResource(id = R.array.worksWithValues).asList()
    val availableValues = stringArrayResource(id = R.array.availableValues).asList()

    var data by rememberSaveable { mutableStateOf("") }
    var first by rememberSaveable { mutableStateOf(true) }

    if (first) {
        first = false
        data = when (dataToChange) {
            DataToChange.FULL_NAME -> user?.fullName ?: ""
            DataToChange.USERNAME -> user?.username ?: ""
            DataToChange.LOCATION -> user?.location ?: ""
            DataToChange.ROLE -> user?.role ?: ""
            DataToChange.WORKS_WITH -> user?.worksWith ?: ""
            DataToChange.AVAILABLE -> user?.available ?: ""
            DataToChange.ABOUT_ME -> user?.aboutMe ?: ""
        }
    }

    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permission ->
        if (permission) {
            userViewModel.getCurrentLocation(context) { location ->
                data = location
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (dataToChange) {
                                DataToChange.FULL_NAME ->
                                    stringResource(id = R.string.change_personal_data_title_full_name)
                                DataToChange.USERNAME ->
                                    stringResource(id = R.string.change_personal_data_title_username)
                                DataToChange.LOCATION ->
                                    stringResource(id = R.string.change_personal_data_title_location)
                                DataToChange.ROLE ->
                                    stringResource(id = R.string.change_personal_data_title_role)
                                DataToChange.WORKS_WITH ->
                                    stringResource(id = R.string.change_personal_data_title_works_with)
                                DataToChange.AVAILABLE ->
                                    stringResource(id = R.string.change_personal_data_title_available)
                                DataToChange.ABOUT_ME ->
                                    stringResource(id = R.string.change_personal_data_title_about_me)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                },
                navigationIcon = {
                    IconButton (
                        onClick = { goBack() }
                    ) {
                        Icon (
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton (
                        onClick = {
                            if (data.isNotEmpty()) {
                                when (dataToChange) {
                                    DataToChange.FULL_NAME -> userViewModel.changeFullName(data)
                                    DataToChange.USERNAME -> userViewModel.changeUsername(data)
                                    DataToChange.LOCATION -> userViewModel.changeLocation(data)
                                    DataToChange.ROLE -> userViewModel.changeRole(data)
                                    DataToChange.WORKS_WITH -> userViewModel.changeWorksWith(data)
                                    DataToChange.AVAILABLE -> userViewModel.changeAvailable(data)
                                    DataToChange.ABOUT_ME -> userViewModel.changeAboutMe(data)
                                }
                                    .addOnSuccessListener {
                                        goBack()
                                    }
                            }
                        }
                    ) {
                        Icon (
                            imageVector = Icons.Outlined.DoneOutline,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = topAppBarContainerColor)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxWidth()
        ) {
            when (dataToChange) {
                DataToChange.FULL_NAME -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_full_name_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    OutlinedTextField(
                        value = data,
                        onValueChange = { data = it },
                        label = {
                            Text(text = stringResource(id = R.string.change_personal_data_full_name_label))
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    )
                }
                DataToChange.USERNAME -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_username_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    OutlinedTextField(
                        value = data,
                        onValueChange = { data = it },
                        label = {
                            Text(text = stringResource(id = R.string.change_personal_data_username_label))
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    )
                }
                DataToChange.LOCATION -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_location_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    OutlinedTextField(
                        value = data,
                        onValueChange = { data = it },
                        label = {
                            Text(text = stringResource(id = R.string.change_personal_data_location_label))
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                                else {
                                    userViewModel.getCurrentLocation(context) { location ->
                                        data = location
                                    }
                                }
                            }
                            else {
                                userViewModel.getCurrentLocation(context) { location ->
                                    data = location
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp, vertical = 15.dp)
                    ) {
                        Text(text = stringResource(id = R.string.change_personal_data_location_button_text))
                    }
                }
                DataToChange.ROLE -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_role_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    SwitchButton(
                        label = stringResource(id = R.string.change_personal_data_role_label),
                        isChecked = data == UserRole.TRAINER.name,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) { checked ->
                        data = if (checked) UserRole.TRAINER.name
                        else UserRole.CLIENT.name
                    }
                }
                DataToChange.WORKS_WITH -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_works_with_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    Spinner(
                        value = worksWithValues[enumValueOf<WorksWith>(data).ordinal],
                        onSelect = { option ->
                            data = WorksWith.entries[worksWithValues.indexOfFirst { it == option }].name
                        },
                        options = worksWithValues
                    )
                }
                DataToChange.AVAILABLE -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_available_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    Spinner(
                        value = availableValues[enumValueOf<Available>(data).ordinal],
                        onSelect = { option ->
                            data = Available.entries[availableValues.indexOfFirst { it == option }].name
                        },
                        options = availableValues
                    )
                }
                DataToChange.ABOUT_ME -> {
                    Text(
                        text = stringResource(id = R.string.change_personal_data_about_me_text),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
                    )

                    BasicTextField(
                        value = data,
                        onValueChange = { data = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        textStyle = TextStyle(fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            Box {
                                if (data.isEmpty()) {
                                    Text("Enter your profile description...", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}
