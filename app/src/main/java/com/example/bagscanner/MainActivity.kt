package com.example.bagscanner

// 1. Standard Android packages
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.provider.Settings
import android.net.Uri

// 2. Activity and Lifecycle-related imports
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

// 3. Permission-related imports
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel


// 4. Project-specific imports
import com.example.bagscanner.controllers.HomeController

class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startApp()
        } else {
            Toast.makeText(this, "La aplicación requiere permiso de cámara para funcionar", Toast.LENGTH_LONG).show()
            showSettingsPrompt()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check camera access
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
            startApp()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startApp() {
        try {
            setContent {
                val controller: HomeController = viewModel()
                controller.DisplayScreen()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar la aplicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSettingsPrompt() {
        Toast.makeText(
            this,
            "Por favor habilita el permiso de cámara en la configuración",
            Toast.LENGTH_LONG
        ).show()

        // Option to go native cell phone settings
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        finish()
    }
}