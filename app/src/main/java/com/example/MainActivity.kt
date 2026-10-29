package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.core.configureAppCheck
import com.example.data.datasource.database.AppDatabase
import com.example.data.datasource.local.ApiKeyPreferences
import com.example.data.datasource.local.ImageFileStorage
import com.example.data.datasource.mediastore.MediaStoreExporter
import com.example.data.repository.GalleryRepositoryImpl
import com.example.data.service.CloudFluxImageGenerator
import com.example.data.service.CompositeImageGenerator
import com.example.data.service.GeminiImageGenerator
import com.example.data.service.GeminiPromptEnhancer
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.navigation.MainScreen
import com.example.ui.studio.StudioViewModel
import com.example.ui.theme.LuminaAiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory: Configure App Check before any UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val appDatabase = AppDatabase.getInstance(applicationContext)
        val imageStorage = ImageFileStorage(applicationContext)
        val mediaStoreExporter = MediaStoreExporter(applicationContext)
        val apiKeyPreferences = ApiKeyPreferences(applicationContext)

        val galleryRepository = GalleryRepositoryImpl(
            dao = appDatabase.generatedImageDao(),
            storage = imageStorage
        )

        val geminiGenerator = GeminiImageGenerator()
        val cloudFluxGenerator = CloudFluxImageGenerator()
        val imageGenerator = CompositeImageGenerator(geminiGenerator, cloudFluxGenerator, apiKeyPreferences)
        val promptEnhancer = GeminiPromptEnhancer()

        val studioViewModel: StudioViewModel by viewModels {
            StudioViewModel.provideFactory(
                imageGenerator = imageGenerator,
                promptEnhancer = promptEnhancer,
                galleryRepository = galleryRepository,
                mediaStoreExporter = mediaStoreExporter,
                preferences = apiKeyPreferences
            )
        }

        val galleryViewModel: GalleryViewModel by viewModels {
            GalleryViewModel.provideFactory(
                repository = galleryRepository,
                mediaStoreExporter = mediaStoreExporter
            )
        }

        setContent {
            LuminaAiTheme {
                MainScreen(
                    studioViewModel = studioViewModel,
                    galleryViewModel = galleryViewModel
                )
            }
        }
    }
}
