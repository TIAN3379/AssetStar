package com.example.assetstar.data.mapper

import com.example.assetstar.data.local.LedgerEntryEntity
import com.example.assetstar.domain.model.LedgerCategory
import com.example.assetstar.domain.model.LedgerEntry
import com.example.assetstar.domain.model.LedgerAccount
import com.example.assetstar.domain.model.LedgerType

fun LedgerEntryEntity.toDomain(): LedgerEntry {
    return LedgerEntry(
        id = id,
        title = title,
        amount = amount,
        type = LedgerType.fromStorage(type),
        category = LedgerCategory.fromStorage(category),
        account = LedgerAccount.fromStorage(account),
        occurredAt = occurredAt,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun LedgerEntry.toEntity(): LedgerEntryEntity {
    return LedgerEntryEntity(
        id = id,
        title = title,
        amount = amount,
        type = type.storageValue,
        category = category.storageValue,
        account = account.storageValue,
        occurredAt = occurredAt,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
