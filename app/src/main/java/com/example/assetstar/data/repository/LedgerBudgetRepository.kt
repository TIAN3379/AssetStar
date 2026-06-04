package com.example.assetstar.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LedgerBudgetRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "ledger_budget_settings",
        Context.MODE_PRIVATE,
    )

    private val mutableMonthlyBudget = MutableStateFlow(loadMonthlyBudget())
    val monthlyBudget: StateFlow<Double> = mutableMonthlyBudget.asStateFlow()

    fun setMonthlyBudget(value: Double) {
        val safeValue = value.takeIf { it.isFinite() && it > 0.0 } ?: 0.0
        preferences.edit()
            .putFloat(KEY_MONTHLY_BUDGET, safeValue.toFloat())
            .apply()
        mutableMonthlyBudget.value = safeValue
    }

    private fun loadMonthlyBudget(): Double {
        return preferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    companion object {
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
    }
}
