package com.example.assetstar.ui.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.ui.list.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CategoryAssetListItem(
    val asset: Asset,
    val metrics: AssetMetrics,
)

data class CategoryAssetsUiState(
    val category: AssetCategory = AssetCategory.ALL,
    val assets: List<CategoryAssetListItem> = emptyList(),
    val categoryStats: CategoryStats? = null,
    val selectedStatus: AssetStatus? = null,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.UPDATED_DESC,
    val isEmpty: Boolean = true,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

private data class CategoryAssetFilters(
    val query: String,
    val status: AssetStatus?,
    val sort: SortOption,
)

class CategoryAssetsViewModel(
    savedStateHandle: SavedStateHandle,
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer
    private val category = AssetCategory.fromStorage(savedStateHandle["category"] ?: AssetCategory.ALL.storageValue)
    private val searchQuery = MutableStateFlow("")
    private val selectedStatus = MutableStateFlow<AssetStatus?>(null)
    private val sortOption = MutableStateFlow(SortOption.UPDATED_DESC)

    private val filters = combine(searchQuery, selectedStatus, sortOption) { query, status, sort ->
        CategoryAssetFilters(query = query, status = status, sort = sort)
    }

    val uiState = combine(
        container.getAssetsByCategoryUseCase(category),
        container.getCategoryStatsUseCase(category),
        filters,
        container.categorySettingsRepository.settings,
    ) { assets, stats, filters, categorySettings ->
        val filtered = assets
            .filter { asset ->
                val matchesQuery = filters.query.isBlank() || asset.name.contains(filters.query, ignoreCase = true)
                val matchesStatus = filters.status == null || asset.status == filters.status
                matchesQuery && matchesStatus
            }
            .sortedWith(filters.sort.comparator())
            .map { asset ->
                CategoryAssetListItem(
                    asset = asset,
                    metrics = container.assetCalculator.calculateMetrics(asset),
                )
            }
        CategoryAssetsUiState(
            category = category,
            assets = filtered,
            categoryStats = stats,
            selectedStatus = filters.status,
            searchQuery = filters.query,
            sortOption = filters.sort,
            isEmpty = filtered.isEmpty(),
            categoryDisplaySettings = categorySettings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryAssetsUiState(category = category),
    )

    fun onSearchQueryChange(value: String) {
        searchQuery.value = value
    }

    fun onStatusFilterChange(status: AssetStatus?) {
        selectedStatus.value = status
    }

    fun onSortTypeChange(option: SortOption) {
        sortOption.value = option
    }

    private fun SortOption.comparator(): Comparator<Asset> {
        return when (this) {
            SortOption.UPDATED_DESC -> compareByDescending { it.updatedAt }
            SortOption.PRICE_DESC -> compareByDescending { it.purchasePrice }
            SortOption.DATE_DESC -> compareByDescending { it.purchaseDate }
            SortOption.DAILY_COST_DESC -> compareByDescending { container.assetCalculator.calculateMetrics(it).dailyCost }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CategoryAssetsViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    application = application(this),
                )
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
