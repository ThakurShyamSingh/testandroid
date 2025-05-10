package com.example.demotest2.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.scale
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.*

class FaceEmbeddingProcessor(private val context: Context) {

    private val faceDetector: Interpreter
    private val landmarkDetector: Interpreter
    private val embedder: Interpreter

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)  // Adjust threads for your device
            setUseXNNPACK(true)
        }
        faceDetector = Interpreter(loadMappedFile("face_detector.tflite"), options)
        landmarkDetector = Interpreter(loadMappedFile("face_landmarks_detector.tflite"), options)
        embedder = Interpreter(loadMappedFile("model.tflite"), options)
    }

    fun getEmbedding(frame: Bitmap): FloatArray? {
        val detection = detectFace(frame) ?: return null
        val faceCrop = cropAndAlignFace(frame, detection) ?: return null
        val embedding = computeEmbedding(faceCrop)
        return embedding
    }

    data class FaceDetectionResult(
        val boundingBox: RectF,
        val keypoints: List<PointF> // [leftEye, rightEye, nose, mouth, leftTragion, rightTragion]
    )

    private fun detectFace(bitmap: Bitmap): FaceDetectionResult? {
        val resized = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
        val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
        inputBuffer.loadArray(bitmapToNormalizedArray(resized))

        val outputRegressors = TensorBuffer.createFixedSize(intArrayOf(1, 896, 16), DataType.FLOAT32)
        val outputScores = TensorBuffer.createFixedSize(intArrayOf(1, 896), DataType.FLOAT32)

        val outputs = mapOf(
            0 to outputRegressors.buffer,
            1 to outputScores.buffer
        )
        faceDetector.runForMultipleInputsOutputs(arrayOf(inputBuffer.buffer), outputs)

        val scores = outputScores.floatArray
        var bestIndex = -1
        var bestScore = 0.5f
        for (i in scores.indices) {
            if (scores[i] > bestScore) {
                bestScore = scores[i]
                bestIndex = i
            }
        }
        if (bestIndex < 0) return null

        val r = outputRegressors.floatArray
        val ymin = r[bestIndex * 16 + 0]
        val xmin = r[bestIndex * 16 + 1]
        val ymax = r[bestIndex * 16 + 2]
        val xmax = r[bestIndex * 16 + 3]

        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val rect = RectF(xmin * width, ymin * height, xmax * width, ymax * height)

        val keypoints = mutableListOf<PointF>()
        for (j in 0 until 6) {
            val x = r[bestIndex * 16 + 4 + j * 2] * width
            val y = r[bestIndex * 16 + 5 + j * 2] * height
            keypoints.add(PointF(x, y))
        }

        return FaceDetectionResult(rect, keypoints)
    }

    private fun cropAndAlignFace(src: Bitmap, det: FaceDetectionResult): Bitmap? {
        val box = RectF(det.boundingBox)
        box.inset(-0.25f * box.width(), -0.25f * box.height())
        box.left = box.left.coerceAtLeast(0f)
        box.top = box.top.coerceAtLeast(0f)
        box.right = box.right.coerceAtMost(src.width.toFloat())
        box.bottom = box.bottom.coerceAtMost(src.height.toFloat())

        val leftEye = det.keypoints[0]
        val rightEye = det.keypoints[1]
        val nose = det.keypoints[2]
        val mouth = det.keypoints[3]

        val dx = rightEye.x - leftEye.x
        val dy = rightEye.y - leftEye.y
        val eyeAngle = atan2(dy.toDouble(), dx.toDouble()).toFloat()

        val noseMouthAngle = atan2((mouth.y - nose.y).toDouble(), (mouth.x - nose.x).toDouble()).toFloat()
        val angle = Math.toDegrees(((eyeAngle + noseMouthAngle) / 2).toDouble()).toFloat()

        val matrix = Matrix().apply {
            postRotate(-angle, box.centerX(), box.centerY())
        }

        val rotated = try {
            Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        } catch (e: IllegalArgumentException) {
            Log.e("FaceAlign", "Rotation failed: ${e.message}")
            return null
        }

        val crop = try {
            Bitmap.createBitmap(
                rotated,
                box.left.toInt(),
                box.top.toInt(),
                box.width().toInt(),
                box.height().toInt()
            )
        } catch (e: IllegalArgumentException) {
            Log.e("FaceAlign", "Cropping failed: ${e.message}")
            return null
        }

        return crop.scale(256, 256)
    }

    private fun computeEmbedding(face256: Bitmap): FloatArray {
        val inp = face256.scale(112, 112)
        val buf = TensorBuffer.createFixedSize(intArrayOf(1, 112, 112, 3), DataType.FLOAT32)
        buf.loadArray(bitmapToNormalizedArray(inp))
        val out = TensorBuffer.createFixedSize(intArrayOf(1, 512), DataType.FLOAT32)
        embedder.run(buf.buffer, out.buffer)
        return out.floatArray
    }

    private fun loadMappedFile(path: String): MappedByteBuffer {
        val afd = context.assets.openFd(path)
        val fis = FileInputStream(afd.fileDescriptor)
        return fis.channel.map(FileChannel.MapMode.READ_ONLY, afd.startOffset, afd.declaredLength)
    }

    private fun bitmapToNormalizedArray(bmp: Bitmap): FloatArray {
        val w = bmp.width
        val h = bmp.height
        val px = IntArray(w * h).also { bmp.getPixels(it, 0, w, 0, 0, w, h) }
        return FloatArray(w * h * 3).also { arr ->
            var i = 0
            for (p in px) {
                val r = ((p shr 16) and 0xFF) / 255f
                val g = ((p shr 8) and 0xFF) / 255f
                val b = (p and 0xFF) / 255f
                arr[i++] = r * 2 - 1
                arr[i++] = g * 2 - 1
                arr[i++] = b * 2 - 1
            }
        }
    }
}
