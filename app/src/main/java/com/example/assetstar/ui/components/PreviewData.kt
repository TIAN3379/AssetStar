package com.example.assetstar.ui.components

import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.AssetStats
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryStat
import com.example.assetstar.domain.model.StatusStat
import com.example.assetstar.util.DateUtils

val PreviewAsset = Asset(
    id = 1L,
    name = "iPhone 14 Pro",
    category = AssetCategory.DIGITAL,
    status = AssetStatus.IN_USE,
    purchasePrice = 8999.0,
    purchaseDate = DateUtils.todayStartMillis(),
    estimatedResidualValue = 3200.0,
    note = "示例预览数据",
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
)

val PreviewAsset2 = Asset(
    id = 2L,
    name = "MacBook Pro 14",
    category = AssetCategory.DIGITAL,
    status = AssetStatus.IN_USE,
    purchasePrice = 14288.0,
    purchaseDate = DateUtils.todayStartMillis(),
    estimatedResidualValue = 7600.0,
    note = "第二张预览卡片",
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis() + 1,
)

val PreviewMetrics = AssetMetrics(
    useDays = 620,
    dailyCost = 14.51,
)

val PreviewMetrics2 = AssetMetrics(
    useDays = 210,
    dailyCost = 31.42,
)

val PreviewStats = AssetStats(
    totalAssetValue = 12580.0,
    totalDailyCost = 38.62,
    totalAssetCount = 8,
    categoryStats = listOf(
        CategoryStat(AssetCategory.ALL, 12580.0, 38.62, 8),
        CategoryStat(AssetCategory.CASH, 2650.5, 0.0, 1),
        CategoryStat(AssetCategory.INVESTMENT, 4350.0, 0.0, 1),
        CategoryStat(AssetCategory.TRANSPORT, 126.5, 2.4, 2),
        CategoryStat(AssetCategory.DAILY, 468.2, 8.2, 3),
        CategoryStat(AssetCategory.LEISURE, 456.0, 6.6, 2),
        CategoryStat(AssetCategory.HEALTH, 123.0, 1.1, 1),
        CategoryStat(AssetCategory.LIVING, 1980.0, 20.32, 2),
    ),
    statusStats = listOf(
        StatusStat(AssetStatus.IN_USE, 6, 11800.0),
        StatusStat(AssetStatus.RETIRED, 1, 480.0),
        StatusStat(AssetStatus.SOLD, 1, 300.0, 220.0),
    ),
    featuredAsset = PreviewAsset,
)
