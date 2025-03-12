package com.example.bagscanner.views

// 1. Standard Kotlin packages
import android.content.Context
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

// 5. Project-specific imports
import com.example.bagscanner.controllers.ScannerController
import com.example.bagscanner.services.CameraService
import com.example.bagscanner.services.ScannerModelService
import com.example.bagscanner.enums.BagTypes
import com.example.bagscanner.enums.Screen

@Composable
fun ScannerView(controller: ScannerController = viewModel()) {
    val homeState by controller.bagState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannerModelService = remember { ScannerModelService(context) }
    val cameraService = remember { CameraService(context, scannerModelService, controller) }

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

            IconButton(
                onClick = { controller.navigateTo(Screen.Home) },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(40.dp)
            ) {
                /*Image(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Volver",
                    modifier = Modifier.size(24.dp)
                )*/
            }
        }

        // Camera box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF0F0F0))
                .border(2.dp, Color(0xFFB0B0B0))
        ) {
            val previewView = rememberPreviewView(context)
            RenderPreviewView(previewView, cameraService, lifecycleOwner)
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
                text = "Objeto encontrado: $bagTypeText",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}
@Composable
fun rememberPreviewView(context: Context): PreviewView {
    val previewView = remember { PreviewView(context) }

    val layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    previewView.layoutParams = layoutParams

    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

    return previewView
}

@Composable
fun RenderPreviewView(previewView: PreviewView, cameraService: CameraService, lifecycleOwner: LifecycleOwner) {
    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize(),
        update = { cameraService.viewCamera(it, lifecycleOwner) }
    )
}

