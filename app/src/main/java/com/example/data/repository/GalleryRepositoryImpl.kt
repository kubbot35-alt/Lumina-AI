package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.datasource.dao.GeneratedImageDao
import com.example.data.datasource.entity.GeneratedImageEntity
import com.example.data.datasource.local.ImageFileStorage
import com.example.domain.model.GeneratedImageItem
import com.example.domain.repository.IGalleryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GalleryRepositoryImpl(
    private val dao: GeneratedImageDao,
    private val storage: ImageFileStorage
) : IGalleryRepository {

    override fun getAllImages(): Flow<List<GeneratedImageItem>> {
        return dao.getAllImages().map { list -> list.map { it.toDomain() } }
    }

    override fun getFavoriteImages(): Flow<List<GeneratedImageItem>> {
        return dao.getFavoriteImages().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveImage(
        bitmap: Bitmap,
        prompt: String,
        enhancedPrompt: String?,
        style: String,
        aspectRatio: String,
        imageSize: String
    ): GeneratedImageItem {
        val filePath = storage.saveBitmap(bitmap)
        val entity = GeneratedImageEntity(
            filePath = filePath,
            prompt = prompt,
            enhancedPrompt = enhancedPrompt,
            style = style,
            aspectRatio = aspectRatio,
            imageSize = imageSize,
            createdAt = System.currentTimeMillis(),
            isFavorite = false
        )
        val id = dao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    override suspend fun deleteImage(item: GeneratedImageItem) {
        dao.deleteById(item.id)
        storage.deleteFile(item.filePath)
    }
}
