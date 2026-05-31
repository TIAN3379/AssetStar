package com.example.assetstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.assetstar.domain.model.Asset
import com.example.assetstar.domain.model.AssetMetrics
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.DateUtils
import com.example.assetstar.util.ImageUtils
import com.example.assetstar.util.MoneyFormatter

@Composable
fun AssetCard(
    asset: Asset,
    metrics: AssetMetrics,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = PanelBlue.copy(alpha = 0.82f),
        tonalElevation = 0.dp,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AccentCyan.copy(alpha = 0.16f),
                            PanelBlue.copy(alpha = 0.25f),
                        ),
                    ),
                )
                .padding(18.dp),
        ) {
            Text(
                text = "我的资产 · ${asset.status.displayName}",
                color = TextSecondary,
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (ImageUtils.hasImage(asset.imageUri)) {
                    AsyncImage(
                        model = asset.imageUri,
                        contentDescription = asset.name,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(SoftWhite.copy(alpha = 0.08f)),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(SoftWhite.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = asset.name,
                        color = SoftWhite,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = asset.category.displayName,
                        color = AccentCyan,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "${DateUtils.formatDate(asset.purchaseDate)} 购入",
                        color = TextSecondary,
                    )
                    Text(
                        text = "购入价 ${MoneyFormatter.format(asset.purchasePrice)}",
                        color = SoftWhite,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "日均成本",
                        color = TextSecondary,
                    )
                    Text(
                        text = MoneyFormatter.formatDailyCost(metrics.dailyCost),
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "预计残值",
                        color = TextSecondary,
                    )
                    Text(
                        text = MoneyFormatter.format(asset.estimatedResidualValue ?: 0.0),
                        color = SoftWhite,
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF050B18)
@Composable
private fun AssetCardPreview() {
    AssetCard(
        asset = PreviewAsset,
        metrics = PreviewMetrics,
        onClick = {},
    )
}
