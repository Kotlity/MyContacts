package com.mycontacts.presentation.contact_operations.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mycontacts.R
import com.mycontacts.presentation.contact_operations.composables.ContactImageActionModalBottomSheet
import com.mycontacts.presentation.contact_operations.composables.ContactInfoButtonOperations
import com.mycontacts.presentation.contact_operations.composables.ContactInfoPhoto
import com.mycontacts.presentation.contact_operations.composables.CustomAlertDialog
import com.mycontacts.presentation.contact_operations.composables.CustomTextField
import com.mycontacts.presentation.contact_operations.composables.DeletePhotoOrLastNameIcon
import com.mycontacts.presentation.contact_operations.events.ContactOperationsEvent
import com.mycontacts.presentation.contact_operations.viewmodels.ContactOperationsViewModel
import com.mycontacts.utils.Constants._075Float
import com.mycontacts.utils.Constants._1000
import com.mycontacts.utils.Constants._16sp
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._1Float
import com.mycontacts.utils.createTempFilePhotoPath
import com.mycontacts.utils.isAppHasPermission
import com.mycontacts.utils.uriToBitmap
import com.mycontacts.utils.validation.ValidationStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactOperationsScreen(
    contactOperationsViewModel: ContactOperationsViewModel,
    event: (ContactOperationsEvent) -> Unit,
    onNavigateUp: () -> Unit
) {

    val editableContactInfo = contactOperationsViewModel.editableContactInfo

    val isModalBottomSheetActive = contactOperationsViewModel.isModalBottomSheetActive

    val photoBitmap = editableContactInfo.photo

    val firstNameInputText = editableContactInfo.firstName
    val firstNameValidationStatus = contactOperationsViewModel.firstNameValidationStatus

    val lastNameInputText = editableContactInfo.lastName
    val lastNameValidationStatus = contactOperationsViewModel.lastNameValidationStatus

    val phoneNumberInputText = editableContactInfo.phoneNumber
    val phoneNumberValidationStatus = contactOperationsViewModel.phoneNumberValidationStatus

    val deleteIconsVisibility = contactOperationsViewModel.deleteIconsVisibility

    val contactOperationsButton = contactOperationsViewModel.contactOperationsButton

    val cameraPermissionResultFlow = contactOperationsViewModel.cameraPermissionResultFlow

    val deleteIconsResultFlow = contactOperationsViewModel.deleteIconsResultFlow

    val contactOperationsResultFlow = contactOperationsViewModel.contactOperationsResultFlow

    val cameraPermissionRationaleAlertDialog = contactOperationsViewModel.cameraPermissionRationaleAlertDialog

    var tempFilePhotoPath by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val modalBottomSheetState = rememberModalBottomSheetState()

    val coroutineScope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
        tempFilePhotoPath.uriToBitmap(contentResolver)?.let { bitmap ->
            event(ContactOperationsEvent.UpdatePhoto(bitmap))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            event(ContactOperationsEvent.UpdateCameraPermissionResult(permissionResult = context.getString(R.string.cameraPermissionIsGranted)))
            val path = createTempFilePhotoPath(context)
            tempFilePhotoPath = path
            cameraLauncher.launch(tempFilePhotoPath)
        } else event(ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { photoUri ->
        photoUri?.let {
            it.uriToBitmap(contentResolver)?.let { bitmap ->
                event(ContactOperationsEvent.UpdatePhoto(bitmap))
            }
        }
    }

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val lastNameFocusRequester = remember {
        FocusRequester()
    }

    val phoneNumberFocusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = editableContactInfo) {
        event(ContactOperationsEvent.UpdateContactOperationsButton)
    }

    LaunchedEffect(
        deleteIconsResultFlow,
        contactOperationsResultFlow,
        cameraPermissionResultFlow
    ) {
        launch {
            deleteIconsResultFlow.collect { message ->
                snackbarHostState.showSnackbar(message = message)
            }
        }
        launch {
            contactOperationsResultFlow.collect { contactOperationsResult ->
                snackbarHostState.showSnackbar(message = contactOperationsResult.message)
                if (contactOperationsResult.isShouldNavigateBack) onNavigateUp()
            }
        }
        launch {
            cameraPermissionResultFlow.collect { permissionResultMessage ->
                if (permissionResultMessage == context.getString(R.string.cameraPermissionIsGranted)) Toast.makeText(context, permissionResultMessage, Toast.LENGTH_SHORT).show()
                else snackbarHostState.showSnackbar(message = permissionResultMessage, duration = SnackbarDuration.Long)
            }
        }
    }

    if (isModalBottomSheetActive) {
        ContactImageActionModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismiss = { event(ContactOperationsEvent.UpdateModalBottomSheetActiveState) },
            onTakeAPhoto = {
                if (isAppHasPermission(context, Manifest.permission.CAMERA)) {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        event(ContactOperationsEvent.UpdateModalBottomSheetActiveState)
                    }
                    val path = createTempFilePhotoPath(context)
                    tempFilePhotoPath = path
                    cameraLauncher.launch(tempFilePhotoPath)
                } else {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        event(ContactOperationsEvent.UpdateModalBottomSheetActiveState)
                    }
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onSelectAPhoto = {
                if (isPhotoPickerAvailable(context)) {
                    photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                coroutineScope.launch {
                    modalBottomSheetState.hide()
                    event(ContactOperationsEvent.UpdateModalBottomSheetActiveState)
                }
            }
        )
    }

    if (cameraPermissionRationaleAlertDialog) {
        CustomAlertDialog(
            icon = Icons.Default.Camera,
            title = R.string.cameraPermissionRationaleAlertDialogTitle,
            fontSizes = listOf(_18sp, _16sp),
            fontWeights = listOf(FontWeight.Bold, FontWeight.W600),
            text = R.string.cameraPermissionRationaleAlertDialogText,
            onConfirmButtonClick = {
                event(ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState)
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismissButtonClick = {
                event(ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState)
                event(ContactOperationsEvent.UpdateCameraPermissionResult(permissionResult = context.getString(R.string.cameraPermissionIsDenied)))
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                AnimatedContent(
                    targetState = photoBitmap,
                    transitionSpec = { fadeIn(animationSpec = tween(durationMillis = _1000)) togetherWith fadeOut(animationSpec = tween(durationMillis = _1000)) },
                    label = ""
                ) { bitmap ->
                    ContactInfoPhoto(
                        photoBitmap = bitmap,
                        onPhotoClick = { event(ContactOperationsEvent.UpdateModalBottomSheetActiveState) }
                    )
                }
                if (deleteIconsVisibility.isDeleteContactInfoPhotoIconVisible) {
                    DeletePhotoOrLastNameIcon(onIconClick = { event(ContactOperationsEvent.DeleteContactInfoPhoto) })
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen._20dp)))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._10dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(_075Float),
                    inputText = firstNameInputText,
                    onInputChange = { newInput -> event(ContactOperationsEvent.UpdateFirstNameTextField(newInput)) },
                    isError = firstNameValidationStatus is ValidationStatus.Error,
                    label = stringResource(id = R.string.firstNameLabel),
                    supportingText = if (firstNameValidationStatus is ValidationStatus.Error) firstNameValidationStatus.errorMessage else null,
                    onTrailingIconClick = { event(ContactOperationsEvent.ClearFirstNameTextField) },
                    onButtonClick = { lastNameFocusRequester.requestFocus() }
                )
                if (deleteIconsVisibility.isDeleteContactInfoLastNameIconVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth(_075Float),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextField(
                            modifier = Modifier.weight(_1Float),
                            inputText = lastNameInputText ?: "",
                            onInputChange = { newInput -> event(ContactOperationsEvent.UpdateLastNameTextField(newInput)) },
                            isError = lastNameValidationStatus is ValidationStatus.Error,
                            label = stringResource(id = R.string.lastNameLabel),
                            supportingText = if (lastNameValidationStatus is ValidationStatus.Error) lastNameValidationStatus.errorMessage else null,
                            focusRequester = lastNameFocusRequester,
                            onTrailingIconClick = { event(ContactOperationsEvent.ClearLastNameTextField) },
                            onButtonClick = { phoneNumberFocusRequester.requestFocus() }
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen._10dp)))
                        DeletePhotoOrLastNameIcon(onIconClick = { event(ContactOperationsEvent.DeleteContactInfoLastName) })
                    }
                } else {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(_075Float),
                        inputText = lastNameInputText ?: "",
                        onInputChange = { newInput -> event(ContactOperationsEvent.UpdateLastNameTextField(newInput)) },
                        isError = lastNameValidationStatus is ValidationStatus.Error,
                        label = stringResource(id = R.string.lastNameLabel),
                        supportingText = if (lastNameValidationStatus is ValidationStatus.Error) lastNameValidationStatus.errorMessage else null,
                        focusRequester = lastNameFocusRequester,
                        onTrailingIconClick = { event(ContactOperationsEvent.ClearLastNameTextField) },
                        onButtonClick = { phoneNumberFocusRequester.requestFocus() }
                    )
                }
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(_075Float),
                    inputText = phoneNumberInputText,
                    onInputChange = { newInput -> event(ContactOperationsEvent.UpdatePhoneNumberTextField(newInput)) },
                    isError = phoneNumberValidationStatus is ValidationStatus.Error,
                    label = stringResource(id = R.string.phoneNumberLabel),
                    supportingText = if (phoneNumberValidationStatus is ValidationStatus.Error) phoneNumberValidationStatus.errorMessage else null,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                    focusRequester = phoneNumberFocusRequester,
                    onTrailingIconClick = { event(ContactOperationsEvent.ClearPhoneNumberTextField) },
                    onButtonClick = {
                        focusManager.clearFocus()
                        softwareKeyboardController?.hide()
                    }
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen._10dp)))
            ContactInfoButtonOperations(
                modifier = Modifier
                    .fillMaxWidth(_075Float),
                onButtonClick = { event(ContactOperationsEvent.UpdateOrInsertContactInfo) },
                text = contactOperationsButton.buttonText,
                supportingText = contactOperationsButton.supportingText,
                isEnabled = contactOperationsButton.isEnabled
            )
        }
    }
}