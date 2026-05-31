package com.example.assetstar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary

@Composable
fun OrbitCategoryNode(
    title: String,
    value: String,
    @DrawableRes iconRes: Int,
    accentColor: Color,
    nodeSize: Dp,
    floatProgress: Float,
    satelliteProgress: Float,
    phase: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "nodeScale",
    )
    val floatOffset = kotlin.math.sin((floatProgress + phase) * Math.PI * 2).toFloat() * 2.2f
    val glowShift = 0.92f + ((kotlin.math.sin(Math.toRadians((satelliteProgress + phase * 360f).toDouble())) + 1f) / 2f).toFloat() * 0.18f

    Box(
        modifier = modifier
            .size(nodeSize + 8.dp)
            .offset(y = floatOffset.dp)
            .scale(pressScale),
        contentAlignment = Alignment.Center,
    ) {
        PlanetSphere(
            planetColor = accentColor,
            accentColor = accentColor,
            pulse = glowShift - 0.92f,
            phase = phase,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .size(nodeSize * 0.80f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CategoryIconBadge(
                    iconRes = iconRes,
                    title = title,
                    accentColor = accentColor,
                )
                Text(
                    text = title,
                    color = SoftWhite,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                )
                Text(
                    text = value,
                    color = TextSecondary.copy(alpha = 0.94f),
                    fontSize = 8.sp,
                    lineHeight = 8.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp),
                )
            }
        }

    }
}

@Composable
private fun CategoryIconBadge(
    @DrawableRes iconRes: Int,
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(iconRes)
    Box(
        modifier = modifier.size(23.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF06101F).copy(alpha = 0.82f),
                        Color(0xFF06101F).copy(alpha = 0.54f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.24f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius * 0.86f,
                ),
                radius = radius * 0.86f,
                center = center,
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(SoftWhite.copy(alpha = 0.58f)),
            modifier = Modifier.size(23.dp),
        )
        Image(
            painter = painter,
            contentDescription = title,
            modifier = Modifier.size(18.dp),
        )
    }
}
