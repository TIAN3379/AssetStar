package com.example.assetstar.ui.edit

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
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.util.AssetVisuals
import com.example.assetstar.util.DateUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddEditAssetUiState(
    val assetId: Long = -1L,
    val isEditing: Boolean = false,
    val name: String = "",
    val category: AssetCategory = AssetCategory.DIGITAL,
    val status: AssetStatus = AssetStatus.IN_USE,
    val purchasePrice: String = "",
    val purchaseDate: Long = DateUtils.todayStartMillis(),
    val estimatedResidualValue: String = "",
    val imageUri: String? = null,
    val note: String = "",
    val soldPrice: String = "",
    val soldDate: Long? = null,
    val nameError: String? = null,
    val purchasePriceError: String? = null,
    val soldPriceError: String? = null,
    val soldDateError: String? = null,
    val saveSuccess: Boolean = false,
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
)

class AddEditAssetViewModel(
    savedStateHandle: SavedStateHandle,
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer
    private val assetId: Long = savedStateHandle["assetId"] ?: -1L
    private val defaultCategory = (savedStateHandle["defaultCategory"] as? String)
        ?.takeIf { it.isNotBlank() }
        ?.let(AssetCategory::fromStorage)
        ?: AssetCategory.DIGITAL
    private var createdAt: Long = System.currentTimeMillis()
    private var isCherished: Boolean = false
    private val state = MutableStateFlow(
        AddEditAssetUiState(
            assetId = assetId,
            isEditing = assetId > 0,
            category = defaultCategory,
        ),
    )

    val uiState = combine(
        state,
        container.categorySettingsRepository.settings,
    ) { current, categorySettings ->
        current.copy(categoryDisplaySettings = categorySettings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = state.value,
    )

    init {
        if (assetId > 0) {
            viewModelScope.launch {
                val asset = container.getAssetByIdUseCase(assetId).first() ?: return@launch
                createdAt = asset.createdAt
                isCherished = AssetVisuals.isCherished(asset)
                state.value = state.value.copy(
                    assetId = asset.id,
                    isEditing = true,
                    name = asset.name,
                    category = asset.category,
                    status = asset.status,
                    purchasePrice = asset.purchasePrice.toString(),
                    purchaseDate = asset.purchaseDate,
                    estimatedResidualValue = asset.estimatedResidualValue?.toString().orEmpty(),
                    imageUri = asset.imageUri,
                    note = AssetVisuals.displayNote(asset.note).orEmpty(),
                    soldPrice = asset.soldPrice?.toString().orEmpty(),
                    soldDate = asset.soldDate,
                )
            }
        }
    }

    fun updateName(value: String) = update { it.copy(name = value, nameError = null) }
    fun updateCategory(value: AssetCategory) = update { it.copy(category = value) }
    fun updateStatus(value: AssetStatus) = update {
        if (value == AssetStatus.SOLD) {
            it.copy(status = value)
        } else {
            it.copy(
                status = value,
                soldPrice = "",
                soldDate = null,
                soldPriceError = null,
                soldDateError = null,
            )
        }
    }
    fun updatePurchasePrice(value: String) = update { it.copy(purchasePrice = value, purchasePriceError = null) }
    fun updatePurchaseDate(value: Long) = update { it.copy(purchaseDate = value) }
    fun updateEstimatedResidualValue(value: String) = update { it.copy(estimatedResidualValue = value) }
    fun updateImageUri(value: String?) = update { it.copy(imageUri = value) }
    fun updateNote(value: String) = update { it.copy(note = value) }
    fun updateSoldPrice(value: String) = update { it.copy(soldPrice = value, soldPriceError = null) }
    fun updateSoldDate(value: Long?) = update { it.copy(soldDate = value, soldDateError = null) }

    fun saveAsset() {
        val current = state.value
        val name = current.name.trim()
        val purchasePrice = current.purchasePrice.toDoubleOrNull()
        val soldPrice = current.soldPrice.toDoubleOrNull()

        var valid = true
        var nameError: String? = null
        var priceError: String? = null
        var soldPriceError: String? = null
        var soldDateError: String? = null

        if (name.isBlank()) {
            valid = false
            nameError = "名称不能为空"
        }
        if (purchasePrice == null || !purchasePrice.isFinite() || purchasePrice < 0.0) {
            valid = false
            priceError = "购买价格必须大于等于 0"
        }
        if (current.status == AssetStatus.SOLD) {
            if (soldPrice == null || !soldPrice.isFinite() || soldPrice < 0.0) {
                valid = false
                soldPriceError = "卖出价格必须大于等于 0"
            }
            if (current.soldDate == null) {
                valid = false
                soldDateError = "请选择卖出日期"
            } else if (current.soldDate < current.purchaseDate) {
                valid = false
                soldDateError = "卖出日期不能早于购买日期"
            }
        }

        state.value = current.copy(
            nameError = nameError,
            purchasePriceError = priceError,
            soldPriceError = soldPriceError,
            soldDateError = soldDateError,
        )

        if (!valid || purchasePrice == null || !purchasePrice.isFinite()) return

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val residualValue = current.estimatedResidualValue
                .toDoubleOrNull()
                ?.takeIf { it.isFinite() && it >= 0.0 }
            val asset = Asset(
                id = if (current.isEditing) current.assetId else 0L,
                name = name,
                category = current.category,
                status = current.status,
                purchasePrice = purchasePrice,
                purchaseDate = current.purchaseDate,
                imageUri = current.imageUri,
                note = AssetVisuals.persistedNote(current.note, isCherished),
                estimatedResidualValue = residualValue,
                soldPrice = if (current.status == AssetStatus.SOLD) soldPrice else null,
                soldDate = if (current.status == AssetStatus.SOLD) current.soldDate else null,
                createdAt = if (current.isEditing) createdAt else now,
                updatedAt = now,
            )
            if (current.isEditing) {
                container.updateAssetUseCase(asset)
            } else {
                container.addAssetUseCase(asset)
            }
            state.value = state.value.copy(saveSuccess = true)
        }
    }

    private fun update(transform: (AddEditAssetUiState) -> AddEditAssetUiState) {
        state.value = transform(state.value).copy(saveSuccess = false)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddEditAssetViewModel(
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
