package com.nemanjamicanovic.only_fitness.ui.screens

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.nemanjamicanovic.only_fitness.R
import com.nemanjamicanovic.only_fitness.ui.theme.topAppBarContainerColor
import com.nemanjamicanovic.only_fitness.util.Available
import com.nemanjamicanovic.only_fitness.util.DataToChange
import com.nemanjamicanovic.only_fitness.util.UserRole
import com.nemanjamicanovic.only_fitness.util.WorksWith
import com.nemanjamicanovic.only_fitness.viewModels.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    userViewModel: UserViewModel,
    goBack: () -> Unit,
    goToChangePersonalData: (DataToChange) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by userViewModel.user.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.edit_profile_title))
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
        },
        modifier = modifier
    ) {
        val fullNameInteractionSource = remember { MutableInteractionSource() }
        if (fullNameInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.FULL_NAME)
        }

        val usernameInteractionSource = remember { MutableInteractionSource() }
        if (usernameInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.USERNAME)
        }

        val locationInteractionSource = remember { MutableInteractionSource() }
        if (locationInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.LOCATION)
        }

        val roleInteractionSource = remember { MutableInteractionSource() }
        if (roleInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.ROLE)
        }

        val worksWithInteractionSource = remember { MutableInteractionSource() }
        if (worksWithInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.WORKS_WITH)
        }

        val availableInteractionSource = remember { MutableInteractionSource() }
        if (availableInteractionSource.collectIsPressedAsState().value) {
            goToChangePersonalData(DataToChange.AVAILABLE)
        }

        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            ProfilePictureSelection(userViewModel)

            Text(
                text = stringResource(id = R.string.edit_profile_personal_data),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp)
            )

            OutlinedTextField(
                value = user?.fullName ?: "",
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_full_name_label))
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                },
                interactionSource = fullNameInteractionSource,
                readOnly = true,
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

//            OutlinedTextField(
//                value = user?.username ?: "",
//                onValueChange = {},
//                label = {
//                    Text(text = stringResource(id = R.string.edit_profile_username_label))
//                },
//                trailingIcon = {
//                    Icon(
//                        imageVector = Icons.Rounded.Edit,
//                        contentDescription = null
//                    )
//                },
//                interactionSource = usernameInteractionSource,
//                readOnly = true,
//                modifier = Modifier
//                    .padding(horizontal = 15.dp, vertical = 10.dp)
//                    .fillMaxWidth()
//            )

            OutlinedTextField(
                value = user?.location ?: "",
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_location_label))
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                },
                interactionSource = locationInteractionSource,
                readOnly = true,
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.edit_profile_quick_info),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp)
            )

            OutlinedTextField(
                value = when (user!!.role) {
                    UserRole.TRAINER.name ->
                        stringResource(id = R.string.edit_profile_role_trainer_text)
                    UserRole.CLIENT.name ->
                        stringResource(id = R.string.edit_profile_role_client_text)
                    else -> ""
                },
                onValueChange = {},
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_role_label))
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                },
                interactionSource = roleInteractionSource,
                readOnly = true,
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            if (user!!.role == UserRole.TRAINER.name) {
                OutlinedTextField(
                    value = when (user!!.worksWith) {
                        WorksWith.MALE_FEMALE.name ->
                            stringResource(id = R.string.edit_profile_works_with_male_female_text)
                        WorksWith.MALE.name ->
                            stringResource(id = R.string.edit_profile_works_with_male_text)
                        WorksWith.FEMALE.name ->
                            stringResource(id = R.string.edit_profile_works_with_female_text)
                        else -> ""
                    },
                    onValueChange = {},
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_works_with_label))
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null
                        )
                    },
                    interactionSource = worksWithInteractionSource,
                    readOnly = true,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .fillMaxWidth()
                )

                OutlinedTextField(
                    value = when (user!!.available) {
                        Available.ONLINE.name ->
                            stringResource(id = R.string.edit_profile_available_online_text)
                        Available.ONSITE.name ->
                            stringResource(id = R.string.edit_profile_available_onsite_text)
                        Available.ONLINE_ONSITE.name ->
                            stringResource(id = R.string.edit_profile_available_online_onsite_text)
                        else -> ""
                    },
                    onValueChange = {},
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_available_label))
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null
                        )
                    },
                    interactionSource = availableInteractionSource,
                    readOnly = true,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(id = R.string.edit_profile_description),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                )

                OutlinedButton(
                    onClick = {
                        goToChangePersonalData(DataToChange.ABOUT_ME)
                    },
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.edit_profile_change_description),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}


@Composable
private fun ProfilePictureSelection(userViewModel: UserViewModel) {
    val context = LocalContext.current

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let {
                userViewModel.changeProfilePicture(context, it)
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val cropOptions = CropImageContractOptions(
                uri,
                CropImageOptions().apply {
                    aspectRatioX = 1
                    aspectRatioY = 1
                    fixAspectRatio = true
                    cropShape = CropImageView.CropShape.OVAL
                    guidelines = CropImageView.Guidelines.ON
                }
            )
            cropImageLauncher.launch(cropOptions)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {
        if (userViewModel.profilePicture != null) {
            Image(
                bitmap = userViewModel.profilePicture!!,
                contentDescription = stringResource(R.string.profile_picture_content_description),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { launchPickImage(pickImageLauncher) }
            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.profile_picture),
                contentDescription = stringResource(R.string.profile_picture_content_description),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { launchPickImage(pickImageLauncher) }
            )
        }

        Text(
            text = "Change Photo",
            fontSize = 16.sp,
            modifier = Modifier.clickable {
                launchPickImage(pickImageLauncher)
            }
        )
    }
}


private fun launchPickImage(pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>) {
    pickImageLauncher.launch("image/*")
}
