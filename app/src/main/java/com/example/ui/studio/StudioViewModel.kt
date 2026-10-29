package com.example.ui.studio

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.datasource.local.ApiKeyPreferences
import com.example.domain.model.AiEngine
import com.example.domain.model.ArtStyle
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.GeneratedImageItem
import com.example.domain.model.ImageGenState
import com.example.domain.model.ImageSizeOption
import com.example.domain.model.PromptEnhanceState
import com.example.domain.repository.IAiImageGenerator
import com.example.domain.repository.IGalleryRepository
import com.example.domain.repository.IMediaStoreExporter
import com.example.domain.repository.IPromptEnhancer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "StudioViewModel"

class StudioViewModel(
    private val imageGenerator: IAiImageGenerator,
    private val promptEnhancer: IPromptEnhancer,
    private val galleryRepository: IGalleryRepository,
    private val mediaStoreExporter: IMediaStoreExporter,
    private val preferences: ApiKeyPreferences
) : ViewModel() {

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow(ArtStyle.NONE)
    val selectedStyle: StateFlow<ArtStyle> = _selectedStyle.asStateFlow()

    private val _selectedSize = MutableStateFlow(ImageSizeOption.SIZE_1K)
    val selectedSize: StateFlow<ImageSizeOption> = _selectedSize.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(AspectRatioOption.SQUARE)
    val selectedAspectRatio: StateFlow<AspectRatioOption> = _selectedAspectRatio.asStateFlow()

    private val _selectedEngine = MutableStateFlow(preferences.selectedEngine)
    val selectedEngine: StateFlow<AiEngine> = _selectedEngine.asStateFlow()

    private val _imageGenState = MutableStateFlow<ImageGenState>(ImageGenState.Idle)
    val imageGenState: StateFlow<ImageGenState> = _imageGenState.asStateFlow()

    private val _enhanceState = MutableStateFlow<PromptEnhanceState>(PromptEnhanceState.Idle)
    val enhanceState: StateFlow<PromptEnhanceState> = _enhanceState.asStateFlow()

    private val _saveGalleryMessage = MutableStateFlow<String?>(null)
    val saveGalleryMessage: StateFlow<String?> = _saveGalleryMessage.asStateFlow()

    fun updatePrompt(text: String) { _prompt.value = text }
    fun selectStyle(style: ArtStyle) { _selectedStyle.value = style }
    fun selectSize(size: ImageSizeOption) { _selectedSize.value = size }
    fun selectAspectRatio(ratio: AspectRatioOption) { _selectedAspectRatio.value = ratio }
    fun clearSaveMessage() { _saveGalleryMessage.value = null }

    fun setEngine(engine: AiEngine) {
        preferences.selectedEngine = engine
        _selectedEngine.value = engine
    }

    fun saveCustomApiKey(key: String) {
        preferences.customGeminiApiKey = key.trim()
    }

    fun enhancePrompt() {
        val currentPrompt = _prompt.value.trim()
        if (currentPrompt.isBlank()) return
        viewModelScope.launch {
            _enhanceState.value = PromptEnhanceState.Enhancing
            try {
                val enhanced = promptEnhancer.enhancePrompt(currentPrompt, _selectedStyle.value)
                _prompt.value = enhanced
                _enhanceState.value = PromptEnhanceState.Enhanced(enhanced)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Enhance prompt failed", e)
                _enhanceState.value = PromptEnhanceState.Error(e.localizedMessage ?: "Błąd ulepszania promptu")
            }
        }
    }

    fun generateImage() {
        val currentPrompt = _prompt.value.trim()
        if (currentPrompt.isBlank()) return

        val finalPrompt = _selectedStyle.value.applyTo(currentPrompt)
        val size = _selectedSize.value
        val ratio = _selectedAspectRatio.value
        val engine = _selectedEngine.value

        viewModelScope.launch {
            _imageGenState.value = ImageGenState.Generating("Syntetyzowanie (${engine.title})...")
            try {
                val bitmap = imageGenerator.generateImage(finalPrompt, size, ratio)
                val savedItem = galleryRepository.saveImage(
                    bitmap = bitmap,
                    prompt = currentPrompt,
                    enhancedPrompt = finalPrompt,
                    style = _selectedStyle.value.title,
                    aspectRatio = ratio.label,
                    imageSize = size.label
                )
                _imageGenState.value = ImageGenState.Success(bitmap, currentPrompt, savedItem)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Image generation error", e)
                _imageGenState.value = ImageGenState.Error(e.localizedMessage ?: "Nie udało się wygenerować obrazu.")
            }
        }
    }

    fun saveToDeviceGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            val title = _prompt.value.ifBlank { "Lumina AI Image" }
            val result = mediaStoreExporter.exportToDeviceGallery(bitmap, title)
            result.onSuccess {
                _saveGalleryMessage.value = "Obraz został pomyślnie zapisany w galerii telefonu!"
            }.onFailure { err ->
                _saveGalleryMessage.value = "Błąd zapisu w galerii: ${err.localizedMessage}"
            }
        }
    }

    companion object {
        fun provideFactory(
            imageGenerator: IAiImageGenerator,
            promptEnhancer: IPromptEnhancer,
            galleryRepository: IGalleryRepository,
            mediaStoreExporter: IMediaStoreExporter,
            preferences: ApiKeyPreferences
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudioViewModel(imageGenerator, promptEnhancer, galleryRepository, mediaStoreExporter, preferences)
            }
        }
    }
}
