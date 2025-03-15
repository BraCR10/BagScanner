package com.example.bagscanner.models

import com.google.android.gms.maps.model.LatLng

data class LocationModel(
    val isLoading: Boolean = false,
    val currentLocation: LatLng? = null,
    val nearbyStores: List<StoreModel> = emptyList(),
    val errorMessage: String = ""
)

