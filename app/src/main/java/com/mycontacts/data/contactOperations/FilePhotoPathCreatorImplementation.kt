package com.mycontacts.data.contactOperations

import android.content.Context
import androidx.core.content.FileProvider
import com.mycontacts.domain.contactOperations.FilePhotoPathCreatorInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FilePhotoPathCreatorImplementation @Inject constructor(private val context: Context): FilePhotoPathCreatorInterface {

    override suspend fun createFilePhotoPath(): String {
        return withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val photoName = "picture_$currentTime.png"
            val photoFile = File(context.filesDir, photoName)
            val photoFileUri = FileProvider.getUriForFile(context, "com.mycontacts.FileProvider", photoFile)
            photoFileUri.toString()
        }
    }
}