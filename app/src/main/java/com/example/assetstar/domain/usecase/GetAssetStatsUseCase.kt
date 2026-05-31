package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.domain.calculator.AssetCalculator
import com.example.assetstar.domain.model.AssetStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAssetStatsUseCase(
    private val repository: AssetRepository,
    private val calculator: AssetCalculator,
) {
    operator fun invoke(): Flow<AssetStats> {
        return repository.observeAssets().map { assets ->
            calculator.buildStats(assets)
        }
    }
}
