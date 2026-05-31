package com.example.assetstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.StatusStat
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentYellow
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.MoneyFormatter

@Composable
fun StatusSummaryBar(
    stats: List<StatusStat>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PanelBlue.copy(alpha = 0.88f),
                        Color(0xD10B1E2D),
                        PanelBlue.copy(alpha = 0.84f),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SoftWhite.copy(alpha = 0.16f),
                        AccentCyan.copy(alpha = 0.08f),
                        SoftWhite.copy(alpha = 0.10f),
                    ),
                ),
                shape = RoundedCornerShape(22.dp),
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stats.forEachIndexed { index, item ->
            StatusCell(
                stat = item,
                modifier = Modifier.weight(1f),
            )
            if (index != stats.lastIndex) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(SoftWhite.copy(alpha = 0.08f)),
                )
            }
        }
    }
}

@Composable
private fun StatusCell(
    stat: StatusStat,
    modifier: Modifier = Modifier,
) {
    val accent = when (stat.status) {
        AssetStatus.IN_USE -> AccentCyan
        AssetStatus.RETIRED -> AccentYellow
        AssetStatus.SOLD -> SoftWhite.copy(alpha = 0.72f)
    }
    val valueText = if (stat.status == AssetStatus.SOLD) {
        MoneyFormatter.format(stat.recoveredValue)
    } else {
        MoneyFormatter.format(stat.totalValue)
    }

    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.94f),
                                accent.copy(alpha = 0.34f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
            Text(
                text = "${stat.status.displayName}  ${stat.count}",
                color = SoftWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            text = valueText,
            color = TextSecondary.copy(alpha = 0.96f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 5.dp),
        )
    }
}
