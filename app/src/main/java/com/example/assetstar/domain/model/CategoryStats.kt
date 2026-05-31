package com.example.assetstar.domain.model

data class CategoryStats(
    val category: AssetCategory,
    val assetCount: Int,
    val inUseCount: Int,
    val retiredCount: Int,
    val soldCount: Int,
    val totalPurchasePrice: Double,
    val totalResidualValue: Double,
    val totalDailyCost: Double,
    val averageDailyCost: Double,
)
