package com.example.bagscanner.views

// 1. Standard Kotlin packages
import androidx.compose.runtime.Composable

// 2. Compose Foundation imports
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

// 3. Compose Material imports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text

// 4. Compose UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 5. Project-specific imports
import com.example.bagscanner.controllers.HomeController
import com.example.bagscanner.enums.Screen

@Composable
fun HomeView(controller: HomeController = viewModel()) {

    val backgroundColor = Color(0xFF8B2B2B)
    val panelColor = Color(0xFFE1C4A2)
    val textColor = Color(0xFFA52A2A)
    val exitButtonColor = Color(0xFF7D3C32)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(panelColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "BagHunter",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bag icon
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
               /* Icon(

                )*/
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            ActionButton(
                text = "Escanear bolsos",
                onClick = { controller.navigateTo(Screen.Scanner) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Explorar locales",
                onClick = { controller.navigateTo(Screen.Explore) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                text = "Localizar tienda",
                onClick = { controller.navigateTo(Screen.Locations) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Exit Button
            Button(
                onClick = { /* Exit function */ },
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .width(120.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = exitButtonColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Salir",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(240.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE8DCC5),
            contentColor = Color(0xFF783F27)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
