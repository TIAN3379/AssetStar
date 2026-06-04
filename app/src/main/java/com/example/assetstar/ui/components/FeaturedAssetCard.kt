package com.example.assetstar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.AccentYellow
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.AssetVisuals
import com.example.assetstar.util.DateUtils
import com.example.assetstar.util.ImageUtils
import com.example.assetstar.util.MoneyFormatter

@Composable
fun FeaturedAssetCarousel(
    assets: List<Asset>,
    metricsByAssetId: Map<Long, AssetMetrics>,
    onAssetClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { assets.size })
    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val asset = assets[page]
            val metrics = metricsByAssetId[asset.id] ?: return@HorizontalPager
            FeaturedAssetCard(
                asset = asset,
                metrics = metrics,
                currentPage = pagerState.currentPage,
                pageCount = assets.size,
                onClick = { onAssetClick(asset.id) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun FeaturedAssetCard(
    asset: Asset,
    metrics: AssetMetrics,
    currentPage: Int = 0,
    pageCount: Int = 1,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = androidx.compose.animation.core.tween(160),
        label = "featuredScale",
    )
    val starLevel = AssetVisuals.starLevel(asset)
    val highValue = AssetVisuals.isHighValue(asset)
    val cherished = AssetVisuals.isCherished(asset)
    val completion = AssetVisuals.completionPercent(asset)
    val highlightColor = if (starLevel >= 5) AccentYellow else AccentLime

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xD6134A48),
                        Color(0xD40E3348),
                        PanelBlue.copy(alpha = 0.96f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (highValue) {
                        listOf(
                            highlightColor.copy(alpha = 0.62f),
                            SoftWhite.copy(alpha = 0.18f),
                            AccentCyan.copy(alpha = 0.18f),
                        )
                    } else {
                        listOf(
                            AccentCyan.copy(alpha = 0.20f),
                            SoftWhite.copy(alpha = 0.14f),
                            AccentCyan.copy(alpha = 0.10f),
                        )
                    },
                ),
                shape = RoundedCornerShape(28.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        if (highValue) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                highlightColor.copy(alpha = 0.14f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(top = 1.dp, start = 18.dp, end = 18.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            SoftWhite.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                    ),
                    shape = RoundedCornerShape(100.dp),
                )
                .padding(vertical = 0.5.dp),
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = "我的资产 · ${asset.status.displayName}",
                color = TextSecondary,
                fontSize = 13.sp,
            )
                Row(
                    modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AssetImage(asset = asset)

                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        text = asset.name,
                        color = SoftWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        lineHeight = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = AssetVisuals.starText(starLevel),
                            color = highlightColor,
                            fontSize = 9.sp,
                            maxLines = 1,
                        )
                        if (cherished) {
                            Text(
                                text = "珍藏",
                                color = AccentLime,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Text(
                            text = "资料 $completion%",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = "购入价 ${MoneyFormatter.format(asset.purchasePrice)}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${asset.status.displayName} · ${DateUtils.formatDate(asset.purchaseDate)} 购入",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(SoftWhite.copy(alpha = 0.10f)),
                )

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(84.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(text = "日均成本", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = MoneyFormatter.formatDailyCost(metrics.dailyCost),
                        color = AccentCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        text = "预计残值 ${MoneyFormatter.format(asset.estimatedResidualValue ?: 0.0)}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SoftWhite.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = SoftWhite.copy(alpha = 0.76f),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pageCount.coerceAtLeast(1)) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == 0) 7.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = if (index == currentPage) {
                                        listOf(AccentCyan.copy(alpha = 0.88f), AccentCyan.copy(alpha = 0.22f))
                                    } else {
                                        listOf(SoftWhite.copy(alpha = 0.30f), SoftWhite.copy(alpha = 0.10f))
                                    },
                                ),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetImage(asset: Asset) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = 0.24f),
                        SoftWhite.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = 0.56f),
                        SoftWhite.copy(alpha = 0.14f),
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (ImageUtils.hasImage(asset.imageUri)) {
            AsyncImage(
                model = asset.imageUri,
                contentDescription = asset.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}
