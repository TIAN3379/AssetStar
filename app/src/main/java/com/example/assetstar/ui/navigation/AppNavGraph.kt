package com.example.assetstar.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assetstar.AssetStarApplication
import com.example.assetstar.domain.model.AssetCategory
import com.example.assetstar.ui.analysis.AnalysisScreen
import com.example.assetstar.ui.analysis.AnalysisViewModel
import com.example.assetstar.ui.category.CategoryAssetsScreen
import com.example.assetstar.ui.category.CategoryAssetsViewModel
import com.example.assetstar.ui.category.CategoryOverviewScreen
import com.example.assetstar.ui.category.CategoryOverviewViewModel
import com.example.assetstar.ui.components.BottomDestinations
import com.example.assetstar.ui.components.StarMapBottomBar
import com.example.assetstar.ui.detail.AssetDetailScreen
import com.example.assetstar.ui.detail.AssetDetailViewModel
import com.example.assetstar.ui.edit.AddEditAssetScreen
import com.example.assetstar.ui.edit.AddEditAssetViewModel
import com.example.assetstar.ui.home.HomeScreen
import com.example.assetstar.ui.home.HomeViewModel
import com.example.assetstar.ui.list.AssetListScreen
import com.example.assetstar.ui.list.AssetListViewModel
import com.example.assetstar.ui.profile.ProfileScreen
import com.example.assetstar.ui.profile.ProfileViewModel
import com.example.assetstar.util.MoneyFormatter

object AppRoutes {
    const val HOME = "home"
    const val LIST = "list"
    const val ANALYSIS = "analysis"
    const val PROFILE = "profile"
    const val DETAIL = "detail"
    const val EDIT = "edit"
    const val CATEGORY_OVERVIEW = "category_overview"
    const val CATEGORY_ASSETS = "category_assets"

    fun listRoute(
        category: AssetCategory = AssetCategory.ALL,
        focusSearch: Boolean = false,
    ): String {
        return "$LIST?category=${category.storageValue}&focusSearch=$focusSearch"
    }

    fun detailRoute(assetId: Long): String = "$DETAIL/$assetId"
    fun categoryAssetsRoute(category: AssetCategory): String {
        return "$CATEGORY_ASSETS/${Uri.encode(category.storageValue)}"
    }

    fun editRoute(
        assetId: Long? = null,
        defaultCategory: AssetCategory? = null,
    ): String {
        val params = buildList {
            if (assetId != null) add("assetId=$assetId")
            if (defaultCategory != null && defaultCategory != AssetCategory.ALL) {
                add("defaultCategory=${Uri.encode(defaultCategory.storageValue)}")
            }
        }
        return if (params.isEmpty()) EDIT else "$EDIT?${params.joinToString("&")}"
    }
}

