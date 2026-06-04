package com.example.assetstar.util

import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetStatus

object AssetVisuals {
    private const val CHERISHED_TOKEN = "#珍藏"

    fun starLevel(asset: Asset): Int {
        return when {
            asset.purchasePrice >= 10_000.0 -> 5
            asset.purchasePrice >= 5_000.0 -> 4
            asset.purchasePrice >= 2_000.0 -> 3
            asset.purchasePrice >= 500.0 -> 2
            else -> 1
        }
    }

    fun starText(level: Int): String {
        val safeLevel = level.coerceIn(1, 5)
        return "★".repeat(safeLevel) + "☆".repeat(5 - safeLevel)
    }

    fun isHighValue(asset: Asset): Boolean = starLevel(asset) >= 4

    fun isCherished(asset: Asset): Boolean {
        return isCherishedNote(asset.note)
    }

    fun toggleCherishedNote(note: String?): String? {
        val current = displayNote(note).orEmpty()
        return if (isCherishedNote(note)) {
            current.ifBlank { null }
        } else {
            (current.trim() + " $CHERISHED_TOKEN").trim()
        }
    }

    fun persistedNote(displayNote: String, cherished: Boolean): String? {
        val cleanNote = displayNote(displayNote).orEmpty()
        val persisted = if (cherished) {
            (cleanNote.trim() + " $CHERISHED_TOKEN").trim()
        } else {
            cleanNote.trim()
        }
        return persisted.ifBlank { null }
    }

    fun displayNote(note: String?): String? {
        return note
            ?.replace(CHERISHED_TOKEN, "")
            ?.replace(Regex("\\s{2,}"), " ")
            ?.trim()
            ?.ifBlank { null }
    }

    private fun isCherishedNote(note: String?): Boolean {
        return note?.contains(CHERISHED_TOKEN) == true
    }

    fun completionPercent(asset: Asset): Int {
        var score = 0
        var total = 0

        fun add(done: Boolean, weight: Int = 1) {
            total += weight
            if (done) score += weight
        }

        add(asset.name.isNotBlank(), 2)
        add(asset.purchasePrice > 0.0, 2)
        add(asset.purchaseDate > 0L, 1)
        add(!asset.imageUri.isNullOrBlank(), 2)
        add(asset.estimatedResidualValue != null, 1)
        add(!displayNote(asset.note).isNullOrBlank(), 1)
        add(asset.status != AssetStatus.SOLD || asset.soldPrice != null, 1)

        return if (total == 0) 0 else ((score.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
    }
}
