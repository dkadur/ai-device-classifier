package com.example.devicedetection2.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.devicedetection2.R
import com.example.devicedetection2.model.DevicePhoto
import com.example.devicedetection2.ui.theme.DeviceDetection2Theme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.devicedetection2.DevicePhotosApplication
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import kotlinx.coroutines.launch

@Composable
fun DevicePhotoCard(
    photo: DevicePhoto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showPopup: Boolean,
    onPopupDismiss: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.device_photo),
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showPopup) {
        ImagePopup(photo = photo, onClose = onPopupDismiss, onAccept = onAccept, onReject = onReject)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PhotosGridScreen(retryAction: () -> Unit, photos: List<DevicePhoto>, modifier: Modifier = Modifier) {
    var selectedPhoto by remember { mutableStateOf<DevicePhoto?>(null) }
    val deviceViewModel: DeviceViewModel =
        viewModel(factory = DeviceViewModel.Factory)
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(items = photos, key = { photo -> photo.id }) { photo ->
            DevicePhotoCard(
                photo,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f),
                onClick = {
                    selectedPhoto = photo
                },
                showPopup = selectedPhoto == photo,
                onPopupDismiss = {
                    selectedPhoto = null
                },
                onAccept = {
                    deleteImage(
                        photo.id,
                        onSuccess = {
                            deviceViewModel.getDevicePhotos()
                            toast(context, "Device classification accepted and logged")

                        },
                        onError = { errorMessage ->
                            // Handle deletion failure with errorMessage
                        }
                    )
                },
                onReject = {
                    deleteImage(
                        photo.id,
                        onSuccess = {
                            deviceViewModel.getDevicePhotos()
                            toast(context, "Device classification rejected")
                        },
                        onError = { errorMessage ->
                            // Handle deletion failure with errorMessage
                        }
                    )
                }
            )
        }
    }
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun deleteImage(
    imageId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    onSuccess()
    Fuel.delete("http://192.168.1.165:5000/delete_image/$imageId")
        .response { result ->
            when (result) {
                is com.github.kittinunf.result.Result.Success -> {
                    onSuccess()

                }
                is com.github.kittinunf.result.Result.Failure -> {
                    onError(result.getException().message ?: "Unknown error")
                }
            }
        }
}

@Composable
fun HomeScreen(
    deviceUiState: DeviceUiState, retryAction: () -> Unit, modifier: Modifier = Modifier
) {
    when (deviceUiState) {
        is DeviceUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        /*is DeviceUiState.Success -> DevicePhotoCard(
            deviceUiState.photos, modifier = modifier.fillMaxWidth()
        )*/
        is DeviceUiState.Success -> PhotosGridScreen(retryAction, deviceUiState.photos, modifier)

        is DeviceUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
fun ImagePopup(
    photo: DevicePhoto,
    onClose: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ),
            //contentAlignment = Alignment.Center
        ) {
            // Display the enlarged image here
            Image(
                painter = rememberAsyncImagePainter(photo.imgSrc),
                contentDescription = stringResource(R.string.device_photo),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClose() } // Close the dialog when clicked
            )

            // Add spacing between image and buttons
            Spacer(modifier = Modifier.height(8.dp))

            // Text below the image
            Text(
                text = "Predicted classification: " + photo.classification + "?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center // Center the text
            )

            // Add spacing between text and buttons
            Spacer(modifier = Modifier.height(8.dp))

            //Accept/reject buttons
            Row(
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        onClose()
                        onAccept()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Accept")
                }
                Button(
                    onClick = {
                        onClose()
                        onReject()
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = "Reject")
                }
            }
        }
    }
}

/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = photos)
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    DeviceDetection2Theme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    DeviceDetection2Theme {
        ErrorScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    DeviceDetection2Theme {
        //val mockData = List(10) { DevicePhoto("$it", "") }
        //PhotosGridScreen(mockData)
        //ResultScreen(stringResource(R.string.placeholder_success))
    }
}