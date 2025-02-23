package com.example.bagscanner.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bagscanner.controllers.HomeController

@Composable
fun HomeScreen(controller: HomeController = viewModel()) {
    val homeState by controller.bagState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bags Scanner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Camera preview placeholder
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "[Camera box]", color = Color.Gray, fontSize = 18.sp)
        }

        // Footer with detected bag type
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bag type: ${homeState.detectedBagType}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val simulatedHomeController = HomeController()
    HomeScreen(controller = simulatedHomeController)
}
