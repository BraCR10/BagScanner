package com.example.bagscanner.services

// 1. Android
import android.content.Context
import android.util.Log

// 2. TensorFlow Lite
import org.tensorflow.lite.Interpreter

// 3. Java IO
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class ScannerModelService(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var isModelLoaded = false
    private val numClasses = 3
    init {
        try {
            loadModel()
            isModelLoaded = true
        } catch (e: Exception) {
            Log.e("ModelService", "Error loading model", e)
            isModelLoaded = false
        }
    }

    private fun loadModel() {
        try {
            val modelFile = "bag_scanner_ml.tflite"
            val modelCharged = context.assets.openFd(modelFile)
            val fileInputStream = FileInputStream(modelCharged.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = modelCharged.startOffset
            val declaredLength = modelCharged.declaredLength
            val mappedByteBuffer: MappedByteBuffer =
                fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            val options = Interpreter.Options()
            options.setNumThreads(4)
            interpreter = Interpreter(mappedByteBuffer, options)

            Log.d("ModelService", "Model loaded successfully")
        } catch (e: Exception) {
            Log.e("ModelService", "Error loading model", e)
            throw e
        }
    }

    fun runModel(input: FloatArray): FloatArray {
        if (!this.isModelLoaded || this.interpreter == null) {
            Log.e("ModelService", "Interpreter not initialized")
            return FloatArray(numClasses) { 0f }
        }

        try {
            val inputShape = this.interpreter!!.getInputTensor(0).shape()
            val inputSize = inputShape[1] * inputShape[2] * inputShape[3]


            if (input.size != inputSize) {
                Log.e("ModelService", "Input size mismatch: expected $inputSize, got ${input.size}")
                return FloatArray(numClasses) { 0f }
            }


            val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize)
            inputBuffer.order(ByteOrder.nativeOrder())
            for (value in input) {
                inputBuffer.putFloat(value)
            }
            inputBuffer.rewind()

            val outputBuffer = ByteBuffer.allocateDirect(4 * numClasses)
            outputBuffer.order(ByteOrder.nativeOrder())


            interpreter!!.run(inputBuffer, outputBuffer)


            outputBuffer.rewind()
            val outputArray = FloatArray(numClasses)
            for (i in 0 until numClasses) {
                outputArray[i] = outputBuffer.float
            }

            return outputArray
        } catch (e: Exception) {
            Log.e("ModelService", "Error running inference", e)
            return FloatArray(numClasses) { 0f }
        }
    }




}