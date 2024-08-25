package com.sublime.lingo.presentation.ui

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceIdManager
    @Inject
    constructor(
        context: Context,
    ) {
        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        fun getDeviceId(): String {
            var deviceId = prefs.getString(KEY_DEVICE_ID, null)
            if (deviceId == null) {
                deviceId = generateDeviceId()
                prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
            }
            return deviceId
        }

        private fun generateDeviceId(): String = UUID.randomUUID().toString()

        companion object {
            private const val PREF_NAME = "DevicePrefs"
            private const val KEY_DEVICE_ID = "device_id"
        }
    }
