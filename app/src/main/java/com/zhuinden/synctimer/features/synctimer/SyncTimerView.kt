package com.zhuinden.synctimer.features.synctimer

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.StateChange
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.onClick
import kotlinx.android.synthetic.main.sync_timer_view.view.*

class SyncTimerView: FrameLayout {
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

    override fun onFinishInflate() {
        super.onFinishInflate()

        buttonExitSession.onClick {
            // TODO: stuff
            backstack.jumpToRoot(StateChange.REPLACE)
        }
    }
}