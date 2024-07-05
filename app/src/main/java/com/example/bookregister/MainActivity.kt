package com.example.bookregister

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.bookregister.ui.theme.BookRegisterTheme
import com.github.tbsten.cameraxcompose.CameraPreview
import com.github.tbsten.cameraxcompose.usecasehelper.previewUseCase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookRegisterTheme {
                CameraView()
            }
        }
    }
}

@Composable
fun CameraView() {
    val context = LocalContext.current
    var barcodeValue by remember { mutableStateOf<String?>(null) }
    var payload by remember { mutableStateOf<BookData?>(null) }
    CameraPreview(
        onBind = {
            val executor = ContextCompat.getMainExecutor(context)
            val preview = previewUseCase()
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, BarcodeAnalyzer { barcode ->
                        if (barcode.length == 13 && (barcode.startsWith("978") || barcode.startsWith("979"))) {
                            barcodeValue = barcode
                        }
                    })
                }
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                analysis,
                preview,
            )
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            payload?.let {
                Text(text = it.summary.isbn)
                Text(text = it.summary.title)
                Text(text = "${it.summary.author}, ${it.summary.publisher}")
            }
        }
    }
    LaunchedEffect(barcodeValue) {
        barcodeValue?.let {
            payload = fetchJSON(it)
        }
    }
}

private suspend fun fetchJSON(isbnCode: String?): BookData {
    val client = HttpClient()
    val url = "https://api.openbd.jp/v1/get?isbn=$isbnCode&pretty"
    val httpResponse: HttpResponse = client.get(url)
    val jsonString: String = httpResponse.body()
    println(jsonString)
    val json = Json {
        ignoreUnknownKeys = true
    }
    val jsonObject = Json.parseToJsonElement(jsonString).jsonArray[0]
    val result = json.decodeFromJsonElement<BookData>(jsonObject)
    return result
}