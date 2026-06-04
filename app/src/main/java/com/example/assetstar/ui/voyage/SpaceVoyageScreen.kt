package com.example.assetstar.ui.voyage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.assetstar.R
import com.example.assetstar.domain.model.LedgerAccount
import com.example.assetstar.domain.model.LedgerCategory
import com.example.assetstar.domain.model.LedgerEntry
import com.example.assetstar.domain.model.LedgerType
import com.example.assetstar.ui.theme.AccentBlue
import com.example.assetstar.ui.theme.AccentCyan
import com.example.assetstar.ui.theme.AccentLime
import com.example.assetstar.ui.theme.AccentYellow
import com.example.assetstar.ui.theme.PanelBlue
import com.example.assetstar.ui.theme.PanelBlueStrong
import com.example.assetstar.ui.theme.SoftWhite
import com.example.assetstar.ui.theme.SpaceBlack
import com.example.assetstar.ui.theme.SpaceBlue
import com.example.assetstar.ui.theme.SpaceNavy
import com.example.assetstar.ui.theme.TextSecondary
import com.example.assetstar.util.MoneyFormatter
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private fun Float.toRadians(): Float {
    return Math.toRadians(toDouble()).toFloat()
}

@Composable
fun SpaceVoyageScreen(
    uiState: SpaceVoyageUiState,
    onBack: () -> Unit,
    onAddEntry: (String, String, LedgerType, LedgerCategory, LedgerAccount, String?) -> Unit,
    onDeleteEntry: (LedgerEntry) -> Unit,
    onMonthlyBudgetChange: (String) -> Unit,
    onAccountBaseBalanceChange: (LedgerAccount, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var showAddDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<LedgerAccount?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpaceBlack, SpaceNavy, SpaceBlue))),
    ) {
        Image(
            painter = painterResource(R.drawable.ledger_voyage_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.72f),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SpaceBlack.copy(alpha = 0.30f),
                            SpaceNavy.copy(alpha = 0.38f),
                            SpaceBlack.copy(alpha = 0.62f),
                        ),
                    ),
                ),
        )
        LedgerStarfield(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            LedgerHeader(onBack = onBack)
            Spacer(modifier = Modifier.height(6.dp))
            ShipEnergyPanel(
                latestEntry = uiState.entries.firstOrNull(),
                balance = uiState.balance,
                income = uiState.totalIncome,
                expense = uiState.totalExpense,
                monthlyBudget = uiState.monthlyBudget,
                monthlyExpense = uiState.monthlyExpense,
                monthlyRemaining = uiState.monthlyRemaining,
                monthlyRemainingRatio = uiState.monthlyRemainingRatio,
                monthlyBudgetUsedRatio = uiState.monthlyBudgetUsedRatio,
                monthElapsedRatio = uiState.monthElapsedRatio,
                todayExpense = uiState.todayExpense,
                energyLevel = uiState.energyLevel,
                onBudgetClick = { showBudgetDialog = true },
            )
            Spacer(modifier = Modifier.height(14.dp))
            AccountDockPanel(
                balances = uiState.accountBalances,
                onEditAccount = { editingAccount = it },
            )
            Spacer(modifier = Modifier.height(14.dp))
            QuickActionPanel(onAddClick = { showAddDialog = true })
            Spacer(modifier = Modifier.height(14.dp))
            LedgerHistoryPanel(
                entries = uiState.entries,
                onDeleteEntry = onDeleteEntry,
            )
        }

        if (showAddDialog) {
            AddLedgerEntryDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, amount, type, category, account, note ->
                    showAddDialog = false
                    onAddEntry(title, amount, type, category, account, note)
                },
            )
        }
        if (showBudgetDialog) {
            MonthlyBudgetDialog(
                currentBudget = uiState.monthlyBudget,
                onDismiss = { showBudgetDialog = false },
                onSave = { budget ->
                    onMonthlyBudgetChange(budget)
                    showBudgetDialog = false
                },
            )
        }
        editingAccount?.let { account ->
            AccountBalanceDialog(
                account = account,
                currentBalance = uiState.accountBalances
                    .firstOrNull { it.account == account }
                    ?.baseBalance
                    ?: 0.0,
                onDismiss = { editingAccount = null },
                onSave = { value ->
                    onAccountBaseBalanceChange(account, value)
                    editingAccount = null
                },
            )
        }
    }
}

