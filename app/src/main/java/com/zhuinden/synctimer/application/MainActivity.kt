/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.application

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.core.navigation.SyncTimerKeyFilter
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.databinding.ActivityMainBinding
import com.zhuinden.synctimer.screens.splash.SplashKey

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.bind(
            (findViewById(Window.ID_ANDROID_CONTENT) as ViewGroup).getChildAt(0)
        )

        val app = application as CustomApplication

        Navigator.configure()
            .setGlobalServices(app.globalServices)
            .setKeyFilter(SyncTimerKeyFilter())
            .setScopedServices(ScopeConfiguration())
            .install(this, binding.container, History.of(SplashKey()))
    }

    override fun onBackPressed() {
        val view = binding.container.getChildAt(0)
        if (view is BackHandler) {
            val handled = view.onBackPressed()
            if (handled) {
                return
            }
        }
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }
}
