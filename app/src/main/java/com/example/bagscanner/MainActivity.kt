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

// 4. Project-specific imports
import com.example.bagscanner.controllers.NavController
import androidx.compose.foundation.Image



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showSettingsPrompt() {
        Toast.makeText(
            this,
            "Por favor habilita el permiso de cámara y ubicacion en la configuración",
            Toast.LENGTH_LONG
        ).show()

        // Option to go native cell phone settings
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        finish()
    }
    private val cameraPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            checkLocationPermission()
        } else {
            Toast.makeText(this, "La aplicación requiere permiso de cámara y ubicacion para funcionar", Toast.LENGTH_LONG).show()
            showSettingsPrompt()
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ->
            {
                startApp()
            }
            else -> {
                showSettingsPrompt()
            }
        }
    }

    private fun checkLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    private fun startApp() {
        try {
            setContent {
                NavController()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al iniciar la aplicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

