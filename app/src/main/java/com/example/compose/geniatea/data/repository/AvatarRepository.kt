package com.example.compose.geniatea.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AvatarRepository(private val context: Context) {

    private val avatarFileName = "user_avatar.jpg"

    suspend fun saveAvatar(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, avatarFileName)
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                stream.flush()
                stream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getAvatar(): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, avatarFileName)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    suspend fun deleteAvatar() {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, avatarFileName)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val avatarVideoFileName = "user_avatar_video.mp4"

    suspend fun saveAvatarVideo(uri: android.net.Uri) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, avatarVideoFileName)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getAvatarVideo(): android.net.Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, avatarVideoFileName)
                if (file.exists()) {
                    android.net.Uri.fromFile(file)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteAvatarVideo() {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, avatarVideoFileName)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
