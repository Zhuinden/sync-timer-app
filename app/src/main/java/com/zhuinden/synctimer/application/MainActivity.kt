/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.core.navigation.SyncTimerKeyFilter
import com.zhuinden.synctimer.core.scoping.ScopeConfiguration
import com.zhuinden.synctimer.screens.splash.SplashKey
import com.zhuinden.synctimer.utils.add
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val app = application as CustomApplication

        Navigator.configure()
            .setGlobalServices(
                GlobalServices.builder()
                    .add(app.settingsManager)
                    .add(app.connectionManager)
                    .build()
            )
            .setKeyFilter(SyncTimerKeyFilter())
            .setScopedServices(ScopeConfiguration())
            .install(this, root, History.of(SplashKey()))
    }

    override fun onBackPressed() {
        val view = root.getChildAt(0)
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
