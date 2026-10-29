package com.example.ui.studio

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.domain.model.AiEngine
import com.example.domain.model.ImageGenState
import com.example.ui.studio.components.ConfigPickerSection
import com.example.ui.studio.components.GeneratedPreviewCard
import com.example.ui.studio.components.PromptInputSection
import com.example.ui.studio.components.StylePickerSection
import com.example.ui.theme.NeonCyan
import java.io.File
import java.io.FileOutputStream

@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val selectedSize by viewModel.selectedSize.collectAsStateWithLifecycle()
    val selectedRatio by viewModel.selectedAspectRatio.collectAsStateWithLifecycle()
    val selectedEngine by viewModel.selectedEngine.collectAsStateWithLifecycle()
    val imageGenState by viewModel.imageGenState.collectAsStateWithLifecycle()
    val enhanceState by viewModel.enhanceState.collectAsStateWithLifecycle()
    val saveMessage by viewModel.saveGalleryMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveMessage) {
        saveMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSaveMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GeneratedPreviewCard(
                state = imageGenState,
                onSaveToDeviceGallery = { bitmap -> viewModel.saveToDeviceGallery(bitmap) },
                onShareImage = { bitmap -> shareBitmap(context, bitmap, prompt) },
                onSwitchToCloudAndRetry = {
                    viewModel.setEngine(AiEngine.CLOUD_FLUX)
                    viewModel.generateImage()
                }
            )

            PromptInputSection(
                prompt = prompt,
                enhanceState = enhanceState,
                onPromptChange = viewModel::updatePrompt,
                onEnhanceClick = viewModel::enhancePrompt
            )

            StylePickerSection(
                selectedStyle = selectedStyle,
                onStyleSelected = viewModel::selectStyle
            )

            ConfigPickerSection(
                selectedEngine = selectedEngine,
                selectedSize = selectedSize,
                selectedRatio = selectedRatio,
                onEngineSelected = viewModel::setEngine,
                onSizeSelected = viewModel::selectSize,
                onRatioSelected = viewModel::selectAspectRatio
            )

            Button(
                onClick = viewModel::generateImage,
                enabled = prompt.isNotBlank() && imageGenState !is ImageGenState.Generating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_image_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (imageGenState is ImageGenState.Generating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Generowanie...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.btn_generate),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

private fun shareBitmap(context: Context, bitmap: Bitmap, prompt: String) {
    try {
        val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(cachePath, "lumina_share.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Wygenerowano przez Lumina AI: $prompt")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Udostępnij obraz"))
    } catch (e: Exception) {
        android.util.Log.e("StudioScreen", "Error sharing image", e)
    }
}
