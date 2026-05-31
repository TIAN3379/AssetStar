package com.example.assetstar.domain.model

import androidx.annotation.DrawableRes
import com.example.assetstar.R

data class CategoryDisplaySettings(
    val names: Map<AssetCategory, String> = emptyMap(),
    val iconSources: Map<AssetCategory, AssetCategory> = emptyMap(),
) {
    fun nameOf(category: AssetCategory): String {
        return names[category]?.takeIf { it.isNotBlank() } ?: category.displayName
    }

    @DrawableRes
    fun iconOf(category: AssetCategory): Int {
        val source = iconSources[category] ?: category
        return source.defaultIconRes()
    }
}

@DrawableRes
fun AssetCategory.defaultIconRes(): Int {
    return when (this) {
        AssetCategory.TRANSPORT -> R.drawable.ic_category_transport
        AssetCategory.DAILY -> R.drawable.ic_category_daily
        AssetCategory.LEISURE -> R.drawable.ic_category_leisure
        AssetCategory.HEALTH -> R.drawable.ic_category_health
        AssetCategory.LIVING -> R.drawable.ic_category_living
        AssetCategory.DIGITAL -> R.drawable.ic_category_digital
        AssetCategory.CASH -> R.drawable.ic_category_books
        AssetCategory.INVESTMENT -> R.drawable.ic_category_investment
        AssetCategory.ALL -> R.drawable.ic_nav_home
        AssetCategory.OTHER -> R.drawable.ic_nav_center
    }
}
