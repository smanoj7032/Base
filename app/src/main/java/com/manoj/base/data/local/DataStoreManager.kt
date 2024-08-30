package com.manoj.base.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) {
    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val LOGIN_USING_KEY = stringPreferencesKey("login_using")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val EMAIL_AT_FORGOT_KEY = stringPreferencesKey("email_at_forgot_password")
        private val USER_KEY = stringPreferencesKey("user")
    }

    /**User Name*/
    suspend fun saveUserName(name: String?) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name.orEmpty()
        }
    }

    val userNameFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY]
    }

    /**Access Token*/
    suspend fun saveAccessToken(token: String?) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token.orEmpty()
        }
    }

    val accessTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    /**Login Platform*/
    suspend fun saveLoginPlatform(loginUsing: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_USING_KEY] = loginUsing
        }
    }

    val loginPlatformFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LOGIN_USING_KEY]
    }

    /**Save User*/
    suspend fun <T> saveUser(bean: T) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = gson.toJson(bean)
        }
    }

    fun <T> getCurrentUser(clazz: Class<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            val json = preferences[USER_KEY]
            runCatching {
                gson.fromJson(json, clazz)
            }.getOrNull()
        }
    }

    /**User Email at Login*/
    suspend fun saveUserEmailAtLogin(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    val userEmailAtLoginFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY]
    }

    /**Email at Forgot Password*/
    suspend fun saveEmailAtForgotPassword(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_AT_FORGOT_KEY] = email
        }
    }

    val emailAtForgotPasswordFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMAIL_AT_FORGOT_KEY]
    }

    /**Clear User Data*/
    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
 