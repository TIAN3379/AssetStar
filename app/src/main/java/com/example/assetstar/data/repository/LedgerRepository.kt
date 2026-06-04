package com.example.assetstar.data.repository

import com.example.assetstar.data.local.LedgerDao
import com.example.assetstar.data.mapper.toDomain
import com.example.assetstar.data.mapper.toEntity
import com.example.assetstar.domain.model.LedgerEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LedgerRepository {
    fun observeEntries(): Flow<List<LedgerEntry>>
    suspend fun insert(entry: LedgerEntry): Long
    suspend fun delete(entry: LedgerEntry)
}

class LedgerRepositoryImpl(
    private val dao: LedgerDao,
) : LedgerRepository {
    override fun observeEntries(): Flow<List<LedgerEntry>> {
        return dao.observeEntries().map { entries -> entries.map { it.toDomain() } }
    }

    override suspend fun insert(entry: LedgerEntry): Long {
        return dao.insert(entry.toEntity())
    }

    override suspend fun delete(entry: LedgerEntry) {
        dao.delete(entry.toEntity())
    }
}
