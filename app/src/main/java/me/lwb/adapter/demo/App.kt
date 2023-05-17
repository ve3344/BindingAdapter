package me.lwb.adapter.demo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import me.lwb.adapter.demo.ui.activity.Page

/**
 * Created by ve3344@qq.com.
 */
open class App: Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val annotation = activity.javaClass.getAnnotation(Page::class.java)?:return
                activity.title=annotation.title
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }
}