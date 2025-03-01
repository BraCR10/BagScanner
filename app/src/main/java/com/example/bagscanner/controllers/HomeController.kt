package com.example.bagscanner.controllers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.bagscanner.models.BagModel
import com.example.bagscanner.views.HomeScreen
import com.example.bagscanner.enums.BagTypes

class HomeController : ViewModel() {

    private val _currentBag = MutableStateFlow(BagModel())
    val bagState: StateFlow<BagModel> = _currentBag

    private var lastDetectedType: BagTypes = BagTypes.Unknown
    private var detectionCount = 0

    private val detectionUpdateLimit = 5

    fun updateBagType(newType: BagTypes) {
        try {
            if (newType == lastDetectedType) {
                detectionCount++

                if (detectionCount >= detectionUpdateLimit &&
                    _currentBag.value.detectedBagType != newType) {
                    _currentBag.value = BagModel(detectedBagType = newType)
                }
            } else {

                lastDetectedType = newType
                detectionCount = 1
            }
        } catch (e: Exception) {
           println("Error has occurred updating the bag state")
        }
    }

    @Composable
    fun DisplayScreen() {
        HomeScreen(this)
    }
}
