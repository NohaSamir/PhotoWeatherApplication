package com.example.photoweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * CountDownLatch: A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
 * A CountDownLatch is initialized with a given count. The await methods block until the current count reaches zero due to invocations of the countDown method,
 * after which all waiting threads are released and any subsequent invocations of await return immediately.
 */
fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }

    observeForever(observer)

    latch.await(2, TimeUnit.SECONDS)
    return value
}