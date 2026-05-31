package com.example.assetstar.ui.category

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.domain.model.CategoryStats
import com.example.assetstar.ui.list.SortOption
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SpaceBlack
import com.example.assetstar.ui.theme.SpaceBlue
import com.example.assetstar.ui.theme.SpaceNavy
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.DateUtils
import com.example.assetstar.util.ImageUtils
import com.example.assetstar.util.MoneyFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryAssetsScreen(
    uiState: CategoryAssetsUiState,
    onBack: () -> Unit,
    onAddAsset: (AssetCategory) -> Unit,
    onAssetClick: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onStatusChange: (AssetStatus?) -> Unit,
    onSortChange: (SortOption) -> Unit,
) {
    val showSortMenu = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue))),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
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
                    Image(
                        painter = painterResource(uiState.categoryDisplaySettings.iconOf(uiState.category)),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f),
                    ) {
                        Text(
                            text = uiState.categoryDisplaySettings.nameOf(uiState.category),
                            color = SoftWhite,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = "共 ${uiState.categoryStats?.assetCount ?: 0} 件资产",
                            color = TextSecondary,
                            fontSize = 13.sp,
                        )
                    }
                    Box {
                        IconButton(onClick = { showSortMenu.value = true }) {
                            Icon(Icons.Outlined.Tune, null, tint = AccentCyan)
                        }
                        DropdownMenu(
                            expanded = showSortMenu.value,
                            onDismissRequest = { showSortMenu.value = false },
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        showSortMenu.value = false
                                        onSortChange(option)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            item {
                CategoryStatsHeader(
                    stats = uiState.categoryStats,
                    categoryName = uiState.categoryDisplaySettings.nameOf(uiState.category),
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("搜索${uiState.categoryDisplaySettings.nameOf(uiState.category)}资产") },
                    shape = RoundedCornerShape(18.dp),
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = uiState.selectedStatus == null,
                        onClick = { onStatusChange(null) },
                        label = { Text("全部状态") },
                    )
                    AssetStatus.entries.forEach { status ->
                        FilterChip(
                            selected = uiState.selectedStatus == status,
                            onClick = { onStatusChange(status) },
                            label = { Text(status.displayName) },
                        )
                    }
                }
            }

            if (uiState.isEmpty) {
                item {
                    CategoryEmptyState(
                        categoryName = uiState.categoryDisplaySettings.nameOf(uiState.category),
                        onAddAsset = { onAddAsset(uiState.category) },
                    )
                }
            } else {
                items(uiState.assets, key = { it.asset.id }) { item ->
                    CategoryAssetCard(
                        item = item,
                        onClick = { onAssetClick(item.asset.id) },
                    )
                }
                item { Spacer(modifier = Modifier.height(84.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { onAddAsset(uiState.category) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 22.dp),
            containerColor = AccentCyan,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
        }
    }
}

@Composable
private fun CategoryStatsHeader(
    stats: CategoryStats?,
    categoryName: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = PanelBlue.copy(alpha = 0.82f),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = categoryName,
                color = SoftWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
            )
            Text(
                text = "总价值 ${MoneyFormatter.format(stats?.totalPurchasePrice ?: 0.0)}",
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "日均成本 ${MoneyFormatter.formatDailyCost(stats?.totalDailyCost ?: 0.0)}",
                color = AccentCyan,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                modifier = Modifier.padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatusMini("使用中", stats?.inUseCount ?: 0)
                StatusMini("已退役", stats?.retiredCount ?: 0)
                StatusMini("已出售", stats?.soldCount ?: 0)
            }
            Text(
                text = "预计残值 ${MoneyFormatter.format(stats?.totalResidualValue ?: 0.0)}",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun StatusMini(label: String, count: Int) {
    Surface(
        color = SoftWhite.copy(alpha = 0.06f),
        shape = RoundedCornerShape(100.dp),
    ) {
        Text(
            text = "$label $count",
            color = SoftWhite,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun CategoryAssetCard(
    item: CategoryAssetListItem,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = 0.84f),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ImageUtils.hasImage(item.asset.imageUri)) {
                AsyncImage(
                    model = item.asset.imageUri,
                    contentDescription = item.asset.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(66.dp)
                        .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(18.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Image, contentDescription = null, tint = TextSecondary)
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .weight(1f),
            ) {
                Text(
                    text = item.asset.name,
                    color = SoftWhite,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${item.asset.status.displayName} · ${DateUtils.formatDate(item.asset.purchaseDate)}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 5.dp),
                )
                Text(
                    text = "购入 ${MoneyFormatter.format(item.asset.purchasePrice)} · 预计残值 ${MoneyFormatter.format(item.asset.estimatedResidualValue ?: 0.0)}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 5.dp),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${item.metrics.useDays} 天", color = SoftWhite, fontSize = 13.sp)
                Text(
                    text = MoneyFormatter.formatDailyCost(item.metrics.dailyCost),
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun CategoryEmptyState(
    categoryName: String,
    onAddAsset: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = PanelBlue.copy(alpha = 0.82f),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = AccentCyan)
            Text(
                text = "这里还没有资产",
                color = SoftWhite,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = "添加第一件「$categoryName」资产",
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp),
            )
            Surface(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable(onClick = onAddAsset),
                shape = RoundedCornerShape(100.dp),
                color = AccentCyan,
            ) {
                Text(
                    text = "添加资产",
                    color = SpaceBlack,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                )
            }
        }
    }
}

fun AssetCategory.icon(): ImageVector {
    return when (this) {
        AssetCategory.CASH -> Icons.AutoMirrored.Outlined.MenuBook
        AssetCategory.INVESTMENT -> Icons.AutoMirrored.Outlined.TrendingUp
        AssetCategory.LIVING -> Icons.Outlined.Apartment
        AssetCategory.TRANSPORT -> Icons.AutoMirrored.Outlined.DirectionsBike
        AssetCategory.HEALTH -> Icons.Outlined.HealthAndSafety
        AssetCategory.LEISURE -> Icons.Outlined.LocalCafe
        AssetCategory.DAILY -> Icons.Outlined.ShoppingBag
        AssetCategory.DIGITAL -> Icons.Outlined.PhoneAndroid
        AssetCategory.ALL,
        AssetCategory.OTHER -> Icons.Outlined.Category
    }
}
