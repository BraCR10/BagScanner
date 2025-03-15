package com.example.bagscanner.views

// 1. Standard Kotlin packages
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

// 2. Compose Foundation imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

// 3. Compose Material imports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect

// 4. Compose UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bagscanner.R

// 5. Google Maps related imports
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// 6. Project-specific imports
import com.example.bagscanner.controllers.ExploreController
import com.example.bagscanner.enums.Screens
import com.example.bagscanner.models.StoreModel

@Composable
fun ExploreView(controller: ExploreController = viewModel()) {
    val backgroundColor = Color(0xFFF5F5F5)
    val headerBackgroundColor = Color(0xFFDAC5A0)
    val headerBorderColor = Color(0xFF388E3C)
    val textColor = Color.Black
    val iconColor = Color.Black

    val locationState by controller.locationState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        controller.startLocationService(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = LocalConfiguration.current.screenHeightDp.dp * 0.2f)
                .background(headerBackgroundColor)
                .border(2.dp, headerBorderColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Explorar locales",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { controller.navigateTo(Screens.Home) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
        }

        // Map or loading state
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                locationState.isLoading -> {
                    CircularProgressIndicator(
                        color = headerBorderColor,
                        modifier = Modifier.size(50.dp)
                    )
                }
                locationState.errorMessage.isNotEmpty() -> {
                    Text(
                        text = locationState.errorMessage,
                        color = Color.Red,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                locationState.currentLocation != null -> {
                    MapContent(
                        currentLocation = locationState.currentLocation!!,
                        nearbyStores = locationState.nearbyStores,
                        controller = controller
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapContent(
    currentLocation: LatLng,
    nearbyStores: List<StoreModel>,
    controller: ExploreController
) {
    val context = LocalContext.current

    // Avoid recomposition
    val mapView = remember { MapView(context).apply { id = R.id.map_view } }


    LaunchedEffect(Unit) {
        mapView.onCreate(null)
        mapView.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)
            )

            nearbyStores.forEach { store ->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(store.latitude, store.longitude))
                        .title(store.name)
                        .snippet(store.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }
        }

        controller.registerMapLifecycle(mapView)
    }


    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize(),
        update = { }
    )

   
    DisposableEffect(Unit) {
        onDispose {
            mapView.onDestroy()
        }
    }
}