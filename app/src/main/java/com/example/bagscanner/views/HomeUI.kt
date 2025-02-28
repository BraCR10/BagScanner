package com.example.bagscanner.views

// 1. Standard Kotlin packages
import androidx.compose.runtime.*

// 2. Compose Foundation imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

// 3. Compose Material imports
import androidx.compose.material3.*

// 4. Compose UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 5. Project-specific imports
import com.example.bagscanner.controllers.HomeController


@Composable
fun HomeScreen(controller: HomeController = viewModel()) {
    val homeState by controller.bagState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = LocalConfiguration.current.screenHeightDp.dp * 0.3f)
                .background(Color(0xFFDAC5A0))
                .border(2.dp, Color(0xFF388E3C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bags Scanner",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Camera preview placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF0F0F0))
                .border(2.dp, Color(0xFFB0B0B0)),
            contentAlignment = Alignment.Center
        ) {

            Text(text = "[Camera box]", color = Color.Gray, fontSize = 18.sp)
        }


        // Footer with detected bag type
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = LocalConfiguration.current.screenHeightDp.dp * 0.2f)
                .background(Color(0xFFDAC5A0))
                //.clip(shape = RoundedCornerShape(10.dp))
                .border(2.dp, Color(0xFF388E3C)),

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bag type: ${homeState.detectedBagType}",
                fontSize = 30.sp,
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
