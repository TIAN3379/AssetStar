package com.example.assetstar.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.domain.model.CategoryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class CategorySortType(val label: String) {
    ASSET_COUNT("资产数量"),
    TOTAL_VALUE("总金额"),
    DAILY_COST("日均成本"),
    NAME("分类名称"),
}

data class CategoryOverviewUiState(
    val categoryStatsList: List<CategoryStats> = emptyList(),
    val sortType: CategorySortType = CategorySortType.ASSET_COUNT,
    val isEmpty: Boolean = true,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

class CategoryOverviewViewModel(
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer
    private val sortType = MutableStateFlow(CategorySortType.ASSET_COUNT)

    val uiState = combine(
        container.getAllCategoryStatsUseCase(),
        sortType,
        container.categorySettingsRepository.settings,
    ) { stats, sort, categorySettings ->
        val categories = stats
            .filter { it.category != AssetCategory.ALL }
            .sortedWith(sort.comparator(categorySettings))
        CategoryOverviewUiState(
            categoryStatsList = categories,
            sortType = sort,
            isEmpty = categories.all { it.assetCount == 0 },
            categoryDisplaySettings = categorySettings,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryOverviewUiState(),
    )

    fun onSortTypeChange(type: CategorySortType) {
        sortType.value = type
    }

    private fun CategorySortType.comparator(settings: CategoryDisplaySettings): Comparator<CategoryStats> {
        return when (this) {
            CategorySortType.ASSET_COUNT -> compareByDescending<CategoryStats> { it.assetCount }
                .thenBy { settings.nameOf(it.category) }
            CategorySortType.TOTAL_VALUE -> compareByDescending<CategoryStats> { it.totalPurchasePrice }
                .thenBy { settings.nameOf(it.category) }
            CategorySortType.DAILY_COST -> compareByDescending<CategoryStats> { it.totalDailyCost }
                .thenBy { settings.nameOf(it.category) }
            CategorySortType.NAME -> compareBy { settings.nameOf(it.category) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CategoryOverviewViewModel(application(this))
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