@Composable
private fun LedgerHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "返回",
                tint = SoftWhite,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ShipEnergyPanel(
    latestEntry: LedgerEntry?,
    balance: Double,
    income: Double,
    expense: Double,
    monthlyBudget: Double,
    monthlyExpense: Double,
    monthlyRemaining: Double,
    monthlyRemainingRatio: Float,
    monthlyBudgetUsedRatio: Float,
    monthElapsedRatio: Float,
    todayExpense: Double,
    energyLevel: Float,
    onBudgetClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "shipEnergy")
    val hoverPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "shipHoverPhase",
    )
    val engineGlow by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "engineGlow",
    )
    val hoverWave = (sin((hoverPhase * 360f).toRadians()) + 1f) / 2f
    val shipYOffset = -17f - hoverWave * 7f
    val padGlow = 0.42f + hoverWave * 0.58f
    val scene = remember(latestEntry?.id, latestEntry?.type, latestEntry?.category) {
        voyageSceneFor(latestEntry)
    }
    val flightProgress = remember { Animatable(if (latestEntry == null) 0f else 1f) }
    var hasBoundInitialFlight by remember { mutableStateOf(false) }

    LaunchedEffect(latestEntry?.id) {
        if (latestEntry == null) {
            flightProgress.snapTo(0f)
            hasBoundInitialFlight = true
        } else if (!hasBoundInitialFlight) {
            flightProgress.snapTo(1f)
            hasBoundInitialFlight = true
        } else {
            flightProgress.snapTo(0f)
            delay(520)
            flightProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VoyageFlightStage(
            scene = scene,
            progress = flightProgress.value,
            energyLevel = energyLevel,
            engineGlow = engineGlow,
            shipYOffset = shipYOffset,
            padGlow = padGlow,
            modifier = Modifier
                .fillMaxWidth()
                .height(276.dp),
        )
        EnergyDevicePanel(
            monthlyBudget = monthlyBudget,
            monthlyExpense = monthlyExpense,
            monthlyRemaining = monthlyRemaining,
            monthlyRemainingRatio = monthlyRemainingRatio,
            monthlyBudgetUsedRatio = monthlyBudgetUsedRatio,
            monthElapsedRatio = monthElapsedRatio,
            onBudgetClick = onBudgetClick,
            modifier = Modifier.padding(top = 14.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricPill("总收入", MoneyFormatter.formatCompact(income), AccentCyan, Modifier.weight(1f))
            MetricPill("总支出", MoneyFormatter.formatCompact(expense), AccentYellow, Modifier.weight(1f))
            MetricPill("今日消耗", MoneyFormatter.formatCompact(todayExpense), AccentBlue, Modifier.weight(1f))
        }
    }
}

@Composable
private fun VoyageFlightStage(
    scene: VoyageScenePresentation,
    progress: Float,
    energyLevel: Float,
    engineGlow: Float,
    shipYOffset: Float,
    padGlow: Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        val hasDestination = scene.imageResId != null
        val destinationAlpha = if (hasDestination) {
            ((clampedProgress - 0.12f) / 0.38f).coerceIn(0f, 1f)
        } else {
            0f
        }
        val travelLift = sin((clampedProgress * 180f).toRadians())
        val startX = maxWidth * 0.20f
        val startY = maxHeight * 0.78f
        val destinationWidth = 268.dp
        val destinationHeight = 154.dp
        val destinationEndPadding = 0.dp
        val destinationTopPadding = 0.dp
        val platformOffsetX = 18.dp
        val platformOffsetY = 52.dp
        val platformWidth = 106.dp
        val platformSurfaceY = 30.dp
        val platformCenterX = maxWidth - destinationEndPadding - destinationWidth + platformOffsetX + platformWidth * 0.50f
        val platformCenterY = destinationTopPadding + platformOffsetY + platformSurfaceY
        val endX = platformCenterX
        val endY = platformCenterY - 2.dp
        val shipX = startX + (endX - startX) * clampedProgress
        val shipY = startY + (endY - startY) * clampedProgress - 24.dp * travelLift
        val shipSize = 54.dp
        val shipScale = 0.86f + clampedProgress * 0.08f
        val shipRotation = -12f + clampedProgress * 8f

        VoyageRouteCanvas(
            progress = clampedProgress,
            energyLevel = energyLevel,
            engineGlow = engineGlow,
            enabled = hasDestination,
            modifier = Modifier.matchParentSize(),
        )

        if (destinationAlpha > 0f) {
            SceneDestinationBackLayer(
                scene = scene,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = destinationTopPadding, end = destinationEndPadding)
                    .size(width = destinationWidth, height = destinationHeight)
                    .graphicsLayer {
                        alpha = destinationAlpha
                        scaleX = 0.96f + destinationAlpha * 0.04f
                        scaleY = 0.96f + destinationAlpha * 0.04f
                    },
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 6.dp, bottom = 2.dp)
                .size(width = 128.dp, height = 92.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.voyage_landing_pad),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 112.dp, height = 78.dp),
            )
            LandingPadGlow(
                glow = padGlow,
                modifier = Modifier.size(width = 112.dp, height = 78.dp),
            )
        }

        Image(
            painter = painterResource(R.drawable.voyage_ship),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(
                    x = shipX - shipSize / 2f,
                    y = shipY - shipSize / 2f,
                )
                .size(shipSize)
                .graphicsLayer {
                    translationY = if (clampedProgress < 0.02f) shipYOffset else 0f
                    scaleX = shipScale
                    scaleY = shipScale
                    rotationZ = if (clampedProgress > 0.92f) shipRotation * (1f - clampedProgress) * 12.5f else shipRotation
                },
        )

        if (destinationAlpha > 0f) {
            SceneDestinationFrontLayer(
                scene = scene,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = destinationTopPadding, end = destinationEndPadding)
                    .size(width = destinationWidth, height = destinationHeight)
                    .graphicsLayer {
                        alpha = destinationAlpha
                        scaleX = 0.96f + destinationAlpha * 0.04f
                        scaleY = 0.96f + destinationAlpha * 0.04f
                    },
            )
        }

    }
}

