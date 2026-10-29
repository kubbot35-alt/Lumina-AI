package com.example.data.service

import android.graphics.Bitmap
import com.example.data.datasource.local.ApiKeyPreferences
import com.example.domain.model.AiEngine
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.ImageSizeOption
import com.example.domain.repository.IAiImageGenerator

class CompositeImageGenerator(
    private val geminiGenerator: GeminiImageGenerator,
    private val cloudFluxGenerator: CloudFluxImageGenerator,
    private val preferences: ApiKeyPreferences
) : IAiImageGenerator {

    override suspend fun generateImage(
        prompt: String,
        size: ImageSizeOption,
        aspectRatio: AspectRatioOption
    ): Bitmap {
        return when (preferences.selectedEngine) {
            AiEngine.GEMINI -> {
                geminiGenerator.generateImage(prompt, size, aspectRatio)
            }
            AiEngine.CLOUD_FLUX -> {
                cloudFluxGenerator.generateImage(prompt, size, aspectRatio)
            }
        }
    }
}
