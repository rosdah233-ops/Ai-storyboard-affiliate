package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.model.DetectedProductInfo
import com.example.model.StoryboardVariation
import com.example.model.UploadedPhoto
import com.example.network.GeminiStoryboardService
import com.example.util.MediaExportUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class StoryboardUiState(
    val productSlots: List<UploadedPhoto?> = listOf(null, null, null, null, null),
    val modelPhoto: UploadedPhoto? = null,
    val backgroundPhoto: UploadedPhoto? = null,
    val productUrl: String = "",
    val isDetectingUrl: Boolean = false,
    val detectedProductInfo: DetectedProductInfo? = null,
    val userNotes: String = "",
    val variationCount: Int = 3,
    val isGeneratingBatch: Boolean = false,
    val generationStatusText: String = "",
    val globalErrorMessage: String? = null,
    val productAnalysisSummary: String = "",
    val variations: List<StoryboardVariation> = emptyList(),
    val zoomedVariation: StoryboardVariation? = null,
    val snackbarMessage: String? = null
) {
    val activeProductPhotos: List<UploadedPhoto>
        get() = productSlots.filterNotNull()
}

class StoryboardViewModel(
    private val service: GeminiStoryboardService = GeminiStoryboardService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryboardUiState())
    val uiState: StateFlow<StoryboardUiState> = _uiState.asStateFlow()

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearGlobalError() {
        _uiState.update { it.copy(globalErrorMessage = null) }
    }

    fun setZoomedVariation(variation: StoryboardVariation?) {
        _uiState.update { it.copy(zoomedVariation = variation) }
    }

    fun onProductUrlChange(newUrl: String) {
        _uiState.update { it.copy(productUrl = newUrl) }
    }

    fun onUserNotesChange(newNotes: String) {
        _uiState.update { it.copy(userNotes = newNotes) }
    }

    fun setVariationCount(count: Int) {
        if (count in 1..5 && !_uiState.value.isGeneratingBatch) {
            _uiState.update { it.copy(variationCount = count) }
        }
    }

    fun handlePickedProductPhotoForSlot(context: Context, slotIndex: Int, uri: Uri) {
        if (slotIndex !in 0..4) return
        viewModelScope.launch {
            val photo = MediaExportUtil.loadUploadedPhotoFromUri(
                context = context,
                uri = uri,
                label = "Produk Slot ${slotIndex + 1}"
            )
            if (photo != null) {
                _uiState.update { state ->
                    val mutable = state.productSlots.toMutableList()
                    mutable[slotIndex] = photo
                    state.copy(
                        productSlots = mutable,
                        globalErrorMessage = null
                    )
                }
            } else {
                showSnackbar("Gagal memuat gambar. Pastikan format JPG, JPEG, atau PNG valid.")
            }
        }
    }

    fun handlePickedMultipleProductPhotos(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            val loadedPhotos = mutableListOf<UploadedPhoto>()
            for (uri in uris.take(5)) {
                val photo = MediaExportUtil.loadUploadedPhotoFromUri(context, uri, "Foto Produk")
                if (photo != null) {
                    loadedPhotos.add(photo)
                }
            }
            if (loadedPhotos.isEmpty()) {
                showSnackbar("Tidak dapat membaca foto yang dipilih.")
                return@launch
            }
            _uiState.update { state ->
                val updated = state.productSlots.toMutableList()
                var queueIdx = 0
                // Fill empty slots first
                for (i in updated.indices) {
                    if (updated[i] == null && queueIdx < loadedPhotos.size) {
                        updated[i] = loadedPhotos[queueIdx].copy(label = "Produk Slot ${i + 1}")
                        queueIdx++
                    }
                }
                // If still remaining photos, overwrite from slot 0
                var overwriteIdx = 0
                while (queueIdx < loadedPhotos.size && overwriteIdx < 5) {
                    updated[overwriteIdx] = loadedPhotos[queueIdx].copy(label = "Produk Slot ${overwriteIdx + 1}")
                    queueIdx++
                    overwriteIdx++
                }
                state.copy(productSlots = updated, globalErrorMessage = null)
            }
        }
    }

    fun removeProductPhotoAtSlot(slotIndex: Int) {
        if (slotIndex !in 0..4) return
        _uiState.update { state ->
            val updated = state.productSlots.toMutableList()
            updated[slotIndex] = null
            state.copy(productSlots = updated)
        }
    }

    fun loadSampleProductPhoto(context: Context) {
        viewModelScope.launch {
            val sample = MediaExportUtil.loadUploadedPhotoFromDrawable(
                context = context,
                drawableResId = R.drawable.img_sample_product_1791747435523,
                label = "Produk Contoh"
            )
            if (sample != null) {
                _uiState.update { state ->
                    val updated = state.productSlots.toMutableList()
                    val targetSlot = updated.indexOfFirst { it == null }.takeIf { it >= 0 } ?: 0
                    updated[targetSlot] = sample.copy(label = "Produk Slot ${targetSlot + 1}")
                    state.copy(
                        productSlots = updated,
                        globalErrorMessage = null,
                        snackbarMessage = "Foto produk contoh berhasil dimuat ke Slot ${targetSlot + 1}."
                    )
                }
            }
        }
    }

    fun handlePickedModelPhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            val photo = MediaExportUtil.loadUploadedPhotoFromUri(context, uri, "Foto Model")
            if (photo != null) {
                _uiState.update { it.copy(modelPhoto = photo) }
            } else {
                showSnackbar("Gagal memuat foto model.")
            }
        }
    }

    fun removeModelPhoto() {
        _uiState.update { it.copy(modelPhoto = null) }
    }

    fun handlePickedBackgroundPhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            val photo = MediaExportUtil.loadUploadedPhotoFromUri(context, uri, "Foto Background")
            if (photo != null) {
                _uiState.update { it.copy(backgroundPhoto = photo) }
            } else {
                showSnackbar("Gagal memuat foto background.")
            }
        }
    }

    fun removeBackgroundPhoto() {
        _uiState.update { it.copy(backgroundPhoto = null) }
    }

    fun detectProductFromUrl() {
        val currentUrl = _uiState.value.productUrl.trim()
        if (currentUrl.isEmpty()) {
            showSnackbar("Masukkan link produk terlebih dahulu.")
            return
        }
        if (_uiState.value.isDetectingUrl) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDetectingUrl = true,
                    detectedProductInfo = null
                )
            }
            val result = service.detectProductFromUrl(currentUrl)
            _uiState.update { state ->
                val updatedSlots = state.productSlots.toMutableList()
                // If link detection also retrieved a valid product image and all slots are currently empty, add it to Slot 1
                if (result.isSuccess && result.detectedBitmap != null && updatedSlots.all { it == null }) {
                    val scaled = MediaExportUtil.scaleBitmapDown(result.detectedBitmap, 1280)
                    updatedSlots[0] = UploadedPhoto(
                        id = UUID.randomUUID().toString(),
                        bitmap = scaled,
                        base64 = MediaExportUtil.bitmapToBase64(scaled),
                        mimeType = "image/jpeg",
                        label = "Produk dari Link"
                    )
                }
                state.copy(
                    isDetectingUrl = false,
                    detectedProductInfo = result,
                    productSlots = updatedSlots
                )
            }
        }
    }

    fun generateStoryboards() {
        val currentState = _uiState.value
        if (currentState.isGeneratingBatch) return

        val activeProducts = currentState.activeProductPhotos
        if (activeProducts.isEmpty()) {
            _uiState.update {
                it.copy(
                    globalErrorMessage = "Wajib mengunggah minimal 1 foto produk (Slot 1–5) sebelum membuat storyboard."
                )
            }
            showSnackbar("Unggah minimal 1 foto produk terlebih dahulu.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGeneratingBatch = true,
                    globalErrorMessage = null,
                    generationStatusText = "Menganalisis ${activeProducts.size} foto produk & menyusun ${currentState.variationCount} konsep storyboard..."
                )
            }

            try {
                // Step 1: Single multimodal analysis & structured prompt generation for all requested variations
                val batchPlan = service.analyzeAndCreateStoryboardPlans(
                    productPhotos = activeProducts,
                    modelPhoto = currentState.modelPhoto,
                    backgroundPhoto = currentState.backgroundPhoto,
                    detectedProductInfo = currentState.detectedProductInfo,
                    userNotes = currentState.userNotes,
                    variationCount = currentState.variationCount
                )

                val initialVariations = batchPlan.variations.take(currentState.variationCount).mapIndexed { idx, dto ->
                    StoryboardVariation(
                        variationNumber = idx + 1,
                        title = dto.title,
                        conceptAndAngle = dto.conceptAndAngle,
                        productConsistencyNote = dto.productConsistencyNote,
                        panels = dto.panels,
                        fullPrompt = dto.fullPrompt,
                        imageGenPrompt = dto.imageGenPrompt,
                        isGeneratingImage = true
                    )
                }

                _uiState.update {
                    it.copy(
                        productAnalysisSummary = batchPlan.productSummary,
                        variations = initialVariations
                    )
                }

                // Step 2: Generate the 5-panel 9:16 composite storyboard sheet for each variation
                for (variation in initialVariations) {
                    _uiState.update {
                        it.copy(
                            generationStatusText = "Membuat gambar lembar storyboard 5-panel (#${variation.variationNumber} dari ${initialVariations.size}: ${variation.title})..."
                        )
                    }

                    try {
                        val compositeBitmap = service.generateStoryboardCompositeImage(
                            productPhotos = activeProducts,
                            modelPhoto = currentState.modelPhoto,
                            backgroundPhoto = currentState.backgroundPhoto,
                            variation = variation
                        )

                        _uiState.update { state ->
                            state.copy(
                                variations = state.variations.map { item ->
                                    if (item.variationNumber == variation.variationNumber) {
                                        item.copy(
                                            compositeBitmap = compositeBitmap,
                                            imageWidth = compositeBitmap.width,
                                            imageHeight = compositeBitmap.height,
                                            isGeneratingImage = false,
                                            errorMessage = null
                                        )
                                    } else {
                                        item
                                    }
                                }
                            )
                        }
                    } catch (imgError: Exception) {
                        _uiState.update { state ->
                            state.copy(
                                variations = state.variations.map { item ->
                                    if (item.variationNumber == variation.variationNumber) {
                                        item.copy(
                                            isGeneratingImage = false,
                                            errorMessage = imgError.localizedMessage
                                                ?: "Gagal membuat gambar storyboard untuk variasi ini."
                                        )
                                    } else {
                                        item
                                    }
                                }
                            )
                        }
                    }
                }

                _uiState.update {
                    it.copy(
                        isGeneratingBatch = false,
                        generationStatusText = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGeneratingBatch = false,
                        generationStatusText = "",
                        globalErrorMessage = e.localizedMessage
                            ?: "Terjadi kendala saat membuat storyboard. Silakan periksa koneksi atau kunci API dan coba lagi."
                    )
                }
            }
        }
    }

    fun toggleRevisionInput(variationNumber: Int) {
        _uiState.update { state ->
            state.copy(
                variations = state.variations.map { v ->
                    if (v.variationNumber == variationNumber) {
                        v.copy(revisionInputVisible = !v.revisionInputVisible)
                    } else {
                        v
                    }
                }
            )
        }
    }

    fun updateRevisionInstruction(variationNumber: Int, instruction: String) {
        _uiState.update { state ->
            state.copy(
                variations = state.variations.map { v ->
                    if (v.variationNumber == variationNumber) {
                        v.copy(revisionInstruction = instruction)
                    } else {
                        v
                    }
                }
            )
        }
    }

    fun reviseSingleStoryboard(variationNumber: Int) {
        val currentState = _uiState.value
        val targetVariation = currentState.variations.find { it.variationNumber == variationNumber } ?: return
        val instruction = targetVariation.revisionInstruction.trim()

        if (instruction.isEmpty()) {
            showSnackbar("Masukkan instruksi perbaikan terlebih dahulu.")
            return
        }
        if (targetVariation.isRevising || targetVariation.isGeneratingImage) return

        val activeProducts = currentState.activeProductPhotos
        if (activeProducts.isEmpty()) {
            showSnackbar("Foto produk referensi tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    variations = state.variations.map { v ->
                        if (v.variationNumber == variationNumber) {
                            v.copy(isRevising = true, isGeneratingImage = true, errorMessage = null)
                        } else {
                            v
                        }
                    }
                )
            }

            try {
                val updatedPlan = service.reviseSingleVariationPlan(
                    productPhotos = activeProducts,
                    modelPhoto = currentState.modelPhoto,
                    backgroundPhoto = currentState.backgroundPhoto,
                    currentVariation = targetVariation,
                    revisionInstruction = instruction
                )

                val updatedVariationModel = targetVariation.copy(
                    title = updatedPlan.title,
                    conceptAndAngle = updatedPlan.conceptAndAngle,
                    productConsistencyNote = updatedPlan.productConsistencyNote,
                    panels = updatedPlan.panels,
                    fullPrompt = updatedPlan.fullPrompt,
                    imageGenPrompt = updatedPlan.imageGenPrompt
                )

                val newBitmap = service.generateStoryboardCompositeImage(
                    productPhotos = activeProducts,
                    modelPhoto = currentState.modelPhoto,
                    backgroundPhoto = currentState.backgroundPhoto,
                    variation = updatedVariationModel,
                    previousCompositeBitmap = targetVariation.compositeBitmap,
                    revisionInstruction = instruction
                )

                _uiState.update { state ->
                    state.copy(
                        variations = state.variations.map { v ->
                            if (v.variationNumber == variationNumber) {
                                updatedVariationModel.copy(
                                    compositeBitmap = newBitmap,
                                    imageWidth = newBitmap.width,
                                    imageHeight = newBitmap.height,
                                    isRevising = false,
                                    isGeneratingImage = false,
                                    revisionInputVisible = false,
                                    revisionInstruction = "",
                                    errorMessage = null
                                )
                            } else {
                                v
                            }
                        },
                        snackbarMessage = "Storyboard #$variationNumber berhasil diperbaiki."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        variations = state.variations.map { v ->
                            if (v.variationNumber == variationNumber) {
                                v.copy(
                                    isRevising = false,
                                    isGeneratingImage = false,
                                    errorMessage = e.localizedMessage ?: "Gagal memperbaiki storyboard."
                                )
                            } else {
                                v
                            }
                        }
                    )
                }
            }
        }
    }

    fun regenerateSingleStoryboard(variationNumber: Int) {
        val currentState = _uiState.value
        val targetVariation = currentState.variations.find { it.variationNumber == variationNumber } ?: return
        if (targetVariation.isGeneratingImage || targetVariation.isRevising) return

        val activeProducts = currentState.activeProductPhotos
        if (activeProducts.isEmpty()) {
            showSnackbar("Unggah minimal 1 foto produk terlebih dahulu.")
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    variations = state.variations.map { v ->
                        if (v.variationNumber == variationNumber) {
                            v.copy(isGeneratingImage = true, errorMessage = null)
                        } else {
                            v
                        }
                    }
                )
            }

            try {
                val refreshedPlan = service.reviseSingleVariationPlan(
                    productPhotos = activeProducts,
                    modelPhoto = currentState.modelPhoto,
                    backgroundPhoto = currentState.backgroundPhoto,
                    currentVariation = targetVariation,
                    revisionInstruction = "Buat alternatif visual dan komposisi adegan baru yang segar namun tetap sesuai fokus angle Variasi #$variationNumber (${targetVariation.title}) serta mempertahankan produk 100% identik."
                )

                val refreshedVariation = targetVariation.copy(
                    title = refreshedPlan.title,
                    conceptAndAngle = refreshedPlan.conceptAndAngle,
                    productConsistencyNote = refreshedPlan.productConsistencyNote,
                    panels = refreshedPlan.panels,
                    fullPrompt = refreshedPlan.fullPrompt,
                    imageGenPrompt = refreshedPlan.imageGenPrompt
                )

                val newBitmap = service.generateStoryboardCompositeImage(
                    productPhotos = activeProducts,
                    modelPhoto = currentState.modelPhoto,
                    backgroundPhoto = currentState.backgroundPhoto,
                    variation = refreshedVariation
                )

                _uiState.update { state ->
                    state.copy(
                        variations = state.variations.map { v ->
                            if (v.variationNumber == variationNumber) {
                                refreshedVariation.copy(
                                    compositeBitmap = newBitmap,
                                    imageWidth = newBitmap.width,
                                    imageHeight = newBitmap.height,
                                    isGeneratingImage = false,
                                    errorMessage = null
                                )
                            } else {
                                v
                            }
                        },
                        snackbarMessage = "Storyboard #$variationNumber berhasil di-generate ulang."
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        variations = state.variations.map { v ->
                            if (v.variationNumber == variationNumber) {
                                v.copy(
                                    isGeneratingImage = false,
                                    errorMessage = e.localizedMessage ?: "Gagal men-generate ulang storyboard."
                                )
                            } else {
                                v
                            }
                        }
                    )
                }
            }
        }
    }

    fun downloadStoryboard(context: Context, variation: StoryboardVariation) {
        val bmp = variation.compositeBitmap
        if (bmp == null) {
            showSnackbar("Gambar storyboard belum tersedia untuk diunduh.")
            return
        }
        viewModelScope.launch {
            val result = MediaExportUtil.saveStoryboardBitmapToGallery(
                context = context,
                bitmap = bmp,
                variationNumber = variation.variationNumber,
                title = variation.title
            )
            result.fold(
                onSuccess = { fileName ->
                    showSnackbar("Disimpan ke Galeri: $fileName")
                },
                onFailure = { err ->
                    showSnackbar("Gagal mengunduh gambar: ${err.localizedMessage}")
                }
            )
        }
    }

    fun copySinglePrompt(context: Context, variation: StoryboardVariation) {
        MediaExportUtil.copyTextToClipboard(
            context = context,
            label = "Prompt Storyboard #${variation.variationNumber}",
            text = variation.fullPrompt
        )
        showSnackbar("Prompt Storyboard #${variation.variationNumber} berhasil disalin!")
    }

    fun copyAllPrompts(context: Context) {
        val variations = _uiState.value.variations
        if (variations.isEmpty()) return

        val combined = variations.joinToString("\n\n========================================\n\n") { v ->
            """
            STORYBOARD #${v.variationNumber}: ${v.title.uppercase()}
            Konsep & Angle: ${v.conceptAndAngle}
            
            PROMPT GOOGLE FLOW (10 DETIK • 5 PANEL • 9:16):
            ${v.fullPrompt}
            """.trimIndent()
        }
        MediaExportUtil.copyTextToClipboard(
            context = context,
            label = "Semua Prompt Storyboard Affiliate",
            text = combined
        )
        showSnackbar("Semua ${variations.size} prompt storyboard berhasil disalin!")
    }
}
