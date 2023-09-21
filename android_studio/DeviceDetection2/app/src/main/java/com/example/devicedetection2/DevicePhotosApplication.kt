package com.example.devicedetection2

import android.app.Application
import com.example.devicedetection2.data.AppContainer
import com.example.devicedetection2.data.DefaultAppContainer

class DevicePhotosApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}