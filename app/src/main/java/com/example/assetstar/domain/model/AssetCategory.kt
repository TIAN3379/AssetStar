package com.example.assetstar.domain.model

enum class AssetCategory(
    val storageValue: String,
    val displayName: String,
) {
    ALL("ALL", "全部资产"),
    DIGITAL("DIGITAL", "电子数码"),
    LIVING("LIVING", "家居家电"),
    TRANSPORT("TRANSPORT", "出行工具"),
    HEALTH("HEALTH", "健康设备"),
    LEISURE("LEISURE", "兴趣收藏"),
    CASH("CASH", "实体书籍"),
    INVESTMENT("INVESTMENT", "投资资产"),
    DAILY("DAILY", "日用资产"),
    OTHER("OTHER", "其他资产");

    companion object {
        val filters: List<AssetCategory> = entries
        val selectable: List<AssetCategory> = entries.filterNot { it == ALL }
        val homeOrbitOrder: List<AssetCategory> = listOf(
            DIGITAL,
            CASH,
            INVESTMENT,
            TRANSPORT,
            DAILY,
            LEISURE,
            HEALTH,
            LIVING,
        )

        fun fromStorage(value: String): AssetCategory {
            return entries.firstOrNull { it.storageValue == value } ?: OTHER
        }
    }
}
