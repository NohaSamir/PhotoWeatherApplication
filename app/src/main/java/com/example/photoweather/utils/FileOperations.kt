package com.example.photoweather.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.photoweather.MyApp
import java.io.*
import java.util.*

object FileOperations {

    private val TAG = FileOperations::class.simpleName

    fun delete(path: String): Boolean {
        val file = File(path)
        Log.d(TAG, file.absolutePath)

        return if (file.isFile) {
            file.delete()
        } else
            false
    }

    fun getAppFolder(context: Context): File {
        val storageDirectory =
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val appFolder =
            File(storageDirectory, FileConstants.PHOTOS_FILE_PATH)

        Log.d(TAG, appFolder.absolutePath)
        if (!appFolder.exists()) {
            val wasCreated = appFolder.mkdirs()
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory")
            }
        }
        return appFolder
    }

    fun replaceOriginalBitmapWithGeneratedBitmap(
        photoPath: String,
        capturedImageBitmap: Bitmap
    ): String? {
        val newBitmap = capturedImageBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val newModifiedCapturedImage = File(photoPath)
        if (!newModifiedCapturedImage.exists()) {
            try {
                val wasCreated = newModifiedCapturedImage.createNewFile()
                if (!wasCreated) {
                    Log.e(TAG, "Failed to create directory")
                    return null
                }
            } catch (e: IOException) {
                Log.e(TAG, e.localizedMessage)
                return null
            }
        } else {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(newModifiedCapturedImage)
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            } catch (e: FileNotFoundException) {
                Log.e(TAG, e.localizedMessage)
                return null
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.fd.sync()
                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(TAG, e.localizedMessage)
                    }
                }
            }
        }
        return newModifiedCapturedImage.absolutePath
    }

    fun insertBitmapInAppFolder(bitmap: Bitmap): String {
        val title = UUID.randomUUID().toString()
        val tempFile = File.createTempFile(title, ".jpg", getAppFolder(MyApp.application))
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData: ByteArray = bytes.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(tempFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return tempFile.absolutePath
    }

    fun getBitmapFromUri(context: Context, photoUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                photoUri
            )
        }
    }

}