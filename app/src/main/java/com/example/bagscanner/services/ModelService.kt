package com.example.bagscanner.services

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class ModelService(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        loadModel()
    }

    private fun loadModel() {
        val modelFile = "bag_scanner_ml.tflite"
        val assetFileDescriptor = context.assets.openFd(modelFile)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val mappedByteBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        interpreter = Interpreter(mappedByteBuffer)
    }

    fun runInference(input: FloatArray): FloatArray {
        val output = Array(1) { FloatArray(NUM_CLASSES) }
        interpreter?.run(input, output)
        return output[0]
    }

    companion object {
        const val NUM_CLASSES = 3 }
}