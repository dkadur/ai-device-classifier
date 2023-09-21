package com.example.devicedetection2.network

import com.example.devicedetection2.model.DevicePhoto
import retrofit2.http.GET

/**
 * A public interface that exposes the [getPhotos] method
 */
interface DeviceApiService {
    /**
     * Returns a [List] of [DevicePhoto] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("get_image_paths")
    //@GET("photos")
    suspend fun getPhotos(): List<DevicePhoto>
}