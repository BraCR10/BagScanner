package com.example.bagscanner.controllers

// 1. Jetpack Compose & ViewModel imports
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

// 2. Project-specific imports
import com.example.bagscanner.models.BagModel
import com.example.bagscanner.views.HomeScreen
import com.example.bagscanner.enums.BagTypes

// 3. Kotlin Coroutines imports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ScannerController : ViewModel() {
    // Attributes
    private val _currentBag = MutableStateFlow(BagModel())
    val bagState: StateFlow<BagModel> = _currentBag
    private var lastDetectedType: BagTypes = BagTypes.Unknown
    private var detectionCount = 0
    private val detectionUpdateLimit = 5

    //Methods 
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
