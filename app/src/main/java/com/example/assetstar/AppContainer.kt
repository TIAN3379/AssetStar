package com.example.assetstar

import android.content.Context
import com.example.assetstar.data.local.AssetDatabase
import com.example.assetstar.data.repository.AssetRepository
import com.example.assetstar.data.repository.AssetRepositoryImpl
import com.example.assetstar.data.repository.CategorySettingsRepository
import com.example.assetstar.data.repository.CurrencySettingsRepository
import com.example.assetstar.data.repository.LedgerRepository
import com.example.assetstar.data.repository.LedgerRepositoryImpl
import com.example.assetstar.data.repository.LedgerBudgetRepository
import com.example.assetstar.data.repository.LedgerAccountRepository
import com.example.assetstar.domain.calculator.AssetCalculator
import com.example.assetstar.domain.usecase.AddAssetUseCase
import com.example.assetstar.domain.usecase.ClearAllAssetsUseCase
import com.example.assetstar.domain.usecase.DeleteAssetUseCase
import com.example.assetstar.domain.usecase.GetAllCategoryStatsUseCase
import com.example.assetstar.domain.usecase.GetAssetByIdUseCase
import com.example.assetstar.domain.usecase.GetAssetStatsUseCase
import com.example.assetstar.domain.usecase.GetAssetsByCategoryUseCase
import com.example.assetstar.domain.usecase.GetAssetsUseCase
import com.example.assetstar.domain.usecase.GetCategoryStatsUseCase
import com.example.assetstar.domain.usecase.UpdateAssetUseCase

class AppContainer(context: Context) {
    private val database: AssetDatabase = AssetDatabase.create(context)
    val assetCalculator = AssetCalculator()
    val categorySettingsRepository = CategorySettingsRepository(context)
    val currencySettingsRepository = CurrencySettingsRepository(context)
    val ledgerBudgetRepository = LedgerBudgetRepository(context)
    val ledgerAccountRepository = LedgerAccountRepository(context)

    val repository: AssetRepository = AssetRepositoryImpl(
        dao = database.assetDao(),
    )
    val ledgerRepository: LedgerRepository = LedgerRepositoryImpl(
        dao = database.ledgerDao(),
    )

    val getAssetsUseCase = GetAssetsUseCase(repository)
    val getAssetsByCategoryUseCase = GetAssetsByCategoryUseCase(repository)
    val getAssetByIdUseCase = GetAssetByIdUseCase(repository)
    val addAssetUseCase = AddAssetUseCase(repository)
    val updateAssetUseCase = UpdateAssetUseCase(repository)
    val deleteAssetUseCase = DeleteAssetUseCase(repository)
    val clearAllAssetsUseCase = ClearAllAssetsUseCase(repository)
    val getAssetStatsUseCase = GetAssetStatsUseCase(
        repository = repository,
        calculator = assetCalculator,
    )
    val getCategoryStatsUseCase = GetCategoryStatsUseCase(
        repository = repository,
        calculator = assetCalculator,
    )
    val getAllCategoryStatsUseCase = GetAllCategoryStatsUseCase(
        repository = repository,
        calculator = assetCalculator,
    )
}
