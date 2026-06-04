package com.example.assetstar.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.domain.model.AssetStats
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.domain.model.LedgerCategory
import com.example.assetstar.domain.model.LedgerEntry
import com.example.assetstar.domain.model.LedgerType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class AnalysisUiState(
    val stats: AssetStats = AssetStats(),
    val assets: List<Asset> = emptyList(),
    val metricsByAssetId: Map<Long, AssetMetrics> = emptyMap(),
    val categoryDisplaySettings: CategoryDisplaySettings = CategoryDisplaySettings(),
    val ledgerMonthReports: List<LedgerMonthReport> = listOf(currentEmptyLedgerReport()),
)

data class LedgerMonthKey(
    val year: Int,
    val month: Int,
) {
    val label: String = "${year}年${month}月"
}

data class LedgerInsight(
    val title: String,
    val subtitle: String,
    val amount: Double = 0.0,
    val count: Int = 0,
)

data class LedgerMonthReport(
    val month: LedgerMonthKey,
    val monthlyBudget: Double,
    val income: Double,
    val expense: Double,
    val balance: Double,
    val budgetDifference: Double,
    val budgetUsedRatio: Double,
    val previousIncome: Double,
    val previousExpense: Double,
    val expenseDelta: Double,
    val incomeDelta: Double,
    val expenseDeltaRate: Double?,
    val topExpenseCategory: LedgerInsight?,
    val frequentPlace: LedgerInsight?,
    val entryCount: Int,
) {
    val hasBudget: Boolean = monthlyBudget > 0.0
    val isOverBudget: Boolean = hasBudget && expense > monthlyBudget
}

class AnalysisViewModel(
    application: AssetStarApplication,
) : ViewModel() {
    private val container = application.appContainer

    val uiState = combine(
        container.getAssetStatsUseCase(),
        container.getAssetsUseCase(),
        container.categorySettingsRepository.settings,
        container.ledgerRepository.observeEntries(),
        container.ledgerBudgetRepository.monthlyBudget,
    ) { stats, assets, categorySettings, ledgerEntries, monthlyBudget ->
        AnalysisUiState(
            stats = stats,
            assets = assets,
            metricsByAssetId = assets.associate { it.id to container.assetCalculator.calculateMetrics(it) },
            categoryDisplaySettings = categorySettings,
            ledgerMonthReports = buildLedgerReports(
                entries = ledgerEntries,
                monthlyBudget = monthlyBudget,
            ),
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalysisUiState(),
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AnalysisViewModel(application(this))
            }
        }

        private fun application(extras: CreationExtras): AssetStarApplication {
            return checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AssetStarApplication
        }
    }
}

private fun buildLedgerReports(
    entries: List<LedgerEntry>,
    monthlyBudget: Double,
): List<LedgerMonthReport> {
    val currentMonth = currentLedgerMonth()
    val months = (entries.map { monthOf(it.occurredAt) } + currentMonth)
        .distinct()
        .sortedWith(compareByDescending<LedgerMonthKey> { it.year }.thenByDescending { it.month })

    return months.map { month ->
        buildLedgerReport(
            month = month,
            entries = entries,
            monthlyBudget = monthlyBudget,
        )
    }
}

private fun buildLedgerReport(
    month: LedgerMonthKey,
    entries: List<LedgerEntry>,
    monthlyBudget: Double,
): LedgerMonthReport {
    val monthEntries = entries.filter { monthOf(it.occurredAt) == month }
    val previousMonth = previousMonthOf(month)
    val previousEntries = entries.filter { monthOf(it.occurredAt) == previousMonth }
    val expenseEntries = monthEntries.filter { it.type == LedgerType.EXPENSE }
    val income = monthEntries
        .filter { it.type == LedgerType.INCOME }
        .sumOf { it.amount }
    val expense = expenseEntries.sumOf { it.amount }
    val previousIncome = previousEntries
        .filter { it.type == LedgerType.INCOME }
        .sumOf { it.amount }
    val previousExpense = previousEntries
        .filter { it.type == LedgerType.EXPENSE }
        .sumOf { it.amount }
    val topCategory = expenseEntries
        .groupBy { it.category }
        .map { (category, items) ->
            LedgerInsight(
                title = category.displayName,
                subtitle = "最大消耗分类",
                amount = items.sumOf { it.amount },
                count = items.size,
            )
        }
        .maxByOrNull { it.amount }
    val frequentPlace = expenseEntries
        .groupBy { it.category }
        .map { (category, items) ->
            LedgerInsight(
                title = placeNameOf(category),
                subtitle = "${category.displayName} · ${items.size} 次停靠",
                amount = items.sumOf { it.amount },
                count = items.size,
            )
        }
        .maxWithOrNull(compareBy<LedgerInsight> { it.count }.thenBy { it.amount })

    return LedgerMonthReport(
        month = month,
        monthlyBudget = monthlyBudget,
        income = income,
        expense = expense,
        balance = income - expense,
        budgetDifference = monthlyBudget - expense,
        budgetUsedRatio = if (monthlyBudget > 0.0) expense / monthlyBudget else 0.0,
        previousIncome = previousIncome,
        previousExpense = previousExpense,
        expenseDelta = expense - previousExpense,
        incomeDelta = income - previousIncome,
        expenseDeltaRate = if (previousExpense > 0.0) {
            (expense - previousExpense) / previousExpense
        } else {
            null
        },
        topExpenseCategory = topCategory,
        frequentPlace = frequentPlace,
        entryCount = monthEntries.size,
    )
}

private fun placeNameOf(category: LedgerCategory): String {
    return when (category) {
        LedgerCategory.FOOD -> "星港餐厅"
        LedgerCategory.SHOPPING, LedgerCategory.LIVING -> "星际商店"
        LedgerCategory.TRANSPORT -> "空天母舰"
        LedgerCategory.ENTERTAINMENT -> "娱乐星域"
        LedgerCategory.SALARY, LedgerCategory.BONUS -> "能源矿区"
        LedgerCategory.OTHER -> "临时航站"
    }
}

private fun currentEmptyLedgerReport(): LedgerMonthReport {
    return buildLedgerReport(
        month = currentLedgerMonth(),
        entries = emptyList(),
        monthlyBudget = 0.0,
    )
}

private fun currentLedgerMonth(): LedgerMonthKey {
    return monthOf(System.currentTimeMillis())
}

private fun monthOf(timeMillis: Long): LedgerMonthKey {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    return LedgerMonthKey(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1,
    )
}

private fun previousMonthOf(month: LedgerMonthKey): LedgerMonthKey {
    return if (month.month == 1) {
        LedgerMonthKey(year = month.year - 1, month = 12)
    } else {
        LedgerMonthKey(year = month.year, month = month.month - 1)
    }
}
