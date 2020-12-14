package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    // when the counter is greater than 0, the app is considered working
    // when it is less than 0, the app is considered idle
    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

    inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
        // Espresso does not work well with coroutines yet. See
        // https://github.com/Kotlin/kotlinx.coroutines/issues/982
        increment() // Set app as busy.
        return try {
            function()
        } finally {
            decrement() // Set app as idle.
        }
    }
}