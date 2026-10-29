package com.example.domain.model

enum class AspectRatioOption(val label: String, val ratio: Float) {
    SQUARE("1:1", 1.0f),
    LANDSCAPE_16_9("16:9", 16f / 9f),
    PORTRAIT_9_16("9:16", 9f / 16f),
    LANDSCAPE_4_3("4:3", 4f / 3f),
    PORTRAIT_3_4("3:4", 3f / 4f);

    companion object {
        fun defaultOption(): AspectRatioOption = SQUARE
    }
}