@Composable
private fun SceneDestinationBackLayer(
    scene: VoyageScenePresentation,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        scene.accent.copy(alpha = 0.18f),
                        scene.accent.copy(alpha = 0.06f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.52f, size.height * 0.58f),
                    radius = size.minDimension * 0.70f,
                ),
                radius = size.minDimension * 0.70f,
                center = Offset(size.width * 0.52f, size.height * 0.58f),
            )
        }
        Image(
            painter = painterResource(R.drawable.ledger_temporary_platform),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = 18.dp, y = 52.dp)
                .size(width = 106.dp, height = 77.dp),
        )
    }
}

@Composable
private fun SceneDestinationFrontLayer(
    scene: VoyageScenePresentation,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        scene.imageResId?.let { imageRes ->
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 0.dp, bottom = 6.dp)
                    .size(width = 196.dp, height = 142.dp)
                    .alpha(0.98f),
            )
        }
    }
}

@Composable
private fun VoyageRouteCanvas(
    progress: Float,
    energyLevel: Float,
    engineGlow: Float,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (!enabled) return@Canvas
        repeat(7) { index ->
            val angle = Math.toRadians((index * 52f + progress * 180f).toDouble()).toFloat()
            drawCircle(
                color = AccentCyan.copy(alpha = 0.10f),
                radius = (1.2f + index % 3).dp.toPx(),
                center = Offset(
                    x = size.width * (0.10f + index * 0.12f),
                    y = size.height * (0.18f + 0.18f * sin(angle)),
                ),
            )
        }
    }
}

