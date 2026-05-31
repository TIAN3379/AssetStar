package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository

class GetAssetByIdUseCase(
    private val repository: AssetRepository,
) {
    operator fun invoke(assetId: Long) = repository.observeAssetById(assetId)
}
