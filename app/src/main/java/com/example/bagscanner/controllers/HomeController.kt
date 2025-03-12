package com.example.bagscanner.controllers


// 1. Standard imports
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

// 2. Project-specific imports
import com.example.bagscanner.enums.Screens
import com.example.bagscanner.views.HomeView


class HomeController(private val navController: NavHostController) :
    ViewModel(),IControllers  {

    @Composable
    override fun DisplayScreen() {
        HomeView(this)
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