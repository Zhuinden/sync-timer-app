/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 */
package com.zhuinden.synctimer.core.timer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimerConfiguration(
    val startValue: Int,
    val endValue: Int,
    val decreaseStep: Int,
    val decreaseInterval: Int
) : Parcelable