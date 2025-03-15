package com.example.bagscanner.services

// 1. Android imports
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log

// 2. Google Play Services imports
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

// 3. Project-specific imports
import com.example.bagscanner.models.StoreModel


class LocationService(
    context: Context,
    private val getNewLocation: (Location) -> Unit,
    private val updateNearbyStores: (List<StoreModel>) -> Unit,
    private val manageError: (String) -> Unit
) {
    // Attributes
    private val currentLocation: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    // Methods
    // Atributos
    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationListener: LocationCallback? = null

    // Métodos
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            // Request new location
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000) // min 5 segs
                .setMaxUpdateDelayMillis(15000)  // max 15 segs
                .build()

            // When new location is received
            locationListener = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        getNewLocation(location)
                        stopLocationUpdates()
                    }
                }
            }

            locationClient.requestLocationUpdates(
                request,
                locationListener as LocationCallback,
                Looper.getMainLooper()
            )


        } catch (e: Exception) {
            manageError("Location was not updated: ${e.message}")
            Log.e("LocationService", "Location was not updated", e)
        }
    }




    fun stopLocationUpdates() {
        locationCallback?.let {
            currentLocation.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    fun searchNearbyBagStores(location: LatLng, radiusInMeters: Int) {
        try {
                //TODO
                val stores: List<StoreModel> = emptyList()
                updateNearbyStores(stores)
        } catch (e: Exception) {
            manageError("${e.message}")
            Log.e("LocationService", "Error searching nearby stores", e)
        }
    }


}