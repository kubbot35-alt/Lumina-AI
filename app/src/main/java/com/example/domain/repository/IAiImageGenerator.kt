package com.example.domain.repository

import android.graphics.Bitmap
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.ImageSizeOption

interface IAiImageGenerator {
    suspend fun generateImage(
        prompt: String,
        size: ImageSizeOption,
        aspectRatio: AspectRatioOption
    ): Bitmap
}
