package com.example.assetstar.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.AssetStats
import com.example.assetstar.domain.model.CategoryDisplaySettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AnalysisUiState(
    val stats: AssetStats = AssetStats(),
    val assets: List<Asset> = emptyList(),
    val metricsByAssetId: Map<Long, AssetMetrics> = emptyMap(),
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

class AnalysisViewModel(
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer

    val uiState = combine(
        container.getAssetStatsUseCase(),
        container.getAssetsUseCase(),
        container.categorySettingsRepository.settings,
    ) { stats, assets, categorySettings ->
        AnalysisUiState(
            stats = stats,
            assets = assets,
            metricsByAssetId = assets.associate { it.id to container.assetCalculator.calculateMetrics(it) },
            categoryDisplaySettings = categorySettings,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalysisUiState(),
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AnalysisViewModel(application(this))
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
