package com.example.domain.model

enum class ArtStyle(
    val title: String,
    val promptModifier: String
) {
    NONE(
        title = "Naturalny",
        promptModifier = ""
    ),
    CYBERPUNK(
        title = "Cyberpunk",
        promptModifier = ", cyberpunk aesthetic, neon glow, futuristic city lights, ultra-detailed octane render"
    ),
    PHOTOREALISTIC(
        title = "Fotorealizm",
        promptModifier = ", photorealistic 8k, professional photography, cinematic lighting, sharp focus, 35mm lens"
    ),
    ANIME(
        title = "Anime",
        promptModifier = ", high quality anime artwork, Makoto Shinkai style, vibrant colors, detailed illustration"
    ),
    FANTASY(
        title = "Dark Fantasy",
        promptModifier = ", dark fantasy concept art, mystical atmosphere, volumetric lighting, epic digital painting"
    ),
    SYNTHWAVE(
        title = "Synthwave",
        promptModifier = ", retro synthwave 80s, glowing wireframe grid, purple and cyan sunset, nostalgic vaporwave"
    ),
    CINEMATIC_3D(
        title = "3D Pixar/Render",
        promptModifier = ", stylized 3D animation render, charming character design, ray tracing, studio lighting"
    );

    fun applyTo(basePrompt: String): String {
        return if (this == NONE || promptModifier.isBlank()) {
            basePrompt
        } else {
            "$basePrompt$promptModifier"
        }
    }
}
