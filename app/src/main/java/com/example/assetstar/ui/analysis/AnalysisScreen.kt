package com.example.assetstar.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryStat
import com.example.assetstar.domain.model.StatusStat
import com.example.assetstar.ui.components.FullScreenStarBackground
import com.example.assetstar.ui.components.rememberGalaxyAnimationState
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.AccentPurple
import com.example.assetstar.ui.theme.AccentYellow
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.MoneyFormatter
import java.util.Calendar
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
fun AnalysisScreen(
    uiState: AnalysisUiState,
) {
    val animationState = rememberGalaxyAnimationState()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    var selectedReportIndex by remember(uiState.ledgerMonthReports) { mutableIntStateOf(0) }
    LaunchedEffect(uiState.ledgerMonthReports.size) {
        selectedReportIndex = selectedReportIndex.coerceIn(0, uiState.ledgerMonthReports.lastIndex.coerceAtLeast(0))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FullScreenStarBackground(
            modifier = Modifier.fillMaxSize(),
            animationState = animationState,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = if (pagerState.currentPage == 0) "物品总览" else "航行报告",
                color = SoftWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (pagerState.currentPage == 0) {
                    "分类占比 · 状态分布 · 持有成本"
                } else {
                    "预算复盘 · 消耗洞察 · 月度对比"
                },
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
            AnalysisSegmentTabs(
                selectedIndex = pagerState.currentPage,
                onSelected = { page ->
                    scope.launch { pagerState.animateScrollToPage(page) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                when (page) {
                    0 -> AssetAnalysisPage(uiState)
                    else -> VoyageReportPage(
                        reports = uiState.ledgerMonthReports,
                        selectedReportIndex = selectedReportIndex,
                        onPreviousMonth = {
                            selectedReportIndex = (selectedReportIndex + 1)
                                .coerceAtMost(uiState.ledgerMonthReports.lastIndex)
                        },
                        onNextMonth = {
                            selectedReportIndex = (selectedReportIndex - 1).coerceAtLeast(0)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetAnalysisPage(
    uiState: AnalysisUiState,
) {
    val categoryStats = uiState.stats.categoryStats
        .filter { it.category != AssetCategory.ALL && it.totalValue > 0.0 }
        .sortedByDescending { it.totalValue }
    val highValueAssets = uiState.assets.sortedByDescending { it.purchasePrice }.take(5)
    val highCostAssets = uiState.assets
        .sortedByDescending { uiState.metricsByAssetId[it.id]?.dailyCost ?: 0.0 }
        .take(5)
    val availableYears = buildAvailableYears(uiState.assets)
    var selectedTrendYear by remember(availableYears) {
        mutableIntStateOf(currentYear().coerceIn(availableYears.first(), availableYears.last()))
    }
    val yearlyTrend = buildYearTrend(uiState.assets, selectedTrendYear)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        GalaxyOverviewCard(
            uiState = uiState,
            categoryStats = categoryStats,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(260.dp),
        )

        StatusDistributionCard(
            statusStats = uiState.stats.statusStats,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        )

        RankSection(
            title = "高价值资产排行",
            subtitle = "按购入价排序",
            items = highValueAssets.map { asset ->
                RankRowData(
                    title = asset.name,
                    meta = uiState.categoryDisplaySettings.nameOf(asset.category),
                    value = MoneyFormatter.format(asset.purchasePrice),
                )
            },
            accent = AccentCyan,
            modifier = Modifier.padding(top = 14.dp),
        )

        RankSection(
            title = "持有成本排行",
            subtitle = "按日均成本排序",
            items = highCostAssets.map { asset ->
                val metrics = uiState.metricsByAssetId[asset.id]
                RankRowData(
                    title = asset.name,
                    meta = "${uiState.categoryDisplaySettings.nameOf(asset.category)} · ${metrics?.useDays ?: 0} 天",
                    value = MoneyFormatter.formatDailyCost(metrics?.dailyCost ?: 0.0),
                )
            },
            accent = AccentLime,
            modifier = Modifier.padding(top = 14.dp),
        )

        TrendCard(
            trend = yearlyTrend,
            selectedYear = selectedTrendYear,
            availableYears = availableYears,
            onPreviousYear = {
                selectedTrendYear = availableYears
                    .lastOrNull { it < selectedTrendYear }
                    ?: selectedTrendYear
            },
            onNextYear = {
                selectedTrendYear = availableYears
                    .firstOrNull { it > selectedTrendYear }
                    ?: selectedTrendYear
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun AnalysisSegmentTabs(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf("资产星图", "航行报告")
    Row(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF071426).copy(alpha = 0.58f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = if (selected) {
                            Brush.horizontalGradient(
                                listOf(
                                    AccentCyan.copy(alpha = 0.34f),
                                    AccentBlue.copy(alpha = 0.18f),
                                ),
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color.Transparent),
                            )
                        },
                    )
                    .clickable { onSelected(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    color = if (selected) SoftWhite else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun VoyageReportPage(
    reports: List<LedgerMonthReport>,
    selectedReportIndex: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val safeReports = reports.ifEmpty { listOf(LedgerMonthReportFallback) }
    val report = safeReports[selectedReportIndex.coerceIn(0, safeReports.lastIndex)]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MonthSwitchCard(
            report = report,
            canPrevious = selectedReportIndex < safeReports.lastIndex,
            canNext = selectedReportIndex > 0,
            onPrevious = onPreviousMonth,
            onNext = onNextMonth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
        )
        BudgetReportCard(
            report = report,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        )
        LedgerInsightCard(
            report = report,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        )
        LedgerCompareCard(
            report = report,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun MonthSwitchCard(
    report: LedgerMonthReport,
    canPrevious: Boolean,
    canNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("月度航行报告", color = SoftWhite, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = report.month.label,
                    color = AccentCyan,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            ReportMonthSwitch(
                label = "${report.month.month}月",
                canPrevious = canPrevious,
                canNext = canNext,
                onPrevious = onPrevious,
                onNext = onNext,
            )
        }
        Text(
            text = if (report.entryCount > 0) {
                "本月记录 ${report.entryCount} 条能源变化"
            } else {
                "本月暂无能源变化记录"
            },
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun ReportMonthSwitch(
    label: String,
    canPrevious: Boolean,
    canNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFF071426).copy(alpha = 0.62f),
        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.20f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "<",
                color = if (canPrevious) SoftWhite else TextSecondary.copy(alpha = 0.38f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(enabled = canPrevious, onClick = onPrevious),
            )
            Text(
                text = label,
                color = SoftWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = ">",
                color = if (canNext) SoftWhite else TextSecondary.copy(alpha = 0.38f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(enabled = canNext, onClick = onNext),
            )
        }
    }
}

@Composable
private fun BudgetReportCard(
    report: LedgerMonthReport,
    modifier: Modifier = Modifier,
) {
    val status = budgetStatusOf(report)
    AnalysisPanel(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("预算能源复盘", color = SoftWhite, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
            Text(status.label, color = status.color, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 1.dp))
        }
        EnergyRouteProgress(
            ratio = report.budgetUsedRatio,
            status = status,
            hasBudget = report.hasBudget,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(52.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ReportMetricTile(
                title = "本月预算",
                value = if (report.hasBudget) MoneyFormatter.formatCompact(report.monthlyBudget) else "未设置",
                accent = AccentCyan,
                modifier = Modifier.weight(1f),
            )
            ReportMetricTile(
                title = "实际支出",
                value = MoneyFormatter.formatCompact(report.expense),
                accent = status.color,
                modifier = Modifier.weight(1f),
            )
            ReportMetricTile(
                title = if (report.isOverBudget) "超支金额" else "节省金额",
                value = if (report.hasBudget) {
                    MoneyFormatter.formatCompact(abs(report.budgetDifference))
                } else {
                    "--"
                },
                accent = if (report.isOverBudget) Color(0xFFFF5A6A) else AccentLime,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = budgetSummaryText(report, status),
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
private fun EnergyRouteProgress(
    ratio: Double,
    status: BudgetStatus,
    hasBudget: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val trackHeight = 14.dp.toPx()
        val centerY = size.height * 0.48f
        val startX = 10.dp.toPx()
        val endX = size.width - 10.dp.toPx()
        val progress = if (hasBudget) ratio.toFloat().coerceIn(0f, 1f) else 0f
        val progressX = startX + (endX - startX) * progress
        drawLine(
            color = Color(0xFF13283B).copy(alpha = 0.92f),
            start = Offset(startX, centerY),
            end = Offset(endX, centerY),
            strokeWidth = trackHeight,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = SoftWhite.copy(alpha = 0.08f),
            start = Offset(startX, centerY - trackHeight * 0.36f),
            end = Offset(endX, centerY - trackHeight * 0.36f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
        if (hasBudget) {
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = 0.98f),
                        status.color.copy(alpha = 0.95f),
                    ),
                    startX = startX,
                    endX = progressX,
                ),
                start = Offset(startX, centerY),
                end = Offset(progressX, centerY),
                strokeWidth = trackHeight,
                cap = StrokeCap.Round,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        status.color.copy(alpha = 0.65f),
                        Color.Transparent,
                    ),
                    center = Offset(progressX, centerY),
                    radius = 18.dp.toPx(),
                ),
                radius = 18.dp.toPx(),
                center = Offset(progressX, centerY),
            )
            drawCircle(
                color = SoftWhite.copy(alpha = 0.92f),
                radius = 3.dp.toPx(),
                center = Offset(progressX, centerY),
            )
        }
        val marks = listOf(0.6f, 0.9f, 1f)
        marks.forEach { mark ->
            val x = startX + (endX - startX) * mark
            drawCircle(
                color = SoftWhite.copy(alpha = 0.18f),
                radius = 2.dp.toPx(),
                center = Offset(x, centerY),
            )
        }
    }
}

@Composable
private fun ReportMetricTile(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF071426).copy(alpha = 0.54f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
            Text(title, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = value,
                color = accent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun LedgerInsightCard(
    report: LedgerMonthReport,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier) {
        Text("消耗洞察", color = SoftWhite, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            InsightTile(
                title = "最大消耗分类",
                insight = report.topExpenseCategory,
                emptyText = "暂无支出分类",
                accent = AccentYellow,
                modifier = Modifier.weight(1f),
            )
            InsightTile(
                title = "最常去的消费场所",
                insight = report.frequentPlace,
                emptyText = "暂无停靠记录",
                accent = AccentCyan,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InsightTile(
    title: String,
    insight: LedgerInsight?,
    emptyText: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF071426).copy(alpha = 0.54f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Text(
                text = insight?.title ?: emptyText,
                color = SoftWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = if (insight != null) {
                    "${MoneyFormatter.formatCompact(insight.amount)} · ${insight.count} 次"
                } else {
                    "--"
                },
                color = accent,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun LedgerCompareCard(
    report: LedgerMonthReport,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier) {
        Text("和上个月对比", color = SoftWhite, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ReportMetricTile(
                title = "本月支出",
                value = MoneyFormatter.formatCompact(report.expense),
                accent = AccentYellow,
                modifier = Modifier.weight(1f),
            )
            ReportMetricTile(
                title = "上月支出",
                value = MoneyFormatter.formatCompact(report.previousExpense),
                accent = TextSecondary,
                modifier = Modifier.weight(1f),
            )
            ReportMetricTile(
                title = "本月收入",
                value = MoneyFormatter.formatCompact(report.income),
                accent = AccentCyan,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = compareSummaryText(report),
            color = if (report.expenseDelta <= 0.0) AccentLime else AccentYellow,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
private fun GalaxyOverviewCard(
    uiState: AnalysisUiState,
    categoryStats: List<CategoryStat>,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryDonutChart(
                categoryStats = categoryStats,
                modifier = Modifier
                    .weight(1f)
                    .height(214.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
            ) {
                Text("资产星环", color = SoftWhite, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = MoneyFormatter.format(uiState.stats.totalAssetValue),
                    color = SoftWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = "总日均成本 ${MoneyFormatter.formatDailyCost(uiState.stats.totalDailyCost)}",
                    color = AccentLime,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
                categoryStats.take(4).forEachIndexed { index, item ->
                    LegendRow(
                        name = uiState.categoryDisplaySettings.nameOf(item.category),
                        value = MoneyFormatter.format(item.totalValue),
                        color = chartColors[index % chartColors.size],
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryDonutChart(
    categoryStats: List<CategoryStat>,
    modifier: Modifier = Modifier,
) {
    val total = categoryStats.sumOf { it.totalValue }.coerceAtLeast(1.0)
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.36f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentCyan.copy(alpha = 0.24f), Color.Transparent),
                center = center,
                radius = radius * 1.8f,
            ),
            radius = radius * 1.8f,
            center = center,
        )
        var startAngle = -90f
        categoryStats.take(8).forEachIndexed { index, item ->
            val sweep = (item.totalValue / total * 360.0).toFloat().coerceAtLeast(4f)
            drawArc(
                color = chartColors[index % chartColors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round),
            )
            startAngle += sweep + 2f
        }
        drawCircle(
            color = Color(0xFF06101F).copy(alpha = 0.86f),
            radius = radius * 0.58f,
            center = center,
        )
        drawCircle(
            color = SoftWhite.copy(alpha = 0.16f),
            radius = radius * 1.08f,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )
    }
}

@Composable
private fun StatusDistributionCard(
    statusStats: List<StatusStat>,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier) {
        Text("状态分布", color = SoftWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            statusStats.forEachIndexed { index, item ->
                val color = listOf(AccentCyan, AccentYellow, TextSecondary)[index % 3]
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF071426).copy(alpha = 0.54f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.22f)),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.status.displayName, color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${item.count} 件",
                            color = SoftWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        Text(
                            text = if (item.recoveredValue > 0.0) {
                                MoneyFormatter.format(item.recoveredValue)
                            } else {
                                MoneyFormatter.format(item.totalValue)
                            },
                            color = color,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

private data class RankRowData(
    val title: String,
    val meta: String,
    val value: String,
)

@Composable
private fun RankSection(
    title: String,
    subtitle: String,
    items: List<RankRowData>,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    AnalysisPanel(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(title, color = SoftWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, bottom = 1.dp))
        }
        if (items.isEmpty()) {
            Text("暂无资产数据", color = TextSecondary, modifier = Modifier.padding(top = 14.dp))
        } else {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = (index + 1).toString().padStart(2, '0'),
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.size(width = 34.dp, height = 24.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, color = SoftWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.meta, color = TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(item.value, color = accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun TrendCard(
    trend: List<MonthlyTrend>,
    selectedYear: Int,
    availableYears: List<Int>,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxCount = trend.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    AnalysisPanel(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("年度购入分布", color = SoftWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "按购买日期统计 1-12 月物品数量",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            YearSwitch(
                year = selectedYear,
                canPrevious = availableYears.any { it < selectedYear },
                canNext = availableYears.any { it > selectedYear },
                onPrevious = onPreviousYear,
                onNext = onNextYear,
            )
        }

        Text(
            text = "全年购入 ${trend.sumOf { it.count }} 件 · 峰值 ${trend.maxOfOrNull { it.count } ?: 0} 件/月",
            color = AccentCyan,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(158.dp)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            trend.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.62f)
                                .height(monthBarHeight(item.count, maxCount))
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            if (item.count > 0) AccentCyan else TextSecondary.copy(alpha = 0.34f),
                                            if (item.count > 0) AccentBlue.copy(alpha = 0.30f) else TextSecondary.copy(alpha = 0.10f),
                                        ),
                                    ),
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 3.dp, bottomEnd = 3.dp),
                                ),
                        )
                    }
                    Text(
                        text = item.count.takeIf { it > 0 }?.toString() ?: "0",
                        color = if (item.count > 0) SoftWhite else TextSecondary.copy(alpha = 0.62f),
                        fontSize = 9.sp,
                        lineHeight = 9.sp,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                    Text(
                        text = "${item.month}",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        lineHeight = 8.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun YearSwitch(
    year: Int,
    canPrevious: Boolean,
    canNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFF071426).copy(alpha = 0.62f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.20f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "<",
                color = if (canPrevious) SoftWhite else TextSecondary.copy(alpha = 0.38f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(enabled = canPrevious, onClick = onPrevious),
            )
            Text(
                text = "${year}年",
                color = SoftWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = ">",
                color = if (canNext) SoftWhite else TextSecondary.copy(alpha = 0.38f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(enabled = canNext, onClick = onNext),
            )
        }
    }
}

@Composable
private fun LegendRow(
    name: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(100.dp)),
        )
        Text(name, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp).weight(1f), maxLines = 1)
        Text(value, color = SoftWhite, fontSize = 12.sp)
    }
}

@Composable
private fun AnalysisPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = PanelBlue.copy(alpha = 0.72f),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftWhite.copy(alpha = 0.10f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

private data class BudgetStatus(
    val label: String,
    val color: Color,
)

private val LedgerMonthReportFallback = LedgerMonthReport(
    month = LedgerMonthKey(
        year = currentYear(),
        month = Calendar.getInstance().get(Calendar.MONTH) + 1,
    ),
    monthlyBudget = 0.0,
    income = 0.0,
    expense = 0.0,
    balance = 0.0,
    budgetDifference = 0.0,
    budgetUsedRatio = 0.0,
    previousIncome = 0.0,
    previousExpense = 0.0,
    expenseDelta = 0.0,
    incomeDelta = 0.0,
    expenseDeltaRate = null,
    topExpenseCategory = null,
    frequentPlace = null,
    entryCount = 0,
)

private fun budgetStatusOf(report: LedgerMonthReport): BudgetStatus {
    if (!report.hasBudget) {
        return BudgetStatus("等待预算注入", TextSecondary)
    }
    return when {
        report.budgetUsedRatio > 1.0 -> BudgetStatus("能源核心故障", Color(0xFFFF5A6A))
        report.budgetUsedRatio >= 0.9 -> BudgetStatus("低能源航行", Color(0xFFFF8F4A))
        report.budgetUsedRatio >= 0.6 -> BudgetStatus("消耗偏快", AccentYellow)
        else -> BudgetStatus("能源稳定", AccentCyan)
    }
}

private fun budgetSummaryText(report: LedgerMonthReport, status: BudgetStatus): String {
    if (!report.hasBudget) {
        return "设置每月预算后，航行报告会自动评估能源消耗速度。"
    }
    val usedPercent = (report.budgetUsedRatio * 100).toInt().coerceAtLeast(0)
    return if (report.isOverBudget) {
        "预算已超支 ${MoneyFormatter.formatCompact(abs(report.budgetDifference))}，${status.label}。"
    } else {
        "已使用预算 $usedPercent%，剩余 ${MoneyFormatter.formatCompact(report.budgetDifference.coerceAtLeast(0.0))}。"
    }
}

private fun compareSummaryText(report: LedgerMonthReport): String {
    if (report.previousExpense <= 0.0 && report.previousIncome <= 0.0) {
        return "暂无上月对比数据。"
    }
    val expenseText = when {
        report.expenseDelta > 0.0 -> "支出比上月增加 ${MoneyFormatter.formatCompact(report.expenseDelta)}"
        report.expenseDelta < 0.0 -> "支出比上月减少 ${MoneyFormatter.formatCompact(abs(report.expenseDelta))}"
        else -> "支出与上月持平"
    }
    val rateText = report.expenseDeltaRate?.let { rate ->
        "（${if (rate >= 0) "+" else "-"}${(abs(rate) * 100).toInt()}%）"
    } ?: ""
    val incomeText = when {
        report.incomeDelta > 0.0 -> "收入增加 ${MoneyFormatter.formatCompact(report.incomeDelta)}"
        report.incomeDelta < 0.0 -> "收入减少 ${MoneyFormatter.formatCompact(abs(report.incomeDelta))}"
        else -> "收入持平"
    }
    return "$expenseText$rateText · $incomeText"
}

private val chartColors = listOf(
    AccentCyan,
    AccentLime,
    AccentBlue,
    AccentPurple,
    AccentYellow,
    Color(0xFF32E6B8),
    Color(0xFFEAF6FF),
    Color(0xFF4C7DFF),
)

private data class MonthlyTrend(
    val month: Int,
    val count: Int,
)

private fun monthBarHeight(count: Int, maxCount: Int) = if (count <= 0) {
    6.dp
} else {
    (18 + 82 * count / maxCount.coerceAtLeast(1)).dp
}

private fun buildAvailableYears(assets: List<Asset>): List<Int> {
    val calendar = Calendar.getInstance()
    val current = calendar.get(Calendar.YEAR)
    val years = assets.map { asset ->
        calendar.timeInMillis = asset.purchaseDate
        calendar.get(Calendar.YEAR)
    } + current
    return years.distinct().sorted()
}

private fun currentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}

private fun buildYearTrend(assets: List<Asset>, year: Int): List<MonthlyTrend> {
    val calendar = Calendar.getInstance()
    return (1..12).map { month ->
        val count = assets.count { asset ->
            calendar.timeInMillis = asset.purchaseDate
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month - 1
        }
        MonthlyTrend(month = month, count = count)
    }
}
