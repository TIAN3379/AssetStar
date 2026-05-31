package com.example.assetstar.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.DateUtils
import com.example.assetstar.util.MoneyFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun StarMapSection(
    specs: List<OrbitNodeSpec>,
    categoryStats: List<CategoryStats>,
    assets: List<Asset> = emptyList(),
    totalAssetValue: Double,
    totalDailyCost: Double,
    amountsVisible: Boolean,
    onToggleAmountsVisible: () -> Unit,
    onCategoryClick: (AssetCategory) -> Unit,
    modifier: Modifier = Modifier,
    animationState: GalaxyAnimationState? = null,
) {
    val state = animationState ?: rememberGalaxyAnimationState()
    val haptic = LocalHapticFeedback.current
    var selectedCategory by remember { mutableStateOf<AssetCategory?>(null) }
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val statMap = categoryStats.associateBy { it.category }
        val centerSize = 198.dp
        val nodeSize = 72.dp

        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val centerX = widthPx / 2f
        val centerY = heightPx * 0.52f
        val radiusX = widthPx * 0.385f
        val radiusY = heightPx * 0.335f

        StarMapCanvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            centerYFraction = 0.52f,
            radiusXFraction = 0.385f,
            radiusYFraction = 0.335f,
            animationState = state,
            orbitCenters = specs.map { spec ->
                val point = orbitPoint(centerX, centerY, radiusX, radiusY, spec.angleDegrees + state.globalRotation)
                (point.x / widthPx) to (point.y / heightPx)
            },
        )

        specs.forEachIndexed { index, spec ->
            val stat = statMap[spec.category]
            val itemSize = nodeSize + categoryScaleBonus(stat?.assetCount ?: 0)
            val glowSizePx = with(density) { (itemSize + 8.dp).toPx() }
            val point = orbitPoint(centerX, centerY, radiusX, radiusY, spec.angleDegrees + state.globalRotation)
            val x = (point.x - glowSizePx / 2f).coerceIn(0f, widthPx - glowSizePx)
            val y = (point.y - glowSizePx / 2f).coerceIn(0f, heightPx - glowSizePx)

            OrbitCategoryNode(
                title = spec.title,
                value = nodeValue(stat),
                iconRes = spec.iconRes,
                accentColor = spec.accent,
                nodeSize = itemSize,
                floatProgress = state.nodeFloat,
                satelliteProgress = state.satelliteAngle,
                phase = index / specs.size.toFloat(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedCategory = spec.category
                },
                modifier = Modifier
                    .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                    .size(with(density) { glowSizePx.toDp() })
                    .zIndex(1f),
            )
        }

        val centerSizePx = with(density) { centerSize.toPx() }
        CenterAssetPlanet(
            totalAssetValue = totalAssetValue,
            totalDailyCost = totalDailyCost,
            amountsVisible = amountsVisible,
            onToggleAmountsVisible = onToggleAmountsVisible,
            diameter = centerSize,
            animationState = state,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (centerX - centerSizePx / 2f).roundToInt(),
                        y = (centerY - centerSizePx / 2f).roundToInt(),
                    )
                }
                .zIndex(2f),
        )

        val selected = selectedCategory
        val selectedSpec = specs.firstOrNull { it.category == selected }
        val selectedStats = statMap[selected]
        AnimatedVisibility(
            visible = selected != null && selectedSpec != null,
            enter = fadeIn() + scaleIn(initialScale = 0.96f),
            exit = fadeOut() + scaleOut(targetScale = 0.96f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 22.dp, vertical = 14.dp)
                .zIndex(4f),
        ) {
            if (selected != null && selectedSpec != null) {
                CategoryInsightPanel(
                    title = selectedSpec.title,
                    accent = selectedSpec.accent,
                    stats = selectedStats,
                    assets = if (selected == AssetCategory.ALL) assets else assets.filter { it.category == selected },
                    onDismiss = { selectedCategory = null },
                    onOpen = {
                        selectedCategory = null
                        onCategoryClick(selected)
                    },
                )
            }
        }
    }
}

@Composable
private fun CategoryInsightPanel(
    title: String,
    accent: Color,
    stats: CategoryStats?,
    assets: List<Asset>,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
) {
    val topAsset = assets.maxByOrNull { it.purchasePrice }
    val recentAsset = assets.maxByOrNull { it.updatedAt }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.42f)),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = 0.16f),
                            Color.Transparent,
                            AccentCyan.copy(alpha = 0.08f),
                        ),
                    ),
                )
                .padding(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = SoftWhite, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${stats?.assetCount ?: assets.size} 件 · ${MoneyFormatter.formatCompact(stats?.totalPurchasePrice ?: 0.0)}",
                        color = accent,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "关闭",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onDismiss)
                        .padding(4.dp),
                )
            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                InsightMetric(
                    label = "最高价值",
                    value = topAsset?.name ?: "暂无资产",
                    helper = topAsset?.let { MoneyFormatter.formatCompact(it.purchasePrice) } ?: "--",
                    modifier = Modifier.weight(1f),
                )
                InsightMetric(
                    label = "最近更新",
                    value = recentAsset?.name ?: "暂无资产",
                    helper = recentAsset?.let { DateUtils.formatDate(it.purchaseDate) } ?: "--",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onOpen),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("查看全部", color = AccentLime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = AccentLime,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun InsightMetric(
    label: String,
    value: String,
    helper: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(
            text = value,
            color = SoftWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = helper,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

private data class OrbitPoint(
    val x: Float,
    val y: Float,
)

private fun orbitPoint(
    centerX: Float,
    centerY: Float,
    radiusX: Float,
    radiusY: Float,
    angleDegrees: Float,
): OrbitPoint {
    val angle = angleDegrees / 180f * PI.toFloat()
    return OrbitPoint(
        x = centerX + radiusX * cos(angle),
        y = centerY + radiusY * sin(angle),
    )
}

private fun nodeValue(stat: CategoryStats?): String {
    if (stat == null) return "¥0"
    return MoneyFormatter.formatCompact(stat.totalPurchasePrice)
}

private fun categoryScaleBonus(assetCount: Int) = when {
    assetCount >= 10 -> 8.dp
    assetCount >= 6 -> 6.dp
    assetCount >= 3 -> 4.dp
    assetCount >= 1 -> 2.dp
    else -> 0.dp
}

data class OrbitNodeSpec(
    val category: AssetCategory,
    val title: String,
    @param:DrawableRes val iconRes: Int,
    val accent: Color,
    val angleDegrees: Float,
)
