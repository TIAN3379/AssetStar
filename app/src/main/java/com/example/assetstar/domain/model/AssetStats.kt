package com.example.assetstar.domain.model

data class AssetMetrics(
    val useDays: Int,
    val dailyCost: Double,
    val actualCost: Double? = null,
    val actualDailyCost: Double? = null,
)

data class CategoryStat(
    val category: AssetCategory,
    val totalValue: Double,
    val dailyCost: Double,
    val assetCount: Int,
)

data class StatusStat(
    val status: AssetStatus,
    val count: Int,
    val totalValue: Double,
    val recoveredValue: Double = 0.0,
)

data class AssetStats(
    val totalAssetValue: Double = 0.0,
    val totalDailyCost: Double = 0.0,
    val totalAssetCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val statusStats: List<StatusStat> = emptyList(),
    val featuredAsset: Asset? = null,
)
