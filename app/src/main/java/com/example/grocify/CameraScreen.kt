import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.grocify.AnalyzerType
import com.example.grocify.BarcodeAnalyzer
import com.example.grocify.ScanProductScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(analyzerType: AnalyzerType) {

    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isClicked by remember { mutableStateOf(false) }

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }

    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = if (isClicked) Color.Red else Color.Black.copy(alpha = 0.4f) // Change background color based on click state
            )
            .clickable { isClicked = !isClicked }
    )
    {*/
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.setSurfaceProvider(previewView.surfaceProvider)

                val imageAnalysis = ImageAnalysis.Builder().build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    if (analyzerType == AnalyzerType.BARCODE) {
                        BarcodeAnalyzer(context)
                    } else {
                        BarcodeAnalyzer(context)
                    }
                )

                runCatching {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                }.onFailure {
                    Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
                }
                previewView
            }
        )
    //}
}
