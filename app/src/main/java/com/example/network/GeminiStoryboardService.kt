package com.example.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.BuildConfig
import com.example.model.DetectedProductInfo
import com.example.model.StoryboardBatchResponseDto
import com.example.model.StoryboardPanelSpec
import com.example.model.StoryboardPlanDto
import com.example.model.StoryboardVariation
import com.example.model.UploadedPhoto
import com.example.model.VariationGuide
import com.example.util.MediaExportUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: JsonObject? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null
)

@Serializable
data class ImageConfig(
    val aspectRatio: String,
    val imageSize: String? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class DetectedProductJsonDto(
    val isReadableProduct: Boolean,
    val productName: String,
    val description: String,
    val variationsAndSpecs: String
)

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

class GeminiStoryboardService {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        isLenient = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GeminiApiService::class.java)
    }

    private fun getValidatedApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY.trim()
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            throw IllegalStateException(
                "Kunci GEMINI_API_KEY belum diatur. Silakan masukkan GEMINI_API_KEY Anda melalui panel Secrets di Google AI Studio."
            )
        }
        return key
    }

    suspend fun detectProductFromUrl(rawUrl: String): DetectedProductInfo = withContext(Dispatchers.IO) {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) {
            return@withContext DetectedProductInfo(
                url = "",
                isSuccess = false,
                statusMessage = "Masukkan URL produk terlebih dahulu."
            )
        }

        val normalizedUrl = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }

        try {
            val request = Request.Builder()
                .url(normalizedUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                )
                .header("Accept-Language", "id-ID,id;q=0.9,en-US;q=0.8")
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()
            val finalUrl = response.request.url.toString()
            val html = response.body?.string().orEmpty()
            response.close()

            if (!response.isSuccessful || html.isBlank()) {
                return@withContext DetectedProductInfo(
                    url = normalizedUrl,
                    isSuccess = false,
                    statusMessage = "Halaman produk tidak dapat diakses (Kode ${response.code}). Silakan lanjutkan menggunakan foto produk."
                )
            }

            val ogTitle = extractMetaTag(html, "og:title") ?: extractHtmlTitle(html)
            val ogDesc = extractMetaTag(html, "og:description") ?: extractMetaName(html, "description")
            val ogImage = extractMetaTag(html, "og:image")
            val jsonLdSnippet = extractJsonLdSnippet(html)
            val bodyTextSnippet = html
                .replace(Regex("<script[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), " ")
                .replace(Regex("<style[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), " ")
                .replace(Regex("<[^>]+>"), " ")
                .replace(Regex("\\s+"), " ")
                .take(1800)

            val lowerTitle = (ogTitle ?: "").lowercase()
            val isLoginOrCaptchaWall = lowerTitle.contains("login") ||
                lowerTitle.contains("masuk") ||
                lowerTitle.contains("captcha") ||
                lowerTitle.contains("verify") ||
                finalUrl.contains("/buyer/login") ||
                (ogTitle.isNullOrBlank() && ogDesc.isNullOrBlank() && jsonLdSnippet.isBlank())

            if (isLoginOrCaptchaWall) {
                return@withContext DetectedProductInfo(
                    url = normalizedUrl,
                    isSuccess = false,
                    statusMessage = "Halaman marketplace membatasi pembacaan otomatis. Silakan lanjutkan menggunakan foto produk yang diunggah."
                )
            }

            val apiKey = getValidatedApiKey()
            val prompt = """
                Analisis informasi halaman produk berikut yang diambil dari URL: $finalUrl
                Judul Halaman / OG Title: ${ogTitle ?: "-"}
                Deskripsi / OG Description: ${ogDesc ?: "-"}
                JSON-LD Produk: $jsonLdSnippet
                Cuplikan Teks Halaman: $bodyTextSnippet

                Tugas:
                1. Tentukan apakah informasi ini benar-benar berisi detail produk spesifik (`isReadableProduct` = true) atau hanya halaman generik/blokir marketplace (`isReadableProduct` = false).
                2. Jika berisi detail produk, ekstrak nama produk (`productName`), ringkasan deskripsi fitur/bahan (`description`), dan variasi/spesifikasi (`variationsAndSpecs`) dalam Bahasa Indonesia yang singkat dan jelas.
            """.trimIndent()

            val schema = buildJsonObject {
                put("type", "OBJECT")
                putJsonObject("properties") {
                    putJsonObject("isReadableProduct") { put("type", "BOOLEAN") }
                    putJsonObject("productName") { put("type", "STRING") }
                    putJsonObject("description") { put("type", "STRING") }
                    putJsonObject("variationsAndSpecs") { put("type", "STRING") }
                }
                putJsonArray("required") {
                    add(kotlinx.serialization.json.JsonPrimitive("isReadableProduct"))
                    add(kotlinx.serialization.json.JsonPrimitive("productName"))
                    add(kotlinx.serialization.json.JsonPrimitive("description"))
                    add(kotlinx.serialization.json.JsonPrimitive("variationsAndSpecs"))
                }
            }

            val genResponse = callTextModelWithFallback(
                apiKey = apiKey,
                request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        responseSchema = schema,
                        temperature = 0.2f
                    )
                )
            )

            val rawJson = genResponse.candidates?.firstOrNull()?.content?.parts
                ?.firstOrNull { !it.text.isNullOrBlank() }?.text.orEmpty()

            val parsed = json.decodeFromString<DetectedProductJsonDto>(rawJson)
            if (!parsed.isReadableProduct || parsed.productName.isBlank()) {
                return@withContext DetectedProductInfo(
                    url = normalizedUrl,
                    isSuccess = false,
                    statusMessage = "Detail produk tidak dapat dibaca dari link ini. Silakan lanjutkan menggunakan foto produk."
                )
            }

            val downloadedBitmap = ogImage?.let { downloadImageBitmap(it) }

            DetectedProductInfo(
                url = normalizedUrl,
                title = parsed.productName,
                description = parsed.description,
                variationsOrDetails = parsed.variationsAndSpecs,
                imageUrl = ogImage,
                detectedBitmap = downloadedBitmap,
                isSuccess = true,
                statusMessage = "Produk berhasil terdeteksi dari link."
            )
        } catch (e: Exception) {
            DetectedProductInfo(
                url = normalizedUrl,
                isSuccess = false,
                statusMessage = "Link produk tidak dapat dibaca secara langsung. Silakan lanjutkan menggunakan foto produk yang diunggah."
            )
        }
    }

    suspend fun analyzeAndCreateStoryboardPlans(
        productPhotos: List<UploadedPhoto>,
        modelPhoto: UploadedPhoto?,
        backgroundPhoto: UploadedPhoto?,
        detectedProductInfo: DetectedProductInfo?,
        userNotes: String,
        variationCount: Int
    ): StoryboardBatchResponseDto = withContext(Dispatchers.IO) {
        val apiKey = getValidatedApiKey()
        val parts = mutableListOf<Part>()

        // 1. Add all product photos with clear labels
        productPhotos.forEachIndexed { index, photo ->
            parts.add(
                Part(text = "FOTO REFERENSI PRODUK #${index + 1} (WAJIB DIPERTAHANKAN 100% IDENTIK: bentuk, warna, motif, tekstur, logo, bahan, dan detail asli):")
            )
            parts.add(
                Part(inlineData = InlineData(mimeType = photo.mimeType, data = photo.base64))
            )
        }

        // 2. Add optional model photo
        if (modelPhoto != null) {
            parts.add(
                Part(text = "FOTO REFERENSI MODEL (Gunakan wajah, proporsi, dan karakteristik model ini secara konsisten):")
            )
            parts.add(
                Part(inlineData = InlineData(mimeType = modelPhoto.mimeType, data = modelPhoto.base64))
            )
        }

        // 3. Add optional background photo
        if (backgroundPhoto != null) {
            parts.add(
                Part(text = "FOTO REFERENSI BACKGROUND / LATAR (Gunakan suasana, arsitektur, dan pencahayaan latar ini):")
            )
            parts.add(
                Part(inlineData = InlineData(mimeType = backgroundPhoto.mimeType, data = backgroundPhoto.base64))
            )
        }

        val guideDescriptions = (1..variationCount).joinToString("\n") { idx ->
            "- Variasi $idx: ${VariationGuide.getGuideTitle(idx)} — ${VariationGuide.getGuideSubtitle(idx)}"
        }

        val productLinkContext = if (detectedProductInfo != null && detectedProductInfo.isSuccess) {
            """
            Informasi Tambahan dari Link Produk:
            - Nama Produk: ${detectedProductInfo.title}
            - Deskripsi: ${detectedProductInfo.description}
            - Variasi/Spesifikasi: ${detectedProductInfo.variationsOrDetails}
            """.trimIndent()
        } else {
            "Informasi Link Produk: Tidak ada (analisis sepenuhnya dari foto produk yang diunggah)."
        }

        val modelContext = if (modelPhoto != null) {
            "Pengguna mengunggah Foto Model referensi. Pertahankan karakter visual model tersebut."
        } else {
            "Tidak ada foto model yang diunggah. Buat konsep model atau penyajian produk yang paling menarik sesuai instruksi pengguna dan jenis produk."
        }

        val bgContext = if (backgroundPhoto != null) {
            "Pengguna mengunggah Foto Background referensi. Gunakan latar tersebut secara konsisten."
        } else {
            "Tidak ada foto background yang diunggah. Tentukan background estetis dan komersial yang paling cocok secara otomatis."
        }

        val notesContext = if (userNotes.isNotBlank()) {
            "Catatan / Instruksi Pengguna ('Mau Dibuat Seperti Apa?'): \"$userNotes\""
        } else {
            "Catatan Pengguna: Kosong (tentukan konsep komersial affiliate terbaik yang sesuai dengan jenis produk)."
        }

        val masterInstruction = """
            Anda adalah Creative Director & Spesialis Storyboard Video Affiliate AI profesional.
            Analisis seluruh Foto Produk yang dilampirkan secara sangat teliti: perhatikan bentuk presisi, warna akurat, motif/pola, tekstur permukaan, letak & bentuk logo/tulisan pada kemasan atau produk, bahan/material, serta setiap detail uniknya.

            $productLinkContext
            $modelContext
            $bgContext
            $notesContext

            Buat tepat $variationCount variasi storyboard untuk video affiliate berdurasi 10 detik.
            Panduan wajib untuk setiap variasi (sesuaikan dengan jenis produk yang diunggah):
            $guideDescriptions

            Setiap variasi WAJIB memiliki 5 panel adegan berurutan dalam satu lembar storyboard:
            - Panel 1: rentang waktu "0–2 detik" (Hook visual pembuka yang menarik perhatian)
            - Panel 2: rentang waktu "2–4 detik" (Penampilan utama / showcase produk)
            - Panel 3: rentang waktu "4–6 detik" (Interaksi, penggunaan, atau angle unggulan sesuai tema variasi)
            - Panel 4: rentang waktu "6–8 detik" (Sorotan detail kualitas bahan, tekstur, atau fitur utama)
            - Panel 5: rentang waktu "8–10 detik" (Adegan penutup meyakinkan / Call to Action visual)

            Untuk setiap variasi, hasilkan:
            1. `variationNumber`: Nomor urut variasi (1 sampai $variationCount).
            2. `title`: Judul storyboard dalam Bahasa Indonesia yang elegan dan sesuai panduan variasi.
            3. `conceptAndAngle`: Deskripsi konsep visual dan angle kamera utama dalam Bahasa Indonesia.
            4. `productConsistencyNote`: Ringkasan detail fisik produk asli (warna, bentuk, motif, logo, tekstur, bahan) yang wajib dipertahankan 100% tanpa perubahan.
            5. `panels`: Daftar 5 panel berurutan (panelNumber 1..5, timeRange "0–2 detik" s/d "8–10 detik", sceneTitle singkat, visualDescription jelas, cameraAngle spesifik).
            6. `fullPrompt`: Prompt lengkap dalam Bahasa Indonesia yang sangat detail dan siap disalin langsung ke Google Flow (Veo) untuk membuat video 10 detik. Wajib memuat:
               - Deskripsi produk & detail asli yang wajib dipertahankan 100%
               - Deskripsi model dan pose (jika menggunakan model)
               - Urutan 5 adegan beserta rentang waktunya (Panel 1: 0–2 detik, Panel 2: 2–4 detik, Panel 3: 4–6 detik, Panel 4: 6–8 detik, Panel 5: 8–10 detik)
               - Angle dan komposisi kamera tiap adegan
               - Background dan pencahayaan
               - Gaya visual fotorealistis komersial tajam
               - Penegasan konsistensi produk di seluruh panel.
            7. `imageGenPrompt`: Instruksi visual detail untuk model pembuat gambar guna menghasilkan SATU GAMBAR UTUH berbentuk lembar storyboard vertikal (rasio 9:16 portrait) yang terbagi rapi menjadi 5 panel adegan berurutan (Panel 1: 0-2s, Panel 2: 2-4s, Panel 3: 4-6s, Panel 4: 6-8s, Panel 5: 8-10s) dalam satu kanvas. Tekankan agar produk tampak 100% identik dengan foto referensi, gambar adegan jernih, fotorealistis, tajam, dan TIDAK dipenuhi tulisan (hanya label angka/waktu kecil yang rapi di sudut tiap panel).
        """.trimIndent()

        parts.add(Part(text = masterInstruction))

        val responseSchema = buildStoryboardBatchSchema()

        val response = callTextModelWithFallback(
            apiKey = apiKey,
            request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    responseSchema = responseSchema,
                    temperature = 0.4f
                )
            )
        )

        val responseText = response.candidates?.firstOrNull()?.content?.parts
            ?.firstOrNull { !it.text.isNullOrBlank() }?.text
            ?: throw IllegalStateException("AI tidak mengembalikan konsep storyboard. Silakan coba lagi.")

        json.decodeFromString<StoryboardBatchResponseDto>(responseText)
    }

    suspend fun generateStoryboardCompositeImage(
        productPhotos: List<UploadedPhoto>,
        modelPhoto: UploadedPhoto?,
        backgroundPhoto: UploadedPhoto?,
        variation: StoryboardVariation,
        previousCompositeBitmap: Bitmap? = null,
        revisionInstruction: String? = null
    ): Bitmap = withContext(Dispatchers.IO) {
        val apiKey = getValidatedApiKey()
        val parts = mutableListOf<Part>()

        // Attach all product reference photos so the image generation model preserves exact visual identity
        productPhotos.forEachIndexed { index, photo ->
            parts.add(Part(text = "Reference Product Photo #${index + 1} (Keep this exact product 100% identical in shape, color, pattern, texture, logo, and material across all 5 panels):"))
            parts.add(Part(inlineData = InlineData(mimeType = photo.mimeType, data = photo.base64)))
        }

        if (modelPhoto != null) {
            parts.add(Part(text = "Reference Model Photo (Keep model appearance consistent):"))
            parts.add(Part(inlineData = InlineData(mimeType = modelPhoto.mimeType, data = modelPhoto.base64)))
        }

        if (backgroundPhoto != null) {
            parts.add(Part(text = "Reference Background Photo (Use this environment style):"))
            parts.add(Part(inlineData = InlineData(mimeType = backgroundPhoto.mimeType, data = backgroundPhoto.base64)))
        }

        if (previousCompositeBitmap != null && !revisionInstruction.isNullOrBlank()) {
            parts.add(Part(text = "Previous Storyboard Sheet to Revise:"))
            parts.add(
                Part(
                    inlineData = InlineData(
                        mimeType = "image/jpeg",
                        data = MediaExportUtil.bitmapToBase64(previousCompositeBitmap, quality = 85)
                    )
                )
            )
        }

        val panelBreakdownText = variation.panels.joinToString("\n") { panel ->
            "- Panel ${panel.panelNumber} (${panel.timeRange}): [${panel.cameraAngle}] ${panel.sceneTitle} — ${panel.visualDescription}"
        }

        val revisionBlock = if (!revisionInstruction.isNullOrBlank()) {
            "\nUSER REVISION INSTRUCTION (MUST APPLY): $revisionInstruction\n"
        } else {
            ""
        }

        val finalImagePrompt = """
            Create ONE single unified vertical 9:16 portrait storyboard sheet image containing exactly 5 sequential video frames/panels arranged cleanly in a single composite canvas for a 10-second affiliate commercial video.

            CRITICAL PRODUCT FIDELITY:
            - The product shown in every panel MUST be 100% identical to the attached Reference Product Photos (${variation.productConsistencyNote}).
            - Do NOT alter the product's color, pattern, silhouette, logo, packaging, or material details.

            STORYBOARD VARIATION THEME:
            Variation #${variation.variationNumber}: ${variation.title}
            Concept & Camera Direction: ${variation.conceptAndAngle}
            $revisionBlock
            FIVE SEQUENTIAL PANELS IN ONE 9:16 CANVAS:
            $panelBreakdownText

            VISUAL & LAYOUT RULES:
            - Output a single composite 9:16 vertical storyboard sheet with 5 clearly separated cinematic frames separated by thin, clean borders.
            - Each panel may only have a tiny, minimal corner badge ("1 • 0-2s", "2 • 2-4s", "3 • 4-6s", "4 • 6-8s", "5 • 8-10s"). Do NOT clutter the image with paragraphs of text or watermarks.
            - Ultra-sharp, photorealistic commercial photography, natural lighting, high contrast and clarity, ready as a visual production reference.
            - Additional direction: ${variation.imageGenPrompt}
        """.trimIndent()

        parts.add(Part(text = finalImagePrompt))

        // Try generating 9:16 storyboard sheet using supported Gemini image generation models
        val modelsToTry = listOf(
            "gemini-2.5-flash-image" to ImageConfig(aspectRatio = "9:16"),
            "gemini-nano-banana-2.1" to ImageConfig(aspectRatio = "9:16", imageSize = "2K")
        )

        var lastError: Exception? = null

        for ((modelName, imgCfg) in modelsToTry) {
            try {
                val requestWithConfig = GenerateContentRequest(
                    contents = listOf(Content(parts = parts)),
                    generationConfig = GenerationConfig(
                        imageConfig = imgCfg,
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )
                val response = apiService.generateContent(
                    model = modelName,
                    apiKey = apiKey,
                    request = requestWithConfig
                )
                val bitmap = extractBitmapFromResponse(response)
                if (bitmap != null) return@withContext bitmap
            } catch (e: HttpException) {
                lastError = e
                // If imageConfig field caused a 400 on a specific endpoint, retry once with responseModalities only
                if (e.code() == 400) {
                    try {
                        val fallbackReq = GenerateContentRequest(
                            contents = listOf(Content(parts = parts)),
                            generationConfig = GenerationConfig(
                                responseModalities = listOf("TEXT", "IMAGE")
                            )
                        )
                        val response = apiService.generateContent(
                            model = modelName,
                            apiKey = apiKey,
                            request = fallbackReq
                        )
                        val bitmap = extractBitmapFromResponse(response)
                        if (bitmap != null) return@withContext bitmap
                    } catch (inner: Exception) {
                        lastError = inner
                    }
                }
            } catch (e: Exception) {
                lastError = e
            }
        }

        throw IllegalStateException(
            "Gagal menghasilkan gambar storyboard (${lastError?.localizedMessage ?: "Tidak ada data gambar dari model"}). Silakan coba lagi."
        )
    }

    suspend fun reviseSingleVariationPlan(
        productPhotos: List<UploadedPhoto>,
        modelPhoto: UploadedPhoto?,
        backgroundPhoto: UploadedPhoto?,
        currentVariation: StoryboardVariation,
        revisionInstruction: String
    ): StoryboardPlanDto = withContext(Dispatchers.IO) {
        val apiKey = getValidatedApiKey()
        val parts = mutableListOf<Part>()

        productPhotos.take(3).forEachIndexed { idx, photo ->
            parts.add(Part(text = "Foto Produk Referensi #${idx + 1}:"))
            parts.add(Part(inlineData = InlineData(mimeType = photo.mimeType, data = photo.base64)))
        }
        if (modelPhoto != null) {
            parts.add(Part(text = "Foto Model Referensi:"))
            parts.add(Part(inlineData = InlineData(mimeType = modelPhoto.mimeType, data = modelPhoto.base64)))
        }
        if (backgroundPhoto != null) {
            parts.add(Part(text = "Foto Background Referensi:"))
            parts.add(Part(inlineData = InlineData(mimeType = backgroundPhoto.mimeType, data = backgroundPhoto.base64)))
        }

        val prompt = """
            Perbarui konsep dan prompt untuk Storyboard Variasi #${currentVariation.variationNumber} berdasarkan instruksi revisi dari pengguna.

            Data Storyboard Saat Ini:
            - Judul: ${currentVariation.title}
            - Konsep & Angle: ${currentVariation.conceptAndAngle}
            - Detail Konsistensi Produk: ${currentVariation.productConsistencyNote}
            - Prompt Sebelumnya: ${currentVariation.fullPrompt}

            INSTRUKSI PERBAIKAN PENGGUNA:
            "$revisionInstruction"

            Ketentuan:
            1. Pertahankan produk 100% sesuai foto referensi (jangan ubah warna, bentuk, motif, logo, bahan).
            2. Terapkan instruksi revisi pengguna pada konsep, rincian 5 panel (Panel 1: 0–2 detik, Panel 2: 2–4 detik, Panel 3: 4–6 detik, Panel 4: 6–8 detik, Panel 5: 8–10 detik), `fullPrompt` Bahasa Indonesia untuk Google Flow, dan `imageGenPrompt` untuk komposit 5 panel 9:16.
        """.trimIndent()

        parts.add(Part(text = prompt))

        val singleSchema = buildSingleVariationSchema()

        val response = callTextModelWithFallback(
            apiKey = apiKey,
            request = GenerateContentRequest(
                contents = listOf(Content(parts = parts)),
                generationConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    responseSchema = singleSchema,
                    temperature = 0.4f
                )
            )
        )

        val responseText = response.candidates?.firstOrNull()?.content?.parts
            ?.firstOrNull { !it.text.isNullOrBlank() }?.text
            ?: throw IllegalStateException("Gagal memperbarui konsep storyboard.")

        json.decodeFromString<StoryboardPlanDto>(responseText)
    }

    private suspend fun callTextModelWithFallback(
        apiKey: String,
        request: GenerateContentRequest
    ): GenerateContentResponse {
        val textModels = listOf("gemini-3.5-flash", "gemini-flash-latest")
        var lastError: Exception? = null
        for (model in textModels) {
            try {
                return apiService.generateContent(model = model, apiKey = apiKey, request = request)
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IllegalStateException("Gagal menghubungi layanan Gemini API.")
    }

    private fun extractBitmapFromResponse(response: GenerateContentResponse): Bitmap? {
        val parts = response.candidates?.firstOrNull()?.content?.parts ?: return null
        for (part in parts) {
            val inline = part.inlineData
            if (inline != null && inline.data.isNotBlank()) {
                val bmp = MediaExportUtil.base64ToBitmap(inline.data)
                if (bmp != null) return bmp
            }
        }
        return null
    }

    private fun buildSingleVariationSchema(): JsonObject = buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
            putJsonObject("variationNumber") { put("type", "INTEGER") }
            putJsonObject("title") { put("type", "STRING") }
            putJsonObject("conceptAndAngle") { put("type", "STRING") }
            putJsonObject("productConsistencyNote") { put("type", "STRING") }
            putJsonObject("panels") {
                put("type", "ARRAY")
                putJsonObject("items") {
                    put("type", "OBJECT")
                    putJsonObject("properties") {
                        putJsonObject("panelNumber") { put("type", "INTEGER") }
                        putJsonObject("timeRange") { put("type", "STRING") }
                        putJsonObject("sceneTitle") { put("type", "STRING") }
                        putJsonObject("visualDescription") { put("type", "STRING") }
                        putJsonObject("cameraAngle") { put("type", "STRING") }
                    }
                    putJsonArray("required") {
                        add(kotlinx.serialization.json.JsonPrimitive("panelNumber"))
                        add(kotlinx.serialization.json.JsonPrimitive("timeRange"))
                        add(kotlinx.serialization.json.JsonPrimitive("sceneTitle"))
                        add(kotlinx.serialization.json.JsonPrimitive("visualDescription"))
                        add(kotlinx.serialization.json.JsonPrimitive("cameraAngle"))
                    }
                }
            }
            putJsonObject("fullPrompt") { put("type", "STRING") }
            putJsonObject("imageGenPrompt") { put("type", "STRING") }
        }
        putJsonArray("required") {
            add(kotlinx.serialization.json.JsonPrimitive("variationNumber"))
            add(kotlinx.serialization.json.JsonPrimitive("title"))
            add(kotlinx.serialization.json.JsonPrimitive("conceptAndAngle"))
            add(kotlinx.serialization.json.JsonPrimitive("productConsistencyNote"))
            add(kotlinx.serialization.json.JsonPrimitive("panels"))
            add(kotlinx.serialization.json.JsonPrimitive("fullPrompt"))
            add(kotlinx.serialization.json.JsonPrimitive("imageGenPrompt"))
        }
    }

    private fun buildStoryboardBatchSchema(): JsonObject = buildJsonObject {
        put("type", "OBJECT")
        putJsonObject("properties") {
            putJsonObject("productSummary") { put("type", "STRING") }
            putJsonObject("variations") {
                put("type", "ARRAY")
                putJsonObject("items") {
                    put("type", "OBJECT")
                    putJsonObject("properties") {
                        putJsonObject("variationNumber") { put("type", "INTEGER") }
                        putJsonObject("title") { put("type", "STRING") }
                        putJsonObject("conceptAndAngle") { put("type", "STRING") }
                        putJsonObject("productConsistencyNote") { put("type", "STRING") }
                        putJsonObject("panels") {
                            put("type", "ARRAY")
                            putJsonObject("items") {
                                put("type", "OBJECT")
                                putJsonObject("properties") {
                                    putJsonObject("panelNumber") { put("type", "INTEGER") }
                                    putJsonObject("timeRange") { put("type", "STRING") }
                                    putJsonObject("sceneTitle") { put("type", "STRING") }
                                    putJsonObject("visualDescription") { put("type", "STRING") }
                                    putJsonObject("cameraAngle") { put("type", "STRING") }
                                }
                                putJsonArray("required") {
                                    add(kotlinx.serialization.json.JsonPrimitive("panelNumber"))
                                    add(kotlinx.serialization.json.JsonPrimitive("timeRange"))
                                    add(kotlinx.serialization.json.JsonPrimitive("sceneTitle"))
                                    add(kotlinx.serialization.json.JsonPrimitive("visualDescription"))
                                    add(kotlinx.serialization.json.JsonPrimitive("cameraAngle"))
                                }
                            }
                        }
                        putJsonObject("fullPrompt") { put("type", "STRING") }
                        putJsonObject("imageGenPrompt") { put("type", "STRING") }
                    }
                    putJsonArray("required") {
                        add(kotlinx.serialization.json.JsonPrimitive("variationNumber"))
                        add(kotlinx.serialization.json.JsonPrimitive("title"))
                        add(kotlinx.serialization.json.JsonPrimitive("conceptAndAngle"))
                        add(kotlinx.serialization.json.JsonPrimitive("productConsistencyNote"))
                        add(kotlinx.serialization.json.JsonPrimitive("panels"))
                        add(kotlinx.serialization.json.JsonPrimitive("fullPrompt"))
                        add(kotlinx.serialization.json.JsonPrimitive("imageGenPrompt"))
                    }
                }
            }
        }
        putJsonArray("required") {
            add(kotlinx.serialization.json.JsonPrimitive("productSummary"))
            add(kotlinx.serialization.json.JsonPrimitive("variations"))
        }
    }

    private fun extractMetaTag(html: String, property: String): String? {
        val regex1 = Regex(
            """<meta[^>]+(?:property|name)\s*=\s*["']$property["'][^>]+content\s*=\s*["']([^"']+)["']""",
            RegexOption.IGNORE_CASE
        )
        val regex2 = Regex(
            """<meta[^>]+content\s*=\s*["']([^"']+)["'][^>]+(?:property|name)\s*=\s*["']$property["']""",
            RegexOption.IGNORE_CASE
        )
        return (regex1.find(html)?.groupValues?.getOrNull(1)
            ?: regex2.find(html)?.groupValues?.getOrNull(1))
            ?.let { decodeHtmlEntities(it).trim() }
            ?.takeIf { it.isNotEmpty() }
    }

    private fun extractMetaName(html: String, name: String): String? = extractMetaTag(html, name)

    private fun extractHtmlTitle(html: String): String? {
        val regex = Regex("""<title[^>]*>([\s\S]*?)</title>""", RegexOption.IGNORE_CASE)
        return regex.find(html)?.groupValues?.getOrNull(1)
            ?.let { decodeHtmlEntities(it).replace(Regex("\\s+"), " ").trim() }
            ?.takeIf { it.isNotEmpty() }
    }

    private fun extractJsonLdSnippet(html: String): String {
        val regex = Regex(
            """<script[^>]+type\s*=\s*["']application/ld\+json["'][^>]*>([\s\S]*?)</script>""",
            RegexOption.IGNORE_CASE
        )
        return regex.findAll(html)
            .mapNotNull { it.groupValues.getOrNull(1)?.trim() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
            .take(1000)
    }

    private fun decodeHtmlEntities(input: String): String {
        return input
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }

    private fun downloadImageBitmap(imageUrl: String): Bitmap? {
        return try {
            val req = Request.Builder().url(imageUrl).get().build()
            okHttpClient.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val bytes = resp.body?.bytes() ?: return null
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            null
        }
    }
}
