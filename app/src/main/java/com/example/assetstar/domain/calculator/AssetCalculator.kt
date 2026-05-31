package com.example.assetstar.domain.calculator

import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.AssetStats
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.domain.model.CategoryStat
import com.example.assetstar.domain.model.StatusStat
import kotlin.math.max

class AssetCalculator {
    fun calculateMetrics(asset: Asset, nowMillis: Long = System.currentTimeMillis()): AssetMetrics {
        val activeDays = calculateUseDays(asset.purchaseDate, nowMillis)
        val baseDailyCost = if (activeDays <= 0) 0.0 else asset.purchasePrice / activeDays

        if (asset.status == AssetStatus.SOLD && asset.soldPrice != null && asset.soldDate != null) {
            val soldDays = calculateUseDays(asset.purchaseDate, asset.soldDate)
            val actualCost = asset.purchasePrice - asset.soldPrice
            return AssetMetrics(
                useDays = soldDays,
                dailyCost = if (soldDays <= 0) 0.0 else actualCost / soldDays,
                actualCost = actualCost,
                actualDailyCost = if (soldDays <= 0) 0.0 else actualCost / soldDays,
            )
        }

        return AssetMetrics(
            useDays = activeDays,
            dailyCost = baseDailyCost,
        )
    }

    fun calculateUseDays(startMillis: Long, endMillis: Long): Int {
        val diff = ((normalizeToDay(endMillis) - normalizeToDay(startMillis)) / DAY_IN_MILLIS).toInt()
        return max(1, diff)
    }

    fun buildStats(assets: List<Asset>, nowMillis: Long = System.currentTimeMillis()): AssetStats {
        val totalAssetValue = assets.sumOf { it.purchasePrice }
        val totalDailyCost = assets
            .filter { it.status == AssetStatus.IN_USE }
            .sumOf { calculateMetrics(it, nowMillis).dailyCost }

        val categoryStats = buildCategoryStats(assets, nowMillis)
        val statusStats = buildStatusStats(assets)

        return AssetStats(
            totalAssetValue = totalAssetValue,
            totalDailyCost = totalDailyCost,
            totalAssetCount = assets.size,
            categoryStats = categoryStats,
            statusStats = statusStats,
            featuredAsset = assets.maxByOrNull { it.purchasePrice },
        )
    }

    private fun buildCategoryStats(
        assets: List<Asset>,
        nowMillis: Long,
    ): List<CategoryStat> {
        val grouped = assets.groupBy { it.category }
        val orbitStats = AssetCategory.homeOrbitOrder
            .filterNot { it == AssetCategory.ALL }
            .map { category ->
                val items = grouped[category].orEmpty()
                CategoryStat(
                    category = category,
                    totalValue = items.sumOf { it.purchasePrice },
                    dailyCost = items
                        .filter { it.status == AssetStatus.IN_USE }
                        .sumOf { calculateMetrics(it, nowMillis).dailyCost },
                    assetCount = items.size,
                )
            }

        val allStat = CategoryStat(
            category = AssetCategory.ALL,
            totalValue = assets.sumOf { it.purchasePrice },
            dailyCost = assets
                .filter { it.status == AssetStatus.IN_USE }
                .sumOf { calculateMetrics(it, nowMillis).dailyCost },
            assetCount = assets.size,
        )

        return listOf(allStat) + orbitStats
    }

    fun buildDetailedCategoryStats(
        assets: List<Asset>,
        nowMillis: Long = System.currentTimeMillis(),
    ): List<CategoryStats> {
        val allStats = buildDetailedCategoryStat(AssetCategory.ALL, assets, nowMillis)
        val categoryStats = AssetCategory.selectable.map { category ->
            buildDetailedCategoryStat(
                category = category,
                assets = assets.filter { it.category == category },
                nowMillis = nowMillis,
            )
        }
        return listOf(allStats) + categoryStats
    }

    fun buildDetailedCategoryStats(
        category: AssetCategory,
        assets: List<Asset>,
        nowMillis: Long = System.currentTimeMillis(),
    ): CategoryStats {
        val filtered = if (category == AssetCategory.ALL) {
            assets
        } else {
            assets.filter { it.category == category }
        }
        return buildDetailedCategoryStat(category, filtered, nowMillis)
    }

    private fun buildDetailedCategoryStat(
        category: AssetCategory,
        assets: List<Asset>,
        nowMillis: Long,
    ): CategoryStats {
        val inUseAssets = assets.filter { it.status == AssetStatus.IN_USE }
        val totalDailyCost = inUseAssets.sumOf { calculateMetrics(it, nowMillis).dailyCost }
        val dailyCostSamples = assets.map { calculateMetrics(it, nowMillis).dailyCost }
        return CategoryStats(
            category = category,
            assetCount = assets.size,
            inUseCount = inUseAssets.size,
            retiredCount = assets.count { it.status == AssetStatus.RETIRED },
            soldCount = assets.count { it.status == AssetStatus.SOLD },
            totalPurchasePrice = assets.sumOf { it.purchasePrice },
            totalResidualValue = assets.sumOf { it.estimatedResidualValue ?: 0.0 },
            totalDailyCost = totalDailyCost,
            averageDailyCost = dailyCostSamples.average().takeUnless { it.isNaN() } ?: 0.0,
        )
    }

    private fun buildStatusStats(assets: List<Asset>): List<StatusStat> {
        return AssetStatus.entries.map { status ->
            val items = assets.filter { it.status == status }
            StatusStat(
                status = status,
                count = items.size,
                totalValue = items.sumOf { it.purchasePrice },
                recoveredValue = items.sumOf { it.soldPrice ?: 0.0 },
            )
        }
    }

    private fun normalizeToDay(value: Long): Long {
        return value - (value % DAY_IN_MILLIS)
    }

    private companion object {
        const val DAY_IN_MILLIS = 24L * 60L * 60L * 1000L
    }
}
