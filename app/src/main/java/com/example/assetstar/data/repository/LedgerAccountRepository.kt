package com.example.assetstar.data.repository

import android.content.Context
import com.example.assetstar.domain.model.LedgerAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LedgerAccountRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "ledger_account_settings",
        Context.MODE_PRIVATE,
    )

    private val mutableBaseBalances = MutableStateFlow(loadBaseBalances())
    val baseBalances: StateFlow<Map<LedgerAccount, Double>> = mutableBaseBalances.asStateFlow()

    fun setBaseBalance(account: LedgerAccount, amount: Double) {
        val safeAmount = amount.takeIf { it.isFinite() } ?: 0.0
        preferences.edit()
            .putFloat(key(account), safeAmount.toFloat())
            .apply()
        mutableBaseBalances.value = mutableBaseBalances.value + (account to safeAmount)
    }

    private fun loadBaseBalances(): Map<LedgerAccount, Double> {
        return LedgerAccount.entries.associateWith { account ->
            preferences.getFloat(key(account), 0f).toDouble()
        }
    }

    private fun key(account: LedgerAccount): String = "base_balance_${account.storageValue}"
}
