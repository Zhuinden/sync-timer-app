package com.zhuinden.synctimer.features.createsession

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.features.synctimer.SyncTimerKey
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.onClick
import kotlinx.android.synthetic.main.create_session_view.view.*

class CreateSessionView: FrameLayout {
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

        buttonBlahGoToTimer.onClick {
            backstack.goTo(SyncTimerKey())
        }
    }
}