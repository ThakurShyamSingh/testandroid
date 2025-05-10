import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.demotest2.face.FaceEmbeddingProcessor
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceCaptureScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val faceProcessor = remember { FaceEmbeddingProcessor(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

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
                Log.e("FaceCaptureScreen", "Camera binding failed", e)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = imageProxyToBitmap(image)
                            image.close()

                            if (bitmap != null) {
                                val embedding = faceProcessor.getEmbedding(bitmap)
                                if (embedding != null) {
                                    Toast.makeText(context, "Face detected!", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(context, "Embedding: ${embedding.take(5)}...", Toast.LENGTH_SHORT).show()
                                    Log.d("FaceCaptureScreen", "Embedding: ${embedding.take(5)}... (${embedding.size} floats)")
                                } else {
                                    Toast.makeText(context, "No face detected", Toast.LENGTH_SHORT).show()
                                    Log.d("FaceCaptureScreen", "Face not detected")
                                }
                            } else {
                                Toast.makeText(context, "Bitmap conversion failed", Toast.LENGTH_SHORT).show()
                                Log.e("FaceCaptureScreen", "Failed to convert image proxy to bitmap")
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(context, "Capture failed", Toast.LENGTH_SHORT).show()
                            Log.e("FaceCaptureScreen", "Capture failed", exception)
                        }
                    }
                )
            }) {
                Text("Capture Face")
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required to use this feature.")
        }
    }
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
