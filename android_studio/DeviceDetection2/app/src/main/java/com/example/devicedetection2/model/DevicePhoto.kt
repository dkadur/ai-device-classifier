package com.example.devicedetection2.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines a Device photo which includes an ID, and the image URL.
 */
@Serializable
data class DevicePhoto(
    val id: String,
    @SerialName(value = "img_src")
    val imgSrc: String,
    val classification: String
)