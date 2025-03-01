package com.example.bagscanner.services

// 1. Android Imports
import android.content.Context
import android.util.Log

// 2. TensorFlow Lite Imports for Model and Image Processing
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class ScannerModelService(private val context: Context) {
    private var model: Model? = null
    private var inputTensorShape: IntArray? = null

    init {
        try {
            model = Model.createModel(context, "bag_scanner_ml.tflite")
            inputTensorShape = model?.getInputTensor(0)?.shape()
            Log.d("ModelService", "Model loaded successfully. Input shape: ${inputTensorShape?.contentToString()}")
        } catch (e: Exception) {
            Log.e("ModelService", "Error loading model", e)
        }
    }

    fun runModel(tensorImage: TensorImage): FloatArray {
        return try {
            if (model == null) {
                Log.e("ModelService", "Model not initialized")
                return FloatArray(0)
            }

            // Create buffer output
            val outputTensor = model?.getOutputTensor(0)
            val outputShape = outputTensor?.shape() ?: intArrayOf(1, 3)
            val outputBuffer = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)

            // Run model
            val inputs = arrayOf<Any>(tensorImage.buffer)
            val outputs = mutableMapOf<Int, Any>().apply {
                put(0, outputBuffer.buffer.rewind())
            }

            model?.run(inputs, outputs)

            Log.d("ModelService", "output: ${outputBuffer.floatArray.joinToString()}")
            // Output
            outputBuffer.floatArray
        } catch (e: Exception) {
            Log.e("ModelService", "Error running model", e)
            FloatArray(0)
        }
    }

    fun shutdown() {
        model?.close()
    }
}