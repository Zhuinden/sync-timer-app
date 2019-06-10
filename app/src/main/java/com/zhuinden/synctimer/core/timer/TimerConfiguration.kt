package com.zhuinden.synctimer.core.timer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimerConfiguration(
    val startValue: Int,
    val endValue: Int,
    val decreaseStep: Int,
    val decreaseInterval: Int
) : Parcelable