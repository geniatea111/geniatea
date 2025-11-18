package com.example.compose.geniatea.data.backendConection

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    data class LoginGoogleRequest(
        val token: String? = null
    )

    data class RefreshTokenRequest(
        val refreshToken: String
    )

    data class LoginRequest(
        val identifier: String? = null,
        val password: String
    )

    data class RegisterRequest(
      //  val name: String,
        val username: String,
        val email: String,
        //val birthdate: String,
        //val gender: String,
        val password: String,
        val roles: List<String>
    )

    data class UpdateUserRequest(
        val name: String? = null,
        val birthdate: String? = null,
        val gender: String? = null,
    )

    data class UpdatePasswordRequest(
        val currentPassword: String,
        val newPassword: String
    )

    data class MessageRequest(
        val userId: String,
        val message: String,
        val image: String? = null // Base64 encoded image string (optional)
    )

    data class EmailCheckRequest(
        val email: String
    )

    /************************************************************/

    data class LoginResponse(
        val id: Long,
        val accessToken: String,
        val refreshToken: String,
        val name: String
    )

    data class GetUserResponse(
        val id: Long,
        val name: String,
        val username: String,
        val email: String,
        val birthdate: String,
        val gender : String,
        val roles: List<String>
    )

    data class RegisterResponse(
        val id: Long,
        val accessToken: String,
        val refreshToken : String,
        val name: String,
    )

    data class MessageResponse(
        val messageId : Long,
        val message: String,
        val createdAt : String,
    )

    data class ChatHistoryResponse(
        val messageId : Long,
        val message: String,
        val createdAt : String,
        val sender: String, // "user" or "assistant"
        val image: String? = null // Base64 encoded image string (optional)
    )

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/google")
    suspend fun loginGoogle(
        @Body loginRequest: LoginGoogleRequest
    ): Response<LoginResponse>

    //with variable id
    @POST("users")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<RegisterResponse>

    //emailCheck TODO
    @POST("auth/email-check")
    suspend fun emailCheck(
        @Body emailCheckRequest: EmailCheckRequest
    ): Response<RegisterResponse>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: String,
        @Body updaterRequest: UpdateUserRequest
    ): Response<ResponseBody>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Long
    ): Response<GetUserResponse>

    @PUT("users/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body updatePasswordRequest: UpdatePasswordRequest
    ): Response<ResponseBody>

    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ResponseBody>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<LoginResponse>


    @POST("chat/request")
    suspend fun requestChat(
        @Body message: MessageRequest
    ): Response<MessageResponse>

    @GET("chat/history")
    suspend fun getChatHistory(
        @Header("Authorization") token: String,
    ): Response<List<ChatHistoryResponse>>

}