package com.example.bagscanner.controllers

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavController() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeController: HomeController = viewModel {
                HomeController(navController)
            }
            homeController.DisplayScreen()
        }
        composable("scanner") {
            val scannerController: ScannerController = viewModel {
                ScannerController(navController)
            }
            scannerController.DisplayScreen()
        }

    }
}