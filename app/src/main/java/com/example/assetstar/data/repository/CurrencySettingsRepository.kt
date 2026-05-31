package com.example.assetstar.data.repository

import android.content.Context
import com.example.assetstar.domain.model.CurrencyDisplaySettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CurrencySettingsRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "currency_display_settings",
        Context.MODE_PRIVATE,
    )

    private val mutableSettings = MutableStateFlow(loadSettings())
    val settings: StateFlow<CurrencyDisplaySettings> = mutableSettings.asStateFlow()

    fun setUseUsd(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_USE_USD, enabled).apply()
        mutableSettings.update { it.copy(useUsd = enabled, errorMessage = null) }
    }

    suspend fun refreshRateIfNeeded(force: Boolean = false) {
        val current = mutableSettings.value
        val isFresh = System.currentTimeMillis() - current.updatedAt < RATE_TTL_MILLIS
        if (!force && current.updatedAt > 0L && isFresh) return

        mutableSettings.update { it.copy(isRefreshing = true, errorMessage = null) }
        runCatching { fetchCnyPerUsd() }
            .onSuccess { cnyPerUsd ->
                val now = System.currentTimeMillis()
                preferences.edit()
                    .putFloat(KEY_CNY_PER_USD, cnyPerUsd.toFloat())
                    .putLong(KEY_UPDATED_AT, now)
                    .apply()
                mutableSettings.update {
                    it.copy(
                        cnyPerUsd = cnyPerUsd,
                        updatedAt = now,
                        isRefreshing = false,
                        errorMessage = null,
                    )
                }
            }
            .onFailure {
                mutableSettings.update { settings ->
                    settings.copy(
                        isRefreshing = false,
                        errorMessage = "实时汇率获取失败，已使用缓存汇率",
                    )
                }
            }
    }

    private fun loadSettings(): CurrencyDisplaySettings {
        return CurrencyDisplaySettings(
            useUsd = preferences.getBoolean(KEY_USE_USD, false),
            cnyPerUsd = preferences.getFloat(
                KEY_CNY_PER_USD,
                CurrencyDisplaySettings.DEFAULT_CNY_PER_USD.toFloat(),
            ).toDouble(),
            updatedAt = preferences.getLong(KEY_UPDATED_AT, 0L),
        )
    }

    private suspend fun fetchCnyPerUsd(): Double = withContext(Dispatchers.IO) {
        val connection = (URL(RATE_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
        }
        try {
            if (connection.responseCode !in 200..299) {
                error("Unexpected rate response: ${connection.responseCode}")
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(body).getJSONObject("rates").getDouble("CNY")
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        private const val KEY_USE_USD = "use_usd"
        private const val KEY_CNY_PER_USD = "cny_per_usd"
        private const val KEY_UPDATED_AT = "updated_at"
        private const val RATE_TTL_MILLIS = 6 * 60 * 60 * 1000L
        private const val RATE_URL = "https://api.frankfurter.app/latest?from=USD&to=CNY"
    }
}
