package com.example.model

import android.graphics.Bitmap
import kotlinx.serialization.Serializable

data class UploadedPhoto(
    val id: String,
    val bitmap: Bitmap,
    val base64: String,
    val mimeType: String = "image/jpeg",
    val label: String = ""
)

data class DetectedProductInfo(
    val url: String,
    val title: String = "",
    val description: String = "",
    val variationsOrDetails: String = "",
    val imageUrl: String? = null,
    val detectedBitmap: Bitmap? = null,
    val isSuccess: Boolean = false,
    val statusMessage: String = ""
)

@Serializable
data class StoryboardPanelSpec(
    val panelNumber: Int,
    val timeRange: String,
    val sceneTitle: String,
    val visualDescription: String,
    val cameraAngle: String
)

@Serializable
data class StoryboardPlanDto(
    val variationNumber: Int,
    val title: String,
    val conceptAndAngle: String,
    val productConsistencyNote: String,
    val panels: List<StoryboardPanelSpec>,
    val fullPrompt: String,
    val imageGenPrompt: String
)

@Serializable
data class StoryboardBatchResponseDto(
    val productSummary: String,
    val variations: List<StoryboardPlanDto>
)

data class StoryboardVariation(
    val variationNumber: Int,
    val title: String,
    val conceptAndAngle: String,
    val productConsistencyNote: String,
    val panels: List<StoryboardPanelSpec>,
    val fullPrompt: String,
    val imageGenPrompt: String,
    val compositeBitmap: Bitmap? = null,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val isGeneratingImage: Boolean = false,
    val isRevising: Boolean = false,
    val errorMessage: String? = null,
    val revisionInputVisible: Boolean = false,
    val revisionInstruction: String = ""
)

object VariationGuide {
    fun getGuideTitle(index: Int): String = when (index) {
        1 -> "Tampilan Keseluruhan Produk"
        2 -> "Angle Lebih Dekat (Close-Up)"
        3 -> "Angle Samping / Tiga Perempat"
        4 -> "Fokus pada Detail Produk"
        5 -> "Angle Kreatif & Sinematik"
        else -> "Konsep Visual Affiliate #$index"
    }

    fun getGuideSubtitle(index: Int): String = when (index) {
        1 -> "Proporsi utuh produk, siluet keseluruhan, dan daya tarik utama secara jelas."
        2 -> "Sorotan jarak dekat untuk menonjolkan kualitas, fungsi, dan kejelasan bentuk produk."
        3 -> "Perspektif samping atau 3/4 dinamis yang memperlihatkan dimensi dan volume produk."
        4 -> "Fokus makro pada tekstur bahan, jahitan, kemasan, logo, dan detail halus produk."
        5 -> "Komposisi sinematik, pencahayaan dramatis elegan, dan storytelling visual premium."
        else -> "Variasi storyboard affiliate profesional."
    }
}
