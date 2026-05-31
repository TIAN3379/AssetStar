package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAssetsByCategoryUseCase(
    private val repository: AssetRepository,
) {
    operator fun invoke(category: AssetCategory): Flow<List<Asset>> {
        return repository.observeAssets().map { assets ->
            if (category == AssetCategory.ALL) {
                assets
            } else {
                assets.filter { it.category == category }
            }
        }
    }
}
