package com.example.bagscanner.services


// 1. CameraX imports
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

// 2. Project-specific imports
import com.example.bagscanner.controllers.HomeController
import com.example.bagscanner.enums.BagTypes
import com.google.common.util.concurrent.ListenableFuture

// 3. TensorFlow Lite imports
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

// 4. Utilities for concurrent operations
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraService(
    private val context: Context,
    private val scannerModelService: ScannerModelService,
    private val controller: HomeController
) {
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageAnalyzer: ImageAnalysis? = null


    private val imageProcessor by lazy {
        ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
    }

    fun viewCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            try {
                                val tensorImage = processImage(imageProxy)
                                val result = scannerModelService.runModel(tensorImage)
                                val detectedBagType = recognizeScannerDetection(result)

                                ContextCompat.getMainExecutor(context).execute {
                                    controller.updateBagType(detectedBagType)
                                }
                            } catch (e: Exception) {
                                Log.e("CameraService", "Error processing image", e)
                            } finally {
                                imageProxy.close()
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
        }, ContextCompat.getMainExecutor(context))
    }

    private fun processImage(imageProxy: ImageProxy): TensorImage {
        val tensorImage = TensorImage(DataType.FLOAT32)
        val bitmap = imageProxy.toBitmap()
        tensorImage.load(bitmap)

        return imageProcessor.process(tensorImage)
    }

    private fun recognizeScannerDetection(result: FloatArray): BagTypes {
        return when (result.indices.maxByOrNull { result[it] } ?: -1) {
            0 -> BagTypes.Briefcase
            1 -> BagTypes.Bag
            2 -> BagTypes.Lunchbox
            else -> BagTypes.Unknown
        }
    }

    fun shutdown() {
        cameraExecutor.shutdown()
        imageAnalyzer?.clearAnalyzer()
    }
}