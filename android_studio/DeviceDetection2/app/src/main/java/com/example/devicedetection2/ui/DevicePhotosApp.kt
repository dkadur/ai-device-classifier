@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.devicedetection2.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.devicedetection2.R
import com.example.devicedetection2.ui.screens.HomeScreen
import com.example.devicedetection2.ui.screens.DeviceViewModel

@Composable
fun DevicePhotosApp() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val deviceViewModel: DeviceViewModel =
        viewModel(factory = DeviceViewModel.Factory)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { DeviceTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onRefreshClick = deviceViewModel::getDevicePhotos)}
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val deviceViewModel: DeviceViewModel =
                viewModel(factory = DeviceViewModel.Factory)
            HomeScreen(
                deviceUiState = deviceViewModel.deviceUiState,
                retryAction = deviceViewModel::getDevicePhotos)
        }
    }
}

@Composable
fun DeviceTopAppBar(scrollBehavior: TopAppBarScrollBehavior, onRefreshClick: () -> Unit,
                    modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        actions = {
            IconButton(
                onClick = { onRefreshClick() }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
            }
        },
        modifier = modifier
    )
}