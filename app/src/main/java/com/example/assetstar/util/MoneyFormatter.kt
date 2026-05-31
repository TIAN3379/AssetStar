package com.example.assetstar.util

import com.example.assetstar.domain.model.CurrencyDisplaySettings
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object MoneyFormatter {
    @Volatile
    private var currencySettings: CurrencyDisplaySettings = CurrencyDisplaySettings()

    private val cnyFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.CHINA).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    private val usdFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun updateCurrencySettings(settings: CurrencyDisplaySettings) {
        currencySettings = settings
    }

    fun format(amountCny: Double): String {
        val settings = currencySettings
        val safeAmountCny = amountCny.takeIf { it.isFinite() } ?: 0.0
        return if (settings.useUsd) {
            "$${formatNumber(usdFormat, safeAmountCny * settings.usdPerCny)}"
        } else {
            "¥${formatNumber(cnyFormat, safeAmountCny)}"
        }
    }

    fun formatCompact(amountCny: Double): String {
        val settings = currencySettings
        val safeAmountCny = amountCny.takeIf { it.isFinite() } ?: 0.0
        val amount = if (settings.useUsd) safeAmountCny * settings.usdPerCny else safeAmountCny
        val symbol = if (settings.useUsd) "$" else "¥"
        val absAmount = abs(amount)
        return when {
            !settings.useUsd && absAmount >= 100_000_000.0 -> "$symbol%.1f亿".format(amount / 100_000_000.0)
            !settings.useUsd && absAmount >= 10_000.0 -> {
                val value = amount / 10_000.0
                if (abs(value) >= 100 || value % 1.0 == 0.0) {
                    "$symbol${value.toInt()}万"
                } else {
                    "$symbol%.1f万".format(value)
                }
            }
            settings.useUsd && absAmount >= 1_000_000.0 -> "$symbol%.1fM".format(amount / 1_000_000.0)
            settings.useUsd && absAmount >= 1_000.0 -> "$symbol%.1fk".format(amount / 1_000.0)
            else -> format(amountCny)
        }
    }

    fun formatDailyCost(amountCny: Double): String = "${format(amountCny)} / 天"

    private fun formatNumber(format: NumberFormat, amount: Double): String {
        return synchronized(format) {
            format.format(amount)
        }
    }
}
