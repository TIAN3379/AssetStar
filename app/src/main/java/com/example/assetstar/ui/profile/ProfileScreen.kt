package com.example.assetstar.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assetstar.BuildConfig
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.CurrencyDisplaySettings
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SpaceBlack
import com.example.assetstar.ui.theme.SpaceBlue
import com.example.assetstar.ui.theme.SpaceNavy
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onClearAll: () -> Unit,
    onCategoryNameChange: (AssetCategory, String) -> Unit,
    onCategoryIconChange: (AssetCategory, AssetCategory) -> Unit,
    onCategoryReset: (AssetCategory) -> Unit,
    onUseUsdChange: (Boolean) -> Unit,
    onRefreshExchangeRate: () -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<AssetCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue)))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
    ) {
        Text(
            text = "我的",
            color = SoftWhite,
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            shape = RoundedCornerShape(24.dp),
            color = PanelBlue.copy(alpha = 0.84f),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ProfileRow("App 名称", "资产星图")
                ProfileRow("版本号", BuildConfig.VERSION_NAME)
                ProfileRow("资产记录数", uiState.assetCount.toString())
                ProfileRow("数据说明", "所有数据仅保存在本机")
                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                ) {
                    Text("清空全部数据")
                }
            }
        }

        CurrencySettingsPanel(
            settings = uiState.currencyDisplaySettings,
            onUseUsdChange = onUseUsdChange,
            onRefreshExchangeRate = onRefreshExchangeRate,
            modifier = Modifier.padding(top = 18.dp),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            shape = RoundedCornerShape(24.dp),
            color = PanelBlue.copy(alpha = 0.84f),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "分类管理",
                    color = SoftWhite,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "可修改首页、明细、建档页面中显示的分类名称和图标样式。",
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp),
                )

                AssetCategory.selectable.forEach { category ->
                    CategoryManageRow(
                        category = category,
                        uiState = uiState,
                        onClick = { editingCategory = category },
                    )
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("清空全部数据") },
            text = { Text("确认清空全部资产记录吗？该操作无法恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onClearAll()
                    },
                ) { Text("确认清空") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) { Text("取消") }
            },
        )
    }

    editingCategory?.let { category ->
        CategoryEditDialog(
            category = category,
            uiState = uiState,
            onDismiss = { editingCategory = null },
            onSave = { name, iconSource ->
                onCategoryNameChange(category, name)
                onCategoryIconChange(category, iconSource)
                editingCategory = null
            },
            onReset = {
                onCategoryReset(category)
                editingCategory = null
            },
        )
    }
}

@Composable
private fun CurrencySettingsPanel(
    settings: CurrencyDisplaySettings,
    onUseUsdChange: (Boolean) -> Unit,
    onRefreshExchangeRate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = 0.84f),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "美元显示",
                        color = SoftWhite,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = if (settings.useUsd) "已按实时汇率换算为美元" else "当前以人民币显示",
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                Switch(
                    checked = settings.useUsd,
                    onCheckedChange = onUseUsdChange,
                )
            }

            Text(
                text = "1 USD ≈ ¥${"%.4f".format(settings.cnyPerUsd)}",
                color = SoftWhite,
                modifier = Modifier.padding(top = 14.dp),
            )
            Text(
                text = exchangeRateTimeText(settings),
                color = TextSecondary,
                modifier = Modifier.padding(top = 5.dp),
            )
            if (settings.errorMessage != null) {
                Text(
                    text = settings.errorMessage,
                    color = com.example.assetstar.ui.theme.AccentYellow,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            OutlinedButton(
                onClick = onRefreshExchangeRate,
                enabled = !settings.isRefreshing,
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text(if (settings.isRefreshing) "正在刷新汇率..." else "刷新实时汇率")
            }
        }
    }
}

private fun exchangeRateTimeText(settings: CurrencyDisplaySettings): String {
    if (settings.updatedAt <= 0L) return "尚未获取实时汇率，默认使用缓存估算"
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    return "汇率更新时间：${formatter.format(Date(settings.updatedAt))}"
}

@Composable
private fun ProfileRow(
    label: String,
    value: String,
) {
    Text(
        text = label,
        color = TextSecondary,
        modifier = Modifier.padding(top = 12.dp),
    )
    Text(
        text = value,
        color = SoftWhite,
        modifier = Modifier.padding(top = 6.dp),
    )
}

@Composable
private fun CategoryManageRow(
    category: AssetCategory,
    uiState: ProfileUiState,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(uiState.categoryDisplaySettings.iconOf(category)),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiState.categoryDisplaySettings.nameOf(category),
                color = SoftWhite,
            )
            Text(
                text = "默认：${category.displayName}",
                color = TextSecondary,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Text(text = "编辑", color = TextSecondary)
    }
}

@Composable
private fun CategoryEditDialog(
    category: AssetCategory,
    uiState: ProfileUiState,
    onDismiss: () -> Unit,
    onSave: (String, AssetCategory) -> Unit,
    onReset: () -> Unit,
) {
    var name by remember(category) {
        mutableStateOf(uiState.categoryDisplaySettings.nameOf(category))
    }
    var iconSource by remember(category) {
        mutableStateOf(uiState.categoryDisplaySettings.iconSources[category] ?: category)
    }
    var iconExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑分类") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("显示名称") },
                    singleLine = true,
                )
                Box(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedButton(onClick = { iconExpanded = true }) {
                        Image(
                            painter = painterResource(uiState.categoryDisplaySettings.iconOf(iconSource)),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = "图标样式：${uiState.categoryDisplaySettings.nameOf(iconSource)}",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = iconExpanded,
                        onDismissRequest = { iconExpanded = false },
                    ) {
                        AssetCategory.selectable.forEach { source ->
                            DropdownMenuItem(
                                text = { Text(uiState.categoryDisplaySettings.nameOf(source)) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(uiState.categoryDisplaySettings.iconOf(source)),
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                    )
                                },
                                onClick = {
                                    iconSource = source
                                    iconExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, iconSource) }) { Text("保存") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReset) { Text("恢复默认") }
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        },
    )
}
