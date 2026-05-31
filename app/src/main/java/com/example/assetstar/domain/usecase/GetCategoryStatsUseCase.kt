package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.domain.calculator.AssetCalculator
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoryStatsUseCase(
    private val repository: AssetRepository,
    private val calculator: AssetCalculator,
) {
    operator fun invoke(category: AssetCategory): Flow<CategoryStats> {
        return repository.observeAssets().map { assets ->
            calculator.buildDetailedCategoryStats(category, assets)
        }
    }
}
