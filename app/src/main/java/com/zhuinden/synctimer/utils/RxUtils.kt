package com.zhuinden.synctimer.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Observable<T>.observeOnMain(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.observeOnMain(): Single<T> = this.observeOn(AndroidSchedulers.mainThread())