@Composable
fun AppNavGraph() {
    val appContainer = (LocalContext.current.applicationContext as AssetStarApplication).appContainer
    val currencySettings by appContainer.currencySettingsRepository.settings.collectAsStateWithLifecycle()
    SideEffect {
        MoneyFormatter.updateCurrencySettings(currencySettings)
    }
    LaunchedEffect(currencySettings.useUsd) {
        if (currencySettings.useUsd) {
            appContainer.currencySettingsRepository.refreshRateIfNeeded()
        }
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = BottomDestinations.any { currentRoute?.startsWith(it.route) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                StarMapBottomBar(
                    currentRoute = BottomDestinations
                        .firstOrNull { currentRoute?.startsWith(it.route) == true }
                        ?.route,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    onNavigate = { route ->
                        val target = when (route) {
                            AppRoutes.LIST -> AppRoutes.listRoute()
                            else -> route
                        }
                        navController.navigate(target) {
                            popUpTo(AppRoutes.HOME) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        contentWindowInsets = WindowInsets.navigationBars,
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.HOME,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(AppRoutes.HOME) {
                val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onSearchClick = { navController.navigate(AppRoutes.listRoute(focusSearch = true)) },
                    onAddClick = { navController.navigate(AppRoutes.editRoute()) },
                    onAnalysisClick = { navController.navigate(AppRoutes.CATEGORY_OVERVIEW) },
                    onCategoryClick = { category ->
                        if (category == AssetCategory.ALL) {
                            navController.navigate(AppRoutes.CATEGORY_OVERVIEW)
                        } else {
                            navController.navigate(AppRoutes.categoryAssetsRoute(category))
                        }
                    },
                    onAssetClick = { assetId ->
                        navController.navigate(AppRoutes.detailRoute(assetId))
                    },
                )
            }

            composable(
                route = "${AppRoutes.LIST}?category={category}&focusSearch={focusSearch}",
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                        defaultValue = AssetCategory.ALL.storageValue
                    },
                    navArgument("focusSearch") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
            ) {
                val viewModel: AssetListViewModel = viewModel(factory = AssetListViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AssetListScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onAdd = { navController.navigate(AppRoutes.editRoute()) },
                    onAssetClick = { assetId ->
                        navController.navigate(AppRoutes.detailRoute(assetId))
                    },
                    onSearchChange = viewModel::updateSearchQuery,
                    onCategoryChange = viewModel::updateCategory,
                    onStatusChange = viewModel::updateStatus,
                    onSortChange = viewModel::updateSortOption,
                    onFocusConsumed = viewModel::consumeFocusRequest,
                )
            }

            composable(
                route = "${AppRoutes.DETAIL}/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.LongType }),
            ) {
                val viewModel: AssetDetailViewModel = viewModel(factory = AssetDetailViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AssetDetailScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onEdit = { assetId -> navController.navigate(AppRoutes.editRoute(assetId)) },
                    onDelete = viewModel::deleteCurrentAsset,
                    onDeleted = { navController.popBackStack() },
                )
            }

            composable(
                route = "${AppRoutes.EDIT}?assetId={assetId}&defaultCategory={defaultCategory}",
                arguments = listOf(
                    navArgument("assetId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument("defaultCategory") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) {
                val viewModel: AddEditAssetViewModel = viewModel(factory = AddEditAssetViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AddEditAssetScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onNameChange = viewModel::updateName,
                    onCategoryChange = viewModel::updateCategory,
                    onStatusChange = viewModel::updateStatus,
                    onPurchasePriceChange = viewModel::updatePurchasePrice,
                    onPurchaseDateChange = viewModel::updatePurchaseDate,
                    onEstimatedResidualValueChange = viewModel::updateEstimatedResidualValue,
                    onImageUriChange = viewModel::updateImageUri,
                    onNoteChange = viewModel::updateNote,
                    onSoldPriceChange = viewModel::updateSoldPrice,
                    onSoldDateChange = viewModel::updateSoldDate,
                    onSave = viewModel::saveAsset,
                    onSaved = { navController.popBackStack() },
                )
            }

            composable(AppRoutes.ANALYSIS) {
                val viewModel: AnalysisViewModel = viewModel(factory = AnalysisViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AnalysisScreen(uiState = uiState)
            }

            composable(AppRoutes.CATEGORY_OVERVIEW) {
                val viewModel: CategoryOverviewViewModel = viewModel(factory = CategoryOverviewViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                CategoryOverviewScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onCategoryClick = { category ->
                        navController.navigate(AppRoutes.categoryAssetsRoute(category))
                    },
                    onSortChange = viewModel::onSortTypeChange,
                )
            }

            composable(
                route = "${AppRoutes.CATEGORY_ASSETS}/{category}",
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                    },
                ),
            ) {
                val viewModel: CategoryAssetsViewModel = viewModel(factory = CategoryAssetsViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                CategoryAssetsScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() },
                    onAddAsset = { category ->
                        navController.navigate(AppRoutes.editRoute(defaultCategory = category))
                    },
                    onAssetClick = { assetId ->
                        navController.navigate(AppRoutes.detailRoute(assetId))
                    },
                    onSearchChange = viewModel::onSearchQueryChange,
                    onStatusChange = viewModel::onStatusFilterChange,
                    onSortChange = viewModel::onSortTypeChange,
                )
            }

            composable(AppRoutes.PROFILE) {
                val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ProfileScreen(
                    uiState = uiState,
                    onClearAll = viewModel::clearAll,
                    onCategoryNameChange = viewModel::updateCategoryName,
                    onCategoryIconChange = viewModel::updateCategoryIcon,
                    onCategoryReset = viewModel::resetCategory,
                    onUseUsdChange = viewModel::setUseUsd,
                    onRefreshExchangeRate = viewModel::refreshExchangeRate,
                )
            }
        }
    }
}
