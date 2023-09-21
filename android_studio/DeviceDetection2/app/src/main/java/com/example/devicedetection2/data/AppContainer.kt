package com.example.devicedetection2.data

import com.example.devicedetection2.network.DeviceApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val devicePhotosRepository: DevicePhotosRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    //private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"
    private val baseUrl = "http://192.168.1.165:5000/"

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: DeviceApiService by lazy {
        retrofit.create(DeviceApiService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val devicePhotosRepository: DevicePhotosRepository by lazy {
        NetworkDevicePhotosRepository(retrofitService)
    }
}