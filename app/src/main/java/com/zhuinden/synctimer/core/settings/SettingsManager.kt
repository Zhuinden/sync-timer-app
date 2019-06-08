package com.zhuinden.synctimer.core.settings

import android.content.SharedPreferences

class SettingsManager(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val USERNAME = "USERNAME"
    }

    fun getUsername(): String? = sharedPreferences.getString(USERNAME, "").ifEmpty { null }

    fun saveUsername(username: String) {
        if (username.isEmpty()) {
            throw IllegalArgumentException("The username should not be empty!")
        }
        sharedPreferences.edit()
            .putString(USERNAME, username)
            .apply()
    }
}