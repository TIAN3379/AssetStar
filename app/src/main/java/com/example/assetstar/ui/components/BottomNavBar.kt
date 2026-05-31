package com.example.assetstar.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.R
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.PanelBlueStrong
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary

data class BottomDestination(
    val route: String,
    val label: String,
    @param:DrawableRes val iconRes: Int,
)

val BottomDestinations = listOf(
    BottomDestination("home", "星图", R.drawable.ic_nav_home),
    BottomDestination("list", "明细", R.drawable.ic_nav_detail),
    BottomDestination("analysis", "分析", R.drawable.ic_nav_analysis),
    BottomDestination("profile", "我的", R.drawable.ic_nav_profile),
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "bottomBar")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "centerPulse",
    )

    val leftItems = BottomDestinations.take(2)
    val rightItems = BottomDestinations.drop(2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(38.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PanelBlueStrong.copy(alpha = 0.96f),
                            PanelBlue.copy(alpha = 0.94f),
                            Color(0xF1050B18),
                        ),
                    ),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SoftWhite.copy(alpha = 0.18f),
                            SoftWhite.copy(alpha = 0.04f),
                        ),
                    ),
                    shape = RoundedCornerShape(38.dp),
                ),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val notchCenter = w / 2f
                val top = 0f
                val path = Path().apply {
                    moveTo(0f, h)
                    lineTo(0f, top + 32.dp.toPx())
                    quadraticTo(0f, top, 34.dp.toPx(), top)
                    lineTo(notchCenter - 76.dp.toPx(), top)
                    cubicTo(
                        notchCenter - 38.dp.toPx(), top,
                        notchCenter - 32.dp.toPx(), 14.dp.toPx(),
                        notchCenter,
                        14.dp.toPx(),
                    )
                    cubicTo(
                        notchCenter + 32.dp.toPx(), 14.dp.toPx(),
                        notchCenter + 38.dp.toPx(), top,
                        notchCenter + 76.dp.toPx(), top,
                    )
                    lineTo(w - 34.dp.toPx(), top)
                    quadraticTo(w, top, w, top + 32.dp.toPx())
                    lineTo(w, h)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color.Transparent,
                )
                drawLine(
                    color = SoftWhite.copy(alpha = 0.14f),
                    start = androidx.compose.ui.geometry.Offset(28.dp.toPx(), 1.dp.toPx()),
                    end = androidx.compose.ui.geometry.Offset(notchCenter - 78.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
                drawLine(
                    color = SoftWhite.copy(alpha = 0.14f),
                    start = androidx.compose.ui.geometry.Offset(notchCenter + 78.dp.toPx(), 1.dp.toPx()),
                    end = androidx.compose.ui.geometry.Offset(w - 28.dp.toPx(), 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leftItems.forEach { destination ->
                        BottomNavItem(
                            destination = destination,
                            selected = currentRoute == destination.route,
                            onClick = { onNavigate(destination.route) },
                        )
                    }
                }

                SpacerSlot()

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    rightItems.forEach { destination ->
                        BottomNavItem(
                            destination = destination,
                            selected = currentRoute == destination.route,
                            onClick = { onNavigate(destination.route) },
                        )
                    }
                }
            }
        }

        val centerInteraction = remember { MutableInteractionSource() }
        val centerPressed by centerInteraction.collectIsPressedAsState()
        val centerScale by animateFloatAsState(
            targetValue = if (centerPressed) 0.96f else 1f,
            animationSpec = tween(160),
            label = "centerScale",
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 2.dp)
                .size(74.dp)
                .scale(centerScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SoftWhite.copy(alpha = 0.22f * pulse),
                            AccentLime.copy(alpha = 0.28f * pulse),
                            AccentCyan.copy(alpha = 0.14f * pulse),
                            Color.Transparent,
                        ),
                    ),
                )
                .clickable(
                    interactionSource = centerInteraction,
                    indication = null,
                ) { onNavigate("home") },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF203424),
                                Color(0xFF102033),
                            ),
                        ),
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SoftWhite.copy(alpha = 0.96f),
                                AccentLime.copy(alpha = 0.70f),
                                AccentCyan.copy(alpha = 0.24f),
                            ),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_nav_center),
                    contentDescription = "星图",
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@Composable
private fun SpacerSlot() {
    Box(modifier = Modifier.width(86.dp))
}