@Composable
private fun LandingPadGlow(
    glow: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width * 0.50f, size.height * 0.52f)
        val cyan = AccentCyan.copy(alpha = 0.26f * glow)
        val blue = AccentBlue.copy(alpha = 0.18f * glow)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    cyan,
                    blue,
                    Color.Transparent,
                ),
                center = center,
                radius = size.minDimension * (0.54f + glow * 0.08f),
            ),
            topLeft = Offset(size.width * 0.18f, size.height * 0.22f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.48f),
        )
        drawCircle(
            color = AccentCyan.copy(alpha = 0.42f * glow),
            radius = 5.5.dp.toPx(),
            center = center,
        )
        repeat(8) { index ->
            val angle = Math.toRadians((index * 45f - 15f).toDouble()).toFloat()
            drawCircle(
                color = AccentCyan.copy(alpha = 0.24f * glow),
                radius = 2.2.dp.toPx(),
                center = Offset(
                    x = center.x + cos(angle) * size.width * 0.22f,
                    y = center.y + sin(angle) * size.height * 0.18f,
                ),
            )
        }
    }
}

private data class VoyageScenePresentation(
    val imageResId: Int?,
    val placeName: String,
    val placeHint: String,
    val routeTitle: String,
    val routeSubtitle: String,
    val accent: Color,
)

private fun voyageSceneFor(entry: LedgerEntry?): VoyageScenePresentation {
    if (entry == null) {
        return VoyageScenePresentation(
            imageResId = null,
            placeName = "停机坪待命",
            placeHint = "等待下一次能源航线",
            routeTitle = "航线未激活",
            routeSubtitle = "记一笔后飞船会驶向对应场所",
            accent = AccentCyan,
        )
    }

    val amount = MoneyFormatter.formatCompact(entry.amount)
    val isIncome = entry.type == LedgerType.INCOME
    return when {
        isIncome && (entry.category == LedgerCategory.SALARY || entry.category == LedgerCategory.BONUS) ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_large_mine,
                placeName = "大型能源矿藏",
                placeHint = "工资与奖金补给",
                routeTitle = "收入补充航线",
                routeSubtitle = "${entry.category.displayName} +$amount",
                accent = AccentLime,
            )

        isIncome ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_small_mine,
                placeName = "小型能源矿藏",
                placeHint = "其他收入补给",
                routeTitle = "收入补充航线",
                routeSubtitle = "${entry.category.displayName} +$amount",
                accent = AccentCyan,
            )

        entry.category == LedgerCategory.FOOD ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_restaurant,
                placeName = "星际餐厅",
                placeHint = "餐饮消费停靠点",
                routeTitle = "支出消耗航线",
                routeSubtitle = "${entry.category.displayName} -$amount",
                accent = AccentYellow,
            )

        entry.category == LedgerCategory.TRANSPORT ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_transport_carrier,
                placeName = "空天母舰",
                placeHint = "出行消费停靠点",
                routeTitle = "支出消耗航线",
                routeSubtitle = "${entry.category.displayName} -$amount",
                accent = AccentBlue,
            )

        entry.category == LedgerCategory.ENTERTAINMENT ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_entertainment,
                placeName = "星际娱乐厅",
                placeHint = "娱乐消费停靠点",
                routeTitle = "支出消耗航线",
                routeSubtitle = "${entry.category.displayName} -$amount",
                accent = AccentYellow,
            )

        entry.category == LedgerCategory.SHOPPING || entry.category == LedgerCategory.LIVING ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_store,
                placeName = "星际商店",
                placeHint = "生活与购物停靠点",
                routeTitle = "支出消耗航线",
                routeSubtitle = "${entry.category.displayName} -$amount",
                accent = AccentCyan,
            )

        else ->
            VoyageScenePresentation(
                imageResId = R.drawable.ledger_scene_small_mine,
                placeName = "临时补给点",
                placeHint = "未归类能源记录",
                routeTitle = if (isIncome) "收入补充航线" else "支出消耗航线",
                routeSubtitle = "${entry.category.displayName} ${if (isIncome) "+" else "-"}$amount",
                accent = if (isIncome) AccentCyan else AccentYellow,
            )
    }
}

