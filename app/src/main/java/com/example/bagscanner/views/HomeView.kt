package com.example.bagscanner.views

// 1. Standard Kotlin packages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// 2. Compose Foundation imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

// 3. Compose Material imports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider

// 4. Compose UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// 5. Project-specific imports
import com.example.bagscanner.controllers.HomeController
import com.example.bagscanner.enums.Screens


@Composable
fun HomeView(controller: HomeController = viewModel()) {
    val backgroundColor = Color(0xFF8B2B2B)
    val panelColor = Color(0xFFE1C4A2)
    val textColor = Color(0xFFA52A2A)
    val exitButtonColor = Color(0xFF7D3C32)
    val menuButtonColor = Color(0xFFE8A87C)
    val menuBackgroundColor = Color(0xFF5D4037)

    // State for burger  menu
    val isMenuVisible = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Main panel
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(panelColor)
                .border(2.dp, Color(0xFFBE9B7B), RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "BagsHunter",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bienvenido a nuestra APP",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = """
                    Esta app es un proyecto académico que puede reconocer 3 objetos:
                    
                    1. Bolso
                    2. Maletín
                    3. Lonchera
                    
                    Además, tiene una función para ver tiendas de bolsos cercanas y localizar una tienda específica.
                """.trimIndent(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFFBE9B7B), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                //Logo img
            }

            Spacer(modifier = Modifier.weight(1f))

            // Exit Button
            Button(
                onClick = { /* Exit function */ },
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .width(120.dp)
                    .height(48.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
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

        // Burger Menu Manager
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(menuButtonColor)
                .border(2.dp, Color.White, RoundedCornerShape(22.dp))
                .shadow(6.dp, RoundedCornerShape(22.dp))
                .clickable { isMenuVisible.value = !isMenuVisible.value },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ">",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D4037)
            )
        }

        // Burger Menu Pop Up
        if (isMenuVisible.value) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(menuBackgroundColor)
                    .padding(end = 2.dp)
                    .align(Alignment.CenterStart)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Header section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BagsHunter",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0x22FFFFFF))
                                .border(1.dp, Color.White, RoundedCornerShape(18.dp))
                                .clickable { isMenuVisible.value = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 24.dp),
                        thickness = 1.dp,
                        color = Color(0x66FFFFFF)
                    )

                    // Menu Items
                    MenuOption  (text="Escanear bolsos") {
                        controller.navigateTo(Screens.Scanner)
                        isMenuVisible.value = false
                    }

                    MenuOption(text = "Explorar locales") {
                        controller.navigateTo(Screens.Explore)
                        isMenuVisible.value = false
                    }

                    MenuOption(text = "Localizar tienda") {
                        controller.navigateTo(Screens.Locations)
                        isMenuVisible.value = false
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer section
                    Text(
                        text = "v2.0.0",
                        fontSize = 14.sp,
                        color = Color(0xAAFFFFFF),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuOption(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33FFFFFF))
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}