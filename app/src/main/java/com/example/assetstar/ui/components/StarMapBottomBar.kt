package com.example.assetstar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StarMapBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavBar(
        currentRoute = currentRoute,
        onNavigate = onNavigate,
        modifier = modifier,
    )
}
