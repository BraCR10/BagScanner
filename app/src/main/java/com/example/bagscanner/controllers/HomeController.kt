package com.example.bagscanner.controllers


import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
