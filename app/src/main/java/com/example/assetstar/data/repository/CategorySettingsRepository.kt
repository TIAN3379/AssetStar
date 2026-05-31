package com.example.assetstar.data.repository

import android.content.Context
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryDisplaySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CategorySettingsRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "category_display_settings",
        Context.MODE_PRIVATE,
    )

    private val mutableSettings = MutableStateFlow(loadSettings())
    val settings: StateFlow<CategoryDisplaySettings> = mutableSettings.asStateFlow()

    fun updateName(category: AssetCategory, name: String) {
        if (category == AssetCategory.ALL) return
        val trimmed = name.trim()
        preferences.edit()
            .putString(nameKey(category), trimmed)
            .apply()
        mutableSettings.update {
            it.copy(names = it.names + (category to trimmed))
        }
    }

    fun updateIconSource(category: AssetCategory, iconSource: AssetCategory) {
        if (category == AssetCategory.ALL) return
        preferences.edit()
            .putString(iconKey(category), iconSource.storageValue)
            .apply()
        mutableSettings.update {
            it.copy(iconSources = it.iconSources + (category to iconSource))
        }
    }

    fun reset(category: AssetCategory) {
        if (category == AssetCategory.ALL) return
        preferences.edit()
            .remove(nameKey(category))
            .remove(iconKey(category))
            .apply()
        mutableSettings.update {
            it.copy(
                names = it.names - category,
                iconSources = it.iconSources - category,
            )
        }
    }

    private fun loadSettings(): CategoryDisplaySettings {
        val names = AssetCategory.selectable.associateWith { category ->
            preferences.getString(nameKey(category), null).orEmpty()
        }.filterValues { it.isNotBlank() }

        val icons = AssetCategory.selectable.mapNotNull { category ->
            val source = preferences.getString(iconKey(category), null)
                ?.let(AssetCategory::fromStorage)
                ?: return@mapNotNull null
            category to source
        }.toMap()

        return CategoryDisplaySettings(
            names = names,
            iconSources = icons,
        )
    }

    private fun nameKey(category: AssetCategory): String = "name_${category.storageValue}"
    private fun iconKey(category: AssetCategory): String = "icon_${category.storageValue}"
}
