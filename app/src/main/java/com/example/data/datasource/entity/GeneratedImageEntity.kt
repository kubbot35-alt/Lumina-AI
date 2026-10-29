package com.example.data.datasource.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.GeneratedImageItem

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val prompt: String,
    val enhancedPrompt: String?,
    val style: String,
    val aspectRatio: String,
    val imageSize: String,
    val createdAt: Long,
    val isFavorite: Boolean
) {
    fun toDomain(): GeneratedImageItem = GeneratedImageItem(
        id = id,
        filePath = filePath,
        prompt = prompt,
        enhancedPrompt = enhancedPrompt,
        style = style,
        aspectRatio = aspectRatio,
        imageSize = imageSize,
        createdAt = createdAt,
        isFavorite = isFavorite
    )

    companion object {
        fun fromDomain(item: GeneratedImageItem): GeneratedImageEntity = GeneratedImageEntity(
            id = item.id,
            filePath = item.filePath,
            prompt = item.prompt,
            enhancedPrompt = item.enhancedPrompt,
            style = item.style,
            aspectRatio = item.aspectRatio,
            imageSize = item.imageSize,
            createdAt = item.createdAt,
            isFavorite = item.isFavorite
        )
    }
}
