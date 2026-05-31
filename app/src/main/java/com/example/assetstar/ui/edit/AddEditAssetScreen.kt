package com.example.assetstar.ui.edit

import android.app.DatePickerDialog
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.domain.model.AssetStatus
import com.example.assetstar.ui.components.FullScreenStarBackground
import com.example.assetstar.ui.components.rememberGalaxyAnimationState
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.DateUtils
import com.example.assetstar.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditAssetScreen(
    uiState: AddEditAssetUiState,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (AssetCategory) -> Unit,
    onStatusChange: (AssetStatus) -> Unit,
    onPurchasePriceChange: (String) -> Unit,
    onPurchaseDateChange: (Long) -> Unit,
    onEstimatedResidualValueChange: (String) -> Unit,
    onImageUriChange: (String?) -> Unit,
    onNoteChange: (String) -> Unit,
    onSoldPriceChange: (String) -> Unit,
    onSoldDateChange: (Long?) -> Unit,
    onSave: () -> Unit,
    onSaved: () -> Unit,
) {
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) onSaved()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val animationState = rememberGalaxyAnimationState()
    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) {
            onImageUriChange(null)
            return@rememberLauncherForActivityResult
        }

        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
            // Some providers do not expose persistable permissions.
        }

        coroutineScope.launch {
            val localImageUri = withContext(Dispatchers.IO) {
                ImageUtils.copyImageToPrivateStorage(context, uri)
            }
            onImageUriChange(localImageUri ?: uri.toString())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FullScreenStarBackground(
            modifier = Modifier.fillMaxSize(),
            animationState = animationState,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = SoftWhite)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.isEditing) "编辑资产档案" else "建立资产档案",
                        color = SoftWhite,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "记录物品价值、状态和持有成本",
                        color = TextSecondary,
                        fontSize = 13.sp,
                    )
                }
            }

            AssetPhotoHero(
                imageUri = uiState.imageUri,
                onPickImage = { pickerLauncher.launch(arrayOf("image/*")) },
                modifier = Modifier.padding(top = 14.dp),
            )

            SectionPanel(title = "基础信息", modifier = Modifier.padding(top = 14.dp)) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("资产名称") },
                    singleLine = true,
                    isError = uiState.nameError != null,
                    supportingText = { uiState.nameError?.let { Text(it) } },
                )
                OutlinedTextField(
                    value = uiState.purchasePrice,
                    onValueChange = onPurchasePriceChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("购入金额") },
                    singleLine = true,
                    isError = uiState.purchasePriceError != null,
                    supportingText = { uiState.purchasePriceError?.let { Text(it) } },
                )
                DateField(
                    label = "购买日期",
                    value = uiState.purchaseDate,
                    onValueChange = onPurchaseDateChange,
                    modifier = Modifier.padding(top = 12.dp),
                )
                OutlinedTextField(
                    value = uiState.estimatedResidualValue,
                    onValueChange = onEstimatedResidualValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("预计残值，可不填") },
                    singleLine = true,
                )
            }

            SectionPanel(title = "资产分类", modifier = Modifier.padding(top = 14.dp)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AssetCategory.selectable.forEach { category ->
                        CategorySelectCard(
                            category = category,
                            label = uiState.categoryDisplaySettings.nameOf(category),
                            iconRes = uiState.categoryDisplaySettings.iconOf(category),
                            selected = uiState.category == category,
                            onClick = { onCategoryChange(category) },
                        )
                    }
                }
            }

            SectionPanel(title = "使用状态", modifier = Modifier.padding(top = 14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AssetStatus.entries.forEach { status ->
                        StatusSegment(
                            status = status,
                            selected = uiState.status == status,
                            onClick = { onStatusChange(status) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (uiState.status == AssetStatus.SOLD) {
                    OutlinedTextField(
                        value = uiState.soldPrice,
                        onValueChange = onSoldPriceChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        label = { Text("卖出价格") },
                        singleLine = true,
                        isError = uiState.soldPriceError != null,
                        supportingText = { uiState.soldPriceError?.let { Text(it) } },
                    )
                    DateField(
                        label = "卖出日期",
                        value = uiState.soldDate ?: DateUtils.todayStartMillis(),
                        onValueChange = { onSoldDateChange(it) },
                        errorText = uiState.soldDateError,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }

            SectionPanel(title = "备注", modifier = Modifier.padding(top = 14.dp)) {
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = onNoteChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    label = { Text("购买渠道、位置、保修信息等") },
                )
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            ) {
                Text("保存资产档案")
            }
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
private fun AssetPhotoHero(
    imageUri: String?,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clickable(onClick = onPickImage),
        shape = RoundedCornerShape(30.dp),
        color = PanelBlue.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.20f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Image, null, tint = AccentCyan, modifier = Modifier.size(42.dp))
                    Text("添加资产图片", color = SoftWhite, modifier = Modifier.padding(top = 10.dp))
                    Text("点击选择照片", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = PanelBlue.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, SoftWhite.copy(alpha = 0.10f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = SoftWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Column(modifier = Modifier.padding(top = 14.dp), content = content)
        }
    }
}

@Composable
private fun CategorySelectCard(
    category: AssetCategory,
    label: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(width = 82.dp, height = 78.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) AccentCyan.copy(alpha = 0.18f) else SoftWhite.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, if (selected) AccentCyan.copy(alpha = 0.70f) else SoftWhite.copy(alpha = 0.10f)),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = category.storageValue,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = label,
                color = if (selected) SoftWhite else TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 7.dp),
            )
        }
    }
}

@Composable
private fun StatusSegment(
    status: AssetStatus,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(100.dp),
        color = if (selected) AccentLime.copy(alpha = 0.16f) else SoftWhite.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, if (selected) AccentLime.copy(alpha = 0.58f) else SoftWhite.copy(alpha = 0.10f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = status.displayName,
                color = if (selected) SoftWhite else TextSecondary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun DateField(
    label: String,
    value: Long,
    onValueChange: (Long) -> Unit,
    errorText: String? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = value }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = DateUtils.formatDate(value),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = errorText != null,
            label = { Text(label) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                onValueChange(DateUtils.toStartOfDayMillis(year, month, day))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                        ).show()
                    },
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null)
                }
            },
        )

        if (errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 6.dp, start = 16.dp),
            )
        }
    }
}
