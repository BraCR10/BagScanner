package com.example.bagscanner.models

data class StoreModel(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Float // meters
)