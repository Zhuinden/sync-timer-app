package com.zhuinden.synctimer.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Observable<T>.onUI(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.onUI(): Single<T> = this.observeOn(AndroidSchedulers.mainThread())