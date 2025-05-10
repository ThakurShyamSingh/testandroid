package com.example.demotest2.ui.screens

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demotest2.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.*
import com.example.demotest2.face.FaceEmbeddingProcessor
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Context
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RegisterStudentsScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val faceProcessor = remember { FaceEmbeddingProcessor(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDarkTheme) listOf(BackgroundDark, Color.Black)
        else listOf(BackgroundLight, Color.White)
    )

    val topBarColor = if (isDarkTheme) BackgroundDarker else BackgroundCardLight
    val textColor = if (isDarkTheme) Color.White else BlueTrue

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    // Request camera permission
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required.")
        }
        return
    }

    // Setup Camera
    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture
            )
        } catch (e: Exception) {
            Log.e("RegisterStudentsScreen", "Camera binding failed", e)
        }
    }

    // Registration form state
    var studentRollNo by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var studentDepartment by remember { mutableStateOf("") }
    var studentSection by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
                    .background(topBarColor),
                title = {
                    Text(
                        "Register Student",
                        style = MaterialTheme.typography.titleLarge.copy(color = textColor)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("RecognizeStudentsScreen") }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.CloudQueue else Icons.Default.Cloud,
                            contentDescription = "Recognize Students",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        },
        bottomBar = { BottomNavigationBar(navController, "RegisterStudentsScreen") },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = backgroundGradient)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // 👁️ Live camera preview in circle
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Register New Student",
                    style = TextStyle(
                        color = BlueTrue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = studentDepartment,
                    onValueChange = { studentDepartment = it },
                    label = { Text("Department") },
                    placeholder = { Text("Enter Department") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = studentSection,
                    onValueChange = { studentSection = it },
                    label = { Text("Section") },
                    placeholder = { Text("Enter Section") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Name") },
                    placeholder = { Text("Enter Student Name") },
//                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )


                OutlinedTextField(
                    value = studentRollNo,
                    onValueChange = { studentRollNo = it },
                    label = { Text("Roll No") },
                    placeholder = { Text("Enter Roll Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )


                // 🔘 Register button triggers face embedding
                Button(
                    onClick = {
                        if (studentName.isBlank() || studentRollNo.isBlank() || studentDepartment.isBlank() || studentSection.isBlank()) {
                            Toast.makeText(context, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        imageCapture?.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(image)
                                    image.close()

                                    if (bitmap != null) {
                                        val embedding = faceProcessor.getEmbedding(bitmap)
                                        if (embedding != null) {
                                            Toast.makeText(context, "Face captured!", Toast.LENGTH_SHORT).show()

                                            val studentData = mapOf(
                                                "name" to studentName,
                                                "rollNo" to studentRollNo,
                                                "department" to studentDepartment,
                                                "section" to studentSection,
                                                "embedding" to embedding.toList() // convert FloatArray to List<Float> for JSON
                                            )

                                            val jsonData = kotlinx.serialization.json.Json.encodeToString(
                                                kotlinx.serialization.json.JsonObject.serializer(),
                                                kotlinx.serialization.json.buildJsonObject {
                                                    studentData.forEach { (key, value) ->
                                                        when (value) {
                                                            is String -> put(key, kotlinx.serialization.json.JsonPrimitive(value))
                                                            is List<*> -> put(key, kotlinx.serialization.json.JsonArray(value.map {
                                                                kotlinx.serialization.json.JsonPrimitive(it as Float)
                                                            }))
                                                        }
                                                    }
                                                }
                                            )

                                            Log.d("RegisterStudent", "Student JSON: $jsonData")
                                            Toast.makeText(context, "Data saved for $studentName", Toast.LENGTH_SHORT).show()
                                            saveStudentJsonLocally(context, jsonData)
                                        } else {
                                            Toast.makeText(context, "Face not detected", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Image conversion failed", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                                    Log.e("RegisterStudent", "Capture failed", exception)
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                ) {
                    Text(
                        text = "Register",
                        style = TextStyle(color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    )
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        Log.e("BitmapConversion", "Error converting ImageProxy to Bitmap", e)
        null
    }
}


fun saveStudentJsonLocally(context: Context, studentJson: String) {
    val fileName = "registeredStudents.json"
    val file = File(context.filesDir, fileName)

    // If file doesn't exist, create it
    if (!file.exists()) {
        file.createNewFile()
    }

    // Write JSON to file (append or replace as needed)
    file.writeText(studentJson)

    Log.d("registerstudentscreen","Student data saved to ${file.absolutePath}")
}

