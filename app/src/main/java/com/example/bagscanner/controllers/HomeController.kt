package com.example.bagscanner.controllers


import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.bagscanner.enums.Screen
// 2. Project-specific imports
import com.example.bagscanner.views.HomeView

class HomeController(private val navController: NavHostController) :
    ViewModel(),IControllers  {

    @Composable
    override fun DisplayScreen() {
        HomeView(this)
    }

    override fun navigateTo(screen: Screen) {
        when (screen) {
            Screen.Home -> navController.navigate("home")
            Screen.Scanner -> navController.navigate("scanner")
            Screen.Explore -> navController.navigate("explore")
            Screen.Locations -> navController.navigate("locations")
        }
    }
}