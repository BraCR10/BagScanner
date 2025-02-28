package com.example.bagscanner.services

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat

import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.bagscanner.services.ModelService
import com.example.bagscanner.enums.BagTypes
import com.example.bagscanner.controllers.HomeController

class CameraService(private val context: Context, private val modelService: ModelService, private val controller: HomeController) {

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

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

                    val imageCapture = ImageCapture.Builder()
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = image.toBitmap()
                                val input = preprocessImage(bitmap)
                                val result = modelService.runInference(input)
                                val detectedBagType = postprocessResult(result)
                                controller.updateBagType(detectedBagType)
                                image.close()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraService", "Error capturing image", exception)
                            }
                        }
                    )

                } catch (exc: Exception) {
                    Log.e("CameraService", "Error camera does not work ", exc)
                }
            },
            ContextCompat.getMainExecutor(context))
    }

    private fun preprocessImage(bitmap: Bitmap): FloatArray {
        val modelInputSize = 224
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputSize, modelInputSize, true)

        val inputArray = FloatArray(modelInputSize * modelInputSize * 3)
        val pixelValues = IntArray(modelInputSize * modelInputSize)
        resizedBitmap.getPixels(pixelValues, 0, modelInputSize, 0, 0, modelInputSize, modelInputSize)

        for (i in pixelValues.indices) {
            val r = (pixelValues[i] shr 16 and 0xFF) / 255.0f
            val g = (pixelValues[i] shr 8 and 0xFF) / 255.0f
            val b = (pixelValues[i] and 0xFF) / 255.0f
            inputArray[i * 3] = r
            inputArray[i * 3 + 1] = g
            inputArray[i * 3 + 2] = b
        }

        return inputArray
    }

    private fun postprocessResult(result: FloatArray): BagTypes{
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: 0

        return when (maxIndex) {
            0 -> BagTypes.Briefcase
            1 -> BagTypes.Bag
            2 -> BagTypes.Lunchbox
            else -> BagTypes.Unknown
        }
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}
