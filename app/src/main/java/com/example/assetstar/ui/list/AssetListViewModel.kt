package com.example.assetstar.ui.list

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class SortOption(val label: String) {
    UPDATED_DESC("最近更新"),
    PRICE_DESC("价格最高"),
    DATE_DESC("最近购入"),
    DAILY_COST_DESC("日均成本"),
}

data class AssetListItem(
    val asset: Asset,
    val metrics: AssetMetrics,
)

data class AssetListUiState(
    val items: List<AssetListItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: AssetCategory = AssetCategory.ALL,
    val selectedStatus: AssetStatus? = null,
    val sortOption: SortOption = SortOption.UPDATED_DESC,
    val focusSearchOnOpen: Boolean = false,
    val isEmpty: Boolean = true,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

private data class AssetListFilters(
    val searchQuery: String,
    val selectedCategory: AssetCategory,
    val selectedStatus: AssetStatus?,
    val sortOption: SortOption,
    val focusSearchOnOpen: Boolean,
)

class AssetListViewModel(
    savedStateHandle: SavedStateHandle,
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer
    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(
        AssetCategory.fromStorage(savedStateHandle["category"] ?: AssetCategory.ALL.storageValue),
    )
    private val selectedStatus = MutableStateFlow<AssetStatus?>(null)
    private val sortOption = MutableStateFlow(SortOption.UPDATED_DESC)
    private val focusSearchOnOpen = MutableStateFlow(savedStateHandle["focusSearch"] ?: false)

    private val filters = combine(
        searchQuery,
        selectedCategory,
        selectedStatus,
        sortOption,
        focusSearchOnOpen,
    ) { query, category, status, sort, focus ->
        AssetListFilters(
            searchQuery = query,
            selectedCategory = category,
            selectedStatus = status,
            sortOption = sort,
            focusSearchOnOpen = focus,
        )
    }

    val uiState = combine(
        container.getAssetsUseCase(),
        filters,
        container.categorySettingsRepository.settings,
    ) { assets, filters, categorySettings ->
        val filtered = assets
            .filter { asset ->
                val matchesQuery = filters.searchQuery.isBlank() ||
                    asset.name.contains(filters.searchQuery, ignoreCase = true)
                val matchesCategory = filters.selectedCategory == AssetCategory.ALL ||
                    asset.category == filters.selectedCategory
                val matchesStatus = filters.selectedStatus == null || asset.status == filters.selectedStatus
                matchesQuery && matchesCategory && matchesStatus
            }
            .sortedWith(filters.sortOption.comparator(container.assetCalculator))
            .map { asset ->
                AssetListItem(
                    asset = asset,
                    metrics = container.assetCalculator.calculateMetrics(asset),
                )
            }

        AssetListUiState(
            items = filtered,
            searchQuery = filters.searchQuery,
            selectedCategory = filters.selectedCategory,
            selectedStatus = filters.selectedStatus,
            sortOption = filters.sortOption,
            focusSearchOnOpen = filters.focusSearchOnOpen,
            isEmpty = filtered.isEmpty(),
            categoryDisplaySettings = categorySettings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AssetListUiState(
            focusSearchOnOpen = focusSearchOnOpen.value,
        ),
    )

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun updateCategory(category: AssetCategory) {
        selectedCategory.value = category
    }

    fun updateStatus(status: AssetStatus?) {
        selectedStatus.value = status
    }

    fun cycleSortOption() {
        val values = SortOption.entries
        val nextIndex = (values.indexOf(sortOption.value) + 1) % values.size
        sortOption.value = values[nextIndex]
    }

    fun updateSortOption(option: SortOption) {
        sortOption.value = option
    }

    fun consumeFocusRequest() {
        focusSearchOnOpen.value = false
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssetListViewModel(
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

private fun SortOption.comparator(calculator: com.example.assetstar.domain.calculator.AssetCalculator): Comparator<Asset> {
    return when (this) {
        SortOption.UPDATED_DESC -> compareByDescending { it.updatedAt }
        SortOption.PRICE_DESC -> compareByDescending { it.purchasePrice }
        SortOption.DATE_DESC -> compareByDescending { it.purchaseDate }
        SortOption.DAILY_COST_DESC -> compareByDescending { calculator.calculateMetrics(it).dailyCost }
    }
}
