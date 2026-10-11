package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.UUID
import com.example.model.UploadedPhoto

object MediaExportUtil {

    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 85): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val cleanBase64 = base64String.substringAfter("base64,", base64String).trim()
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadUploadedPhotoFromUri(
        context: Context,
        uri: Uri,
        label: String = "Foto"
    ): UploadedPhoto? = withContext(Dispatchers.IO) {
        try {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val normalizedMime = when {
                mimeType.contains("png", ignoreCase = true) -> "image/png"
                else -> "image/jpeg"
            }

            val rawBitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return@withContext null

            val scaledBitmap = scaleBitmapDown(rawBitmap, maxDimension = 1280)
            val base64 = bitmapToBase64(scaledBitmap, quality = 86)

            UploadedPhoto(
                id = UUID.randomUUID().toString(),
                bitmap = scaledBitmap,
                base64 = base64,
                mimeType = normalizedMime,
                label = label
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadUploadedPhotoFromDrawable(
        context: Context,
        drawableResId: Int,
        label: String
    ): UploadedPhoto? = withContext(Dispatchers.IO) {
        try {
            val rawBitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
                ?: return@withContext null
            val scaledBitmap = scaleBitmapDown(rawBitmap, maxDimension = 1280)
            val base64 = bitmapToBase64(scaledBitmap, quality = 86)
            UploadedPhoto(
                id = UUID.randomUUID().toString(),
                bitmap = scaledBitmap,
                base64 = base64,
                mimeType = "image/jpeg",
                label = label
            )
        } catch (e: Exception) {
            null
        }
    }

    fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val (targetWidth, targetHeight) = if (width > height) {
            maxDimension to (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            (maxDimension * ratio).toInt().coerceAtLeast(1) to maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    suspend fun saveStoryboardBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        variationNumber: Int,
        title: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val safeTitle = title
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                .take(24)
                .trim('_')
            val fileName = "Storyboard_V${variationNumber}_${safeTitle}_${System.currentTimeMillis()}.png"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/AiStoryboardAffiliate"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext Result.failure(Exception("Gagal membuat file di Galeri."))

            var outputStream: OutputStream? = null
            try {
                outputStream = resolver.openOutputStream(uri)
                    ?: return@withContext Result.failure(Exception("Gagal membuka stream penyimpanan."))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            } finally {
                outputStream?.close()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Result.success(fileName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun copyTextToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
