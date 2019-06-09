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