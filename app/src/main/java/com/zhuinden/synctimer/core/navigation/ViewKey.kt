package com.zhuinden.synctimer.core.navigation

import android.os.Parcelable
import com.zhuinden.simplestack.navigator.DefaultViewKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler

interface ViewKey: DefaultViewKey, Parcelable {
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}