package com.example.assetstar.data.repository

import com.example.assetstar.data.local.AssetDao
import com.example.assetstar.data.mapper.toDomain
import com.example.assetstar.data.mapper.toEntity
import com.example.assetstar.domain.model.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AssetRepository {
    fun observeAssets(): Flow<List<Asset>>
    fun observeAssetById(assetId: Long): Flow<Asset?>
    suspend fun insert(asset: Asset): Long
    suspend fun update(asset: Asset)
    suspend fun delete(asset: Asset)
    suspend fun clearAll()
}

class AssetRepositoryImpl(
    private val dao: AssetDao,
) : AssetRepository {
    override fun observeAssets(): Flow<List<Asset>> {
        return dao.observeAssets().map { items -> items.map { it.toDomain() } }
    }

    override fun observeAssetById(assetId: Long): Flow<Asset?> {
        return dao.observeAssetById(assetId).map { it?.toDomain() }
    }

    override suspend fun insert(asset: Asset): Long {
        return dao.insert(asset.toEntity())
    }

    override suspend fun update(asset: Asset) {
        dao.update(asset.toEntity())
    }

    override suspend fun delete(asset: Asset) {
        dao.delete(asset.toEntity())
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}
