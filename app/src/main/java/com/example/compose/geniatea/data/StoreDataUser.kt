package com.example.compose.geniatea.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.compose.geniatea.domain.User
import com.example.compose.geniatea.theme.AppColorVariant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")

class StoreDataUser(private val context: Context) {

    companion object {
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        val USER_ID_KEY = longPreferencesKey("user_id")
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        val USER_TOKEN_REFRESH = stringPreferencesKey("user_token_refresh")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_BIRTHDATE_KEY = stringPreferencesKey("user_birth_date")
        val USER_GENDER_KEY = stringPreferencesKey("user_gender")
        val DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ROL_KEY = stringPreferencesKey("user_rol")
        val ANIMATIONS_ENABLED_KEY = booleanPreferencesKey("animations_enabled")
        val PICTOGRAMS_ENABLED_KEY = booleanPreferencesKey("pictograms_enabled")
        val THEME_VARIANT_KEY = stringPreferencesKey("theme_variant")
        val FONT_SIZE_KEY = stringPreferencesKey("font_size")
        val RESPONSE_STYLE_KEY = stringPreferencesKey("response_style")
        val SHOW_AVATAR_KEY = booleanPreferencesKey("show_avatar")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        val CONTINUOUS_VOICE_MODE_KEY = booleanPreferencesKey("continuous_voice_mode")
        val CONTINUOUS_VOICE_KEYWORD_KEY = stringPreferencesKey("continuous_voice_keyword")
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_ID_KEY] = user.id
            preferences[USER_TOKEN_KEY] = user.accessToken
            preferences[USER_TOKEN_REFRESH] = user.refreshToken
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_USERNAME_KEY] = user.username
            preferences[USER_BIRTHDATE_KEY] = user.birthdate
            preferences[USER_GENDER_KEY] = user.gender
            preferences[USER_ROL_KEY] = user.rol
            preferences[PICTOGRAMS_ENABLED_KEY] = user.showPictograms ?: false
            preferences[ONBOARDING_COMPLETED_KEY] = user.onboardingCompleted
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun refreshTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = accessToken
            preferences[USER_TOKEN_REFRESH] = refreshToken
        }
    }

    suspend fun updateUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_USERNAME_KEY] = user.username
            preferences[USER_BIRTHDATE_KEY] = user.birthdate
            preferences[USER_GENDER_KEY] = user.gender
            preferences[USER_ROL_KEY] = user.rol
            preferences[PICTOGRAMS_ENABLED_KEY] = user.showPictograms ?: false
        }
    }

    suspend fun getName(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_NAME_KEY]
    }

    suspend fun getId(): Long? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_TOKEN_KEY]
    }

    suspend fun getRefreshToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_TOKEN_REFRESH]
    }

    suspend fun getUsername(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_USERNAME_KEY]
    }


    suspend fun getUser(): User? {
        val preferences = context.dataStore.data.first()
        return if (preferences[IS_LOGGED_IN] == true) {
            User(
                id = preferences[USER_ID_KEY] ?: 0L,
                token = preferences[USER_TOKEN_KEY] ?: "",
                refreshToken = preferences[USER_TOKEN_REFRESH] ?: "",
                email = preferences[USER_EMAIL_KEY] ?: "",
                name = preferences[USER_NAME_KEY] ?: "",
                username = preferences[USER_USERNAME_KEY] ?: "",
                birthdate = preferences[USER_BIRTHDATE_KEY] ?: "",
                gender = preferences[USER_GENDER_KEY] ?: "",
                rol = preferences[USER_ROL_KEY] ?: "",
                showPictograms = preferences[PICTOGRAMS_ENABLED_KEY] ?: false,
                onboardingCompleted = preferences[ONBOARDING_COMPLETED_KEY] ?: false
            )
        } else {
            null
        }
    }

    suspend fun logoutUser() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getDarkMode(): Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE_KEY] ?: false }

    fun getThemeVariant(): Flow<AppColorVariant> {
        return context.dataStore.data.map {
            AppColorVariant.valueOf(it[THEME_VARIANT_KEY] ?: AppColorVariant.BLUE.name)
        }
    }

    suspend fun setThemeVariant(variant: AppColorVariant) {
        context.dataStore.edit { preferences ->
            preferences[THEME_VARIANT_KEY] = variant.name
        }
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    fun getAnimationsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[ANIMATIONS_ENABLED_KEY] ?: true }

    suspend fun saveAnimationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANIMATIONS_ENABLED_KEY] = isEnabled
        }
    }

    fun getPictogramsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PICTOGRAMS_ENABLED_KEY] ?: true }

    suspend fun savePictogramsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PICTOGRAMS_ENABLED_KEY] = isEnabled
        }
    }

    // AI Settings Preferences

    fun getFontSize(): Flow<String> = context.dataStore.data.map { it[FONT_SIZE_KEY] ?: "M" }

    suspend fun saveFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }

    fun getResponseStyle(): Flow<String> = context.dataStore.data.map { it[RESPONSE_STYLE_KEY] ?: "normal" }

    suspend fun saveResponseStyle(style: String) {
        context.dataStore.edit { preferences ->
            preferences[RESPONSE_STYLE_KEY] = style
        }
    }

    fun getShowAvatar(): Flow<Boolean> = context.dataStore.data.map { it[SHOW_AVATAR_KEY] ?: false }

    suspend fun saveShowAvatar(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_AVATAR_KEY] = show
        }
    }
    
    fun getLanguage(): Flow<String?> = context.dataStore.data.map { it[LANGUAGE_KEY] }

    suspend fun saveLanguage(language: String?) {
        context.dataStore.edit { preferences ->
            if (language != null) {
                preferences[LANGUAGE_KEY] = language
            } else {
                preferences.remove(LANGUAGE_KEY)
            }
        }
    }

    val isUserLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    fun getOnboardingCompleted(): Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }

    fun getContinuousVoiceMode(): Flow<Boolean> = context.dataStore.data.map { it[CONTINUOUS_VOICE_MODE_KEY] ?: false }

    suspend fun saveContinuousVoiceMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CONTINUOUS_VOICE_MODE_KEY] = isEnabled
        }
    }

    fun getContinuousVoiceKeyword(): Flow<String> = context.dataStore.data.map { it[CONTINUOUS_VOICE_KEYWORD_KEY] ?: "Genia" }

    suspend fun saveContinuousVoiceKeyword(keyword: String) {
        context.dataStore.edit { preferences ->
            preferences[CONTINUOUS_VOICE_KEYWORD_KEY] = keyword
        }
    }
}