private fun quadraticBezier(
    start: Offset,
    control: Offset,
    end: Offset,
    t: Float,
): Offset {
    val oneMinusT = 1f - t
    return Offset(
        x = oneMinusT * oneMinusT * start.x + 2f * oneMinusT * t * control.x + t * t * end.x,
        y = oneMinusT * oneMinusT * start.y + 2f * oneMinusT * t * control.y + t * t * end.y,
    )
}


@Composable
private fun EnergyDevicePanel(
    monthlyBudget: Double,
    monthlyExpense: Double,
    monthlyRemaining: Double,
    monthlyRemainingRatio: Float,
    monthlyBudgetUsedRatio: Float,
    monthElapsedRatio: Float,
    onBudgetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val warning = budgetWarningFor(
        monthlyBudget = monthlyBudget,
        monthlyRemainingRatio = monthlyRemainingRatio,
        monthlyBudgetUsedRatio = monthlyBudgetUsedRatio,
        monthElapsedRatio = monthElapsedRatio,
    )
    val device = energyDeviceFor(monthlyBudget, monthlyRemainingRatio, warning)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = if (warning == BudgetWarningLevel.FAULT) {
            AccentYellow.copy(alpha = 0.12f)
        } else {
            Color.White.copy(alpha = 0.065f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = warning.color.copy(alpha = if (warning == BudgetWarningLevel.NORMAL) 0.12f else 0.36f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(device.resId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(82.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (warning == BudgetWarningLevel.NORMAL) "本月预算能源核心" else warning.title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = device.label,
                    color = device.color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp),
                )
                LinearProgressIndicator(
                    progress = { monthlyRemainingRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(7.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = device.color,
                    trackColor = Color.White.copy(alpha = 0.10f),
                )
                Text(
                    text = "剩余 ${MoneyFormatter.formatCompact(monthlyRemaining.coerceAtLeast(0.0))} / 预算 ${MoneyFormatter.formatCompact(monthlyBudget)}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "本月已消耗 ${MoneyFormatter.formatCompact(monthlyExpense)}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1,
                )
                if (warning != BudgetWarningLevel.NORMAL) {
                    Text(
                        text = warning.message,
                        color = warning.color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                OutlinedButton(
                    onClick = onBudgetClick,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(if (monthlyBudget > 0.0) "调整预算" else "设置预算")
                }
            }
        }
    }
}

@Composable
private fun MonthlyBudgetDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var budget by remember(currentBudget) {
        mutableStateOf(if (currentBudget > 0.0) "%.2f".format(Locale.US, currentBudget) else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置每月预算") },
        text = {
            Column {
                Text(
                    text = "预算用于判断能源装置等级，本月支出会消耗该预算。",
                    color = TextSecondary,
                    fontSize = 13.sp,
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("每月预算") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(budget) },
                enabled = budget.trim().toDoubleOrNull()?.let { it >= 0.0 } == true,
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

private data class EnergyDevicePresentation(
    val resId: Int,
    val label: String,
    val color: Color,
)

private enum class BudgetWarningLevel(
    val title: String,
    val message: String,
    val color: Color,
) {
    NORMAL(
        title = "本月预算能源核心",
        message = "",
        color = TextSecondary,
    ),
    FAST(
        title = "能源消耗过快",
        message = "本月进度未过半，预算消耗已明显超前。",
        color = AccentYellow,
    ),
    LOW(
        title = "进入低能源航行",
        message = "预算剩余低于 20%，建议放缓支出。",
        color = AccentYellow,
    ),
    FAULT(
        title = "能源核心故障",
        message = "本月预算已超支，能源核心进入故障保护。",
        color = Color(0xFFFF5C5C),
    ),
}

private fun budgetWarningFor(
    monthlyBudget: Double,
    monthlyRemainingRatio: Float,
    monthlyBudgetUsedRatio: Float,
    monthElapsedRatio: Float,
): BudgetWarningLevel {
    if (monthlyBudget <= 0.0) return BudgetWarningLevel.NORMAL
    return when {
        monthlyBudgetUsedRatio > 1f -> BudgetWarningLevel.FAULT
        monthlyRemainingRatio < 0.20f -> BudgetWarningLevel.LOW
        monthElapsedRatio >= 0.30f && monthlyBudgetUsedRatio >= 0.60f -> BudgetWarningLevel.FAST
        else -> BudgetWarningLevel.NORMAL
    }
}

private fun energyDeviceFor(
    monthlyBudget: Double,
    ratio: Float,
    warning: BudgetWarningLevel,
): EnergyDevicePresentation {
    return when {
        warning == BudgetWarningLevel.FAULT -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_fault,
            label = "故障保护中",
            color = warning.color,
        )
        monthlyBudget <= 0.0 -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_zero,
            label = "等待预算注入",
            color = TextSecondary,
        )
        warning == BudgetWarningLevel.LOW -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_critical,
            label = "低能源航行",
            color = warning.color,
        )
        warning == BudgetWarningLevel.FAST -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_low,
            label = "消耗过快",
            color = warning.color,
        )
        ratio <= 0f -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_zero,
            label = "能源归零",
            color = AccentYellow,
        )
        ratio < 0.15f -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_critical,
            label = "能源严重不足",
            color = AccentYellow,
        )
        ratio < 0.35f -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_low,
            label = "能源略微不足",
            color = AccentYellow,
        )
        ratio < 0.55f -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_normal,
            label = "能源一般充足",
            color = AccentCyan,
        )
        ratio < 0.80f -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_high,
            label = "能源大部分充足",
            color = AccentCyan,
        )
        else -> EnergyDevicePresentation(
            resId = R.drawable.energy_device_full,
            label = "能源完全充足",
            color = AccentLime,
        )
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.06f),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
            Text(text = label, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
            Text(
                text = value,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AccountDockPanel(
    balances: List<LedgerAccountBalance>,
    onEditAccount: (LedgerAccount) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = 0.74f),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "资金舱",
                        color = SoftWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "记录银行卡、支付宝、微信当前可用金额。",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ledger_money_pod),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(width = 86.dp, height = 64.dp)
                        .padding(start = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                balances.forEach { balance ->
                    AccountDockCard(
                        balance = balance,
                        onClick = { onEditAccount(balance.account) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountDockCard(
    balance: LedgerAccountBalance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.065f),
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            Image(
                painter = painterResource(R.drawable.ledger_money_pod),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
            )
            Text(
                text = balance.account.shortName,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = MoneyFormatter.formatCompact(balance.currentBalance),
                color = if (balance.currentBalance >= 0.0) AccentCyan else AccentYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
    }
}

@Composable
private fun QuickActionPanel(onAddClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlueStrong.copy(alpha = 0.76f),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "记录一次能源变化",
                    color = SoftWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "收入补充动力，支出消耗燃料。",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Button(onClick = onAddClick) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("记一笔")
            }
        }
    }
}

@Composable
private fun LedgerHistoryPanel(
    entries: List<LedgerEntry>,
    onDeleteEntry: (LedgerEntry) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = PanelBlue.copy(alpha = 0.74f),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "航行日志",
                color = SoftWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            if (entries.isEmpty()) {
                Text(
                    text = "还没有记账记录。",
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
                )
            } else {
                entries.take(30).forEach { entry ->
                    LedgerEntryRow(
                        entry = entry,
                        onDelete = { onDeleteEntry(entry) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LedgerEntryRow(
    entry: LedgerEntry,
    onDelete: () -> Unit,
) {
    val isIncome = entry.type == LedgerType.INCOME
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isIncome) AccentCyan.copy(alpha = 0.18f) else AccentYellow.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isIncome) "+" else "-",
                color = if (isIncome) AccentCyan else AccentYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = entry.title,
                color = SoftWhite,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${entry.account.shortName} · ${entry.category.displayName} · ${formatDate(entry.occurredAt)}",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 3.dp),
                maxLines = 1,
            )
        }
        Text(
            text = "${if (isIncome) "+" else "-"}${MoneyFormatter.format(entry.amount)}",
            color = if (isIncome) AccentCyan else AccentYellow,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(start = 8.dp),
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "删除",
                tint = TextSecondary,
            )
        }
    }
}

