package com.joke.mon

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application()
{

    override fun onCreate() {
        super.onCreate()

        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !isDebug
    }
}