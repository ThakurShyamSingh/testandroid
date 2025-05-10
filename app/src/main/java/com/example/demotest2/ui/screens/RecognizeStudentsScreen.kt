package com.example.demotest2.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.example.demotest2.face.FaceEmbeddingProcessor
import com.example.demotest2.ui.theme.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.sqrt



@Serializable
data class StudentData(
    val name: String,
    val rollNo: String,
    val department: String,
    val section: String,
    val embedding: List<Float>
)

/** Load JSON, wrap single‐object into array if needed */
fun loadRegisteredStudents(context: Context): List<StudentData> {
    val file = File(context.filesDir, "registeredStudents.json")
    if (!file.exists()) return emptyList()
    val raw = file.readText().trim()
    if (raw.isEmpty()) return emptyList()
    val json = if (raw.startsWith("[")) raw else "[$raw]"
    return try {
        Json { ignoreUnknownKeys = true }
            .decodeFromString<List<StudentData>>(json)
    } catch (e: Exception) {
        Log.e("RecognizeScreen", "JSON parse error", e)
        emptyList()
    }
}

/** Cosine similarity: dot(a,b) / (‖a‖·‖b‖) */
fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
    var dot = 0f
    var normA = 0f
    var normB = 0f
    for (i in a.indices) {
        dot += a[i] * b[i]
        normA += a[i] * a[i]
        normB += b[i] * b[i]
    }
    if (normA == 0f || normB == 0f) return -1f
    return dot / (sqrt(normA) * sqrt(normB))
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecognizeStudentsScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val faceProcessor = remember { FaceEmbeddingProcessor(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var overlayColor by remember { mutableStateOf(Color.Transparent) }
    val registeredStudents = remember { loadRegisteredStudents(context) }

    // Cosine score threshold: tune between 0.6f (loose) and 0.9f (strict)
    val threshold = 0.7f

    // CAMERA PERMISSION
    val cameraPerm = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(cameraPerm.status) {
        if (!cameraPerm.status.isGranted) cameraPerm.launchPermissionRequest()
    }
    if (!cameraPerm.status.isGranted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
        return
    }

    // BIND CAMERA ONCE
    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("RecognizeScreen", "Camera bind failed", e)
        }
    }

    val bgGradient = Brush.verticalGradient(
        if (isDarkTheme) listOf(BackgroundDark, Color.Black)
        else listOf(BackgroundLight, Color.White)
    )
    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(topBarColor),
                title = { Text("Recognize Student", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        },
        bottomBar = { BottomNavigationBar(navController, "RecognizeStudentsScreen") }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView({ previewView }, Modifier.fillMaxSize())
                if (overlayColor != Color.Transparent) {
                    Box(Modifier.fillMaxSize().background(overlayColor.copy(alpha = 0.5f)))
                }
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    imageCapture?.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bmp = imageProxyToBitmap(image)
                                image.close()
                                if (bmp == null) {
                                    Toast.makeText(context, "Bitmap failed", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                val newEmb = faceProcessor.getEmbedding(bmp)
                                if (newEmb == null) {
                                    Toast.makeText(context, "No face detected", Toast.LENGTH_SHORT).show()
                                    return
                                }

                                // compute and log each student's cosine score
                                var bestSim = -1f
                                var bestStudent: StudentData? = null
                                registeredStudents.forEach { st ->
                                    val sim = cosineSimilarity(newEmb, st.embedding.toFloatArray())
                                    Log.d(
                                        "RecognizeScreen",
                                        "Cosine sim for ${st.name} (${st.rollNo}): ${"%.3f".format(sim)}"
                                    )
                                    if (sim > bestSim) {
                                        bestSim = sim
                                        bestStudent = st
                                    }
                                }

                                val verified = bestSim >= threshold
                                overlayColor = if (verified) Color.Green else Color.Red
                                Log.d("RecognizeScreen", "Best cosine sim: $bestSim")
                                Toast.makeText(
                                    context,
                                    if (verified)
                                        "Verified: ${bestStudent?.name} (score ${"%.2f".format(bestSim)})"
                                    else
                                        "Not verified (best ${"%.2f".format(bestSim)})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onError(exc: ImageCaptureException) {
                                Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                                Log.e("RecognizeScreen", "Capture error", exc)
                            }
                        }
                    )
                },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
            ) {
                Text(
                    "Recognize",
                    style = TextStyle(
                        color = TextLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
