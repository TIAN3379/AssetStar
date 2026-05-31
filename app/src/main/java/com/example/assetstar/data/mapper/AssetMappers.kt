package com.example.assetstar.data.mapper

import com.example.assetstar.data.local.AssetEntity
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetStatus

fun AssetEntity.toDomain(): Asset {
    return Asset(
        id = id,
        name = name,
        category = AssetCategory.fromStorage(category),
        status = AssetStatus.fromStorage(status),
        purchasePrice = purchasePrice,
        purchaseDate = purchaseDate,
        imageUri = imageUri,
        note = note,
        estimatedResidualValue = estimatedResidualValue,
        soldPrice = soldPrice,
        soldDate = soldDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun Asset.toEntity(): AssetEntity {
    return AssetEntity(
        id = id,
        name = name,
        category = category.storageValue,
        status = status.storageValue,
        purchasePrice = purchasePrice,
        purchaseDate = purchaseDate,
        imageUri = imageUri,
        note = note,
        estimatedResidualValue = estimatedResidualValue,
        soldPrice = soldPrice,
        soldDate = soldDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
