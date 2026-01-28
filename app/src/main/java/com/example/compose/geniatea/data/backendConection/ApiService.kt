package com.example.compose.geniatea.data.backendConection

import com.example.compose.geniatea.presentation.funcionalidades.tasklist.TaskDTO
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Multipart
import retrofit2.http.Part

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
        val username: String,
        val email: String,
        val password: String
    )

    data class UpdateUserRequest(
        val name: String,
        val gender: String,
        val birthdate: String,
        val roles: Set<String>? = null,
        val showPictograms: Boolean? = null
    )

    data class UpdatePasswordRequest(
        val currentPassword: String,
        val newPassword: String
    )

    data class MessageRequest(
        val userId: String,
        val message: String,
        val image: String? = null, // Base64 encoded image string (optional)
        val style: String? = null
    )

    data class EmailCheckRequest(
        val email: String
    )

    data class CreateTaskRequest(
        val title: String,
        val time: String,
        val date: String,
        val note: String,
        val parentId: String?,
        val subtasks: List<CreateTaskRequest>
    )

    data class UpdateTaskRequest(
        val isCompleted: Boolean
    )

    data class GenerateSubtaskRequest(
        val task: String
    )

    data class AnalyzeIntentRequest(
        val text: String
    )

    data class RewriteRequest(
        val text: String,
        val style: String
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

    data class ChatSessionResponse(
        val sessionId: Long,
        val createdAt: String,
        val topic: String
    )

    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/google")
    suspend fun loginGoogle(
        @Body loginRequest: LoginGoogleRequest
    ): Response<LoginResponse>

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

    @POST("users/{id}/avatar")
    @Multipart
    suspend fun updateAvatar(
        @Header("Authorization") token: String,
        @Path("id") userId: Long,
        @Part avatar: okhttp3.MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("users/{id}/avatar")
    suspend fun getAvatar(
        @Header("Authorization") token: String,
        @Path("id") userId: Long
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

    @GET("chat/sessions")
    suspend fun getChatSessions(
        @Header("Authorization") token: String
    ): Response<List<ChatSessionResponse>>

    @POST("chat/generate-subtask")
    suspend fun generateSubtask(
        @Body request: GenerateSubtaskRequest
    ): Response<ResponseBody>

    @POST("chat/analyze-intent")
    suspend fun analyzeIntent(
        @Body request: AnalyzeIntentRequest
    ): Response<ResponseBody>

    @POST("chat/rewrite")
    suspend fun rewrite(
        @Body request: RewriteRequest
    ): Response<ResponseBody>

    @GET("chat/sessions/{sessionId}")
    suspend fun getChatSession(
        @Header("Authorization") token: String,
        @Path("sessionId") sessionId: Long
    ): Response<List<ChatHistoryResponse>>

    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskDTO>>

    @GET("tasks/pending")
    suspend fun getPendingTasks(): Response<List<TaskDTO>>

    @POST("tasks")
    suspend fun createTask(@Body task: CreateTaskRequest): Response<ResponseBody>

    @PUT("tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body updateTaskRequest: UpdateTaskRequest
    ): Response<ResponseBody>

    data class UserPreferenceDTO(
        val showPictograms: Boolean?,
        val language: String?,
        val showAvatar: Boolean?,
        val clearLanguage: Boolean?,
        val responseStyle: String?,
        val fontSize: String?
    )

    @GET("preferences")
    suspend fun getUserPreferences(
        @Header("Authorization") token: String
    ): Response<UserPreferenceDTO>

    @POST("preferences")
    suspend fun updateUserPreferences(
        @Header("Authorization") token: String,
        @Body preferences: UserPreferenceDTO
    ): Response<ResponseBody>

}
