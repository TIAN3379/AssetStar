package com.example.assetstar.domain.model

data class CurrencyDisplaySettings(
    val useUsd: Boolean = false,
    val cnyPerUsd: Double = DEFAULT_CNY_PER_USD,
    val updatedAt: Long = 0L,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
) {
    val usdPerCny: Double
        get() = if (cnyPerUsd > 0.0) 1.0 / cnyPerUsd else 1.0 / DEFAULT_CNY_PER_USD

    companion object {
        const val DEFAULT_CNY_PER_USD = 7.20
    }
}
