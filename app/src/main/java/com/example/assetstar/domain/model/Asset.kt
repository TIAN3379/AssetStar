package com.example.assetstar.domain.model

data class Asset(
    val id: Long = 0L,
    val name: String = "",
    val category: AssetCategory = AssetCategory.DIGITAL,
    val status: AssetStatus = AssetStatus.IN_USE,
    val purchasePrice: Double = 0.0,
    val purchaseDate: Long = 0L,
    val imageUri: String? = null,
    val note: String? = null,
    val estimatedResidualValue: Double? = null,
    val soldPrice: Double? = null,
    val soldDate: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)
