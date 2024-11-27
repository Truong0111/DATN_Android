package com.truongtq_datn_user.activity

import android.app.Application
import com.truongtq_datn_user.extensions.ActivityTracker

class MyApplication : Application() {

    val activityTracker = ActivityTracker()

    companion object {
        var instance: MyApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityTracker)
        instance = this
    }
}