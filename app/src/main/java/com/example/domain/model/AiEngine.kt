package com.example.domain.model

enum class AiEngine(
    val title: String,
    val description: String
) {
    GEMINI(
        title = "Google Gemini Pro",
        description = "Model gemini-3-pro-image (Wymaga planu Blaze/billing)"
    ),
    CLOUD_FLUX(
        title = "Silnik AI Cloud (Darmowy)",
        description = "Szybkie generowanie Flux/SDXL bez limitów"
    );

    companion object {
        fun defaultEngine(): AiEngine = CLOUD_FLUX
    }
}
