package com.example.data.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.ImageSizeOption
import com.example.domain.repository.IAiImageGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

private const val TAG = "CloudFluxGen"

class CloudFluxImageGenerator : IAiImageGenerator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override suspend fun generateImage(
        prompt: String,
        size: ImageSizeOption,
        aspectRatio: AspectRatioOption
    ): Bitmap = withContext(Dispatchers.IO) {
        val (width, height) = calculateDimensions(size, aspectRatio)
        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
        val seed = (System.currentTimeMillis() % 1_000_000).toInt()
        val url = "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&seed=$seed&model=flux&nologo=true"

        Log.d(TAG, "Requesting cloud image: $url")
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "LuminaAI/1.0")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Błąd serwera generowania: HTTP ${response.code}")
            }
            val bytes = response.body?.bytes()
                ?: throw IllegalStateException("Pusta odpowiedź serwera obrazów")

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw IllegalStateException("Nie udało się zdekodować obrazu z chmury")

            bitmap
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Cloud generation failed", e)
            throw IllegalStateException("Nie udało się pobrać obrazu: ${e.localizedMessage}")
        }
    }

    private fun calculateDimensions(
        size: ImageSizeOption,
        ratio: AspectRatioOption
    ): Pair<Int, Int> {
        val baseDim = when (size) {
            ImageSizeOption.SIZE_1K -> 1024
            ImageSizeOption.SIZE_2K -> 1280
            ImageSizeOption.SIZE_4K -> 1440
        }
        return when (ratio) {
            AspectRatioOption.SQUARE -> Pair(baseDim, baseDim)
            AspectRatioOption.LANDSCAPE_16_9 -> Pair(baseDim, (baseDim * 9) / 16)
            AspectRatioOption.PORTRAIT_9_16 -> Pair((baseDim * 9) / 16, baseDim)
            AspectRatioOption.LANDSCAPE_4_3 -> Pair(baseDim, (baseDim * 3) / 4)
            AspectRatioOption.PORTRAIT_3_4 -> Pair((baseDim * 3) / 4, baseDim)
        }
    }
}
