/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
package com.zhuinden.synctimer.application

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.core.settings.SettingsManager

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