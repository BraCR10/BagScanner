package com.example.bagscanner.views

// 1. Standard Kotlin packages
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*

// 2. Compose Foundation imports
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

// 3. Compose Material imports
import androidx.compose.material3.*

// 4. Compose UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

// 5. Project-specific imports
import com.example.bagscanner.controllers.HomeController
import com.example.bagscanner.services.CameraService
import com.example.bagscanner.services.ScannerModelService
import com.example.bagscanner.enums.BagTypes

@Composable
fun HomeScreen(controller: HomeController = viewModel()) {
    val homeState by controller.bagState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannerModelService = remember { ScannerModelService(context) }
    val cameraService = remember { CameraService(context, scannerModelService, controller) }

    /*
    *
    * With this feature the camera will blind once a new type is detected
    *
    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraService.shutdown()
        }
    }
    * */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = LocalConfiguration.current.screenHeightDp.dp * 0.2f)
                .background(Color(0xFFDAC5A0))
                .border(2.dp, Color(0xFF388E3C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Escaner de bolsos",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        // Camera box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF0F0F0))
                .border(2.dp, Color(0xFFB0B0B0))
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    cameraService.viewCamera(previewView, lifecycleOwner)
                }
            )
        }

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = LocalConfiguration.current.screenHeightDp.dp * 0.2f)
                .background(Color(0xFFDAC5A0))
                .border(2.dp, Color(0xFF388E3C)),
            contentAlignment = Alignment.Center
        ) {
            // Conversion
            val bagTypeText = when (homeState.detectedBagType) {
                BagTypes.Briefcase -> "Maletín"
                BagTypes.Bag -> "Bolso"
                BagTypes.Lunchbox -> "Lonchera"
                BagTypes.Unknown -> "Desconocido"
            }

            Text(
                text = "Tipo de bolso: $bagTypeText",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
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