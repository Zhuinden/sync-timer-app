package com.zhuinden.synctimer.features.splash

import android.os.Parcelable
import com.zhuinden.simplestack.navigator.DefaultViewKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import com.zhuinden.synctimer.R
import com.zhuinden.synctimer.core.navigation.ViewKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SplashKey(private val placeholder: String = ""): ViewKey {
    override fun layout(): Int = R.layout.splash_view

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}