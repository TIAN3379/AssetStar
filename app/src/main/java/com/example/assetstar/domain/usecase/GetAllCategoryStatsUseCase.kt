package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.domain.calculator.AssetCalculator
import com.example.assetstar.domain.model.CategoryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllCategoryStatsUseCase(
    private val repository: AssetRepository,
    private val calculator: AssetCalculator,
) {
    operator fun invoke(): Flow<List<CategoryStats>> {
        return repository.observeAssets().map { assets ->
            calculator.buildDetailedCategoryStats(assets)
        }
    }
}