@Composable
private fun BottomNavItem(
    destination: BottomDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(140),
        label = "navScale",
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BottomNavIcon(
            iconRes = destination.iconRes,
            label = destination.label,
            selected = selected,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = destination.label,
            color = if (selected) AccentCyan else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun BottomNavIcon(
    @DrawableRes iconRes: Int,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selected) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentCyan.copy(alpha = 0.34f),
                            AccentCyan.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                        center = c,
                        radius = size.minDimension * 0.62f,
                    ),
                    radius = size.minDimension * 0.62f,
                    center = c,
                )
            }
        }
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            colorFilter = if (selected) null else ColorFilter.tint(TextSecondary.copy(alpha = 0.80f)),
            modifier = Modifier.size(if (selected) 31.dp else 29.dp),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStarMapNavIcon(
    color: Color,
    glowColor: Color,
    selected: Boolean,
) {
    val c = Offset(size.width / 2f, size.height / 2f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor.copy(alpha = if (selected) 0.38f else 0.12f), Color.Transparent),
            center = c,
            radius = size.minDimension * 0.58f,
        ),
        radius = size.minDimension * 0.58f,
        center = c,
    )
    listOf(0.30f, 0.47f).forEachIndexed { index, scale ->
        drawCircle(
            color = color.copy(alpha = if (index == 0) 0.75f else 0.42f),
            radius = size.minDimension * scale,
            center = c,
            style = Stroke(width = 1.35.dp.toPx()),
        )
    }
    drawCircle(color = color.copy(alpha = 0.78f), radius = 2.2.dp.toPx(), center = Offset(c.x + size.width * 0.31f, c.y - size.height * 0.22f))
    drawCircle(color = color.copy(alpha = 0.62f), radius = 1.8.dp.toPx(), center = Offset(c.x - size.width * 0.24f, c.y + size.height * 0.20f))
    val star = Path().apply {
        moveTo(c.x, c.y - 8.dp.toPx())
        lineTo(c.x + 2.4.dp.toPx(), c.y - 2.4.dp.toPx())
        lineTo(c.x + 8.dp.toPx(), c.y)
        lineTo(c.x + 2.4.dp.toPx(), c.y + 2.4.dp.toPx())
        lineTo(c.x, c.y + 8.dp.toPx())
        lineTo(c.x - 2.4.dp.toPx(), c.y + 2.4.dp.toPx())
        lineTo(c.x - 8.dp.toPx(), c.y)
        lineTo(c.x - 2.4.dp.toPx(), c.y - 2.4.dp.toPx())
        close()
    }
    drawPath(star, color = color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDetailNavIcon(
    color: Color,
    glowColor: Color,
    selected: Boolean,
) {
    val stroke = Stroke(width = 1.8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
    drawRoundRect(
        color = glowColor.copy(alpha = 0.18f),
        topLeft = Offset(size.width * 0.30f, size.height * 0.14f),
        size = Size(size.width * 0.46f, size.height * 0.56f),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
    )
    drawRoundRect(
        color = color.copy(alpha = 0.90f),
        topLeft = Offset(size.width * 0.22f, size.height * 0.25f),
        size = Size(size.width * 0.46f, size.height * 0.58f),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
        style = stroke,
    )
    repeat(3) { index ->
        val y = size.height * (0.40f + index * 0.14f)
        drawCircle(
            color = (if (selected) AccentCyan else color).copy(alpha = if (selected) 0.85f else 0.58f),
            radius = 1.9.dp.toPx(),
            center = Offset(size.width * 0.33f, y),
        )
        drawLine(color = color.copy(alpha = 0.72f), start = Offset(size.width * 0.42f, y), end = Offset(size.width * 0.58f, y), strokeWidth = 1.5.dp.toPx())
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAnalysisNavIcon(
    color: Color,
    glowColor: Color,
    selected: Boolean,
) {
    val c = Offset(size.width / 2f, size.height / 2f)
    drawCircle(
        brush = Brush.radialGradient(listOf(glowColor.copy(alpha = 0.20f), Color.Transparent), c, size.minDimension * 0.52f),
        radius = size.minDimension * 0.52f,
        center = c,
    )
    drawArc(color = color.copy(alpha = 0.70f), startAngle = 200f, sweepAngle = 235f, useCenter = false, topLeft = Offset(size.width * 0.15f, size.height * 0.15f), size = Size(size.width * 0.70f, size.height * 0.70f), style = Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawArc(color = (if (selected) AccentCyan else color).copy(alpha = if (selected) 0.82f else 0.34f), startAngle = -78f, sweepAngle = 95f, useCenter = false, topLeft = Offset(size.width * 0.15f, size.height * 0.15f), size = Size(size.width * 0.70f, size.height * 0.70f), style = Stroke(width = 2.2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
    val p1 = Offset(size.width * 0.28f, size.height * 0.62f)
    val p2 = Offset(size.width * 0.43f, size.height * 0.46f)
    val p3 = Offset(size.width * 0.56f, size.height * 0.55f)
    val p4 = Offset(size.width * 0.73f, size.height * 0.34f)
    drawLine(color = color, start = p1, end = p2, strokeWidth = 2.3.dp.toPx())
    drawLine(color = color, start = p2, end = p3, strokeWidth = 2.3.dp.toPx())
    drawLine(color = color, start = p3, end = p4, strokeWidth = 2.3.dp.toPx())
    drawCircle(color = (if (selected) AccentCyan else color).copy(alpha = if (selected) 1f else 0.55f), radius = 2.5.dp.toPx(), center = p4)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProfileNavIcon(
    color: Color,
    glowColor: Color,
    selected: Boolean,
) {
    val c = Offset(size.width / 2f, size.height / 2f)
    drawArc(color = (if (selected) AccentCyan else color).copy(alpha = if (selected) 0.58f else 0.30f), startAngle = -42f, sweepAngle = 132f, useCenter = false, topLeft = Offset(size.width * 0.20f, size.height * 0.12f), size = Size(size.width * 0.60f, size.height * 0.60f), style = Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawArc(color = glowColor.copy(alpha = 0.34f), startAngle = 188f, sweepAngle = 105f, useCenter = false, topLeft = Offset(size.width * 0.20f, size.height * 0.12f), size = Size(size.width * 0.60f, size.height * 0.60f), style = Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.96f), color.copy(alpha = 0.70f), Color.Transparent),
            center = Offset(c.x, size.height * 0.34f),
            radius = size.minDimension * 0.20f,
        ),
        radius = size.minDimension * 0.14f,
        center = Offset(c.x, size.height * 0.34f),
    )
    drawArc(color = color.copy(alpha = 0.88f), startAngle = 198f, sweepAngle = 144f, useCenter = false, topLeft = Offset(size.width * 0.25f, size.height * 0.50f), size = Size(size.width * 0.50f, size.height * 0.46f), style = Stroke(width = 2.1.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

@Composable
private fun CenterPlanetNavIcon(
    modifier: Modifier = Modifier,
    pulse: Float,
) {
    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension * 0.28f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentCyan.copy(alpha = 0.92f), AccentLime.copy(alpha = 0.70f), Color.Transparent),
                center = c,
                radius = r * 2.2f * pulse,
            ),
            radius = r * 2.2f * pulse,
            center = c,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.92f), AccentCyan, Color(0xFF0B6A59)),
                center = Offset(c.x - r * 0.25f, c.y - r * 0.30f),
                radius = r * 1.45f,
            ),
            radius = r,
            center = c,
        )
        rotate(-18f, c) {
            drawOval(
                color = SoftWhite.copy(alpha = 0.92f),
                topLeft = Offset(c.x - r * 1.55f, c.y - r * 0.42f),
                size = Size(r * 3.10f, r * 0.84f),
                style = Stroke(width = 2.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
            drawArc(
                color = AccentLime.copy(alpha = 0.82f),
                startAngle = 18f,
                sweepAngle = 138f,
                useCenter = false,
                topLeft = Offset(c.x - r * 1.55f, c.y - r * 0.42f),
                size = Size(r * 3.10f, r * 0.84f),
                style = Stroke(width = 2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
        }
    }
}
