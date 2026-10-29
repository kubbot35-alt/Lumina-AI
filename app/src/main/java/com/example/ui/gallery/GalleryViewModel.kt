package com.example.ui.gallery

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.domain.model.GeneratedImageItem
import com.example.domain.repository.IGalleryRepository
import com.example.domain.repository.IMediaStoreExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GalleryFilter(val label: String) {
    ALL("Wszystkie"),
    FAVORITES("Ulubione")
}

class GalleryViewModel(
    private val repository: IGalleryRepository,
    private val mediaStoreExporter: IMediaStoreExporter
) : ViewModel() {

    private val _filter = MutableStateFlow(GalleryFilter.ALL)
    val filter: StateFlow<GalleryFilter> = _filter.asStateFlow()

    private val _selectedItem = MutableStateFlow<GeneratedImageItem?>(null)
    val selectedItem: StateFlow<GeneratedImageItem?> = _selectedItem.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    val images: StateFlow<List<GeneratedImageItem>> = combine(
        repository.getAllImages(),
        _filter
    ) { all, currentFilter ->
        when (currentFilter) {
            GalleryFilter.ALL -> all
            GalleryFilter.FAVORITES -> all.filter { it.isFavorite }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(newFilter: GalleryFilter) {
        _filter.value = newFilter
    }

    fun selectItem(item: GeneratedImageItem?) {
        _selectedItem.value = item
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun toggleFavorite(item: GeneratedImageItem) {
        viewModelScope.launch {
            val newFav = !item.isFavorite
            repository.toggleFavorite(item.id, newFav)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = _selectedItem.value?.copy(isFavorite = newFav)
            }
        }
    }

    fun deleteImage(item: GeneratedImageItem) {
        viewModelScope.launch {
            repository.deleteImage(item)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = null
            }
            _userMessage.value = "Obraz usunięty z kolekcji."
        }
    }

    fun saveToDeviceGallery(item: GeneratedImageItem) {
        viewModelScope.launch {
            val bitmap = BitmapFactory.decodeFile(item.filePath)
            if (bitmap != null) {
                val result = mediaStoreExporter.exportToDeviceGallery(bitmap, item.prompt)
                result.onSuccess {
                    _userMessage.value = "Zapisano pomyślnie w galerii telefonu!"
                }.onFailure {
                    _userMessage.value = "Błąd zapisu w galerii: ${it.localizedMessage}"
                }
            } else {
                _userMessage.value = "Nie odnaleziono pliku obrazu."
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: IGalleryRepository,
            mediaStoreExporter: IMediaStoreExporter
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GalleryViewModel(repository, mediaStoreExporter)
            }
        }
    }
}
