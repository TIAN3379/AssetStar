package com.example.assetstar.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.util.MoneyFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun StarMapSection(
    specs: List<OrbitNodeSpec>,
    categoryStats: List<CategoryStats>,
    totalAssetValue: Double,
    totalDailyCost: Double,
    amountsVisible: Boolean,
    onToggleAmountsVisible: () -> Unit,
    onCategoryClick: (AssetCategory) -> Unit,
    modifier: Modifier = Modifier,
    animationState: GalaxyAnimationState? = null,
) {
    val state = animationState ?: rememberGalaxyAnimationState()
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
            val itemSize = nodeSize
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
                onClick = { onCategoryClick(spec.category) },
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

data class OrbitNodeSpec(
    val category: AssetCategory,
    val title: String,
    @param:DrawableRes val iconRes: Int,
    val accent: Color,
    val angleDegrees: Float,
)
