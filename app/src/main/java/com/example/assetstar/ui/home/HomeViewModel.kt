package com.example.assetstar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AppContainer
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.domain.model.AssetStats
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val stats: AssetStats = AssetStats(),
    val assets: List<Asset> = emptyList(),
    val categoryStats: List<CategoryStats> = emptyList(),
    val featuredAssets: List<Asset> = emptyList(),
    val featuredMetrics: Map<Long, AssetMetrics> = emptyMap(),
    val isEmpty: Boolean = true,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

class HomeViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val uiState = combine(
        container.getAssetStatsUseCase(),
        container.getAssetsUseCase(),
        container.getAllCategoryStatsUseCase(),
        container.categorySettingsRepository.settings,
    ) { stats, assets, categoryStats, categorySettings ->
            val orderedAssets = assets.sortedByDescending { it.updatedAt }
            val inUseAssets = orderedAssets.filter { it.status == AssetStatus.IN_USE }
            val carouselAssets = if (inUseAssets.isNotEmpty()) inUseAssets else orderedAssets
            HomeUiState(
                stats = stats,
                assets = orderedAssets,
                categoryStats = categoryStats,
                featuredAssets = carouselAssets,
                featuredMetrics = carouselAssets.associate { asset ->
                    asset.id to container.assetCalculator.calculateMetrics(asset)
                },
                isEmpty = stats.totalAssetCount == 0,
                categoryDisplaySettings = categorySettings,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(application(this).appContainer)
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
