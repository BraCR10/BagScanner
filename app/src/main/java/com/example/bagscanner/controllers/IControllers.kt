package com.example.bagscanner.controllers

// 1. Standard imports
import androidx.compose.runtime.Composable

// 2. Project-specific imports
import com.example.bagscanner.enums.Screens


interface IControllers {
    @Composable
    fun DisplayScreen()

    fun navigateTo(screen: Screens)
}