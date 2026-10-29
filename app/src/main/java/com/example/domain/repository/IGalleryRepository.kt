package com.example.domain.repository

import android.graphics.Bitmap
import com.example.domain.model.GeneratedImageItem
import kotlinx.coroutines.flow.Flow

interface IGalleryRepository {
    fun getAllImages(): Flow<List<GeneratedImageItem>>
    fun getFavoriteImages(): Flow<List<GeneratedImageItem>>
    suspend fun saveImage(
        bitmap: Bitmap,
        prompt: String,
        enhancedPrompt: String?,
        style: String,
        aspectRatio: String,
        imageSize: String
    ): GeneratedImageItem
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
    suspend fun deleteImage(item: GeneratedImageItem)
}
