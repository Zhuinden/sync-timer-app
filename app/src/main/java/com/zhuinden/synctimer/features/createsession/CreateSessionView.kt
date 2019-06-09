package com.zhuinden.synctimer.features.createsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.settings.SettingsManager
import com.zhuinden.synctimer.features.serverlobby.ServerLobbyKey
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.lookup
import com.zhuinden.synctimer.utils.onClick
import com.zhuinden.synctimer.utils.onTextChanged
import kotlinx.android.synthetic.main.create_session_view.view.*

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
class CreateSessionView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private val settingsManager by lazy { lookup<SettingsManager>() }

    private var startValue: Int = 0
    private var endValue: Int = 0
    private var decreaseStep: Int = 0
    private var decreaseInterval: Int = 0

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isInEditMode) {
            return
        }

        startValue = settingsManager.getStartValue()
        endValue = settingsManager.getEndValue()
        decreaseStep = settingsManager.getDecreaseStep()
        decreaseInterval = settingsManager.getDecreaseInterval()

        inputStartValue.setText("$startValue")
        inputStartValue.onTextChanged { startValue ->
            if (startValue.isNotEmpty()) {
                this.startValue = startValue.toInt()
                settingsManager.saveStartValue(startValue.toInt())
            }
        }

        inputEndValue.setText("$endValue")
        inputEndValue.onTextChanged { endValue ->
            if (endValue.isNotEmpty()) {
                this.endValue = endValue.toInt()
                settingsManager.saveEndValue(endValue.toInt())
            }
        }

        inputDecreaseStep.setText("$decreaseStep")
        inputDecreaseStep.onTextChanged { decreaseStep ->
            if (decreaseStep.isNotEmpty()) {
                this.decreaseStep = decreaseStep.toInt()
                settingsManager.saveDecreaseStep(decreaseStep.toInt())
            }
        }

        inputDecreaseInterval.setText("$decreaseInterval")
        inputDecreaseInterval.onTextChanged { decreaseInterval ->
            if (decreaseInterval.isNotEmpty()) {
                this.decreaseInterval = decreaseInterval.toInt()
                settingsManager.saveDecreaseInterval(decreaseInterval.toInt())
            }
        }

        buttonCreateSession.onClick {
            backstack.goTo(ServerLobbyKey(startValue, endValue, decreaseStep, decreaseInterval))
        }
    }
}