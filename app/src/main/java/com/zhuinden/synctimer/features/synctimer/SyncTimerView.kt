package com.zhuinden.synctimer.features.synctimer

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.simplestack.StateChange
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.utils.backstack
import com.zhuinden.synctimer.utils.onClick
import kotlinx.android.synthetic.main.sync_timer_view.view.*

class SyncTimerView : FrameLayout, BackHandler {
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

    private fun showLeavingAlert() {
        AlertDialog.Builder(context)
            .setTitle("Leaving session")
            .setMessage("Are you sure you want to quit the session?")
            .setPositiveButton("Quit") { _, _ ->
                backstack.jumpToRoot(StateChange.REPLACE)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        buttonExitSession.onClick {
            showLeavingAlert()
        }
    }

    override fun onBackPressed(): Boolean {
        showLeavingAlert()
        return true
    }
}