@Composable
private fun AddLedgerEntryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, LedgerType, LedgerCategory, LedgerAccount, String?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(LedgerType.EXPENSE) }
    var category by remember { mutableStateOf(LedgerCategory.LIVING) }
    var account by remember { mutableStateOf(LedgerAccount.BANK_CARD) }
    var note by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }
    val amountValue = amount.trim().toDoubleOrNull()
    val canSave = amountValue?.let { it > 0.0 } == true
    val typeColor = if (type == LedgerType.INCOME) AccentCyan else AccentYellow

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxWidth()
                .widthIn(max = 430.dp),
            shape = RoundedCornerShape(30.dp),
            color = PanelBlueStrong.copy(alpha = 0.98f),
            border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.26f)),
            shadowElevation = 18.dp,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(typeColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (type == LedgerType.INCOME) "+" else "-",
                            color = typeColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                    ) {
                        Text(
                            text = "记录能源变化",
                            color = SoftWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "资金舱、分类和金额会写入航行日志",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 3.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LedgerType.entries.forEach { item ->
                        SegmentPill(
                            text = item.displayName,
                            selected = item == type,
                            selectedColor = if (item == LedgerType.INCOME) AccentCyan else AccentYellow,
                            modifier = Modifier.weight(1f),
                            onClick = { type = item },
                        )
                    }
                }

                Text(
                    text = "金额",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp),
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    leadingIcon = {
                        Text(
                            text = "¥",
                            color = typeColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )

                Text(
                    text = "流水名称",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 14.dp),
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("例如 午餐、工资、设备收入") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )

                Text(
                    text = "资金舱与分类",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 14.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SelectPill(
                            label = "资金舱",
                            value = account.shortName,
                            color = AccentCyan,
                            onClick = { accountExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        DropdownMenu(
                            expanded = accountExpanded,
                            onDismissRequest = { accountExpanded = false },
                            modifier = Modifier.widthIn(min = 184.dp),
                            shape = RoundedCornerShape(20.dp),
                            containerColor = PanelBlueStrong.copy(alpha = 0.98f),
                            tonalElevation = 0.dp,
                            shadowElevation = 14.dp,
                            border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.26f)),
                        ) {
                            LedgerAccount.entries.forEach { item ->
                                StyledDropdownItem(
                                    title = item.shortName,
                                    subtitle = item.displayName,
                                    selected = item == account,
                                    color = AccentCyan,
                                    onClick = {
                                        account = item
                                        accountExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SelectPill(
                            label = "分类",
                            value = category.displayName,
                            color = AccentBlue,
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.widthIn(min = 184.dp),
                            shape = RoundedCornerShape(20.dp),
                            containerColor = PanelBlueStrong.copy(alpha = 0.98f),
                            tonalElevation = 0.dp,
                            shadowElevation = 14.dp,
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.28f)),
                        ) {
                            LedgerCategory.entries.forEach { item ->
                                StyledDropdownItem(
                                    title = item.displayName,
                                    subtitle = if (item == LedgerCategory.OTHER) "未归类航行记录" else "能源流向分类",
                                    selected = item == category,
                                    color = AccentBlue,
                                    onClick = {
                                        category = item
                                        categoryExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "备注",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 14.dp),
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("可选") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                    ) {
                        Text(
                            text = "取消",
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Button(
                        onClick = { onSave(title, amount, type, category, account, note) },
                        enabled = canSave,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                    ) {
                        Text(
                            text = if (type == LedgerType.INCOME) "补充能源" else "消耗能源",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyledDropdownItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) color.copy(alpha = 0.14f) else Color.Transparent),
        text = {
            Column {
                Text(
                    text = title,
                    color = if (selected) color else SoftWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = if (selected) 0.24f else 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color),
                )
            }
        },
        colors = MenuDefaults.itemColors(
            textColor = SoftWhite,
            leadingIconColor = color,
        ),
        onClick = onClick,
    )
}

@Composable
private fun SegmentPill(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) selectedColor.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.06f),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) selectedColor.copy(alpha = 0.58f) else Color.White.copy(alpha = 0.10f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = if (selected) selectedColor else TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SelectPill(
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.065f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = value,
                color = SoftWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun AccountBalanceDialog(
    account: LedgerAccount,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var amount by remember(account, currentBalance) {
        mutableStateOf("%.2f".format(Locale.US, currentBalance))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelBlueStrong,
        titleContentColor = SoftWhite,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(26.dp),
        title = {
            Column {
                Text(
                    text = "设置${account.displayName}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "这里记录该资金舱当前已有多少钱。",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("当前余额") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(amount) },
                enabled = amount.trim().toDoubleOrNull() != null,
            ) {
                Text("保存", color = AccentCyan)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消", color = TextSecondary) }
        },
    )
}

@Composable
private fun ShipOrbitCanvas(
    energyLevel: Float,
    engineGlow: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.34f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentCyan.copy(alpha = 0.14f * engineGlow),
                    Color.Transparent,
                ),
                center = center,
                radius = radius * (1.45f + energyLevel * 0.30f),
            ),
            radius = radius * (1.45f + energyLevel * 0.30f),
            center = center,
        )
        for (i in 0 until 3) {
            drawCircle(
                color = AccentCyan.copy(alpha = 0.08f - i * 0.018f),
                radius = radius + i * 14.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx()),
            )
        }
        drawArc(
            color = AccentLime.copy(alpha = 0.50f + energyLevel * 0.35f),
            startAngle = 28f,
            sweepAngle = 62f + energyLevel * 120f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 1.16f, center.y - radius * 0.58f),
            size = androidx.compose.ui.geometry.Size(radius * 2.32f, radius * 1.16f),
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun LedgerStarfield(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ledgerStarfield")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(22_000, easing = LinearEasing)),
        label = "starfieldPhase",
    )
    Canvas(modifier = modifier.alpha(0.9f)) {
        val center = Offset(size.width * 0.5f, size.height * 0.42f)
        for (i in 0 until 72) {
            val angle = Math.toRadians((i * 37f + phase * 0.08f).toDouble()).toFloat()
            val distance = (size.minDimension * 0.12f) + (i % 12) * size.minDimension * 0.035f
            drawCircle(
                color = listOf(AccentCyan, SoftWhite, AccentBlue)[i % 3].copy(alpha = 0.16f + (i % 5) * 0.025f),
                radius = (0.8f + i % 3).dp.toPx(),
                center = Offset(
                    x = center.x + cos(angle) * distance,
                    y = center.y + sin(angle) * distance * 1.8f,
                ),
            )
        }
    }
}

private fun formatDate(value: Long): String {
    return SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(Date(value))
}
