package com.example.devicedetection2.data

import com.example.devicedetection2.model.DevicePhoto
import com.example.devicedetection2.network.DeviceApiService

/**
 * Repository that fetch device photos list from deviceApi.
 */
interface DevicePhotosRepository {
    /** Fetches list of DevicePhoto from deviceApi */
    suspend fun getDevicePhotos(): List<DevicePhoto>
}

/**
 * Network Implementation of Repository that fetch device photos list from deviceApi.
 */
class NetworkDevicePhotosRepository(
    private val deviceApiService: DeviceApiService
) : DevicePhotosRepository {
    /** Fetches list of DevicePhoto from deviceApi*/
    override suspend fun getDevicePhotos(): List<DevicePhoto> = deviceApiService.getPhotos()
}