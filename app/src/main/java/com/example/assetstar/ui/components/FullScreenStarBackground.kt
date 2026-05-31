package com.example.assetstar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentPurple
import com.example.assetstar.ui.theme.SoftWhite
import kotlin.math.PI
import kotlin.math.sin

private data class StarSpec(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val alpha: Float,
    val phase: Float,
    val speed: Float,
    val color: Color,
)

@Composable
fun FullScreenStarBackground(
    modifier: Modifier = Modifier,
    animationState: GalaxyAnimationState? = null,
) {
    val stars = remember {
        List(112) { index ->
            val color = when (index % 4) {
                0 -> AccentCyan
                1 -> SoftWhite
                2 -> AccentPurple
                else -> AccentBlue
            }
            StarSpec(
                xFraction = (((index * 61) % 997) / 997f).coerceIn(0.03f, 0.97f),
                yFraction = (((index * 89) % 991) / 991f).coerceIn(0.02f, 0.98f),
                radiusDp = 0.9f + (index % 3) * 0.55f,
                alpha = 0.16f + (index % 5) * 0.03f,
                phase = (index % 11) / 11f,
                speed = 0.35f + (index % 7) * 0.08f,
                color = color,
            )
        }
    }
    val state = animationState ?: rememberGalaxyAnimationState()

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF050B18),
                    Color(0xFF071426),
                    Color(0xFF0B1E2D),
                ),
            ),
        )

        val glowCenter = Offset(
            x = size.width / 2f,
            y = size.height * (0.49f + sin(state.orbitFlow * PI * 2).toFloat() * 0.006f),
        )
        val glowRadius = size.width * 0.80f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentCyan.copy(alpha = 0.20f),
                    AccentBlue.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
                center = glowCenter,
                radius = glowRadius,
            ),
            radius = glowRadius,
            center = glowCenter,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFB6FF4A).copy(alpha = 0.10f),
                    Color.Transparent,
                ),
                center = Offset(size.width * 0.52f, size.height * 0.58f),
                radius = size.width * 0.44f,
            ),
            radius = size.width * 0.44f,
            center = Offset(size.width * 0.52f, size.height * 0.58f),
        )

        val orbitCenter = Offset(size.width / 2f, size.height * 0.44f)
        listOf(
            Triple(0.88f, 0.34f, SoftWhite.copy(alpha = 0.08f)),
            Triple(1.05f, 0.41f, AccentCyan.copy(alpha = 0.10f)),
            Triple(1.22f, 0.49f, SoftWhite.copy(alpha = 0.07f)),
        ).forEachIndexed { index, spec ->
            val orbitWidth = size.width * spec.first
            val orbitHeight = size.height * spec.second
            drawOval(
                color = spec.third,
                topLeft = Offset(orbitCenter.x - orbitWidth / 2f, orbitCenter.y - orbitHeight / 2f),
                size = Size(orbitWidth, orbitHeight),
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = if (index != 1) {
                        PathEffect.dashPathEffect(floatArrayOf(9f, 14f), index * 7f)
                    } else {
                        null
                    },
                ),
            )
        }

        stars.forEach { star ->
            val driftX = sin((state.orbitFlow * 0.18f + star.phase) * PI * 2).toFloat() * size.width * 0.003f
            val driftY = sin((state.orbitFlow * 0.12f + star.phase * 1.7f) * PI * 2).toFloat() * size.height * 0.002f
            val pulse = 0.55f + 0.45f * ((sin((state.starTwinkle * star.speed + star.phase) * PI * 2) + 1f) / 2f).toFloat()
            drawCircle(
                color = star.color.copy(alpha = star.alpha * pulse),
                radius = star.radiusDp.dp.toPx(),
                center = Offset(size.width * star.xFraction + driftX, size.height * star.yFraction + driftY),
            )
        }

        repeat(24) { index ->
            val x = size.width * (((index * 47) % 983) / 983f)
            val y = size.height * (((index * 71) % 977) / 977f)
            val alpha = 0.035f + (index % 4) * 0.012f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = alpha),
                        Color.Transparent,
                    ),
                    center = Offset(x, y),
                    radius = (10 + index % 9).dp.toPx(),
                ),
                radius = (1.5f + (index % 3)).dp.toPx(),
                center = Offset(x, y),
            )
        }
    }
}
