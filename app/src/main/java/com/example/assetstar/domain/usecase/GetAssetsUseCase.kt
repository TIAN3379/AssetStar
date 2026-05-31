package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository

class GetAssetsUseCase(
    private val repository: AssetRepository,
) {
    operator fun invoke() = repository.observeAssets()
}
