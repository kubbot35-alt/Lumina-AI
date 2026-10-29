package com.example.domain.model

enum class ImageSizeOption(val label: String, val resolutionDescription: String) {
    SIZE_1K("1K", "1024 × 1024"),
    SIZE_2K("2K", "2048 × 2048"),
    SIZE_4K("4K", "4096 × 4096");

    companion object {
        fun defaultOption(): ImageSizeOption = SIZE_1K
    }
}
