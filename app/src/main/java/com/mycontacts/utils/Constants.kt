package com.mycontacts.utils

import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey

object Constants {

    const val zero = 0
    const val second = 2
    const val third = 3
    const val pagerImageSmallSizeInPixels = 512

    const val _500 = 500
    const val _1000 = 1000

    const val _300L = 300L
    const val _500L = 500L

    const val _06Float = 0.6f
    const val _04Float = 0.4f
    const val _05Float = 0.5f
    const val _075Float = 0.75f
    const val _085Float = 0.85f
    const val _1Float = 1f

    const val _180f = 180f
    const val _0f = 0f

    val _15sp = 15.sp
    val _16sp = 16.sp
    val _17sp = 17.sp
    val _18sp = 18.sp
    val _20sp = 20.sp
    val _22sp = 22.sp
    val _24sp = 24.sp

    const val dataStoreName = "dataStore"
    private const val hasUserAlreadyClickedOnStartButtonName = "hasUserAlreadyClickedOnStartButton"
    const val onDismissButtonClicked = "You have denied permission to access all files.\nPlease restart the application and accept this permission"
    const val dismissSnackbarActionLabel = "Understood..."
    const val emptyContactsErrorMessage = "Nothing to show..."
    const val contactsNotFound = "Sorry, but there are no such contacts"
    const val writeContactsPermissionGranted = "Permission granted, you can now edit/delete contacts"
    const val writeContactsPermissionNotGranted = "Permission not granted, repeat the action again"
    const val deleteContactSuccessful = "The contact was successfully deleted"
    const val deleteContactNotSuccessful = "Sorry, an unknown error occurred"
    const val deleteSelectedContactsSuccessful = "The selected contacts were successfully deleted"
    const val deleteContactUndo = "Undo"
    const val dialPart = "tel"

    const val datePattern = "dd:MM:yyyy HH:mm:ss"

    const val contactInfoKey = "ContactInfoKey"

    const val firstNameIsEmpty = "Firstname cannot be empty"
    const val firstNameLessThanMinLength = "Firstname cannot contains less than 2 letters"
    const val firstNameContainsSpaces = "Firstname cannot contains spaces"
    const val firstNameStartsWithALowerCaseLetter = "Firstname cannot starts with a lowercase letter"
    const val firstNameContainsDigits = "Firstname cannot contains digits"
    const val firstNameContainsSymbols = "Firstname cannot contains symbols"
    const val firstNameContainsMoreThanOneCapitalLetter = "Firstname cannot contains more than one capital letter"

    const val lastNameIsEmpty = "Lastname cannot be empty"
    const val lastNameLessThanMinLength = "Lastname cannot contains less than 2 letters"
    const val lastNameContainsSpaces = "Lastname cannot contains spaces"
    const val lastNameStartsWithALowerCaseLetter = "Lastname cannot starts with a lowercase letter"
    const val lastNameContainsDigits = "Lastname cannot contains digits"
    const val lastNameContainsSymbols = "Lastname cannot contains symbols"
    const val lastNameContainsMoreThanOneCapitalLetter = "Lastname cannot contains more than one capital letter"

    const val phoneNumberIsEmpty = "Phone number cannot be empty"
    const val phoneNumberIsWrongFormat = "Phone number must comply with the international format"

    const val wrongInput = "Please fill in the fields with correct input"
    const val theSameInput = "Please change at least one input field"

    const val successfulAddingContactMessage = "Contact was added successfully"
    const val unsuccessfulAddingContactMessage = "An unknown error occurred while adding the contact"

    const val successfulUpdatingContactMessage = "Contact was updated successfully"
    const val unsuccessfulUpdatingContactMessage = "An unknown error occurred while updating the contact"

    const val successfulPhotoDeletion = "The photo was deleted successful"
    const val unsuccessfulPhotoDeletion = "An unknown error occurred while deleting the photo"

    const val successfulLastNameDeletion = "Last name was deleted successful"
    const val unsuccessfulLastNameDeletion = "An unknown error occurred while deleting last name"

    const val updateContactButtonText = "Update contact"
    const val addContactButtonText = "Add contact"

    val hasUserAlreadyClickedOnStartButton = booleanPreferencesKey(hasUserAlreadyClickedOnStartButtonName)
}