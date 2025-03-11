package com.example.bagscanner.controllers


import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.bagscanner.enums.Screen
// 2. Project-specific imports
import com.example.bagscanner.views.HomeView

class HomeController : ViewModel() {
    // Attributes

    //Methods

    @Composable
    fun DisplayScreen() {
        HomeView(this)
    }

    fun navigateTo(screen: Screen){

    }
}