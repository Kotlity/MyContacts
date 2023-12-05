package com.mycontacts.domain.contactOperations

interface FilePhotoPathCreatorInterface {

    suspend fun createFilePhotoPath(): String
}