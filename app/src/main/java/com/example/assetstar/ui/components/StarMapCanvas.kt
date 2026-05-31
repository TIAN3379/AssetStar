package com.example.assetstar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.SoftWhite
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private data class OrbitGlowSpec(
    val scaleX: Float,
    val scaleY: Float,
    val color: Color,
    val phase: Float,
)

@Composable
fun StarMapCanvas(
    modifier: Modifier = Modifier,
    orbitCenters: List<Pair<Float, Float>> = emptyList(),
    centerYFraction: Float = 0.52f,
    radiusXFraction: Float = 0.40f,
    radiusYFraction: Float = 0.34f,
    animationState: GalaxyAnimationState? = null,
) {
    val state = animationState ?: rememberGalaxyAnimationState()

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height * centerYFraction)
        val radiusX = size.width * radiusXFraction
        val radiusY = size.height * radiusYFraction
        val baseRadius = min(radiusX, radiusY)
        val glowPulse = 0.86f + state.starPulse * 0.34f
        val orbitGold = Color(0xFFFFB72E)
        val orbitOrange = Color(0xFFFF7A1A)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    orbitGold.copy(alpha = 0.12f * glowPulse),
                    AccentCyan.copy(alpha = 0.13f * glowPulse),
                    orbitOrange.copy(alpha = 0.08f * glowPulse),
                    Color.Transparent,
                ),
                center = center,
                radius = baseRadius * (1.55f + (glowPulse - 1f) * 0.14f),
            ),
            radius = baseRadius * (1.55f + (glowPulse - 1f) * 0.14f),
            center = center,
        )

        val orbitScales = listOf(
            0.72f to 0.68f,
            0.86f to 0.82f,
            1.00f to 1.00f,
            1.10f to 1.08f,
            1.18f to 1.15f,
        )
        orbitScales.forEachIndexed { index, scale ->
            val width = radiusX * 2f * scale.first
            val height = radiusY * 2f * scale.second
            val alpha = when (index) {
                2 -> 0.24f
                4 -> 0.12f
                else -> 0.10f + index * 0.02f
            }
            drawOval(
                color = when (index) {
                    2 -> orbitGold.copy(alpha = alpha + 0.04f)
                    3 -> AccentCyan.copy(alpha = alpha)
                    else -> SoftWhite.copy(alpha = alpha)
                },
                topLeft = Offset(center.x - width / 2f, center.y - height / 2f),
                size = Size(width, height),
                style = Stroke(
                    width = if (index == 2) 1.35.dp.toPx() else 1.dp.toPx(),
                    pathEffect = when (index) {
                        1, 4 -> PathEffect.dashPathEffect(floatArrayOf(8f, 12f), state.orbitFlow * 80f + index * 8f)
                        3 -> PathEffect.dashPathEffect(floatArrayOf(3f, 10f), state.orbitFlow * 55f)
                        else -> null
                    },
                ),
            )
            if (index == 2 || index == 3) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            orbitGold.copy(alpha = 0.72f),
                            AccentCyan.copy(alpha = 0.44f),
                            Color.Transparent,
                        ),
                        center = center,
                    ),
                    startAngle = state.orbitFlow * 360f + index * 70f,
                    sweepAngle = 36f,
                    useCenter = false,
                    topLeft = Offset(center.x - width / 2f, center.y - height / 2f),
                    size = Size(width, height),
                    style = Stroke(width = 1.7.dp.toPx()),
                )
            }
        }

        orbitCenters.take(8).forEachIndexed { index, point ->
            val node = Offset(size.width * point.first, size.height * point.second)
            val control = Offset(
                x = (center.x + node.x) / 2f,
                y = (center.y + node.y) / 2f + if (node.y < center.y) -26.dp.toPx() else 26.dp.toPx(),
            )
            val path = Path().apply {
                moveTo(center.x, center.y)
                quadraticTo(control.x, control.y, node.x, node.y)
            }
            drawPath(
                path = path,
                color = SoftWhite.copy(alpha = if (index % 2 == 0) 0.15f else 0.10f),
                style = Stroke(
                    width = 0.85.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6f, 12f),
                        state.orbitFlow * 60f + index * 5f,
                    ),
                ),
            )
            drawCircle(
                color = if (index % 3 == 0) orbitGold.copy(alpha = 0.62f) else AccentCyan.copy(alpha = 0.54f),
                radius = 2.8.dp.toPx(),
                center = node,
            )
        }

        val movingDots = listOf(
            OrbitGlowSpec(0.86f, 0.82f, AccentCyan, 0.08f),
            OrbitGlowSpec(1.00f, 1.00f, orbitGold, 0.34f),
            OrbitGlowSpec(1.10f, 1.08f, AccentBlue, 0.58f),
            OrbitGlowSpec(1.18f, 1.15f, SoftWhite, 0.81f),
        )
        movingDots.forEachIndexed { index, dot ->
            val angle = (state.orbitFlow * (0.42f + index * 0.13f) + dot.phase) * PI * 2f
            val x = center.x + radiusX * dot.scaleX * cos(angle).toFloat()
            val y = center.y + radiusY * dot.scaleY * sin(angle).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        dot.color.copy(alpha = 0.80f),
                        dot.color.copy(alpha = 0.20f),
                        Color.Transparent,
                    ),
                    center = Offset(x, y),
                    radius = 12.dp.toPx(),
                ),
                radius = if (index == 0) 4.4.dp.toPx() else 3.4.dp.toPx(),
                center = Offset(x, y),
            )
        }

        repeat(18) { index ->
            val angle = Math.toRadians((index * 360f / 18f).toDouble())
            val rx = radiusX * (0.80f + (index % 3) * 0.16f)
            val ry = radiusY * (0.78f + (index % 3) * 0.14f)
            val x = center.x + rx * cos(angle).toFloat()
            val y = center.y + ry * sin(angle).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (index % 3 == 0) orbitGold.copy(alpha = 0.48f) else AccentBlue.copy(alpha = 0.44f),
                        AccentCyan.copy(alpha = 0.16f),
                        Color.Transparent,
                    ),
                    center = Offset(x, y),
                    radius = 14.dp.toPx(),
                ),
                radius = if (index % 4 == 0) 4.dp.toPx() else 2.4.dp.toPx(),
                center = Offset(x, y),
            )
        }
    }
}
