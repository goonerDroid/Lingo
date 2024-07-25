package com.sublime.lingo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LingoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
