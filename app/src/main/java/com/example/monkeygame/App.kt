package com.example.monkeygame

import android.app.Application
import com.example.monkeygame.utilities.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}