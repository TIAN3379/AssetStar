package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.domain.model.Asset

class UpdateAssetUseCase(
    private val repository: AssetRepository,
) {
    suspend operator fun invoke(asset: Asset) = repository.update(asset)
}
