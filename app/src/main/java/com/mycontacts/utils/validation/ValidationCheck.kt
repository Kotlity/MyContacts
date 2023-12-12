package com.mycontacts.utils.validation

import android.telephony.PhoneNumberUtils
import com.mycontacts.utils.Constants.firstNameContainsDigits
import com.mycontacts.utils.Constants.firstNameContainsMoreThanOneCapitalLetter
import com.mycontacts.utils.Constants.firstNameContainsSpaces
import com.mycontacts.utils.Constants.firstNameContainsSymbols
import com.mycontacts.utils.Constants.firstNameStartsWithALowerCaseLetter
import com.mycontacts.utils.Constants.firstNameIsEmpty
import com.mycontacts.utils.Constants.firstNameLessThanMinLength
import com.mycontacts.utils.Constants.lastNameContainsDigits
import com.mycontacts.utils.Constants.lastNameContainsMoreThanOneCapitalLetter
import com.mycontacts.utils.Constants.lastNameContainsSpaces
import com.mycontacts.utils.Constants.lastNameContainsSymbols
import com.mycontacts.utils.Constants.lastNameStartsWithALowerCaseLetter
import com.mycontacts.utils.Constants.lastNameLessThanMinLength
import com.mycontacts.utils.Constants.phoneNumberIsEmpty
import com.mycontacts.utils.Constants.phoneNumberIsWrongFormat
import com.mycontacts.utils.Constants.phoneNumberIsWrongLength

private val digitsRegex = Regex("[0-9]")
private val symbolsRegex = Regex("[!@\\\$%^&*(),?\\\":{}|<>]")
private const val minLength = 2
private const val maxUpperCases = 1
private const val phoneNumberLength = 12
private const val phoneNumberPrefix = "380"

fun String.firstNameValidation(): ValidationStatus {
    var uppercaseCount = 0
    var index = 0

    while (index < length) {
        if (index != 0 && this[index].isUpperCase()) {
            uppercaseCount++
            break
        }
        index++
    }

    return if (isEmpty()) ValidationStatus.Error(firstNameIsEmpty)
        else if (length < minLength) ValidationStatus.Error(firstNameLessThanMinLength)
        else if (contains(" ")) ValidationStatus.Error(firstNameContainsSpaces)
        else if (first().isLowerCase()) ValidationStatus.Error(firstNameStartsWithALowerCaseLetter)
        else if (contains(digitsRegex)) ValidationStatus.Error(firstNameContainsDigits)
        else if (contains(symbolsRegex)) ValidationStatus.Error(firstNameContainsSymbols)
        else if (uppercaseCount >= maxUpperCases) ValidationStatus.Error(firstNameContainsMoreThanOneCapitalLetter)
        else ValidationStatus.Success
}

fun String.lastNameValidation(): ValidationStatus {
    var uppercaseCount = 0
    var index = 0

    while (index < length) {
        if (index != 0 && this[index].isUpperCase()) {
            uppercaseCount++
            break
        }
        index++
    }

    return if (length < minLength) ValidationStatus.Error(lastNameLessThanMinLength)
    else if (contains(" ")) ValidationStatus.Error(lastNameContainsSpaces)
    else if (first().isLowerCase()) ValidationStatus.Error(lastNameStartsWithALowerCaseLetter)
    else if (contains(digitsRegex)) ValidationStatus.Error(lastNameContainsDigits)
    else if (contains(symbolsRegex)) ValidationStatus.Error(lastNameContainsSymbols)
    else if (uppercaseCount >= maxUpperCases) ValidationStatus.Error(lastNameContainsMoreThanOneCapitalLetter)
    else ValidationStatus.Success
}

fun String.phoneNumberValidation(): ValidationStatus {
    return if (isEmpty()) ValidationStatus.Error(phoneNumberIsEmpty)
    else if (length != phoneNumberLength) ValidationStatus.Error(phoneNumberIsWrongLength)
    else if (!PhoneNumberUtils.isGlobalPhoneNumber(this) || !startsWith(phoneNumberPrefix)) ValidationStatus.Error(phoneNumberIsWrongFormat)
    else ValidationStatus.Success
}