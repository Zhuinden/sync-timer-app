/*
 * Created in 2019 by Gabor Varadi
 *
 * You may not use this file except in compliance with the license.
 *
 * The license says: "please don't release this app as is under your own name, thanks".
 */
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