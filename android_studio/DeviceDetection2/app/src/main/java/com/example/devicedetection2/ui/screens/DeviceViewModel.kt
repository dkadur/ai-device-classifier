package com.example.devicedetection2.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.devicedetection2.DevicePhotosApplication
import com.example.devicedetection2.data.DevicePhotosRepository
import com.example.devicedetection2.model.DevicePhoto
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface DeviceUiState {
    data class Success(val photos: List<DevicePhoto>) : DeviceUiState
    object Error : DeviceUiState
    object Loading : DeviceUiState
}

class DeviceViewModel(private val devicePhotosRepository: DevicePhotosRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var deviceUiState: DeviceUiState by mutableStateOf(DeviceUiState.Loading)
        private set

    /**
     * Call getDevicePhotos() on init so we can display status immediately.
     */
    init {
        getDevicePhotos()
    }

    /**
     * Gets Device photos information from the Device API Retrofit service and updates the
     * [DevicePhoto] [List] [MutableList].
     */
    fun getDevicePhotos() {
        viewModelScope.launch {
            deviceUiState = DeviceUiState.Loading
            deviceUiState = try {
                DeviceUiState.Success(devicePhotosRepository.getDevicePhotos())
            } catch (e: IOException) {
                DeviceUiState.Error
            } catch (e: HttpException) {
                DeviceUiState.Error
            }
        }
    }

    /**
     * Factory for [DeviceViewModel] that takes [DevicePhotosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DevicePhotosApplication)
                val devicePhotosRepository = application.container.devicePhotosRepository
                DeviceViewModel(devicePhotosRepository = devicePhotosRepository)
            }
        }
    }
}