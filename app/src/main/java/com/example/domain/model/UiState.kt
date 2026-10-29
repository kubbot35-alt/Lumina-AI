package com.example.domain.model

import android.graphics.Bitmap

sealed interface ImageGenState {
    data object Idle : ImageGenState
    data class Generating(val progressMessage: String) : ImageGenState
    data class Success(val bitmap: Bitmap, val prompt: String, val savedItem: GeneratedImageItem?) : ImageGenState
    data class Error(val message: String) : ImageGenState
}

sealed interface PromptEnhanceState {
    data object Idle : PromptEnhanceState
    data object Enhancing : PromptEnhanceState
    data class Enhanced(val enhancedPrompt: String) : PromptEnhanceState
    data class Error(val message: String) : PromptEnhanceState
}
