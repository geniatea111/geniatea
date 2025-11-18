package com.example.compose.geniatea.data.backendConection

import android.content.Context
import android.util.Log
import com.example.compose.geniatea.data.backendConection.ApiService.LoginResponse
import com.example.compose.geniatea.data.StoreDataUser
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(
    private val context: Context,
    private val store: StoreDataUser
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Add access token
        val token = runBlocking { store.getToken(context) }
        if (!token.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.w("AuthInterceptor", "Access token expired → trying refresh...")

            response.close()

            val refreshToken = runBlocking { store.getRefreshToken(context) }
            Log.w("AuthInterceptor", "Stored refreshToken = $refreshToken")

            if (!refreshToken.isNullOrBlank()) {
                val refreshResponse = refreshTokenRequest(refreshToken)

                Log.w("AuthInterceptor", "Refresh call executed → success=${refreshResponse?.isSuccessful}, code=${refreshResponse?.code()}")

                if (refreshResponse != null && refreshResponse.isSuccessful) {
                    val tokenResponse = refreshResponse.body()
                    if (tokenResponse != null) {
                        val newAccessToken = tokenResponse.accessToken
                        val newRefreshToken = tokenResponse.refreshToken
                        Log.w("AuthInterceptor", "New tokens received ✅")

                        runBlocking {
                            store.refreshTokens(context, newAccessToken, newRefreshToken)
                        }

                        val newRequest = request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()

                        Log.w("AuthInterceptor", "Retrying original request with new token")
                        return chain.proceed(newRequest)
                    } else {
                        Log.e("AuthInterceptor", "Refresh response body is null ❌")
                    }
                } else {
                    Log.e("AuthInterceptor", "Refresh failed → ${refreshResponse?.errorBody()?.string()}")
                }
            } else {
                Log.e("AuthInterceptor", "No refresh token available ❌")
            }

            // If refresh fails → return explicit 401
            return Response.Builder()
                .request(request)
                .protocol(response.protocol)
                .code(401)
                .message("Unauthorized")
                .body("".toResponseBody(null))
                .build()
        }

        return response
    }

/* ESTE METODO TAMBIEN FUNCIONA PERO NO DA TANTA INFO EN LOGS
    override fun interceptOLD(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Add access token
        val token = runBlocking { store.getToken(context) }
        if (!token.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)

        // If unauthorized → try refresh
        if (response.code == 401) {
            response.close()

            val refreshToken = runBlocking { store.getRefreshToken(context) }
            if (!refreshToken.isNullOrBlank()) {
                val refreshResponse = refreshTokenRequest(refreshToken)

                if (refreshResponse?.isSuccessful == true) {
                    val tokenResponse = refreshResponse.body()
                    val newAccessToken = tokenResponse?.accessToken.orEmpty()
                    val newRefreshToken = tokenResponse?.refreshToken.orEmpty()

                    runBlocking {
                        store.refreshTokens(context, newAccessToken, newRefreshToken)
                    }

                    // Retry with new token
                    val newRequest = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    return chain.proceed(newRequest) // ✅ only return this
                }else{
                    Log.e("AuthInterceptor", "Error refreshing token: ${refreshResponse?.errorBody()?.string()}")
                }
            }

            // important: if refresh fails, return a new response not the closed one
            return response.newBuilder()
                .body("".toResponseBody(null))
                .build()
        }

        return response // original response, untouched
    }
*/

    private fun refreshTokenRequest(refreshToken: String): retrofit2.Response<LoginResponse>? {
        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BackendAPI.BASE_URL_LOCAL)
            .addConverterFactory(GsonConverterFactory.create(BackendAPI.gson))
            .client(client)
            .build()

        val service = retrofit.create(ApiService::class.java)

        return runBlocking {
            try {
                service.refreshToken(ApiService.RefreshTokenRequest(refreshToken))
            } catch (e: Exception) {
                null
            }
        }
    }

}