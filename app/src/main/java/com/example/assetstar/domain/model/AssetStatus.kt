package com.example.assetstar.domain.model

enum class AssetStatus(
    val storageValue: String,
    val displayName: String,
) {
    IN_USE("IN_USE", "使用中"),
    RETIRED("RETIRED", "已退役"),
    SOLD("SOLD", "已出售");

    companion object {
        fun fromStorage(value: String): AssetStatus {
            return entries.firstOrNull { it.storageValue == value } ?: IN_USE
        }
    }
}
