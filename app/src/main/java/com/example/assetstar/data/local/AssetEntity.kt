package com.example.assetstar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String,
    val status: String,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val imageUri: String? = null,
    val note: String? = null,
    val estimatedResidualValue: Double? = null,
    val soldPrice: Double? = null,
    val soldDate: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
