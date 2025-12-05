package com.joke.mon.core.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsLogger {
    private val crashlytics get() = FirebaseCrashlytics.getInstance()

    fun logException(
        throwable: Throwable,
        tag: String? = null,
        extras: Map<String, Any?> = emptyMap()
    ) {
        tag?.let { crashlytics.setCustomKey("tag", it) }
        extras.forEach { (key, value) ->
            when (value) {
                is String -> crashlytics.setCustomKey(key, value)
                is Int -> crashlytics.setCustomKey(key, value)
                is Boolean -> crashlytics.setCustomKey(key, value)
                is Long -> crashlytics.setCustomKey(key, value)
                is Double -> crashlytics.setCustomKey(key, value)
                else -> crashlytics.setCustomKey(key, value?.toString() ?: "null")
            }
        }
        crashlytics.recordException(throwable)
    }

    fun log(message: String) {
        crashlytics.log(message)
    }
}