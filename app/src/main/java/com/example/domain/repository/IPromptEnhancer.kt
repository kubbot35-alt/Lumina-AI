package com.example.domain.repository

import com.example.domain.model.ArtStyle

interface IPromptEnhancer {
    suspend fun enhancePrompt(userPrompt: String, style: ArtStyle): String
}
