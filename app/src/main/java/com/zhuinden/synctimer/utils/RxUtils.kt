package com.zhuinden.synctimer.utils

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5

fun <T> Observable<T>.observeOnMain(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.observeOnMain(): Single<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <A, B> Observable<A>.combineWith(second: Observable<B>): Observable<Pair<A, B>> =
    Observable.combineLatest(this, second,
        BiFunction<A, B, Pair<A, B>> { t1, t2 -> t1 to t2 })

fun <A, B, C> Observable<A>.combineWith(second: Observable<B>, third: Observable<C>): Observable<Triple<A, B, C>> =
    Observable.combineLatest(this, second, third,
        Function3<A, B, C, Triple<A, B, C>> { t1, t2, t3 -> t1 to t2 to t3 })

fun <A, B, C, D> Observable<A>.combineWith(
    second: Observable<B>,
    third: Observable<C>,
    fourth: Observable<D>
): Observable<Tuple4<A, B, C, D>> =
    Observable.combineLatest(this, second, third, fourth,
        Function4<A, B, C, D, Tuple4<A, B, C, D>> { t1, t2, t3, t4 -> t1 to t2 to t3 to t4 })

fun <A, B, C, D, E> Observable<A>.combineWith(
    second: Observable<B>,
    third: Observable<C>,
    fourth: Observable<D>,
    fifth: Observable<E>
): Observable<Tuple5<A, B, C, D, E>> =
    Observable.combineLatest(this, second, third, fourth, fifth,
        Function5<A, B, C, D, E, Tuple5<A, B, C, D, E>> { t1, t2, t3, t4, t5 -> t1 to t2 to t3 to t4 to t5 })

