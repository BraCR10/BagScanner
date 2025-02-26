package com.example.bagscanner

// 1. Standard Android packages
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast

// 2. Activity and Lifecycle-related imports
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

// 3. Permission-related imports
import androidx.core.content.ContextCompat

// 4. Project-specific imports
import com.example.bagscanner.controllers.HomeController

class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted)  {
            Toast.makeText(this, "Please, enable the camera to the app!! ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setContent {
                val controller = HomeController()
                controller.DisplayScreen()
            }
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }
}