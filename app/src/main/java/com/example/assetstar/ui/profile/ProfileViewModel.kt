package com.example.assetstar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.domain.model.CurrencyDisplaySettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val assetCount: Int = 0,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
    val currencyDisplaySettings: CurrencyDisplaySettings = CurrencyDisplaySettings(),
)

class ProfileViewModel(
    private val application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer

    val uiState = combine(
        container.getAssetsUseCase().map { it.size },
        container.categorySettingsRepository.settings,
        container.currencySettingsRepository.settings,
    ) { assetCount, categorySettings, currencySettings ->
        ProfileUiState(
            assetCount = assetCount,
            categoryDisplaySettings = categorySettings,
            currencyDisplaySettings = currencySettings,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(),
        )

    fun clearAll() {
        viewModelScope.launch {
            container.clearAllAssetsUseCase()
        }
    }

    fun updateCategoryName(category: AssetCategory, name: String) {
        container.categorySettingsRepository.updateName(category, name)
    }

    fun updateCategoryIcon(category: AssetCategory, iconSource: AssetCategory) {
        container.categorySettingsRepository.updateIconSource(category, iconSource)
    }

    fun resetCategory(category: AssetCategory) {
        container.categorySettingsRepository.reset(category)
    }

    fun setUseUsd(enabled: Boolean) {
        container.currencySettingsRepository.setUseUsd(enabled)
        if (enabled) {
            viewModelScope.launch {
                container.currencySettingsRepository.refreshRateIfNeeded(force = true)
            }
        }
    }

    fun refreshExchangeRate() {
        viewModelScope.launch {
            container.currencySettingsRepository.refreshRateIfNeeded(force = true)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel(application(this))
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
