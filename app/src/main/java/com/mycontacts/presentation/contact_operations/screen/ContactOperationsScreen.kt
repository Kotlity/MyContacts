package com.mycontacts.presentation.contact_operations.screen

import android.Manifest
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mycontacts.R
import com.mycontacts.presentation.contact_operations.composables.ContactImageActionModalBottomSheet
import com.mycontacts.presentation.contact_operations.composables.ContactInfoButtonOperations
import com.mycontacts.presentation.contact_operations.composables.ContactInfoPhoto
import com.mycontacts.presentation.contact_operations.composables.CustomAlertDialog
import com.mycontacts.presentation.contact_operations.composables.CustomTextField
import com.mycontacts.presentation.contact_operations.composables.DeletePhotoOrLastNameIcon
import com.mycontacts.presentation.contact_operations.events.ContactOperationsEvent
import com.mycontacts.presentation.contact_operations.viewmodels.ContactOperationsViewModel
import com.mycontacts.utils.Constants._05Float
import com.mycontacts.utils.Constants._075Float
import com.mycontacts.utils.Constants._085Float
import com.mycontacts.utils.Constants._16sp
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._500
import com.mycontacts.utils.isAppHasPermission
import com.mycontacts.utils.stringToUri
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

    val filePhotoPath = contactOperationsViewModel.filePhotoPath

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val modalBottomSheetState = rememberModalBottomSheetState()

    val coroutineScope = rememberCoroutineScope()

    val scaffoldScrollState = rememberScrollState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccessful ->
        if (isSuccessful) {
            try {
                val filePhotoPathUri = filePhotoPath!!.stringToUri()
                filePhotoPathUri.uriToBitmap(contentResolver)?.let { bitmap ->
                    event(ContactOperationsEvent.UpdatePhoto(bitmap))
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            event(ContactOperationsEvent.UpdateFilePhotoPath)
            event(ContactOperationsEvent.UpdateCameraPermissionResult(permissionResult = context.getString(R.string.cameraPermissionIsGranted)))
            cameraLauncher.launch(filePhotoPath!!.stringToUri())
        } else event(ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { photoUri ->
        photoUri?.let {
            it.uriToBitmap(contentResolver)?.let { bitmap ->
                event(ContactOperationsEvent.UpdatePhoto(bitmap))
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

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
        cameraPermissionResultFlow,
        lifecycleOwner.lifecycle
    ) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                    if (permissionResultMessage == context.getString(R.string.cameraPermissionIsGranted)) snackbarHostState.showSnackbar(message = permissionResultMessage)
                    else snackbarHostState.showSnackbar(message = permissionResultMessage, duration = SnackbarDuration.Long)
                }
            }
        }
    }

    if (isModalBottomSheetActive) {
        ContactImageActionModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismiss = { event(ContactOperationsEvent.UpdateModalBottomSheetActiveState) },
            onTakeAPhoto = {
                if (isAppHasPermission(context, Manifest.permission.CAMERA)) {
                    event(ContactOperationsEvent.UpdateFilePhotoPath)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        event(ContactOperationsEvent.UpdateModalBottomSheetActiveState)
                    }
                    cameraLauncher.launch(filePhotoPath!!.stringToUri())
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        event(ContactOperationsEvent.UpdateModalBottomSheetActiveState)
                    }
                }
            },
            onSelectAPhoto = {
                if (isPhotoPickerAvailable(context)) {
                    photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
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
            onDismissButtonClick = { event(ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState) }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scaffoldScrollState),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen._20dp)),
                contentAlignment = Alignment.TopCenter
            ) {
                AnimatedContent(
                    targetState = photoBitmap,
                    transitionSpec = { fadeIn(animationSpec = tween(durationMillis = _500)) togetherWith fadeOut(animationSpec = tween(durationMillis = _500)) }
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
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._10dp))
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(_075Float),
                    inputText = firstNameInputText,
                    onInputChange = { newInput -> event(ContactOperationsEvent.UpdateFirstNameTextField(newInput)) },
                    isError = firstNameValidationStatus is ValidationStatus.Error,
                    label = stringResource(id = R.string.firstNameLabel),
                    supportingText = if (firstNameValidationStatus is ValidationStatus.Error) firstNameValidationStatus.errorMessage else "",
                    onTrailingIconClick = { event(ContactOperationsEvent.ClearFirstNameTextField) },
                    onButtonClick = { lastNameFocusRequester.requestFocus() }
                )
                if (deleteIconsVisibility.isDeleteContactInfoLastNameIconVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth(_085Float),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(_075Float),
                            inputText = lastNameInputText ?: "",
                            onInputChange = { newInput -> event(ContactOperationsEvent.UpdateLastNameTextField(newInput)) },
                            isError = lastNameValidationStatus is ValidationStatus.Error,
                            label = stringResource(id = R.string.lastNameLabel),
                            supportingText = if (lastNameValidationStatus is ValidationStatus.Error) lastNameValidationStatus.errorMessage else "",
                            focusRequester = lastNameFocusRequester,
                            onTrailingIconClick = { event(ContactOperationsEvent.ClearLastNameTextField) },
                            onShowKeyboard = { softwareKeyboardController?.show() },
                            onButtonClick = { phoneNumberFocusRequester.requestFocus() }
                        )
//                        Spacer(modifier = Modifier.weight(_1Float))
                        DeletePhotoOrLastNameIcon(onIconClick = { event(ContactOperationsEvent.DeleteContactInfoLastName) })
                    }
                } else {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(_075Float),
                        inputText = lastNameInputText ?: "",
                        onInputChange = { newInput -> event(ContactOperationsEvent.UpdateLastNameTextField(newInput)) },
                        isError = lastNameValidationStatus is ValidationStatus.Error,
                        label = stringResource(id = R.string.lastNameLabel),
                        supportingText = if (lastNameValidationStatus is ValidationStatus.Error) lastNameValidationStatus.errorMessage else "",
                        focusRequester = lastNameFocusRequester,
                        onTrailingIconClick = { event(ContactOperationsEvent.ClearLastNameTextField) },
                        onShowKeyboard = { softwareKeyboardController?.show() },
                        onButtonClick = { phoneNumberFocusRequester.requestFocus() }
                    )
                }
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(_075Float),
                    inputText = phoneNumberInputText,
                    onInputChange = { newInput -> event(ContactOperationsEvent.UpdatePhoneNumberTextField(newInput)) },
                    isError = phoneNumberValidationStatus is ValidationStatus.Error,
                    label = stringResource(id = R.string.phoneNumberLabel),
                    supportingText = if (phoneNumberValidationStatus is ValidationStatus.Error) phoneNumberValidationStatus.errorMessage else "",
                    focusRequester = phoneNumberFocusRequester,
                    onTrailingIconClick = { event(ContactOperationsEvent.ClearPhoneNumberTextField) },
                    onShowKeyboard = { softwareKeyboardController?.show() },
                    onButtonClick = {
                        focusManager.clearFocus()
                        softwareKeyboardController?.hide()
                    }
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen._10dp)))
            ContactInfoButtonOperations(
                modifier = Modifier
                    .fillMaxWidth(_05Float),
                onButtonClick = { event(ContactOperationsEvent.UpdateOrInsertContactInfo) },
                text = contactOperationsButton.buttonText,
                isEnabled = contactOperationsButton.isEnabled
            )
        }
    }
}