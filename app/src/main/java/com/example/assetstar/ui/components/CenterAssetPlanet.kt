package com.example.assetstar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.MoneyFormatter

@Composable
fun CenterAssetPlanet(
    totalAssetValue: Double,
    totalDailyCost: Double,
    amountsVisible: Boolean,
    onToggleAmountsVisible: () -> Unit,
    modifier: Modifier = Modifier,
    diameter: Dp = 210.dp,
    animationState: GalaxyAnimationState? = null,
) {
    val amountText = if (amountsVisible) MoneyFormatter.formatCompact(totalAssetValue) else "••••"
    val dailyCostText = if (amountsVisible) "${MoneyFormatter.formatCompact(totalDailyCost)} / 天" else "•• / 天"
    val amountFontSize = when {
        amountText.length >= 10 -> 32.sp
        amountText.length >= 8 -> 36.sp
        else -> 40.sp
    }
    val state = animationState ?: rememberGalaxyAnimationState()
    val pulseScale = state.starPulse
    val starCyan = Color(0xFF45EFFF)
    val starBlue = Color(0xFF2C7DFF)
    val starCore = Color(0xFFEAF6FF)

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDimension = size.minDimension
            val center = Offset(size.width / 2f, size.height / 2f)
            val sphereRadius = minDimension * 0.43f
            val glowRadius = sphereRadius * (1.24f + (pulseScale - 1f) * 0.08f)
            val darkColor = Color(0xFF050B18)

            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.44f),
                        Color.Transparent,
                    ),
                    center = Offset(center.x + sphereRadius * 0.18f, center.y + sphereRadius * 0.36f),
                    radius = sphereRadius * 1.35f,
                ),
                topLeft = Offset(center.x - sphereRadius * 0.96f, center.y + sphereRadius * 0.46f),
                size = Size(sphereRadius * 1.92f, sphereRadius * 0.56f),
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        starCyan.copy(alpha = 0.34f + (pulseScale - 0.92f) * 0.16f),
                        starBlue.copy(alpha = 0.12f),
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
                        Color(0xFFBFFFF6).copy(alpha = 0.96f),
                        Color(0xFF37BDEB).copy(alpha = 0.88f),
                        Color(0xFF0C4F83).copy(alpha = 0.96f),
                        Color(0xFF061C35).copy(alpha = 1f),
                        darkColor.copy(alpha = 1f),
                    ),
                    center = Offset(center.x - sphereRadius * 0.44f, center.y - sphereRadius * 0.48f),
                    radius = sphereRadius * 1.34f,
                ),
                radius = sphereRadius,
                center = center,
            )

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.34f),
                    ),
                    start = Offset(center.x - sphereRadius * 0.72f, center.y - sphereRadius * 0.78f),
                    end = Offset(center.x + sphereRadius * 0.70f, center.y + sphereRadius * 0.74f),
                ),
                radius = sphereRadius,
                center = center,
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.34f),
                        Color.Black.copy(alpha = 0.72f),
                    ),
                    center = Offset(center.x + sphereRadius * 0.58f, center.y + sphereRadius * 0.60f),
                    radius = sphereRadius * 0.96f,
                ),
                radius = sphereRadius,
                center = center,
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color(0xFF020711).copy(alpha = 0.58f),
                    ),
                    center = center,
                    radius = sphereRadius,
                ),
                radius = sphereRadius,
                center = center,
            )

            repeat(12) { index ->
                drawArc(
                    color = Color.White.copy(alpha = 0.034f + (index % 3) * 0.012f),
                    startAngle = (index * 31f + state.textureFlow * 0.12f) % 360f,
                    sweepAngle = 30f + (index % 4) * 10f,
                    useCenter = false,
                    topLeft = Offset(
                        x = center.x - sphereRadius * (0.46f + index % 4 * 0.10f),
                        y = center.y - sphereRadius * 0.60f + index * sphereRadius * 0.062f,
                    ),
                    size = Size(
                        width = sphereRadius * (0.92f + index % 4 * 0.20f),
                        height = sphereRadius * 0.34f,
                    ),
                    style = Stroke(width = 0.7.dp.toPx()),
                )
            }

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.44f),
                        Color.Transparent,
                        starCyan.copy(alpha = 0.24f),
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.40f),
                    ),
                    center = center,
                ),
                radius = sphereRadius * 1.02f,
                center = center,
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round),
            )
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        starCyan.copy(alpha = 0.64f),
                        Color.White.copy(alpha = 0.96f),
                        Color.Transparent,
                        AccentLime.copy(alpha = 0.34f),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                radius = sphereRadius,
                center = center,
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = AccentLime.copy(alpha = 0.44f),
                startAngle = 62f + state.starFlowAngle * 0.12f,
                sweepAngle = 64f,
                useCenter = false,
                topLeft = Offset(center.x - sphereRadius, center.y - sphereRadius),
                size = Size(sphereRadius * 2f, sphereRadius * 2f),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round),
            )
        }

        Column(
            modifier = Modifier
                .size(diameter * 0.92f)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "物品总值", color = SoftWhite, fontSize = 18.sp)
                Icon(
                    imageVector = if (amountsVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (amountsVisible) "隐藏金额" else "显示金额",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onToggleAmountsVisible),
                )
            }
            Text(
                text = amountText,
                color = SoftWhite,
                fontSize = amountFontSize,
                lineHeight = (amountFontSize.value + 2).sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = SoftWhite.copy(alpha = 0.24f),
                        blurRadius = 10f,
                    ),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
            )
            Text(
                text = "持有日均成本",
                color = SoftWhite.copy(alpha = 0.86f),
                fontSize = 14.sp,
                lineHeight = 14.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF06101F).copy(alpha = 0.86f),
                        blurRadius = 8f,
                    ),
                ),
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = dailyCostText,
                color = AccentLime,
                fontSize = if (dailyCostText.length >= 11) 22.sp else 25.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = AccentLime.copy(alpha = 0.28f),
                        blurRadius = 10f,
                    ),
                ),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
