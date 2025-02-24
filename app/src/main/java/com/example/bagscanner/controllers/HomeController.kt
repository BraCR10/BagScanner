package com.example.bagscanner.controllers



// 1. Standard Kotlin packages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 2. Compose imports
import androidx.compose.runtime.Composable

// 3. Jetpack libraries
import androidx.lifecycle.ViewModel

// 4. Project-specific imports
import com.example.bagscanner.models.HomeModel
import com.example.bagscanner.views.HomeScreen
import com.example.bagscanner.enums.BagTypes


class HomeController : ViewModel() {

    private val _currentBag = MutableStateFlow(HomeModel())

    val bagState: StateFlow<HomeModel> = _currentBag

    fun updateBagType(newType: BagTypes) {
        _currentBag.value = HomeModel(detectedBagType = newType)
    }

    @Composable
    fun DisplayScreen() {
        HomeScreen(this)
    }
}
