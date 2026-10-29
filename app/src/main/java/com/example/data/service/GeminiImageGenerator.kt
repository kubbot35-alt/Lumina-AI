package com.example.data.service

import android.graphics.Bitmap
import android.util.Log
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.ImageSizeOption
import com.example.domain.repository.IAiImageGenerator
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.AspectRatio
import com.google.firebase.ai.type.ImageConfig
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ImageSize
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiImageGen"

class GeminiImageGenerator : IAiImageGenerator {

    override suspend fun generateImage(
        prompt: String,
        size: ImageSizeOption,
        aspectRatio: AspectRatioOption
    ): Bitmap {
        val mappedSize = mapSize(size)
        val mappedRatio = mapRatio(aspectRatio)

        val config = generationConfig {
            responseModalities = listOf(ResponseModality.IMAGE)
            imageConfig = ImageConfig.builder()
                .setImageSize(mappedSize)
                .setAspectRatio(mappedRatio)
                .build()
        }

        val modelNames = listOf("gemini-3-pro-image-preview", "gemini-nano-banana-2.1", "gemini-3-pro-image")
        var lastError: Exception? = null

        for (modelName in modelNames) {
            try {
                Log.d(TAG, "Attempting generation with model $modelName at size ${size.label}")
                val model = Firebase.ai.generativeModel(
                    modelName = modelName,
                    generationConfig = config
                )

                val response = withTimeout(60_000L) {
                    model.generateContent(prompt)
                }

                val imagePart = response.candidates.firstOrNull()
                    ?.content?.parts?.filterIsInstance<ImagePart>()
                    ?.firstOrNull()

                val bitmap = imagePart?.image
                if (bitmap != null) {
                    return bitmap
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Model $modelName failed: ${e.message}")
                lastError = e
            }
        }

        val rawMsg = lastError?.localizedMessage ?: "Nieznany błąd generowania"
        if (rawMsg.contains("limit: 0", ignoreCase = true) || rawMsg.contains("Quota exceeded", ignoreCase = true)) {
            throw IllegalStateException(
                "Darmowy limit Firebase dla gemini-3-pro-image wynosi 0 (wymagany plan Blaze/billing). Przełącz na Silnik Cloud lub skonfiguruj własny klucz API."
            )
        }
        throw IllegalStateException(rawMsg)
    }

    private fun mapSize(size: ImageSizeOption): ImageSize = when (size) {
        ImageSizeOption.SIZE_1K -> ImageSize.SIZE_1K
        ImageSizeOption.SIZE_2K -> ImageSize.SIZE_2K
        ImageSizeOption.SIZE_4K -> ImageSize.SIZE_4K
    }

    private fun mapRatio(ratio: AspectRatioOption): AspectRatio = when (ratio) {
        AspectRatioOption.SQUARE -> AspectRatio.SQUARE_1x1
        AspectRatioOption.LANDSCAPE_16_9 -> AspectRatio.LANDSCAPE_16x9
        AspectRatioOption.PORTRAIT_9_16 -> AspectRatio.PORTRAIT_9x16
        AspectRatioOption.LANDSCAPE_4_3 -> AspectRatio.LANDSCAPE_4x3
        AspectRatioOption.PORTRAIT_3_4 -> AspectRatio.PORTRAIT_3x4
    }
}
