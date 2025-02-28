package com.example.bagscanner.services

// Importing necessary Android and CameraX libraries
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

// Importing concurrency utilities
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Importing application-specific services
import com.example.bagscanner.enums.BagTypes
import com.example.bagscanner.controllers.HomeController


class CameraService(private val context: Context, private val scannerModelService: ScannerModelService, private val controller: HomeController) {

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageAnalyzer: ImageAnalysis? = null

    fun viewCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderThread: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraProviderThread.addListener(
            {
                try {
                    val cameraProvider: ProcessCameraProvider = cameraProviderThread.get()
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }


                    imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor) { image ->
                                try {
                                    val bitmap = image.toBitmap()
                                    val input = processImage(bitmap)
                                    val result = scannerModelService.runModel(input)
                                    val detectedBagType = recognizeScannerDetection(result)

                                    ContextCompat.getMainExecutor(context).execute {
                                        controller.updateBagType(detectedBagType)
                                    }
                                } catch (e: Exception) {
                                    Log.e("CameraService", "Error processing image", e)
                                } finally {
                                    image.close()
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )

                } catch (exc: Exception) {
                    Log.e("CameraService", "Error binding camera use cases", exc)
                }
            },
            ContextCompat.getMainExecutor(context))
    }

    private fun processImage(bitmap: Bitmap): FloatArray {
        val modelInputSize = 225
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputSize, modelInputSize, true)

        val inputArray = FloatArray(modelInputSize * modelInputSize * 3)
        val pixelValues = IntArray(modelInputSize * modelInputSize)
        resizedBitmap.getPixels(pixelValues, 0, modelInputSize, 0, 0, modelInputSize, modelInputSize)

        for (i in pixelValues.indices) {
            val r = (pixelValues[i] shr 16 and 0xFF) / 255.0f
            val g = (pixelValues[i] shr 8 and 0xFF) / 255.0f
            val b = (pixelValues[i] and 0xFF) / 255.0f


            inputArray[i * 3] = (r - 0.5f) * 2.0f
            inputArray[i * 3 + 1] = (g - 0.5f) * 2.0f
            inputArray[i * 3 + 2] = (b - 0.5f) * 2.0f
        }

        return inputArray
    }

    private fun recognizeScannerDetection(result: FloatArray): BagTypes {
        if (result.isEmpty()) return BagTypes.Unknown
        var maxIndex = 0
        var maxValue = result[0]

        for (i in 1 until result.size) {
            if (result[i] > maxValue) {
                maxIndex = i
                maxValue = result[i]
            }
        }

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