package com.example.assetstar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary

@Composable
fun OrbitNode(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    badgeCount: Int = 0,
    nodeSize: Dp = 112.dp,
    modifier: Modifier = Modifier,
) {
    val compactNode = nodeSize <= 92.dp
    val iconSize = if (compactNode) 20.dp else 24.dp
    val titleFontSize = if (compactNode) 13.sp else 14.sp
    val valueFontSize = if (compactNode) 12.sp else 13.sp
    val horizontalPadding = if (compactNode) 8.dp else 10.dp
    val verticalPadding = if (compactNode) 10.dp else 12.dp

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .size(nodeSize)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.28f),
                            Color(0xB0142234),
                            Color(0x880A1627),
                        ),
                    ),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.95f),
                            accentColor.copy(alpha = 0.18f),
                        ),
                    ),
                    shape = CircleShape,
                )
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(iconSize),
            )
            Text(
                text = title,
                color = SoftWhite,
                textAlign = TextAlign.Center,
                fontSize = titleFontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = if (compactNode) 8.dp else 10.dp),
            )
            Text(
                text = value,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = valueFontSize,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        if (badgeCount > 0) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(accentColor)
                    .padding(horizontal = 7.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
