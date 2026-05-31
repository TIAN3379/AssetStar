package com.example.assetstar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private data class PlanetTexture(
    val yFactor: Float,
    val startAngle: Float,
    val sweepAngle: Float,
    val widthFactor: Float,
    val alpha: Float,
)

@Composable
fun PlanetSphere(
    planetColor: Color,
    accentColor: Color,
    pulse: Float,
    phase: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val textures = remember(planetColor, phase) {
        List(12) { index ->
            PlanetTexture(
                yFactor = 0.22f + ((index * 17 + (phase * 100).toInt()) % 58) / 100f,
                startAngle = (index * 31f + phase * 120f) % 360f,
                sweepAngle = 34f + (index % 5) * 11f,
                widthFactor = 0.50f + (index % 4) * 0.10f,
                alpha = 0.045f + (index % 4) * 0.015f,
            )
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDimension = size.minDimension
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minDimension * 0.36f
            val glowRadius = radius * (1.22f + pulse * 0.06f)
            val darkColor = Color(0xFF050B18)

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.42f),
                        Color.Transparent,
                    ),
                    center = Offset(center.x + radius * 0.18f, center.y + radius * 0.36f),
                    radius = radius * 1.28f,
                ),
                topLeft = Offset(center.x - radius * 0.92f, center.y + radius * 0.42f),
                size = Size(radius * 1.84f, radius * 0.52f),
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.36f + pulse * 0.12f),
                        accentColor.copy(alpha = 0.12f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = glowRadius,
                ),
                radius = glowRadius,
                center = center,
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        planetColor.copy(alpha = 1f),
                        planetColor.copy(alpha = 0.72f),
                        darkColor.copy(alpha = 0.96f),
                    ),
                    center = Offset(size.width * 0.38f, size.height * 0.30f),
                    radius = minDimension * 0.50f,
                ),
                radius = radius,
                center = center,
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.28f),
                        Color.Black.copy(alpha = 0.50f),
                    ),
                    center = Offset(size.width * 0.68f, size.height * 0.68f),
                    radius = minDimension * 0.42f,
                ),
                radius = radius,
                center = center,
            )

            textures.forEach { texture ->
                drawArc(
                    color = Color.White.copy(alpha = texture.alpha),
                    startAngle = texture.startAngle,
                    sweepAngle = texture.sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        x = center.x - radius * texture.widthFactor,
                        y = center.y - radius + radius * texture.yFactor,
                    ),
                    size = Size(
                        width = radius * texture.widthFactor * 2f,
                        height = radius * 0.35f,
                    ),
                    style = Stroke(width = 0.6.dp.toPx()),
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.44f),
                        planetColor.copy(alpha = 0.18f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.36f, size.height * 0.31f),
                    radius = minDimension * 0.17f,
                ),
                radius = minDimension * 0.11f,
                center = Offset(size.width * 0.34f, size.height * 0.30f),
            )

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        accentColor.copy(alpha = 0.46f),
                        Color.White.copy(alpha = 0.92f),
                        Color.Transparent,
                        accentColor.copy(alpha = 0.42f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 1.55.dp.toPx()),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension * 0.36f
        }
    }
}
