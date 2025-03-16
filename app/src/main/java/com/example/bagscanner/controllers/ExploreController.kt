package com.example.bagscanner.controllers

// 1. Standard imports
import android.content.Context
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController

// 2. Google Maps imports
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng

// 3. Project-specific imports
import com.example.bagscanner.enums.Screens
import com.example.bagscanner.models.LocationModel
import com.example.bagscanner.models.StoreModel
import com.example.bagscanner.services.LocationService
import com.example.bagscanner.views.ExploreView

// 4. Kotlin coroutines imports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExploreController(private val navController: NavHostController) :
    ViewModel(), IControllers {

    // Attributes
    private val _locationState = MutableStateFlow(LocationModel())
    val locationState: StateFlow<LocationModel> = _locationState
    private var locationService: LocationService? = null
    private var isPeriodicSearchEnabled = false
    private val searchRadius = 10000 // 10km radius

    // Methods
    fun startLocationService(context: Context) {
        if (locationService == null) {
            locationService = LocationService(
                context,
                getNewLocation = { location -> getCurrentLocation(location) },
                updateNearbyStores = { stores -> updateStores(stores) },
                manageError = { message -> updateError(message) }
            )
        }
        startLocationUpdates()
    }

    private fun getCurrentLocation(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        _locationState.value = _locationState.value.copy(
            currentLocation = currentLatLng,
            isLoading = false
        )

        searchNearbyStores(currentLatLng)
    }

    private fun searchNearbyStores(location: LatLng) {
        viewModelScope.launch {
            _locationState.value = _locationState.value.copy(isLoading = true)

            locationService?.searchNearbyBagStores(location, searchRadius)

            // To search periodically
            if (!isPeriodicSearchEnabled) {
                locationService?.startPeriodicSearch(location, searchRadius)
                isPeriodicSearchEnabled = true
            }
        }
    }

    private fun updateStores(stores: List<StoreModel>) {
        _locationState.value = _locationState.value.copy(
            nearbyStores = stores,
            isLoading = false
        )
    }

    private fun updateError(message: String) {
        _locationState.value = _locationState.value.copy(
            errorMessage = message,
            isLoading = false
        )
    }

    private fun startLocationUpdates() {
        _locationState.value = _locationState.value.copy(
            isLoading = true,
            errorMessage = ""
        )
        locationService?.startLocationUpdates()
    }

    // Handle map lifecycle events to prevent memory leaks
    fun registerMapLifecycle(mapView: MapView) {
        mapView.onResume()
    }

    override fun onCleared() {
        locationService?.stopLocationUpdates()
        locationService?.stopPeriodicSearch()
        isPeriodicSearchEnabled = false
        locationService = null
        super.onCleared()
    }

    @Composable
    override fun DisplayScreen() {
        ExploreView(this)
    }

    override fun navigateTo(screen: Screens) {
        when (screen) {
            Screens.Home -> navController.navigate("home")
            Screens.Scanner -> navController.navigate("scanner")
            Screens.Explore -> navController.navigate("explore")
            Screens.Locations -> navController.navigate("locations")
        }
    }
}