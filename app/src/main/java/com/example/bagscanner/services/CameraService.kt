package com.example.bagscanner.services

// 1. Standard Android packages
import android.content.Context
import android.util.Log

// 2. CameraX and Lifecycle-related imports
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

// 3. Permission and Context-related imports
import androidx.core.content.ContextCompat

// 4. Google and Java utility imports
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraService(private val context: Context) {

    //The camera service runs once it is instanced
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // This function is calling on the UI to show the camera in real time
    fun viewCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {

        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
                {
                try {
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )

                } catch (exc: Exception) {
                    Log.e("CameraService", "Error camera does not work ", exc)
                }
            },
            ContextCompat.getMainExecutor(context))
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
