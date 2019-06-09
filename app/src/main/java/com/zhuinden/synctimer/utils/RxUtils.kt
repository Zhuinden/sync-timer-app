package com.zhuinden.synctimer.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Observable<T>.onUI(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())