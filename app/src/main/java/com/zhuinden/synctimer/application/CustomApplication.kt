/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.application

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.features.settings.SettingsManager

class CustomApplication : Application() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var settingsManager: SettingsManager
        private set
    lateinit var connectionManager: ConnectionManager
        private set

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        settingsManager = SettingsManager(sharedPreferences)
        connectionManager = ConnectionManager()
    }
}