package com.example.assetstar.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.assetstar.R
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryDisplaySettings
import com.example.assetstar.ui.components.FeaturedAssetCarousel
import com.example.assetstar.ui.components.FullScreenStarBackground
import com.example.assetstar.ui.components.OrbitNodeSpec
import com.example.assetstar.ui.components.PreviewAsset
import com.example.assetstar.ui.components.PreviewAsset2
import com.example.assetstar.ui.components.PreviewMetrics
import com.example.assetstar.ui.components.PreviewMetrics2
import com.example.assetstar.ui.components.PreviewStats
import com.example.assetstar.ui.components.StarMapBottomBar
import com.example.assetstar.ui.components.StarMapSection
import com.example.assetstar.ui.components.StatusSummaryBar
import com.example.assetstar.ui.components.rememberGalaxyAnimationState
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentBlueSoft
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.AccentPurple
import com.example.assetstar.ui.theme.AccentTeal
import com.example.assetstar.ui.theme.AccentYellow
import com.example.assetstar.ui.theme.AssetStarTheme
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onCategoryClick: (AssetCategory) -> Unit,
    onAssetClick: (Long) -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    var amountsVisible by rememberSaveable { mutableStateOf(true) }
    var previousAssetStatuses by remember { mutableStateOf<Map<Long, AssetStatus>?>(null) }
    var visualEvent by remember { mutableStateOf<HomeVisualEvent?>(null) }
    var visualEventNonce by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { visible = true }
    LaunchedEffect(uiState.assets) {
        val currentStatuses = uiState.assets.associate { it.id to it.status }
        val previous = previousAssetStatuses
        if (previous != null) {
            val hasRetired = uiState.assets.any { asset ->
                previous[asset.id] != null &&
                    previous[asset.id] != AssetStatus.RETIRED &&
                    asset.status == AssetStatus.RETIRED
            }
            val hasSold = uiState.assets.any { asset ->
                previous[asset.id] != null &&
                    previous[asset.id] != AssetStatus.SOLD &&
                    asset.status == AssetStatus.SOLD
            }
            val type = when {
                hasSold -> HomeVisualEventType.SOLD_SHATTER
                hasRetired -> HomeVisualEventType.RETIRED_FADE
                else -> null
            }
            if (type != null) {
                visualEventNonce += 1
                visualEvent = HomeVisualEvent(type = type, nonce = visualEventNonce)
            }
        }
        previousAssetStatuses = currentStatuses
    }
    val galaxyAnimationState = rememberGalaxyAnimationState()
    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "headerAlpha",
    )
    val headerOffset by animateFloatAsState(
        targetValue = if (visible) 0f else -8f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "headerOffset",
    )
    val starAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 720, delayMillis = 80, easing = FastOutSlowInEasing),
        label = "starAlpha",
    )
    val starScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.96f,
        animationSpec = tween(durationMillis = 720, delayMillis = 80, easing = FastOutSlowInEasing),
        label = "starScale",
    )
    val summaryAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 180, easing = FastOutSlowInEasing),
        label = "summaryAlpha",
    )
    val summaryOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 8f,
        animationSpec = tween(durationMillis = 600, delayMillis = 180, easing = FastOutSlowInEasing),
        label = "summaryOffset",
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 760, delayMillis = 240, easing = FastOutSlowInEasing),
        label = "cardAlpha",
    )
    val cardOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 12f,
        animationSpec = tween(durationMillis = 760, delayMillis = 240, easing = FastOutSlowInEasing),
        label = "cardOffset",
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        FullScreenStarBackground(
            modifier = Modifier.fillMaxSize(),
            animationState = galaxyAnimationState,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(top = 4.dp),
        ) {
            HeaderSection(
                onSearchClick = onSearchClick,
                onAddClick = onAddClick,
                onAnalysisClick = onAnalysisClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .graphicsLayer {
                        alpha = headerAlpha
                        translationY = headerOffset.dp.toPx()
                    },
            )

            StarMapSection(
                specs = orbitSpecs(uiState.categoryDisplaySettings),
                categoryStats = uiState.categoryStats,
                assets = uiState.assets,
                totalAssetValue = uiState.stats.totalAssetValue,
                totalDailyCost = uiState.stats.totalDailyCost,
                amountsVisible = amountsVisible,
                onToggleAmountsVisible = { amountsVisible = !amountsVisible },
                onCategoryClick = onCategoryClick,
                animationState = galaxyAnimationState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(492.dp)
                    .offset(y = (-4).dp)
                    .graphicsLayer {
                        alpha = starAlpha
                        scaleX = starScale
                        scaleY = starScale
                    },
            )

            StatusSummaryBar(
                stats = uiState.stats.statusStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .height(64.dp)
                    .graphicsLayer {
                        alpha = summaryAlpha
                        translationY = summaryOffset.dp.toPx()
                    }
                    .offset(y = (-4).dp),
            )

            if (uiState.featuredAssets.isNotEmpty()) {
                FeaturedAssetCarousel(
                    assets = uiState.featuredAssets,
                    metricsByAssetId = uiState.featuredMetrics,
                    onAssetClick = onAssetClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .padding(top = 10.dp)
                        .height(132.dp)
                        .graphicsLayer {
                            alpha = cardAlpha
                            translationY = cardOffset.dp.toPx()
                        },
                )
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        HomeAssetEventEffect(
            event = visualEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private enum class HomeVisualEventType {
    RETIRED_FADE,
    SOLD_SHATTER,
}

private data class HomeVisualEvent(
    val type: HomeVisualEventType,
    val nonce: Int,
)

@Composable
private fun HomeAssetEventEffect(
    event: HomeVisualEvent?,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(event?.nonce) {
        if (event != null) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1_300, easing = LinearEasing),
            )
        }
    }
    val currentEvent = event ?: return
    val value = progress.value
    if (value >= 1f) return

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height * 0.43f)
        when (currentEvent.type) {
            HomeVisualEventType.RETIRED_FADE -> {
                val alpha = (1f - value).coerceIn(0f, 1f)
                val radius = 34.dp.toPx() * (1f + value)
                drawCircle(
                    color = SoftWhite.copy(alpha = 0.16f * alpha),
                    radius = radius,
                    center = Offset(size.width * 0.28f, size.height * 0.52f),
                    style = Stroke(width = 1.dp.toPx()),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentCyan.copy(alpha = 0.28f * alpha),
                            Color.Transparent,
                        ),
                    ),
                    radius = radius * 0.70f,
                    center = Offset(size.width * 0.28f, size.height * 0.52f),
                )
            }

            HomeVisualEventType.SOLD_SHATTER -> {
                val alpha = (1f - value).coerceIn(0f, 1f)
                repeat(10) { index ->
                    val angle = (index * 36f / 180f * PI).toFloat()
                    val distance = value * 54.dp.toPx()
                    val p = Offset(
                        x = center.x + cos(angle) * distance,
                        y = center.y + sin(angle) * distance,
                    )
                    drawCircle(
                        color = if (index % 2 == 0) AccentLime.copy(alpha = alpha) else AccentCyan.copy(alpha = alpha),
                        radius = (2.6f - (index % 3) * 0.35f).dp.toPx(),
                        center = p,
                    )
                }
                drawCircle(
                    color = SoftWhite.copy(alpha = 0.16f * alpha),
                    radius = 26.dp.toPx() * (1f + value * 0.8f),
                    center = center,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val dataInteraction = remember { MutableInteractionSource() }
    val dataPressed by dataInteraction.collectIsPressedAsState()
    val dataScale by animateFloatAsState(
        targetValue = if (dataPressed) 0.97f else 1f,
        animationSpec = tween(140),
        label = "dataScale",
    )
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 18.dp),
        ) {
            Text(
                text = "资产星图",
                color = SoftWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = SoftWhite.copy(alpha = 0.16f),
                        blurRadius = 8f,
                    ),
                ),
            )
            Text(
                text = "掌控当下 · 规划未来",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeaderCircleButton(icon = Icons.Outlined.Search, onClick = onSearchClick)
            HeaderCircleButton(
                icon = Icons.Outlined.Add,
                onClick = onAddClick,
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 74.dp, end = 28.dp)
                .size(width = 122.dp, height = 34.dp)
                .graphicsLayer {
                    scaleX = dataScale
                    scaleY = dataScale
                }
                .clickable(
                    interactionSource = dataInteraction,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAnalysisClick()
                    },
                ),
            color = PanelBlue.copy(alpha = 0.42f),
            shape = RoundedCornerShape(17.dp),
            border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.14f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Insights,
                    contentDescription = null,
                    tint = SoftWhite.copy(alpha = 0.82f),
                    modifier = Modifier.size(16.dp),
                )
                Text(text = "物品总览", color = SoftWhite, fontSize = 13.sp)
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun HeaderCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(140),
        label = "headerButtonScale",
    )
    Surface(
        modifier = modifier
            .size(46.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        color = PanelBlue.copy(alpha = 0.42f),
        shape = CircleShape,
        border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.18f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SoftWhite,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private fun orbitSpecs(settings: CategoryDisplaySettings = CategoryDisplaySettings()): List<OrbitNodeSpec> {
    return listOf(
        OrbitNodeSpec(
            category = AssetCategory.CASH,
            title = settings.nameOf(AssetCategory.CASH),
            iconRes = settings.iconOf(AssetCategory.CASH),
            accent = SoftWhite,
            angleDegrees = -90f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.INVESTMENT,
            title = settings.nameOf(AssetCategory.INVESTMENT),
            iconRes = settings.iconOf(AssetCategory.INVESTMENT),
            accent = AccentYellow,
            angleDegrees = -38f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.TRANSPORT,
            title = settings.nameOf(AssetCategory.TRANSPORT),
            iconRes = settings.iconOf(AssetCategory.TRANSPORT),
            accent = Color(0xFF32E6B8),
            angleDegrees = 8f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.DAILY,
            title = settings.nameOf(AssetCategory.DAILY),
            iconRes = settings.iconOf(AssetCategory.DAILY),
            accent = Color(0xFFFFD166),
            angleDegrees = 48f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.LEISURE,
            title = settings.nameOf(AssetCategory.LEISURE),
            iconRes = settings.iconOf(AssetCategory.LEISURE),
            accent = AccentPurple,
            angleDegrees = 90f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.HEALTH,
            title = settings.nameOf(AssetCategory.HEALTH),
            iconRes = settings.iconOf(AssetCategory.HEALTH),
            accent = AccentPurple,
            angleDegrees = 132f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.LIVING,
            title = settings.nameOf(AssetCategory.LIVING),
            iconRes = settings.iconOf(AssetCategory.LIVING),
            accent = Color(0xFF6D7DFF),
            angleDegrees = 178f,
        ),
        OrbitNodeSpec(
            category = AssetCategory.DIGITAL,
            title = settings.nameOf(AssetCategory.DIGITAL),
            iconRes = settings.iconOf(AssetCategory.DIGITAL),
            accent = AccentCyan,
            angleDegrees = 218f,
        ),
    )
}

@Preview(name = "Samsung S20 Ultra Home", widthDp = 411, heightDp = 914, showBackground = true)
@Composable
private fun HomeScreenS20UltraPreview() {
    AssetStarTheme {
        Scaffold(
            bottomBar = {
                StarMapBottomBar(
                    currentRoute = "home",
                    onNavigate = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                HomeScreen(
                    uiState = HomeUiState(
                        stats = PreviewStats,
                        categoryStats = com.example.assetstar.domain.calculator.AssetCalculator()
                            .buildDetailedCategoryStats(listOf(PreviewAsset2, PreviewAsset)),
                        featuredAssets = listOf(PreviewAsset2, PreviewAsset),
                        featuredMetrics = mapOf(
                            PreviewAsset.id to PreviewMetrics,
                            PreviewAsset2.id to PreviewMetrics2,
                        ),
                        isEmpty = false,
                    ),
                    onSearchClick = {},
                    onAddClick = {},
                    onAnalysisClick = {},
                    onCategoryClick = {},
                    onAssetClick = {},
                )
            }
        }
    }
}
