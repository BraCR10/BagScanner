package com.example.bagscanner.services

// Android  Imports
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log

// Project-specific Imports
import com.example.bagscanner.models.StoreModel

// Google Play Services & Maps Imports
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

// Kotlin Coroutines Imports
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// JSON Parsing & Networking Imports
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


interface LocationServiceListener {
    fun onNewLocation(location: Location)
    fun onNearbyStoresUpdated(stores: List<StoreModel>)
    fun onError(errorMessage: String)
}

class LocationService(
    context: Context,
    private val listener: LocationServiceListener
) {

    private val currentLocation: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var updateNewLocationCall: LocationCallback? = null
    private var searchNewLocals: Job? = null
    private val searchLocalsInterval = TimeUnit.MINUTES.toMillis(5)
    private val updateLocalsCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10000
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build()

            updateNewLocationCall = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        listener.onNewLocation(location)
                        stopLocationUpdates()
                    }
                }
            }

            currentLocation.requestLocationUpdates(
                locationRequest,
                updateNewLocationCall as LocationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            listener.onError("The location was not updated: ${e.message}")
            Log.e("LocationService", "The location was not updated", e)
        }
    }

    fun stopLocationUpdates() {
        if (updateNewLocationCall != null) {
            currentLocation.removeLocationUpdates(updateNewLocationCall!!)
            updateNewLocationCall = null
        }
        if (searchNewLocals != null) {
            searchNewLocals!!.cancel()
            searchNewLocals = null
        }
    }

    fun searchBagStoresOnArea(location: LatLng, radiusInMeters: Int) {
        updateLocalsCoroutineScope.launch {
            try {
                val stores = fetchNearbyStoresFromOverpass(
                    location.latitude,
                    location.longitude,
                    radiusInMeters
                )
                withContext(Dispatchers.Main) {
                    listener.onNearbyStoresUpdated(stores)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onError("Error en búsqueda de tiendas: ${e.message}")
                    Log.e("LocationService", "Error searching stores", e)
                }
            }
        }
    }

    fun startPeriodicSearch(location: LatLng, radiusInMeters: Int) {
        searchNewLocals?.cancel()
        searchNewLocals = updateLocalsCoroutineScope.launch {
            while (isActive) {
                try {
                    val stores = fetchNearbyStoresFromOverpass(
                        location.latitude,
                        location.longitude,
                        radiusInMeters
                    )
                    withContext(Dispatchers.Main) {
                        listener.onNearbyStoresUpdated(stores)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        listener.onError("Error en búsqueda periódica: ${e.message}")
                        Log.e("LocationService", "Error searching stores", e)
                    }
                }
                delay(searchLocalsInterval)
            }
        }
    }

    fun stopPeriodicSearch() {
        searchNewLocals?.cancel()
        searchNewLocals = null
    }

    private suspend fun fetchNearbyStoresFromOverpass(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Int
    ): List<StoreModel> {
        try {
            val response = fetchResponseFromOverpass(latitude, longitude, radiusInMeters)
            return parseStoresFromResponse(response, latitude, longitude)
        } catch (e: JSONException) {
            Log.e("LocationService", "Error parsing response", e)
            throw Exception("Error parsing response")
        } catch (e: Exception) {
            Log.e("LocationService", "Error using Overpass API", e)
            throw e
        }
    }

    private suspend fun fetchResponseFromOverpass(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Int
    ): String {
        val query = """
        [out:json];
        (
          node["shop"="bag"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="accessories"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="mall"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="boutique"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="fashion"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="department_store"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="second_hand"](around:$radiusInMeters,$latitude,$longitude);
          node["shop"="discount"](around:$radiusInMeters,$latitude,$longitude);
        );
        out;
        """.trimIndent()

        val encodedQuery = withContext(Dispatchers.IO) {
            URLEncoder.encode(query, "UTF-8")
        }
        val url = URL("https://overpass-api.de/api/interpreter?data=$encodedQuery")

        val connection = withContext(Dispatchers.IO) {
            url.openConnection() as HttpURLConnection
        }
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            return withContext(Dispatchers.IO) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            }
        } else {
            throw Exception("Error HTTP: ${connection.responseCode}")
        }
    }

    private fun parseStoresFromResponse(
        response: String,
        latitude: Double,
        longitude: Double
    ): List<StoreModel> {
        val stores = mutableListOf<StoreModel>()
        val jsonResponse = JSONObject(response)
        val elements = jsonResponse.getJSONArray("elements")

        for (i in 0 until elements.length()) {
            val element = elements.getJSONObject(i)
            if (element.has("tags")) {
                val tags = element.getJSONObject("tags")
                val name = if (tags.has("name")) tags.getString("name") else "Tienda sin nombre"

                val street = if (tags.has("addr:street")) tags.getString("addr:street") else ""
                val houseNumber = if (tags.has("addr:housenumber")) tags.getString("addr:housenumber") else ""
                val city = if (tags.has("addr:city")) tags.getString("addr:city") else ""

                val address = listOfNotNull(
                    if (street.isNotEmpty() && houseNumber.isNotEmpty()) "$street $houseNumber"
                    else if (street.isNotEmpty()) street else null,
                    city.takeIf { it.isNotEmpty() }
                ).joinToString(", ")

                val finalAddress = address.ifEmpty { "Sin dirección disponible" }

                val storeLat = element.getDouble("lat")
                val storeLon = element.getDouble("lon")

                val results = FloatArray(1)
                Location.distanceBetween(latitude, longitude, storeLat, storeLon, results)

                val store = StoreModel(
                    name = name,
                    address = finalAddress,
                    latitude = storeLat,
                    longitude = storeLon,
                    distance = results[0]
                )
                stores.add(store)
            }
        }
        stores.sortBy { it.distance }
        return stores
    }
}
