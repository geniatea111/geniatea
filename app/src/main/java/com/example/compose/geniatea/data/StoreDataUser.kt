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

class StoreDataUser() {

    // to make sure there's only one instance
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")
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
    }

     suspend fun saveUser(context: Context, user: User) {
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
            preferences[IS_LOGGED_IN] = true
        }
    }

    suspend fun refreshTokens(context: Context, accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = accessToken
            preferences[USER_TOKEN_REFRESH] = refreshToken
        }
    }

    suspend fun updateUser(context: Context, user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_USERNAME_KEY] = user.username
            preferences[USER_BIRTHDATE_KEY] = user.birthdate
            preferences[USER_GENDER_KEY] = user.gender
            preferences[USER_ROL_KEY] = user.rol
        }
    }

    suspend fun getName(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_NAME_KEY]
    }

    suspend fun getId(context: Context): Long? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    suspend fun getToken(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_TOKEN_KEY]
    }

    suspend fun getRefreshToken(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_TOKEN_REFRESH]
    }

    suspend fun getUsername(context: Context): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_USERNAME_KEY]
    }


    suspend fun getUser(context: Context): User? {
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
            )
        } else {
            null
        }
    }

    suspend fun logoutUser(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = ""
            preferences[USER_ID_KEY] = 0L
            preferences[USER_TOKEN_KEY] = ""
            preferences[USER_TOKEN_REFRESH] = ""
            preferences[USER_NAME_KEY] = ""
            preferences[USER_USERNAME_KEY] = ""
            preferences[USER_BIRTHDATE_KEY] = ""
            preferences[USER_GENDER_KEY] = ""
            preferences[USER_ROL_KEY] = ""
            preferences[IS_LOGGED_IN] = false
        }
    }

    suspend fun getDarkMode(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }.first()
    }

    suspend fun getThemeVariant(context: Context): AppColorVariant {
        return context.dataStore.data
            .map { preferences ->
                val value = preferences[THEME_VARIANT_KEY] ?: AppColorVariant.BLUE.name
                AppColorVariant.valueOf(value)
            }
            .first()
    }

    suspend fun setThemeVariant(context: Context, variant: AppColorVariant){
        context.dataStore.edit { preferences ->
            preferences[THEME_VARIANT_KEY] = variant.name
        }
    }

    suspend fun saveDarkMode(context: Context, isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    suspend fun getAnimationsEnabled(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[ANIMATIONS_ENABLED_KEY] ?: true
        }.first()
    }

    suspend fun saveAnimationsEnabled(context: Context, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANIMATIONS_ENABLED_KEY] = isEnabled
        }
    }

    suspend fun getPictogramsEnabled(context: Context): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences[PICTOGRAMS_ENABLED_KEY] ?: true
        }.first()
    }

    suspend fun savePictogramsEnabled(context: Context, isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PICTOGRAMS_ENABLED_KEY] = isEnabled
        }
    }




}