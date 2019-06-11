/* Copyright (c) 2019, Gabor Varadi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - The binary form of the software cannot be redistributed for commercial use under the name of a separate entity
 * when the binary form is built from using all included source files, or any of the full and complete compilation modules of the software are used as a submodule of another software.
 * - Prior written permission must be given in order to use the name of its author or other project contributors
 * for endorsing or promoting products derived from this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.zhuinden.synctimer.core.settings

import android.content.SharedPreferences
import com.zhuinden.synctimer.utils.save

class SettingsManager(
    private val sharedPreferences: SharedPreferences
) {
    private fun String.saveAs(key: String) {
        val self = this
        sharedPreferences.save { putString(key, self) }
    }

    private fun Int.saveAs(key: String) {
        val self = this
        sharedPreferences.save { putInt(key, self) }
    }

    companion object {
        const val USERNAME = "USERNAME"

        const val START_VALUE = "START_VALUE"
        const val END_VALUE = "END_VALUE"
        const val DECREASE_STEP = "DECREASE_STEP"
        const val DECREASE_INTERVAL = "DECREASE_INTERVAL"
    }

    fun getUsername(): String? = sharedPreferences.getString(USERNAME, "").ifEmpty { null }

    fun saveUsername(username: String) {
        if (username.isEmpty()) {
            throw IllegalArgumentException("The username should not be empty!")
        }
        username.saveAs(USERNAME)
    }

    fun getStartValue(): Int = sharedPreferences.getInt(START_VALUE, 200)

    fun saveStartValue(startValue: Int) {
        startValue.saveAs(START_VALUE)
    }

    fun getEndValue(): Int = sharedPreferences.getInt(END_VALUE, 50)

    fun saveEndValue(endValue: Int) {
        endValue.saveAs(END_VALUE)
    }

    fun getDecreaseStep(): Int = sharedPreferences.getInt(DECREASE_STEP, 10)

    fun saveDecreaseStep(decreaseStep: Int) {
        decreaseStep.saveAs(DECREASE_STEP)
    }

    fun getDecreaseInterval(): Int = sharedPreferences.getInt(DECREASE_INTERVAL, 5)

    fun saveDecreaseInterval(decreaseInterval: Int) {
        decreaseInterval.saveAs(DECREASE_INTERVAL)
    }
}