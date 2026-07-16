package com.example.AppRazer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RazerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        org.osmdroid.config.Configuration.getInstance().load(
            this,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        )
    }
}