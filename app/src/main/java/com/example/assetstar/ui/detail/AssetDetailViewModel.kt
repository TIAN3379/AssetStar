package com.example.assetstar.ui.detail

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
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.util.AssetVisuals
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AssetDetailUiState(
    val asset: Asset? = null,
    val metrics: AssetMetrics? = null,
    val deleted: Boolean = false,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

class AssetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer
    private val assetId: Long = checkNotNull(savedStateHandle["assetId"])
    private val deleted = MutableStateFlow(false)

    val uiState = combine(
        container.getAssetByIdUseCase(assetId),
        deleted,
        container.categorySettingsRepository.settings,
    ) { asset, isDeleted, categorySettings ->
            AssetDetailUiState(
                asset = asset,
                metrics = asset?.let { container.assetCalculator.calculateMetrics(it) },
                deleted = isDeleted,
                categoryDisplaySettings = categorySettings,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AssetDetailUiState(),
        )

    fun deleteCurrentAsset() {
        val asset = uiState.value.asset ?: return
        viewModelScope.launch {
            container.deleteAssetUseCase(asset)
            deleted.value = true
        }
    }

    fun toggleCherished() {
        val asset = uiState.value.asset ?: return
        viewModelScope.launch {
            container.updateAssetUseCase(
                asset.copy(
                    note = AssetVisuals.toggleCherishedNote(asset.note),
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssetDetailViewModel(
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
