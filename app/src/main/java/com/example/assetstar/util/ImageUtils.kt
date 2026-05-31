package com.example.assetstar.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    private const val MAX_IMAGE_DIMENSION = 1600
    private const val IMAGE_QUALITY = 88

    fun hasImage(uri: String?): Boolean = !uri.isNullOrBlank()

    fun toUriOrNull(uri: String?): Uri? = uri?.takeIf { it.isNotBlank() }?.let(Uri::parse)

    fun copyImageToPrivateStorage(
        context: Context,
        sourceUri: Uri,
    ): String? {
        return runCatching {
            val appContext = context.applicationContext
            val outputDir = File(appContext.filesDir, "asset_images").apply { mkdirs() }
            val outputFile = File(outputDir, "asset_${System.currentTimeMillis()}.jpg")

            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                BitmapFactory.decodeStream(input, null, bounds)
            }

            val bitmap = if (bounds.outWidth > 0 && bounds.outHeight > 0) {
                val sampleSize = calculateSampleSize(
                    width = bounds.outWidth,
                    height = bounds.outHeight,
                    maxDimension = MAX_IMAGE_DIMENSION,
                )
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, decodeOptions)
                }
            } else {
                null
            }

            if (bitmap != null) {
                try {
                    FileOutputStream(outputFile).use { output ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, output)
                    }
                } finally {
                    bitmap.recycle()
                }
            } else {
                appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                } ?: return@runCatching null
            }

            Uri.fromFile(outputFile).toString()
        }.getOrNull()
    }

    private fun calculateSampleSize(
        width: Int,
        height: Int,
        maxDimension: Int,
    ): Int {
        var sampleSize = 1
        while (width / sampleSize > maxDimension || height / sampleSize > maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }
}
