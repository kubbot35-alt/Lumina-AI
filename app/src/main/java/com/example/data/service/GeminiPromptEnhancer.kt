package com.example.data.service

import android.util.Log
import com.example.domain.model.ArtStyle
import com.example.domain.repository.IPromptEnhancer
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiPromptEnhancer"

class GeminiPromptEnhancer : IPromptEnhancer {
    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    override suspend fun enhancePrompt(userPrompt: String, style: ArtStyle): String {
        return try {
            val styleInstruction = if (style != ArtStyle.NONE) {
                "Incorporate the artistic style: ${style.title} (${style.promptModifier})."
            } else {
                "Keep the style natural, photographic, or as implied by the user."
            }

            val metaPrompt = """
                You are a world-class AI art prompt engineer.
                Enhance and expand the following user idea into a single, cohesive, vivid, highly detailed image generation prompt.
                Include specific visual details such as subject appearance, environment, lighting, mood, color palette, camera angle, and artistic texture.
                $styleInstruction
                Important: Output ONLY the enhanced prompt text directly without quotes, markdown, greetings, or explanations.
                
                User idea: $userPrompt
            """.trimIndent()

            val response = withTimeout(30_000L) {
                model.generateContent(metaPrompt)
            }
            val enhanced = response.text?.trim()
            if (!enhanced.isNullOrBlank()) {
                enhanced
            } else {
                style.applyTo(userPrompt)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enhance prompt with Gemini, falling back to style modifier", e)
            style.applyTo(userPrompt)
        }
    }
}
