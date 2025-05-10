package com.example.demotest2.face

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.scale
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.OutputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.atan2

class FaceEmbeddingProcessor(private val context: Context) {

    private val faceDetector: Interpreter
    private val landmarkDetector: Interpreter
    private val embedder: Interpreter

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            setUseXNNPACK(true)
        }
        faceDetector = Interpreter(loadMappedFile("face_detector.tflite"), options)
        landmarkDetector = Interpreter(loadMappedFile("face_landmarks_detector.tflite"), options)
        embedder = Interpreter(loadMappedFile("model.tflite"), options)
    }

    fun getEmbedding(frame: Bitmap): FloatArray? {


        val detection = detectFace(frame) ?: return null

        val cropped = cropFace(frame, detection) ?: return null

        var bit=frame.scale(256, 256)

        val landmarks = detectLandmarks(bit)
        Log.d("FaceEmbeddingProcessor", "Landmarks detected: size = ${landmarks.size}")
        val sb = StringBuilder()

        for (i in landmarks.indices step 3) {
            if (i + 2 < landmarks.size) {
                sb.append("x,y,${landmarks[i + 2]}| ")
            }
        }

        Log.d("YourTag", sb.toString())

        Log.d("FaceEmbeddingProcessor", "Landmarks detected: ${landmarks.joinToString(",")}")
        saveBitmapToGallery(bit,"resized before landmarks")
        val resultBitmap = drawLandmarksOnBitmap(bit, landmarks)
        saveBitmapToGallery(resultBitmap, "landmarks_overlay")

        val overlayedBitmap = overlayLandmarksOnBitmap(landmarks, bit)
        saveBitmapToGallery(overlayedBitmap, "landmarks_overlay")

        val overlay = overlayEyesNoseMouth(landmarks,bit)
        saveBitmapToGallery(overlay, "landmarks_overlay_eyes_nose_mouth")

        val face112 = cropFaceTo112(landmarks, bit)
        val face112withp= drawLandmarksOnBitmap(face112.scale(256, 256), landmarks)
        val face112withpoints=overlayLandmarksOnBitmap(landmarks, face112.scale(256, 256))
        val eyesnose= overlayEyesNoseMouth(landmarks, face112.scale(256, 256))
        saveBitmapToGallery(eyesnose, "eyes_nose_mouth_overlay")
        saveBitmapToGallery(face112, "face_112")
        saveBitmapToGallery(face112withpoints, "face_112_with_points")
        saveBitmapToGallery(face112withp, "face_112_with_p")

        val aligned = alignFace(cropped, detection) ?: return null

        val finalInput = aligned.scale(112, 112)
        saveBitmapToGallery(finalInput, "face_final112")

        return computeEmbedding(finalInput)
    }

    data class FaceDetectionResult(
        val boundingBox: RectF,
        val keypoints: List<PointF>
    )

    private fun detectFace(bitmap: Bitmap): FaceDetectionResult? {
        val resized = bitmap.scale(128, 128)
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

    private fun detectLandmarks(face256: Bitmap): FloatArray {
        Log.d("FaceEmbeddingProcessor", "Detecting landmarks")
        val buf = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
        buf.loadArray(bitmapToUnitArray(face256))
        val out = TensorBuffer.createFixedSize(intArrayOf(1, 478, 3), DataType.FLOAT32)
        landmarkDetector.run(buf.buffer, out.buffer)
        Log.d("FaceEmbeddingProcessor", "Landmarks detection complete")
        return out.floatArray
    }

    private fun bitmapToUnitArray(bmp: Bitmap): FloatArray {
        Log.d("FaceEmbeddingProcessor", "Converting bitmap to unit array [0,1]")
        val w = bmp.width
        val h = bmp.height
        val px = IntArray(w * h)
        bmp.getPixels(px, 0, w, 0, 0, w, h)
        val arr = FloatArray(w * h * 3)
        var i = 0
        for (p in px) {
            arr[i++] = (p shr 16 and 0xFF) / 255f
            arr[i++] = (p shr 8 and 0xFF) / 255f
            arr[i++] = (p and 0xFF) / 255f
        }
        return arr
    }

    private fun cropFace(src: Bitmap, det: FaceDetectionResult): Bitmap? {
        val keypoints = det.keypoints
        if (keypoints.size < 5) return null

        val leftEye = keypoints[0]
        val rightEye = keypoints[1]
        val nose = keypoints[2]
        val mouthLeft = keypoints[3]
        val mouthRight = keypoints[4]

        val eyeCenter = PointF((leftEye.x + rightEye.x) / 2f, (leftEye.y + rightEye.y) / 2f)
        val mouthCenter = PointF((mouthLeft.x + mouthRight.x) / 2f, (mouthLeft.y + mouthRight.y) / 2f)

        val centerX = (eyeCenter.x + mouthCenter.x) / 2f
        val centerY = (eyeCenter.y + mouthCenter.y) / 2f

        val faceHeight = (mouthCenter.y - eyeCenter.y) * 2.5f
        val faceWidth = (rightEye.x - leftEye.x) * 2.5f

        val left = (centerX - faceWidth / 2f).coerceAtLeast(0f)
        val top = (centerY - faceHeight / 2f).coerceAtLeast(0f)
        val right = (centerX + faceWidth / 2f).coerceAtMost(src.width.toFloat())
        val bottom = (centerY + faceHeight / 2f).coerceAtMost(src.height.toFloat())

        val width = (right - left).toInt()
        val height = (bottom - top).toInt()

        Log.d("SmartCrop", "$keypoints")

        return try {
            Bitmap.createBitmap(src, left.toInt(), top.toInt(), width, height)

        } catch (e: Exception) {
            Log.e("SmartCrop", "Failed to crop: ${e.message}")
            null
        }
    }


    private fun alignFace(bmp: Bitmap, det: FaceDetectionResult): Bitmap? {
        val leftEye = det.keypoints[0]
        val rightEye = det.keypoints[1]
        val dx = rightEye.x - leftEye.x
        val dy = rightEye.y - leftEye.y
        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

        val matrix = Matrix().apply {
            postRotate(-angle)
        }

        return try {
            Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        } catch (e: Exception) {
            Log.e("Align", "Failed: ${e.message}")
            null
        }
    }

    private fun computeEmbedding(face112: Bitmap): FloatArray {
        val buf = TensorBuffer.createFixedSize(intArrayOf(1, 112, 112, 3), DataType.FLOAT32)
        buf.loadArray(bitmapToNormalizedArray(face112))
        val out = TensorBuffer.createFixedSize(intArrayOf(1, 512), DataType.FLOAT32)
        embedder.run(buf.buffer, out.buffer)
        return out.floatArray
    }


    private fun saveBitmapToGallery(bitmap: Bitmap, name: String) {
        val filename = "${name}_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/FaceDebug")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }
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



