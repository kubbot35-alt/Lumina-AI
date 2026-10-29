package com.example.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.AiEngine

class ApiKeyPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lumina_settings", Context.MODE_PRIVATE)

    var customGeminiApiKey: String?
        get() = prefs.getString("custom_gemini_api_key", null)
        set(value) = prefs.edit().putString("custom_gemini_api_key", value).apply()

    var selectedEngine: AiEngine
        get() {
            val name = prefs.getString("selected_ai_engine", AiEngine.CLOUD_FLUX.name)
            return try {
                AiEngine.valueOf(name ?: AiEngine.CLOUD_FLUX.name)
            } catch (e: Exception) {
                AiEngine.CLOUD_FLUX
            }
        }
        set(value) = prefs.edit().putString("selected_ai_engine", value.name).apply()
}
