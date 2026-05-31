package com.example.assetstar.ui.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SpaceBlack
import com.example.assetstar.ui.theme.SpaceBlue
import com.example.assetstar.ui.theme.SpaceNavy
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.MoneyFormatter

@Composable
fun CategoryOverviewScreen(
    uiState: CategoryOverviewUiState,
    onBack: () -> Unit,
    onCategoryClick: (AssetCategory) -> Unit,
    onSortChange: (CategorySortType) -> Unit,
) {
    val showSortMenu = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue)))
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = SoftWhite)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "分类总览",
                        color = SoftWhite,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = "按${uiState.sortType.label}排序",
                        color = TextSecondary,
                        fontSize = 13.sp,
                    )
                }
                androidx.compose.foundation.layout.Box {
                    IconButton(onClick = { showSortMenu.value = true }) {
                        Icon(Icons.Outlined.Tune, null, tint = AccentCyan)
                    }
                    DropdownMenu(
                        expanded = showSortMenu.value,
                        onDismissRequest = { showSortMenu.value = false },
                    ) {
                        CategorySortType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                onClick = {
                                    showSortMenu.value = false
                                    onSortChange(type)
                                },
                            )
                        }
                    }
                }
            }
        }

        if (uiState.categoryStatsList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = PanelBlue.copy(alpha = 0.82f),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("还没有分类数据", color = SoftWhite)
                        Text("新增资产后，这里会显示各分类统计。", color = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        } else {
            items(uiState.categoryStatsList, key = { it.category.storageValue }) { item ->
                CategoryOverviewCard(
                    stats = item,
                    categoryName = uiState.categoryDisplaySettings.nameOf(item.category),
                    iconRes = uiState.categoryDisplaySettings.iconOf(item.category),
                    onClick = { onCategoryClick(item.category) },
                )
            }
            item { Spacer(modifier = Modifier.height(84.dp)) }
        }
    }
}

@Composable
private fun CategoryOverviewCard(
    stats: CategoryStats,
    categoryName: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    val muted = stats.assetCount == 0
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = if (muted) 0.48f else 0.82f),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(18.dp),
                color = AccentCyan.copy(alpha = if (muted) 0.08f else 0.16f),
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
            ) {
                Text(
                    text = categoryName,
                    color = if (muted) TextSecondary else SoftWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                )
                Text(
                    text = "${stats.assetCount} 件资产 · 使用中 ${stats.inUseCount}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = MoneyFormatter.format(stats.totalPurchasePrice),
                    color = if (muted) TextSecondary else SoftWhite,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = MoneyFormatter.formatDailyCost(stats.totalDailyCost),
                    color = if (muted) TextSecondary else AccentCyan,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
        }
    }
}
