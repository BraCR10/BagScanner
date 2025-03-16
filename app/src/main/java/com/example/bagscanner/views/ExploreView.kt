package com.example.bagscanner.views

// 1. Standard Kotlin packages

// 2. Compose Foundation imports

// 3. Compose Material imports

// 4. Compose UI imports

// 5. Google Maps related imports

// 6. Project-specific imports
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bagscanner.R
import com.example.bagscanner.controllers.ExploreController
import com.example.bagscanner.enums.Screens
import com.example.bagscanner.models.StoreModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


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

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                locationState.isLoading -> {
                    CircularProgressIndicator(
                        color = headerBorderColor,
                        modifier = Modifier.size(50.dp))
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

        if (locationState.nearbyStores.isNotEmpty()) {
            StoreListFooter(
                stores = locationState.nearbyStores,
            )
        }
    }
}

@Composable
private fun StoreListFooter(
    stores: List<StoreModel>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color.White)
            .shadow(elevation = 4.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(stores) { store ->
                StoreCardComponent(store = store)
            }
        }
    }
}

@Composable
private fun StoreCardComponent(
    store: StoreModel,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(160.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = store.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = store.address,
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text =
            if (store.distance >= 1000)
                "%.1f km".format(store.distance / 1000)
            else
                "%d m".format(store.distance.toInt()),

            fontSize = 12.sp,
            color = Color(0xFF388E3C)
        )

        Button(
            onClick = { /*    WAZE  */

                // "https://www.waze.com/ul?ll=9.896741056495557, -83.99821234578992&navigate=yes"

            val latitude : String = store.latitude.toBigDecimal().toPlainString()
            val longitude : String = store.longitude.toBigDecimal().toPlainString()
            val waze = "https://www.waze.com/ul?ll="
            val navigate = "&navigate=yes"
            val coordinates = "$waze$latitude, $longitude$navigate"

                val uri = Uri.parse(coordinates)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(context, intent, null) // Deprecated pero no encontre la alternativa :P

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
        ) {
            Text("Ir al local", fontSize = 12.sp)
        }
    }
}
@SuppressLint("MissingPermission")//location was already taken
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