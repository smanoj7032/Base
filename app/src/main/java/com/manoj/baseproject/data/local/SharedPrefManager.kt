package com.manoj.baseproject.data.local

import android.content.SharedPreferences

import com.google.gson.Gson
import com.manoj.baseproject.core.utils.getValue
import com.manoj.baseproject.core.utils.saveValue
import javax.inject.Inject

open class SharedPrefManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()


    companion object {
        const val USER = "user"
        const val ACCESS_TOKEN = "access_token"
        const val USER_EMAIL = "user_email"
        const val EMAIL_AT_FORGOT = "email_at_forgot_password"
        const val LOGIN_USING = "login_using"
        const val USER_NAME = "user_name"
        const val FILE_PATH = "file_path"
    }

    fun saveUserName(name: String?) {
        sharedPreferences.saveValue(USER_NAME, name)
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME, "").toString()
    }

    fun saveAccessToken(token: String?) {
        sharedPreferences.saveValue(ACCESS_TOKEN, token)
    }

    fun getAccessToken(): String {
        return sharedPreferences.getValue<String>(ACCESS_TOKEN, null).toString()
    }


    fun saveLoginPlatform(loginUsing: String) {
        sharedPreferences.saveValue(LOGIN_USING, loginUsing)
    }

    fun getLoginPlatform(): String {
        return sharedPreferences.getValue<String>(LOGIN_USING, null).toString()
    }

    fun <T> saveUser(bean: T) {
        sharedPreferences.saveValue(USER, gson.toJson(bean))
    }

    fun <T> getCurrentUser(): T? {
        return sharedPreferences.getValue(USER)
    }

    fun saveUserEmailAtLogin(email: String) {
        sharedPreferences.saveValue(USER_EMAIL, email)
    }

    fun getCurrentUserEmail(): String {

        return sharedPreferences.getValue<String?>(USER_EMAIL, null).toString()
    }

    fun saveEmailAtForgotPassword(email: String) {
        sharedPreferences.saveValue(EMAIL_AT_FORGOT, email)

    }

    fun getEmailAtForgotPassword(): String {
        return sharedPreferences.getValue<String>(EMAIL_AT_FORGOT, null).toString()
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }

}


