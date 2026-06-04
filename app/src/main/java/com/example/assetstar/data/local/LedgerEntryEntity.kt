package com.example.assetstar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val account: String = "bank_card",
    val occurredAt: Long,
    val note: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
)
