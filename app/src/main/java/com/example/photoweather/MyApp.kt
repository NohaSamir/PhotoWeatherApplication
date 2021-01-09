package com.example.photoweather

import android.app.Application

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }

    companion object Injection {

        lateinit var application: Application private set

        fun initApplication(application: Application) {
            this.application = application
        }
    }
}