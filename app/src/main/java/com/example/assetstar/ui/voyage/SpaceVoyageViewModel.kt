package com.example.assetstar.ui.voyage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AppContainer
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.LedgerAccount
import com.example.assetstar.domain.model.LedgerCategory
import com.example.assetstar.domain.model.LedgerEntry
import com.example.assetstar.domain.model.LedgerType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

data class SpaceVoyageUiState(
    val entries: List<LedgerEntry> = emptyList(),
    val accountBalances: List<LedgerAccountBalance> = emptyList(),
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthlyBudget: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyRemaining: Double = 0.0,
    val monthlyRemainingRatio: Float = 0f,
    val monthlyBudgetUsedRatio: Float = 0f,
    val monthElapsedRatio: Float = 0f,
    val todayExpense: Double = 0.0,
    val energyLevel: Float = 0.2f,
)

data class LedgerAccountBalance(
    val account: LedgerAccount,
    val baseBalance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0,
) {
    val currentBalance: Double = baseBalance + income - expense
}

class SpaceVoyageViewModel(
    private val container: AppContainer,
) : ViewModel() {
    val uiState = combine(
        container.ledgerRepository.observeEntries(),
        container.ledgerBudgetRepository.monthlyBudget,
        container.ledgerAccountRepository.baseBalances,
    ) { entries, monthlyBudget, baseBalances ->
            val income = entries
                .filter { it.type == LedgerType.INCOME }
                .sumOf { it.amount }
            val expense = entries
                .filter { it.type == LedgerType.EXPENSE }
                .sumOf { it.amount }
            val todayStart = startOfToday()
            val todayExpense = entries
                .filter { it.type == LedgerType.EXPENSE && it.occurredAt >= todayStart }
                .sumOf { it.amount }
            val monthStart = startOfMonth()
            val monthEntries = entries.filter { it.occurredAt >= monthStart }
            val monthlyExpense = monthEntries
                .filter { it.type == LedgerType.EXPENSE }
                .sumOf { it.amount }
            val monthlyRemaining = monthlyBudget - monthlyExpense
            val monthlyRemainingRatio = if (monthlyBudget > 0.0) {
                (monthlyRemaining / monthlyBudget).toFloat().coerceIn(0f, 1f)
            } else {
                0f
            }
            val monthlyBudgetUsedRatio = if (monthlyBudget > 0.0) {
                (monthlyExpense / monthlyBudget).toFloat().coerceAtLeast(0f)
            } else {
                0f
            }
            val monthElapsedRatio = monthElapsedRatio()
            val balance = income - expense
            val accountBalances = LedgerAccount.entries.map { account ->
                val accountEntries = entries.filter { it.account == account }
                LedgerAccountBalance(
                    account = account,
                    baseBalance = baseBalances[account] ?: 0.0,
                    income = accountEntries
                        .filter { it.type == LedgerType.INCOME }
                        .sumOf { it.amount },
                    expense = accountEntries
                        .filter { it.type == LedgerType.EXPENSE }
                        .sumOf { it.amount },
                )
            }
            SpaceVoyageUiState(
                entries = entries,
                accountBalances = accountBalances,
                balance = balance,
                totalIncome = income,
                totalExpense = expense,
                monthlyBudget = monthlyBudget,
                monthlyExpense = monthlyExpense,
                monthlyRemaining = monthlyRemaining,
                monthlyRemainingRatio = monthlyRemainingRatio,
                monthlyBudgetUsedRatio = monthlyBudgetUsedRatio,
                monthElapsedRatio = monthElapsedRatio,
                todayExpense = todayExpense,
                energyLevel = energyLevel(balance = balance, income = income, expense = expense),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SpaceVoyageUiState(),
        )

    fun addEntry(
        title: String,
        amountText: String,
        type: LedgerType,
        category: LedgerCategory,
        account: LedgerAccount,
        note: String?,
    ) {
        val amount = amountText.trim().toDoubleOrNull()?.takeIf { it > 0.0 } ?: return
        val now = System.currentTimeMillis()
        val safeTitle = title.trim().ifBlank { category.displayName }
        viewModelScope.launch {
            container.ledgerRepository.insert(
                LedgerEntry(
                    title = safeTitle,
                    amount = amount,
                    type = type,
                    category = category,
                    account = account,
                    occurredAt = now,
                    note = note?.trim()?.ifBlank { null },
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }
    }

    fun deleteEntry(entry: LedgerEntry) {
        viewModelScope.launch {
            container.ledgerRepository.delete(entry)
        }
    }

    fun updateMonthlyBudget(amountText: String) {
        val amount = amountText.trim().toDoubleOrNull()?.takeIf { it >= 0.0 } ?: return
        container.ledgerBudgetRepository.setMonthlyBudget(amount)
    }

    fun updateAccountBaseBalance(account: LedgerAccount, amountText: String) {
        val amount = amountText.trim().toDoubleOrNull() ?: return
        container.ledgerAccountRepository.setBaseBalance(account, amount)
    }

    private fun energyLevel(
        balance: Double,
        income: Double,
        expense: Double,
    ): Float {
        val base = max(income, expense).takeIf { it > 0.0 } ?: return 0.2f
        return (balance / base).toFloat().coerceIn(0.08f, 1f)
    }

    private fun startOfToday(): Long {
        val now = java.util.Calendar.getInstance()
        now.set(java.util.Calendar.HOUR_OF_DAY, 0)
        now.set(java.util.Calendar.MINUTE, 0)
        now.set(java.util.Calendar.SECOND, 0)
        now.set(java.util.Calendar.MILLISECOND, 0)
        return now.timeInMillis
    }

    private fun startOfMonth(): Long {
        val now = java.util.Calendar.getInstance()
        now.set(java.util.Calendar.DAY_OF_MONTH, 1)
        now.set(java.util.Calendar.HOUR_OF_DAY, 0)
        now.set(java.util.Calendar.MINUTE, 0)
        now.set(java.util.Calendar.SECOND, 0)
        now.set(java.util.Calendar.MILLISECOND, 0)
        return now.timeInMillis
    }

    private fun monthElapsedRatio(): Float {
        val now = java.util.Calendar.getInstance()
        val day = now.get(java.util.Calendar.DAY_OF_MONTH)
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val second = now.get(java.util.Calendar.SECOND)
        val maxDay = now.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val elapsedDays = (day - 1).toDouble() +
            hour / 24.0 +
            minute / 1440.0 +
            second / 86400.0
        return (elapsedDays / maxDay).toFloat().coerceIn(0f, 1f)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SpaceVoyageViewModel(application(this).appContainer)
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}
