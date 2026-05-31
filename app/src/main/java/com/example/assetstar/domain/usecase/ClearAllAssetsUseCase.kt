package com.example.assetstar.domain.usecase

import com.example.assetstar.data.repository.AssetRepository

class ClearAllAssetsUseCase(
    private val repository: AssetRepository,
) {
    suspend operator fun invoke() = repository.clearAll()
}
