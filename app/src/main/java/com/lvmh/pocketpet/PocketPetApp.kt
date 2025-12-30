package com.lvmh.pocketpet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PocketPetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Aquí puedes inicializar cosas globales si es necesario
    }
}