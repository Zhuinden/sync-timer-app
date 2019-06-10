package com.zhuinden.synctimer.features.clientlobby

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class ClientLobbyView : FrameLayout {
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
}