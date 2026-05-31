package com.example.assetstar.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

@Composable
fun AssetDetailScreen(
    uiState: AssetDetailUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: () -> Unit,
    onDeleted: () -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) {
            onDeleted()
        }
    }

    val asset = uiState.asset ?: return
    val metrics = uiState.metrics ?: return
    val currentValuation = asset.soldPrice ?: asset.estimatedResidualValue ?: asset.purchasePrice

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = SoftWhite)
                }
                Text(
                    text = "资产详情",
                    color = SoftWhite,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onEdit(asset.id) }) {
                    Icon(Icons.Outlined.Edit, null, tint = AccentCyan)
                }
                IconButton(onClick = { showDialog.value = true }) {
                    Icon(Icons.Outlined.DeleteOutline, null, tint = SoftWhite)
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(28.dp),
                color = PanelBlue.copy(alpha = 0.84f),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (ImageUtils.hasImage(asset.imageUri)) {
                        AsyncImage(
                            model = asset.imageUri,
                            contentDescription = asset.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(22.dp)),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(SoftWhite.copy(alpha = 0.06f), RoundedCornerShape(22.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(44.dp),
                            )
                        }
                    }

                    Text(
                        text = asset.name,
                        color = SoftWhite,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 18.dp),
                    )
                    Text(
                        text = "${uiState.categoryDisplaySettings.nameOf(asset.category)} · ${asset.status.displayName}",
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    DetailRow("购买价格", MoneyFormatter.format(asset.purchasePrice))
                    DetailRow("当前估值", MoneyFormatter.format(currentValuation))
                    DetailRow("预计残值", MoneyFormatter.format(asset.estimatedResidualValue ?: 0.0))
                    DetailRow("购买日期", DateUtils.formatDate(asset.purchaseDate))
                    DetailRow("已使用天数", "${metrics.useDays} 天")
                    DetailRow(
                        label = if (metrics.actualDailyCost != null) "实际日均成本" else "日均成本",
                        value = MoneyFormatter.formatDailyCost(metrics.dailyCost),
                    )
                    if (metrics.actualCost != null) {
                        DetailRow("实际成本", MoneyFormatter.format(metrics.actualCost))
                    }
                    if (asset.status.displayName == "已出售") {
                        DetailRow("卖出价格", MoneyFormatter.format(asset.soldPrice ?: 0.0))
                        DetailRow("卖出日期", DateUtils.formatDate(asset.soldDate))
                    }
                    if (!asset.note.isNullOrBlank()) {
                        Text(
                            text = "备注",
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        Text(
                            text = asset.note,
                            color = SoftWhite,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { onEdit(asset.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            ) {
                Text("编辑资产")
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("删除资产") },
            text = { Text("确认删除“${asset.name}”吗？这个操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onDelete()
                    },
                ) { Text("删除") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) { Text("取消") }
            },
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = SoftWhite,
        )
    }
}
