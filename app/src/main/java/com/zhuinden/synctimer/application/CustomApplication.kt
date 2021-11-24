/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.application

import android.app.Application
import android.preference.PreferenceManager
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.synctimer.core.networking.ConnectionManager
import com.zhuinden.synctimer.features.settings.SettingsManager

class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val settingsManager = SettingsManager(sharedPreferences)
        val connectionManager = ConnectionManager()

        globalServices = GlobalServices.builder()
            .add(settingsManager)
            .add(sharedPreferences)
            .add(connectionManager)
            .build()
    }
}