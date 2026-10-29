package com.example.domain.model

data class GeneratedImageItem(
    val id: Long = 0,
    val filePath: String,
    val prompt: String,
    val enhancedPrompt: String? = null,
    val style: String = ArtStyle.NONE.title,
    val aspectRatio: String = AspectRatioOption.SQUARE.label,
    val imageSize: String = ImageSizeOption.SIZE_1K.label,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
