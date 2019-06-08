package com.zhuinden.synctimer.application

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.zhuinden.synctimer.core.settings.SettingsManager

class CustomApplication : Application() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var settingsManager: SettingsManager
        private set

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        settingsManager = SettingsManager(sharedPreferences)
    }
}