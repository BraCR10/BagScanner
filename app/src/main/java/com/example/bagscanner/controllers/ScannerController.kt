package com.example.bagscanner.controllers

// 1. Jetpack Compose & ViewModel imports
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

// 2. Project-specific imports
import com.example.bagscanner.models.BagModel
import com.example.bagscanner.views.ScannerView
import com.example.bagscanner.enums.BagTypes
import com.example.bagscanner.enums.Screens

// 3. Kotlin  imports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ScannerController(private val navController: NavHostController) :
    ViewModel(),IControllers {
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
    override fun DisplayScreen() {
        ScannerView(this)
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
