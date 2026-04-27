package com.example.compose.geniatea.notifications

import android.content.Context
import android.util.Log
import com.example.compose.geniatea.data.backendConection.ApiService
import com.example.compose.geniatea.data.backendConection.BackendAPI
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages the FCM registration token lifecycle:
 * - [refreshAndSendToken] should be called on app startup / after login.
 * - [sendTokenToBackend] is called automatically on token refresh (from GenIATEAMessagingService).
 */
object FcmTokenManager {

    private const val TAG = "FcmTokenManager"

    /**
     * Retrieves the current FCM token from Firebase and sends it to the backend.
     * Call this once after the user logs in.
     */
    fun refreshAndSendToken(context: Context) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "FCM token retrieved")
                sendTokenToBackend(context, token)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get FCM token", e)
            }
    }

    /**
     * Sends the given FCM token to the backend POST /api/users/fcm-token.
     * Runs on the IO dispatcher so it's safe to call from any context.
     */
    fun sendTokenToBackend(context: Context, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = BackendAPI.retrofitService.updateFcmToken(
                    ApiService.FcmTokenRequest(token)
                )
                if (response.isSuccessful) {
                    Log.d(TAG, "FCM token sent to backend successfully")
                } else {
                    Log.w(TAG, "Backend rejected FCM token update: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while sending FCM token to backend", e)
            }
        }
    }
}