//    import android.graphics.Bitmap
//    import android.graphics.Canvas
//    import android.graphics.Color
//    import android.graphics.Paint

    fun drawLandmarksOnBitmap(inputBitmap: Bitmap, landmarks: FloatArray): Bitmap {
        if (landmarks.size != 1434) {
            throw IllegalArgumentException("Expected 1434 floats for 478 landmarks (x,y,z), got ${landmarks.size}")
        }

        // Copy the bitmap to draw on it
        val outputBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)

        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        for (i in 0 until 478) {
            val x = landmarks[i * 3]
            val y = landmarks[i * 3 + 1]
            val z = landmarks[i * 3 + 2] // Not used, but available for size/depth-based styling

            // Optional: scale or clip the coordinates if needed
            // Here assuming the landmark x/y values are in image pixel coordinates

            if (x in 0f..inputBitmap.width.toFloat() && y in 0f..inputBitmap.height.toFloat()) {
                canvas.drawCircle(x, y, 2.5f, paint)
            }
        }
        return outputBitmap
    }

    fun overlayLandmarksOnBitmap(
        landmarksFloatArray: FloatArray,
        bitmap: Bitmap
    ): Bitmap {
        require(landmarksFloatArray.size == 1434) {
            "Expected 1434 floats for 478 landmarks (x,y,z), got ${landmarksFloatArray.size}"
        }

        // Build point objects from your raw pixel coords
        data class P(val x: Float, val y: Float, val z: Float)
        val landmarks = List(478) { i ->
            val b = i * 3
            P(
                x = landmarksFloatArray[b],
                y = landmarksFloatArray[b + 1],
                z = landmarksFloatArray[b + 2]
            )
        }

        // Prepare canvas
        val output = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            style = Paint.Style.FILL
            strokeWidth = 2f
            textSize = 18f
            isAntiAlias = true
        }

        // Compute z‑range for color mapping
        val zMin = landmarks.minOf { it.z }
        val zMax = landmarks.maxOf { it.z }
        val zRange = (zMax - zMin).coerceAtLeast(0.0001f)

        // 1) Draw all points with blue→red by depth
        for (p in landmarks) {
            val norm = ((p.z - zMin) / zRange).coerceIn(0f, 1f)
            val r = (norm * 255).toInt()
            val b = 255 - r
            paint.color = Color.rgb(r, 0, b)
            canvas.drawCircle(p.x, p.y, 3f, paint)
        }

        // Helper to draw & label a single landmark
        fun drawLabel(idx: Int, label: String, color: Int) {
            val p = landmarks[idx]
            paint.color = color
            canvas.drawCircle(p.x, p.y, 6f, paint)
            canvas.drawText(label, p.x + 6f, p.y - 6f, paint)
        }

        // Nose tip, mouth corners
        drawLabel(1,   "Nose",    Color.GREEN)
        drawLabel(61,  "MouthL",  Color.YELLOW)
        drawLabel(291, "MouthR",  Color.YELLOW)

        // Iris centers (average of 4 points each)
        fun drawIris(indices: List<Int>, label: String, color: Int) {
            val xs = indices.map { landmarks[it].x }
            val ys = indices.map { landmarks[it].y }
            val cx = xs.average().toFloat()
            val cy = ys.average().toFloat()
            paint.color = color
            canvas.drawCircle(cx, cy, 6f, paint)
            canvas.drawText(label, cx + 6f, cy - 6f, paint)
        }
        drawIris(listOf(469,470,471,472), "RightEye", Color.CYAN)
        drawIris(listOf(474,475,476,477), "LeftEye",  Color.CYAN)

        // Draw polylines for face oval & lips
        val faceOval = listOf(
            10,338,297,332,284,251,389,356,454,323,
            361,288,397,365,379,378,400,377,152,148,
            176,149,150,136,172, 58,132, 93,234,127,
            162, 21, 54,103, 67,109
        )
        val upperLips = listOf(
            185,40,39,37,0,267,269,270,409,415,
            310,311,312,13, 82, 81, 42,183, 78
        )
        val lowerLips = listOf(
            61,146, 91,181, 84, 17,314,405,321,375,
            291,308,324,318,402,317, 14, 87,178, 88, 95
        )

        fun drawPolyline(indices: List<Int>, color: Int) {
            paint.color = color
            for (i in 1 until indices.size) {
                val p1 = landmarks[indices[i-1]]
                val p2 = landmarks[indices[i]]
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
            }
        }
        drawPolyline(faceOval,  Color.MAGENTA)
        drawPolyline(upperLips, Color.RED)
        drawPolyline(lowerLips, Color.RED)

        return output
    }


    fun cropFaceTo112(
        landmarksFloatArray: FloatArray,
        bitmap: Bitmap
    ): Bitmap {
        require(landmarksFloatArray.size == 478 * 3) {
            "Expected ${478*3} floats, got ${landmarksFloatArray.size}"
        }

        // 1) Extract only the x,y coords
        val xs = FloatArray(478) { i -> landmarksFloatArray[i * 3] }
        val ys = FloatArray(478) { i -> landmarksFloatArray[i * 3 + 1] }

        // 2) Compute bounding box
        val minX = xs.minOrNull()!!.coerceAtLeast(0f)
        val maxX = xs.maxOrNull()!!.coerceAtMost(bitmap.width  .toFloat())
        val minY = ys.minOrNull()!!.coerceAtLeast(0f)
        val maxY = ys.maxOrNull()!!.coerceAtMost(bitmap.height .toFloat())

        // 3) Convert to integer rect, ensure non-zero size
        val left   = minX.toInt()
        val top    = minY.toInt()
        val widthF = (maxX - minX).toInt().coerceAtLeast(1)
        val heightF= (maxY - minY).toInt().coerceAtLeast(1)

        // Optionally, add padding around the face:
        // val padX = (widthF * 0.1f).toInt()
        // val padY = (heightF* 0.1f).toInt()
        // left = (left - padX).coerceAtLeast(0)
        // top  = (top  - padY).coerceAtLeast(0)
        // widthF  = (widthF  + padX*2).coerceAtMost(bitmap.width  - left)
        // heightF = (heightF + padY*2).coerceAtMost(bitmap.height - top)

        // 4) Crop and resize
        val faceCrop = Bitmap.createBitmap(bitmap, left, top, widthF, heightF)
        return Bitmap.createScaledBitmap(faceCrop, 112, 112, true)
    }


    fun overlayEyesNoseMouth(
        landmarksFloatArray: FloatArray,
        bitmap: Bitmap
    ): Bitmap {
        require(landmarksFloatArray.size == 478 * 3) {
            "Expected 1434 floats (478 landmarks × 3), got ${landmarksFloatArray.size}"
        }

        // 1) Build a list of points
        data class P(val x: Float, val y: Float, val z: Float)
        val pts = List(478) { i ->
            val b = i * 3
            P(
                x = landmarksFloatArray[b],
                y = landmarksFloatArray[b + 1],
                z = landmarksFloatArray[b + 2]
            )
        }

        // 2) Prepare canvas
        val outBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outBmp)
        val paint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            textSize = 16f
            strokeWidth = 2f
        }

        // 3) Draw Nose Tip
        run {
            val p = pts[1]
            paint.color = Color.GREEN
            canvas.drawCircle(p.x, p.y, 6f, paint)
            canvas.drawText("Nose", p.x + 6f, p.y - 6f, paint)
        }

        // 4) Draw Mouth Corners
        val mouthIndices = listOf(61 to "MouthL", 291 to "MouthR")
        for ((idx, label) in mouthIndices) {
            val p = pts[idx]
            paint.color = Color.YELLOW
            canvas.drawCircle(p.x, p.y, 6f, paint)
            canvas.drawText(label, p.x + 6f, p.y - 6f, paint)
        }

        // 5) Draw Iris Centers
        fun drawIris(indices: List<Int>, label: String) {
            val xs = indices.map { pts[it].x }
            val ys = indices.map { pts[it].y }
            val cx = xs.average().toFloat()
            val cy = ys.average().toFloat()
            paint.color = Color.CYAN
            canvas.drawCircle(cx, cy, 6f, paint)
            canvas.drawText(label, cx + 6f, cy - 6f, paint)
        }
        drawIris(listOf(469, 470, 471, 472), "R Eye")
        drawIris(listOf(474, 475, 476, 477), "L Eye")

        return outBmp
    }




}
