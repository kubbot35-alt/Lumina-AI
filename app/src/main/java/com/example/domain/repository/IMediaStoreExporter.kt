package com.example.domain.repository

import android.graphics.Bitmap

interface IMediaStoreExporter {
    suspend fun exportToDeviceGallery(bitmap: Bitmap, title: String): Result<String>
}
