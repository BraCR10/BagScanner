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
    //Attributes
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageAnalyzer: ImageAnalysis? = null
    private val imageProcessor: ImageProcessor = createImageProcessor()

    //Methods
    fun viewCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraThread: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)

        cameraThread.addListener(
            {
            try {
                val cameraProvider: ProcessCameraProvider = cameraThread.get()
                val preview = createPreview(previewView)
                val imageAnalyzer = createImageAnalyzer()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e("CameraService", "Error setting up camera uses cases", exc)
            }
        },ContextCompat.getMainExecutor(context))
    }

    private fun createImageProcessor(): ImageProcessor {
        val builder = ImageProcessor.Builder()
        //224x224
        builder.add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
        //TODO: Set proper way to normalize the img
        builder.add(NormalizeOp(0f, 255f))
        return builder.build()
    }

    private fun createPreview(previewView: PreviewView): Preview {
        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider
        return preview
    }

    private fun createImageAnalyzer(): ImageAnalysis {
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        //Setting analyzer
        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy -> scanImage(imageProxy) }
        return imageAnalyzer
    }

    private fun scanImage(image: ImageProxy) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val tensorImage = processImgToTensor(image)
                val result = scannerModelService.runModel(tensorImage)
                val detectedBagType = recognizeScannerDetection(result)
                updateBagTypeOnMainThread(detectedBagType)
            } catch (e: Exception) {
                Log.e("CameraService", "Error scanning image", e)
            } finally {
                image.close()
            }
        }
    }

    private fun updateBagTypeOnMainThread(detectedBagType: BagTypes) {
        ContextCompat.getMainExecutor(context).execute {
            controller.updateBagType(detectedBagType)
        }
    }

    private fun processImgToTensor(image: ImageProxy): TensorImage {
        val tensorImage = TensorImage(DataType.FLOAT32)
        val bitmap = image.toBitmap()
        tensorImage.load(bitmap)

        return this.imageProcessor.process(tensorImage)
    }

    private fun recognizeScannerDetection(result: FloatArray): BagTypes {
        if (result.isEmpty()) {
            return BagTypes.Unknown
        }

        var maxIndex = 0
        var maxValue = result[0]
        for (i in 1 until result.size) {
            if (result[i] > maxValue) {
                maxValue = result[i]
                maxIndex = i
            }
        }

        return when (maxIndex) {
            0 -> BagTypes.Bag
            1 -> BagTypes.Briefcase
            2 -> BagTypes.Lunchbox
            else -> BagTypes.Unknown
        }
    }

    fun shutdown() {
        cameraExecutor.shutdown()
        imageAnalyzer?.clearAnalyzer()
    }
}