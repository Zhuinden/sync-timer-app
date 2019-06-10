package com.zhuinden.synctimer.features.clientlobby

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.zhuinden.synctimer.core.navigation.BackHandler
import com.zhuinden.synctimer.utils.backstack

class ClientLobbyView : FrameLayout, BackHandler {
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
    }

    override fun onBackPressed(): Boolean {
        AlertDialog.Builder(context)
            .setTitle("Leaving session")
            .setMessage("Are you sure you want to quit the session?")
            .setPositiveButton("Quit") { _, _ ->
                backstack.goBack()
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
        return true
    }
}