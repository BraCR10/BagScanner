package com.example.bagscanner.controllers

import androidx.compose.runtime.Composable
import com.example.bagscanner.enums.Screen

interface IControllers {
    @Composable
    fun DisplayScreen()

    fun navigateTo(screen: Screen)
}