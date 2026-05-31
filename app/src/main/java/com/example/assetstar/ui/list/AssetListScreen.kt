package com.example.assetstar.ui.list

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetStatus
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
fun AssetListScreen(
    uiState: AssetListUiState,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onAssetClick: (Long) -> Unit,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (AssetCategory) -> Unit,
    onStatusChange: (AssetStatus?) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onFocusConsumed: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val showSortMenu = remember { mutableStateOf(false) }

    LaunchedEffect(uiState.focusSearchOnOpen) {
        if (uiState.focusSearchOnOpen) {
            focusRequester.requestFocus()
            onFocusConsumed()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue)))
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = SoftWhite,
                )
            }
            Text(
                text = "资产明细",
                color = SoftWhite,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
            )
            Box {
                IconButton(onClick = { showSortMenu.value = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = AccentCyan,
                    )
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
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = AccentCyan,
                )
            }
        }

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(top = 12.dp),
            label = { Text("搜索资产") },
            shape = RoundedCornerShape(18.dp),
        )

        Surface(
            modifier = Modifier.padding(top = 10.dp),
            color = PanelBlue.copy(alpha = 0.56f),
            shape = RoundedCornerShape(100.dp),
        ) {
            Text(
                text = "排序：${uiState.sortOption.label}",
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        FlowRow(
            modifier = Modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AssetCategory.filters.forEach { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category,
                    onClick = { onCategoryChange(category) },
                    label = { Text(uiState.categoryDisplaySettings.nameOf(category)) },
                )
            }
        }

        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
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

        if (uiState.isEmpty) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = PanelBlue.copy(alpha = 0.82f),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "没有找到匹配资产",
                        color = SoftWhite,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "调整筛选条件，或新增一条资产记录。",
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.items, key = { it.asset.id }) { item ->
                    AssetListRow(
                        item = item,
                        categoryName = uiState.categoryDisplaySettings.nameOf(item.asset.category),
                        onClick = { onAssetClick(item.asset.id) },
                    )
                }
                item { Spacer(modifier = Modifier.height(18.dp)) }
            }
        }
    }
}

@Composable
private fun AssetListRow(
    item: AssetListItem,
    categoryName: String,
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ImageUtils.hasImage(item.asset.imageUri)) {
                AsyncImage(
                    model = item.asset.imageUri,
                    contentDescription = item.asset.name,
                    modifier = Modifier
                        .size(72.dp)
                        .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(20.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = TextSecondary,
                    )
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
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "$categoryName · ${item.asset.status.displayName}",
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = "购入 ${MoneyFormatter.format(item.asset.purchasePrice)} · ${DateUtils.formatDate(item.asset.purchaseDate)}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.metrics.useDays} 天",
                    color = SoftWhite,
                )
                Text(
                    text = MoneyFormatter.formatDailyCost(item.metrics.dailyCost),
                    color = AccentCyan,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}
