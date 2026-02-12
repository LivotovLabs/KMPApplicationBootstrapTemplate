package com.watermelonkode.simpletemplate

import android.app.Application

class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(platformContext = PlatformContext(this)).koin
    }
}