package com.example.bagscanner.services

// 1. Android Imports
import android.content.Context
import android.util.Log

// 2. TensorFlow Lite Imports for Model and Image Processing
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class ScannerModelService(context: Context) {
    //Attributes
    private var model: Model? = null
    private var expectedTensorMetrics: IntArray? = null

    //Constructor
    init {
        try {
            model = Model.createModel(context, "bag_scanner_ml.tflite")
            expectedTensorMetrics = model?.getInputTensor(0)?.shape()
            Log.d("ModelService", "Model loaded successfully. Input expected: ${expectedTensorMetrics?.contentToString()}")
        } catch (e: Exception) {
            Log.e("ModelService", "Error loading model", e)
        }
    }

    //Methods
    fun runModel(tensorImage: TensorImage): FloatArray {
        if (model == null) {
            Log.e("ModelService", "Model not initialized")
            return FloatArray(0)
        }

        try {
            // Validate input tensor size
            if (expectedTensorMetrics != null) {
                val expectedHeight = expectedTensorMetrics!![1]
                val expectedWidth = expectedTensorMetrics!![2]

                if (tensorImage.height != expectedHeight || tensorImage.width != expectedWidth) {
                    Log.e("ModelService",
                        "Invalid input size: expected ${expectedWidth}x${expectedHeight}, but got ${tensorImage.width}x${tensorImage.height}")
                    return FloatArray(0)
                }
            }

            // Create output buffer
            val outputTensor = model?.getOutputTensor(0)
            val outputData = outputTensor?.shape() ?: intArrayOf(0,0,0,1)//Unknown
            val outputProcessed = TensorBuffer.createFixedSize(outputData, DataType.FLOAT32)

            // Ensure the output buffer has enough space
            var requiredCapacity = 1
            for (i in outputData) {
                requiredCapacity *= i
            }

            if (outputProcessed.buffer.capacity() < requiredCapacity) {
                Log.e("ModelService", "Output processed buffer is too small for the output shape: ${outputData.contentToString()}")
                return FloatArray(0)
            }

            // Prepare inputs and outputs
            val inputs = arrayOf<Any>(tensorImage.buffer)
            val outputs = mutableMapOf<Int, Any>()

            //Just 1 exit
            outputs[0] = outputProcessed.buffer.rewind()

            model?.run(inputs, outputs)

            val result = outputProcessed.floatArray

            return result
        } catch (e: Exception) {
            Log.e("ModelService", "Error running model", e)
            return FloatArray(0)
        }
    }

    fun shutdown() {
        model?.close()
    }
}