package com.example.synamedia_assignment.ui

import android.app.Application
import com.example.synamedia_assignment.ApplicationComponent.ApplicationComponent
import com.example.synamedia_assignment.ApplicationComponent.DaggerApplicationComponent


class MyApp: Application() {
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().build()

    }
}